# Sunucu Yükleme Yol Haritası (DigitalOcean Droplet - GitHub Entegrasyonu)

Projenizi GitHub üzerinden sunucuya çekerek `www.suleymansecgin.com.tr/admin_panel` adresine yüklemek için aşağıdaki adımları takip edebilirsiniz.

**Proje Özeti:**
*   **Backend:** Java 17 (Spring Boot)
*   **Frontend:** React (Vite)
*   **Veritabanı:** PostgreSQL
*   **Sunucu:** Ubuntu (DigitalOcean Droplet)

---

## 1. Sunucu Hazırlığı

Sunucunuza SSH ile bağlandıktan sonra gerekli paketleri yükleyin.

### 1.1. Sistemi Güncelleme ve Git Kurulumu
```bash
sudo apt update
sudo apt upgrade -y
sudo apt install git -y
```

### 1.2. Java 17 Kurulumu (Backend için)
```bash
sudo apt install openjdk-17-jdk -y
java -version # Kurulumu doğrula
```

### 1.3. PostgreSQL Kurulumu (Veritabanı için)
```bash
sudo apt install postgresql postgresql-contrib -y
```

### 1.4. Nginx Kurulumu (Web Sunucusu)
```bash
sudo apt install nginx -y
sudo systemctl start nginx
sudo systemctl enable nginx
```

### 1.5. Node.js ve npm Kurulumu (Frontend Build için)
Frontend sunucuda build edileceği için Node.js gereklidir.
```bash
curl -fsSL https://deb.nodesource.com/setup_20.x | sudo -E bash -
sudo apt install -y nodejs
node -v # Versiyonu kontrol et (v20.x olmalı)
```

---

## 2. Projeyi GitHub'dan Çekme

Projeyi `/var/www` dizinine klonlayacağız.

```bash
cd /var/www
sudo git clone https://github.com/suleymansecgin/admin-panel.git admin_panel
# Eğer repo gizliyse, kullanıcı adı ve token soracaktır veya SSH key kullanmanız gerekir.
```

Dizin sahipliğini ayarlayın (kendi kullanıcınızla işlem yapabilmek için):
```bash
sudo chown -R $USER:$USER /var/www/admin_panel
```

---

## 3. Veritabanı Konfigürasyonu

```bash
sudo -i -u postgres
psql
```

PostgreSQL komut satırında:
```sql
-- Şifre application.properties ile aynı olmalı
ALTER USER postgres PASSWORD '207615';
CREATE DATABASE admin_panel;
\q
exit
```

---

## 4. Backend (Spring Boot) Kurulumu

### 4.1. Build Alma
Sunucuda projeyi derleyin.
```bash
cd /var/www/admin_panel/admin_panel
chmod +x mvnw
./mvnw clean package -DskipTests
```
Başarılı olursa `target` klasöründe `.jar` dosyası oluşacaktır.

### 4.2. Servis Oluşturma (Systemd)
```bash
sudo nano /etc/systemd/system/admin_panel.service
```

İçerik:
```ini
[Unit]
Description=Admin Panel Backend Service
After=syslog.target network.target postgresql.service

[Service]
User=root
# Jar dosyasının tam yoluna dikkat edin
ExecStart=/usr/bin/java -jar /var/www/admin_panel/admin_panel/target/admin_panel-0.0.1-SNAPSHOT.jar
SuccessExitStatus=143
Restart=always
RestartSec=5

[Install]
WantedBy=multi-user.target
```

Servisi başlatın:
```bash
sudo systemctl daemon-reload
sudo systemctl start admin_panel
sudo systemctl enable admin_panel
sudo systemctl status admin_panel
```

---

## 5. Frontend (React) Kurulumu

### 5.1. Build Alma
```bash
cd /var/www/admin_panel/admin_panel/admin_panel-react
npm install
npm run build
```
Bu işlem `dist` klasörünü oluşturacaktır.

---

## 6. Nginx Konfigürasyonu

```bash
sudo nano /etc/nginx/sites-available/suleymansecgin.com.tr
```

İçerik:
```nginx
server {
    listen 80;
    server_name www.suleymansecgin.com.tr suleymansecgin.com.tr;

    # Frontend (React) - /admin_panel altında
    location /admin_panel {
        # Build alınan dist klasörünün yolu
        alias /var/www/admin_panel/admin_panel/admin_panel-react/dist;
        index index.html index.htm;
        try_files $uri $uri/ /admin_panel/index.html;
    }

    # Ana sayfa yönlendirmesi (Opsiyonel)
    location / {
        return 301 /admin_panel;
    }

    # Backend API Proxy
    location /admin_panel/api/ {
        proxy_pass http://localhost:8080/api/;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_cache_bypass $http_upgrade;
        
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

Aktif etme:
```bash
sudo ln -s /etc/nginx/sites-available/suleymansecgin.com.tr /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

---

## 7. SSL (HTTPS)

```bash
sudo apt install certbot python3-certbot-nginx -y
sudo certbot --nginx -d www.suleymansecgin.com.tr -d suleymansecgin.com.tr
```

---

## Güncelleme Nasıl Yapılır? (CI/CD Olmadan Manuel)

Kodda değişiklik yaptığınızda sunucuda şu adımları izleyin:

1.  **Kodu Çek:**
    ```bash
    cd /var/www/admin_panel
    git pull origin main
    ```

2.  **Backend Güncellemesi (Eğer Java kodu değiştiyse):**
    ```bash
    cd /var/www/admin_panel/admin_panel
    ./mvnw clean package -DskipTests
    sudo systemctl restart admin_panel
    ```

3.  **Frontend Güncellemesi (Eğer React kodu değiştiyse):**
    ```bash
    cd /var/www/admin_panel/admin_panel/admin_panel-react
    npm install # Yeni paket eklendiyse
    npm run build
    ```
