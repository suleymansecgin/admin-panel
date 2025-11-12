# 🚀 Sunucuya Yükleme Yol Haritası

Bu kılavuz, projenizi DigitalOcean droplet'ine ve suleymansecgin.com.tr domain'ine yüklemek için adım adım talimatlar içerir.

---

## 📋 ÖN HAZIRLIK

### Gereksinimler
- ✅ DigitalOcean droplet (Ubuntu 22.04 LTS)
- ✅ Domain: suleymansecgin.com.tr
- ✅ Bilgisayarınızda SSH erişimi
- ✅ GitHub hesabı ve projenin GitHub'da olması

### Bilmeniz Gerekenler
- DigitalOcean droplet IP adresiniz
- Root şifreniz veya SSH key'iniz
- GitHub repository URL'niz (örnek: `https://github.com/kullaniciadi/suleymansecgin-proje.git`)

### ⚠️ ÖNEMLİ: Projeyi GitHub'a Yükleyin

Eğer projeniz henüz GitHub'da değilse:

1. **GitHub'da repository oluşturun:**
   - https://github.com > "+" > "New repository"
   - Repository adı: `suleymansecgin-proje`
   - "Initialize with README" seçeneğini **İŞARETLEMEYİN**
   - "Create repository" tıklayın

2. **Projeyi GitHub'a push edin:**
   - Cursor'da Source Control panelini açın (Ctrl+Shift+G)
   - "Initialize Repository" butonuna tıklayın
   - Dosyaları stage'e ekleyin (+ butonu)
   - Commit mesajı: `Initial commit`
   - Commit edin
   - Terminal'de: `git remote add origin https://github.com/KULLANICI_ADINIZ/suleymansecgin-proje.git`
   - Terminal'de: `git push -u origin main`

---

## 🎯 ADIM 1: SUNUCUYA BAĞLANMA

### Windows'ta:

1. **PowerShell** veya **Command Prompt** açın
2. Şu komutu çalıştırın (IP adresinizi yazın):

```bash
ssh root@DROPLET_IP_ADRESI
```

**Örnek:**
```bash
ssh root@123.456.789.0
```

3. İlk bağlantıda "Are you sure?" sorusuna `yes` yazın
4. Şifre sorulursa, DigitalOcean'dan aldığınız root şifresini girin

**✅ Başarılı bağlantı:** Terminal'de şunu göreceksiniz:
```
root@your-droplet:~#
```

---

## 🎯 ADIM 2: SUNUCUYU HAZIRLAMA

SSH ile bağlı olduğunuz sunucu terminal penceresinde şu komutları sırayla çalıştırın:

### 2.1. Sistem Güncellemesi
```bash
apt update && apt upgrade -y
```

### 2.2. Java 17 Kurulumu
```bash
apt install -y openjdk-17-jdk
```

### 2.3. PostgreSQL Kurulumu
```bash
apt install -y postgresql postgresql-contrib
systemctl start postgresql
systemctl enable postgresql
```

### 2.4. Nginx Kurulumu
```bash
apt install -y nginx
systemctl start nginx
systemctl enable nginx
```

### 2.5. Node.js Kurulumu (React build için)
```bash
curl -fsSL https://deb.nodesource.com/setup_18.x | bash -
apt install -y nodejs
```

### 2.6. Git Kurulumu
```bash
apt install -y git
```

---

## 🎯 ADIM 3: VERİTABANI OLUŞTURMA

### 3.1. PostgreSQL'e Bağlanma
```bash
sudo -u postgres psql
```

### 3.2. Veritabanı ve Kullanıcı Oluşturma

PostgreSQL konsolunda şu komutları çalıştırın:

```sql
CREATE DATABASE admin_panel_db;
CREATE USER admin_user WITH PASSWORD 'ss207615';
GRANT ALL PRIVILEGES ON DATABASE admin_panel_db TO admin_user;
\q
```

**Not:** `\q` ile PostgreSQL'den çıkış yaparsınız.

---

## 🎯 ADIM 4: PROJEYİ GİT İLE SUNUCUYA YÜKLEME

**Sunucuda** (SSH ile bağlı olduğunuz terminal penceresinde):

### 4.1. Proje Klasörüne Gidin

```bash
cd /opt
```

### 4.2. GitHub'dan Projeyi Klonlayın

```bash
git clone https://github.com/KULLANICI_ADINIZ/suleymansecgin-proje.git
```

**Örnek:**
```bash
git clone https://github.com/suleymansecgin/suleymansecgin-proje.git
```

**Not:** `KULLANICI_ADINIZ` yerine GitHub kullanıcı adınızı, `suleymansecgin-proje` yerine repository adınızı yazın.

### 4.3. Proje Klasörüne Gidin

```bash
cd suleymansecgin-proje/admin_panel
```

**✅ Başarılı:** Proje dosyaları `/opt/suleymansecgin-proje/admin_panel/` klasörüne indirildi.

---

## 🎯 ADIM 5: PRODUCTION AYARLARINI YAPMA

**Sunucuda** (SSH ile bağlı terminal):

### 5.1. Proje Klasöründe Olduğunuzdan Emin Olun

```bash
cd /opt/suleymansecgin-proje/admin_panel
```

### 5.2. Production Ayarlarını Düzenleyin

```bash
nano src/main/resources/application-prod.properties
```

**Nano editör kullanımı:**
- Ok tuşları ile hareket edin
- Şu değerleri güncelleyin:
  - `DB_PASSWORD=ss207615` (PostgreSQL şifreniz)
  - `JWT_SECRET=` (Güçlü bir şifre yazın, en az 32 karakter)
- Kaydetmek: `Ctrl + O`, sonra `Enter`
- Çıkmak: `Ctrl + X`

---

## 🎯 ADIM 6: UYGULAMAYI BUILD ETME

**Sunucuda** (aynı terminal penceresinde):

### 6.1. React Frontend Build
```bash
cd admin_panel-react
npm install
npm run build
```

### 6.2. Build Çıktısını Kopyalama
```bash
rm -rf ../src/main/resources/static/*
cp -r dist/* ../src/main/resources/static/
cd ..
```

### 6.3. Spring Boot JAR Oluşturma
```bash
./mvnw clean package -DskipTests
```

**Not:** Bu işlem 5-10 dakika sürebilir.

### 6.4. JAR Dosyasını Kopyalama
```bash
mkdir -p /opt/admin-panel
cp target/admin_panel-0.0.1-SNAPSHOT.jar /opt/admin-panel/
```

---

## 🎯 ADIM 7: UYGULAMAYI SERVİS OLARAK ÇALIŞTIRMA

### 7.1. Systemd Service Dosyası Oluşturma

```bash
nano /etc/systemd/system/admin-panel.service
```

Şu içeriği yapıştırın:

```ini
[Unit]
Description=Admin Panel Spring Boot Application
After=network.target postgresql.service

[Service]
Type=simple
User=root
WorkingDirectory=/opt/admin-panel
ExecStart=/usr/bin/java -jar -Dspring.profiles.active=prod /opt/admin-panel/admin_panel-0.0.1-SNAPSHOT.jar
Restart=always
RestartSec=10
StandardOutput=journal
StandardError=journal
SyslogIdentifier=admin-panel

Environment="DB_USERNAME=admin_user"
Environment="DB_PASSWORD=ss207615"
Environment="JWT_SECRET=your_super_secret_jwt_key_change_this_min_32_characters"

[Install]
WantedBy=multi-user.target
```

**Önemli:** `JWT_SECRET` değerini güçlü bir şifre ile değiştirin!

Kaydetmek: `Ctrl + O`, `Enter`
Çıkmak: `Ctrl + X`

### 7.2. Servisi Başlatma

```bash
systemctl daemon-reload
systemctl enable admin-panel
systemctl start admin-panel
systemctl status admin-panel
```

**✅ Başarılı:** "active (running)" yazısını görmelisiniz.

---

## 🎯 ADIM 8: NGINX YAPILANDIRMASI

### 8.1. Nginx Config Dosyası Oluşturma

```bash
nano /etc/nginx/sites-available/admin-panel
```

Şu içeriği yapıştırın:

```nginx
server {
    listen 80;
    server_name suleymansecgin.com.tr www.suleymansecgin.com.tr;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }
}
```

Kaydetmek: `Ctrl + O`, `Enter`
Çıkmak: `Ctrl + X`

### 8.2. Nginx'i Aktif Etme

```bash
rm /etc/nginx/sites-enabled/default
ln -s /etc/nginx/sites-available/admin-panel /etc/nginx/sites-enabled/
nginx -t
systemctl restart nginx
```

---

## 🎯 ADIM 9: DNS AYARLARI

Domain sağlayıcınızın (suleymansecgin.com.tr'yi aldığınız yer) DNS ayarlarına gidin:

### A Kaydı Ekleme

1. **Host:** `@` veya boş bırakın
2. **Type:** `A`
3. **Value:** DigitalOcean droplet IP adresiniz
4. **TTL:** `3600`

### A Kaydı (www için)

1. **Host:** `www`
2. **Type:** `A`
3. **Value:** DigitalOcean droplet IP adresiniz
4. **TTL:** `3600`

**Not:** DNS değişiklikleri 24-48 saat içinde yayılır (genellikle birkaç saat).

---

## 🎯 ADIM 10: SSL SERTİFİKASI (HTTPS)

### 10.1. Certbot Kurulumu

```bash
apt install -y certbot python3-certbot-nginx
```

### 10.2. SSL Sertifikası Alma

```bash
certbot --nginx -d suleymansecgin.com.tr -d www.suleymansecgin.com.tr
```

Certbot size sorular soracak:
- Email adresinizi girin
- Şartları kabul edin
- HTTP'den HTTPS'e yönlendirme: `2` seçin (önerilen)

**✅ Başarılı:** SSL sertifikası otomatik olarak kurulacak!

---

## ✅ KONTROL VE TEST

### Uygulama Durumu

```bash
systemctl status admin-panel
systemctl status nginx
systemctl status postgresql
```

### Log Kontrolü

```bash
journalctl -u admin-panel -f
```

### Web Tarayıcıdan Test

- http://suleymansecgin.com.tr
- https://suleymansecgin.com.tr (SSL kurulumundan sonra)

---

## 🔒 GÜVENLİK AYARLARI

### Firewall Kurulumu

```bash
ufw allow 22/tcp    # SSH
ufw allow 80/tcp    # HTTP
ufw allow 443/tcp   # HTTPS
ufw enable
```

---

## 🔄 GÜNCELLEME İŞLEMİ

Uygulamayı güncellemek için:

### 1. Bilgisayarınızda Değişiklikleri Yapın ve GitHub'a Push Edin

**Cursor'da:**
1. Dosyaları değiştirin
2. Source Control panelinde değişiklikleri stage'e ekleyin (+ butonu)
3. Commit mesajı yazın ve commit edin
4. Terminal'de: `git push`

### 2. Sunucuda Güncellemeleri Çekin

**Sunucuda** (SSH ile bağlı terminal):

```bash
cd /opt/suleymansecgin-proje
git pull
cd admin_panel
```

### 3. Uygulamayı Yeniden Build Edin

```bash
cd admin_panel-react
npm install
npm run build
rm -rf ../src/main/resources/static/*
cp -r dist/* ../src/main/resources/static/
cd ..
./mvnw clean package -DskipTests
cp target/admin_panel-0.0.1-SNAPSHOT.jar /opt/admin-panel/
systemctl restart admin-panel
```

**✅ Güncelleme tamamlandı!** Uygulama otomatik olarak yeniden başlatılacak.

---

## 🐛 SORUN GİDERME

### Uygulama Başlamıyor

```bash
journalctl -u admin-panel -n 50
```

### Nginx 502 Bad Gateway

- Spring Boot uygulamasının çalıştığını kontrol edin
- Port 8080'in açık olduğunu kontrol edin: `netstat -tulpn | grep 8080`

### Veritabanı Bağlantı Hatası

- PostgreSQL'in çalıştığını kontrol edin: `systemctl status postgresql`
- Şifreleri kontrol edin

---

## 📞 ÖZET KOMUTLAR

Tüm adımları tamamladıktan sonra, hızlı kontrol için:

```bash
# Servis durumları
systemctl status admin-panel
systemctl status nginx
systemctl status postgresql

# Loglar
journalctl -u admin-panel -f

# Port kontrolü
netstat -tulpn | grep 8080
```

---

**Başarılar! 🎉**

Projeniz artık https://suleymansecgin.com.tr adresinde yayında olacak!

