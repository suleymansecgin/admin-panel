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

**✅ PostgreSQL Durumunu Kontrol Edin:**
```bash
systemctl status postgresql
```

**⚠️ ÖNEMLİ:** `postgresql.service` bir meta-servistir ve `active (exited)` durumu **NORMALDİR**. Bu servis gerçek PostgreSQL sunucusunu başlatmaz, sadece yapılandırma dosyalarını yükler.

**Gerçek PostgreSQL servisini başlatmanız gerekiyor:**

```bash
# PostgreSQL versiyonunu öğrenin
psql --version
```

Genellikle şu servislerden biri çalışıyor olmalıdır:
- `postgresql@16-main.service` (PostgreSQL 16 için)
- `postgresql@15-main.service` (PostgreSQL 15 için)
- `postgresql@14-main.service` (PostgreSQL 14 için)

**PostgreSQL cluster servisini başlatın:**
```bash
# Hangi PostgreSQL servislerinin mevcut olduğunu görün
systemctl list-units --all | grep postgresql

# PostgreSQL 16 için (en yaygın)
systemctl start postgresql@16-main
systemctl enable postgresql@16-main
systemctl status postgresql@16-main
```

**✅ Başarılı:** Artık `active (running)` görmelisiniz.

**PostgreSQL'in gerçekten çalıştığını test edin:**
```bash
# PostgreSQL process'lerini kontrol edin
ps aux | grep postgres

# PostgreSQL'e bağlanmayı test edin
sudo -u postgres psql -c "SELECT version();"
```

Bu komutlar PostgreSQL'in çalıştığını doğrulamalıdır.

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

**⚠️ ÖNEMLİ:** Eğer `ERROR: database "admin_panel_db" already exists` veya `ERROR: role "admin_user" already exists` hatası alırsanız, bu **NORMALDİR**. Bu, veritabanı ve kullanıcının zaten mevcut olduğu anlamına gelir. Hata mesajlarını görmezden gelip devam edebilirsiniz.

**GRANT komutu başarılı olduysa (GRANT yazısını gördüyseniz), işlem tamamlanmıştır.** Devam edebilirsiniz.

**Not:** `\q` ile PostgreSQL'den çıkış yaparsınız.

### 3.3. Schema Oluşturma

**⚠️ ÖNEMLİ:** Spring Boot uygulaması `admin_panel` schema'sını kullanıyor. Bu schema'yı oluşturmanız gerekiyor.

PostgreSQL'e tekrar bağlanın:
```bash
sudo -u postgres psql -d admin_panel_db
```

PostgreSQL konsolunda şu komutları çalıştırın:

```sql
CREATE SCHEMA admin_panel;
GRANT ALL PRIVILEGES ON SCHEMA admin_panel TO admin_user;
ALTER USER admin_user SET search_path TO admin_panel, public;
\q
```

**Not:** Bu schema, Spring Boot uygulamasının tablolarını oluşturması için gereklidir.

---

## 🎯 ADIM 4: PROJEYİ GİT İLE SUNUCUYA YÜKLEME

**Sunucuda** (SSH ile bağlı olduğunuz terminal penceresinde):

### 4.1. Proje Klasörüne Gidin

```bash
cd /opt
```

### 4.2. GitHub'dan Projeyi Klonlayın

**Önce mevcut klasörü kontrol edin:**
```bash
ls -la
```

**Eğer repository adınızla bir klasör zaten varsa (örnek: `admin-panel` veya `suleymansecgin-proje`):**

**Seçenek 1: Mevcut klasörü kullanmak (önerilen - eğer zaten git repo ise):**
```bash
# Mevcut klasöre gidin
cd admin-panel  # veya suleymansecgin-proje (repository adınıza göre)

# Git repo olup olmadığını kontrol edin
git status
```

**✅ Eğer `git status` çalışıyorsa (git repo olduğunu gösteriyorsa):**

```bash
# Güncellemeleri çekin
git pull
```

**⚠️ ÖNEMLİ:** `git status` komutunda şunları görebilirsiniz:
- `modified: admin_panel/mvnw` - Bu normal, build sırasında değişmiş olabilir
- `Untracked files: admin_panel-0.0.1-SNAPSHOT.jar` - Bu build çıktısı, normal
- `Untracked files: admin_panel/src/main/resources/static/` - Bu React build çıktısı, normal

**Bu dosyalar build çıktıları olduğu için git'te takip edilmez. Bu durum NORMALDİR.** Devam edebilirsiniz.

**Eğer git repo değilse (git status hata verirse), Seçenek 2'ye geçin.**

**Seçenek 2: Mevcut klasörü silip yeniden klonlamak:**
```bash
# Önce klasörün içeriğini kontrol edin (önemli dosyalar varsa yedekleyin)
ls -la admin-panel  # veya suleymansecgin-proje

# Klasörü silin
rm -rf admin-panel  # veya suleymansecgin-proje (repository adınıza göre)

# Şimdi yeniden klonlayın
git clone https://github.com/KULLANICI_ADINIZ/REPOSITORY_ADI.git
```

**Eğer klasör yoksa, normal şekilde klonlayın:**
```bash
git clone https://github.com/KULLANICI_ADINIZ/suleymansecgin-proje.git
```

**Örnek:**
```bash
git clone https://github.com/suleymansecgin/admin-panel.git
```

**⚠️ ÖNEMLİ:** Eğer `fatal: destination path 'admin-panel' already exists and is not an empty directory` hatası alırsanız:
- Yukarıdaki **Seçenek 1** veya **Seçenek 2**'den birini kullanın
- Seçenek 1 daha güvenlidir çünkü mevcut dosyaları korur
- Seçenek 2 klasörü tamamen siler ve yeniden klonlar

**Not:** `KULLANICI_ADINIZ` yerine GitHub kullanıcı adınızı, `REPOSITORY_ADI` yerine repository adınızı yazın.

### 4.3. Proje Klasörüne Gidin

**⚠️ ÖNEMLİ:** Git clone işlemi, repository adınızla bir klasör oluşturur. Önce hangi klasörün oluştuğunu kontrol edin:

```bash
ls -la
```

Bu komut klasörleri listeleyecektir. Repository adınızla oluşan klasörü göreceksiniz.

**Sonra o klasöre ve admin_panel klasörüne gidin:**

Eğer repository adınız `admin-panel` ise:
```bash
cd admin-panel/admin_panel
```

Eğer repository adınız `suleymansecgin-proje` ise:
```bash
cd suleymansecgin-proje/admin_panel
```

**✅ Başarılı:** Artık `/opt/REPOSITORY_ADI/admin_panel/` klasöründesiniz.

---

## 🎯 ADIM 5: PRODUCTION AYARLARINI YAPMA

**Sunucuda** (SSH ile bağlı terminal):

### 5.1. Proje Klasöründe Olduğunuzdan Emin Olun

Eğer repository adınız `admin-panel` ise:
```bash
cd /opt/admin-panel/admin_panel
```

Eğer repository adınız `suleymansecgin-proje` ise:
```bash
cd /opt/suleymansecgin-proje/admin_panel
```

**Hangi klasörde olduğunuzu kontrol etmek için:**
```bash
pwd
```

Bu komut mevcut klasör yolunu gösterecektir.

### 5.2. Production Ayarlarını Düzenleyin

**Önce dosyanın var olup olmadığını kontrol edin:**
```bash
ls -la src/main/resources/application-prod.properties
```

**Eğer dosya yoksa, oluşturun:**
```bash
nano src/main/resources/application-prod.properties
```

**Dosyaya şu içeriği yapıştırın:**

```properties
spring.application.name=admin_panel

# PostgreSQL Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/admin_panel_db
spring.jpa.properties.hibernate.default_schema=admin_panel
spring.datasource.username=${DB_USERNAME:admin_user}
spring.datasource.password=${DB_PASSWORD:ss207615}

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=false

# Server Configuration
server.port=8080
server.address=0.0.0.0

# JWT Configuration
jwt.secret=${JWT_SECRET:your_super_secret_jwt_key_change_this_in_production_min_256_bits}
jwt.expiration=86400000

# Logging
logging.level.root=INFO
logging.level.com.suleymansecgin=DEBUG
```

**⚠️ ÖNEMLİ:** `spring.profiles.active=prod` satırını **KALDIRMAYIN** veya eklemeyin! Bu satır profile-specific dosyalarda olamaz. Profile zaten service dosyasında `-Dspring.profiles.active=prod` ile belirtilmiş.

**Nano editör kullanımı:**
1. Dosya boşsa, yukarıdaki içeriği kopyalayıp yapıştırın (sağ tık > Paste veya Shift+Insert)
2. Ok tuşları ile hareket edin
3. Şu değerleri güncelleyin:
   - `DB_PASSWORD=ss207615` (PostgreSQL şifreniz - zaten doğru görünüyor)
   - `JWT_SECRET=` (Güçlü bir şifre yazın, en az 32 karakter)
     - Örnek: `MySuperSecretJWTKeyForProduction2024!@#$%^&*()`
4. Kaydetmek: `Ctrl + O`, sonra `Enter`
5. Çıkmak: `Ctrl + X`

**Eğer dosya zaten varsa ve içeriği görmek istiyorsanız:**
```bash
cat src/main/resources/application-prod.properties
```

Bu komut dosyanın içeriğini terminalde gösterecektir.

---

## 🎯 ADIM 6: UYGULAMAYI BUILD ETME

**Sunucuda** (aynı terminal penceresinde):

**⚠️ ÖNEMLİ:** Önce doğru klasörde olduğunuzdan emin olun:
```bash
pwd
```

Bu komut şunu göstermeli: `/opt/admin-panel/admin_panel` veya `/opt/suleymansecgin-proje/admin_panel`

### 6.1. React Frontend Build

```bash
cd admin_panel-react
```

**Klasörün var olduğunu kontrol edin:**
```bash
ls -la
```

Bu komut `package.json` dosyasını göstermelidir.

**npm install:**
```bash
npm install
```

Bu işlem birkaç dakika sürebilir. Başarılı olduğunda `node_modules` klasörü oluşacaktır.

**React build:**
```bash
npm run build
```

**✅ Build başarılı kontrolü:**
```bash
ls -la dist
```

Bu komut `dist` klasörünün içeriğini göstermelidir. Eğer `dist` klasörü yoksa, build başarısız olmuştur. Hata mesajlarını kontrol edin.

### 6.2. Build Çıktısını Kopyalama

**Önce admin_panel klasörüne geri dönün:**
```bash
cd ..
```

**Hangi klasörde olduğunuzu kontrol edin:**
```bash
pwd
```

Bu komut şunu göstermeli: `/opt/admin-panel/admin_panel` veya `/opt/suleymansecgin-proje/admin_panel`

**static klasörünü oluşturun (eğer yoksa):**
```bash
mkdir -p src/main/resources/static
```

**static klasörünün var olduğunu kontrol edin:**
```bash
ls -la src/main/resources/
```

Bu komut `static` klasörünü göstermelidir.

**Mevcut static dosyalarını temizleyin:**
```bash
rm -rf src/main/resources/static/*
```

**dist klasörünün var olduğunu kontrol edin:**
```bash
ls -la admin_panel-react/dist
```

Bu komut `dist` klasörünün içeriğini göstermelidir. Eğer hata alırsanız, React build başarısız olmuştur. 6.1 adımını tekrar yapın.

**Build çıktısını kopyalayın:**
```bash
cp -r admin_panel-react/dist/* src/main/resources/static/
```

**✅ Kopyalama başarılı kontrolü:**
```bash
ls -la src/main/resources/static/
```

Bu komut kopyalanan dosyaları göstermelidir (index.html, assets/, vb.).

### 6.3. Spring Boot JAR Oluşturma

**Doğru klasörde olduğunuzdan emin olun:**
```bash
pwd
```

Bu komut şunu göstermeli: `/opt/admin-panel/admin_panel` veya `/opt/suleymansecgin-proje/admin_panel`

**mvnw dosyasının var olduğunu kontrol edin:**
```bash
ls -la mvnw
```

Bu komut `mvnw` dosyasını göstermelidir. Eğer yoksa, proje klasöründe değilsinizdir.

**⚠️ ÖNEMLİ: mvnw dosyasına çalıştırma izni verin:**

`mvnw` dosyası çalıştırılabilir olmalıdır. İzinleri kontrol edin ve gerekirse verin:

```bash
chmod +x mvnw
```

**İzinleri kontrol edin:**
```bash
ls -la mvnw
```

Bu komut şunu göstermelidir: `-rwxr-xr-x` (x harfleri çalıştırma iznini gösterir)

**JAR dosyasını oluşturun:**
```bash
./mvnw clean package -DskipTests
```

**Alternatif (eğer hala çalışmazsa):**
```bash
bash mvnw clean package -DskipTests
```

veya

```bash
sh mvnw clean package -DskipTests
```

**Not:** Bu işlem 5-10 dakika sürebilir. İlk çalıştırmada daha uzun sürebilir (Maven bağımlılıklarını indirir).

**✅ Build başarılı kontrolü:**
```bash
ls -la target/admin_panel-0.0.1-SNAPSHOT.jar
```

Bu komut JAR dosyasını göstermelidir.

### 6.4. JAR Dosyasını Kopyalama
```bash
mkdir -p /opt/admin-panel
cp target/admin_panel-0.0.1-SNAPSHOT.jar /opt/admin-panel/
```

---

## 🎯 ADIM 7: UYGULAMAYI SERVİS OLARAK ÇALIŞTIRMA

### 7.1. Systemd Service Dosyası Oluşturma

**Önce JAR dosyasının var olduğunu kontrol edin:**
```bash
ls -la /opt/admin-panel/admin_panel-0.0.1-SNAPSHOT.jar
```

Bu komut JAR dosyasını göstermelidir. Eğer yoksa, 6.4 adımını tekrar yapın.

**Java'nın doğru yolda olduğunu kontrol edin:**
```bash
which java
```

Bu komut şunu göstermelidir: `/usr/bin/java` veya `/usr/lib/jvm/...`

**Service dosyasını oluşturun:**
```bash
nano /etc/systemd/system/admin-panel.service
```

**⚠️ ÖNEMLİ:** Dosyanın ilk satırı `[Unit]` ile başlamalıdır. Boş satır veya başka bir şey olmamalı!

Şu içeriği **tam olarak** yapıştırın (dosyanın başından itibaren):

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

**Önemli:** 
- `JWT_SECRET` değerini güçlü bir şifre ile değiştirin (en az 32 karakter)
- Dosyanın başında boş satır olmamalı
- Her bölüm (`[Unit]`, `[Service]`, `[Install]`) doğru yerde olmalı

**Nano editör kullanımı:**
1. Dosyayı açın
2. Eğer içinde bir şey varsa, tümünü silin (`Ctrl + K` ile satır silme)
3. Yukarıdaki içeriği kopyalayıp yapıştırın (sağ tık > Paste veya Shift+Insert)
4. `JWT_SECRET` değerini güncelleyin
5. Kaydetmek: `Ctrl + O`, sonra `Enter`
6. Çıkmak: `Ctrl + X`

**Service dosyasının syntax'ını kontrol edin:**
```bash
systemd-analyze verify /etc/systemd/system/admin-panel.service
```

Bu komut hata göstermemelidir. Eğer hata gösterirse, service dosyasını tekrar düzenleyin.

### 7.2. Servisi Başlatma

**Systemd'yi yeniden yükleyin:**
```bash
systemctl daemon-reload
```

**Servisi otomatik başlatma için etkinleştirin:**
```bash
systemctl enable admin-panel
```

**Servisi başlatın:**
```bash
systemctl start admin-panel
```

**Servis durumunu kontrol edin:**
```bash
systemctl status admin-panel
```

**✅ Başarılı:** "active (running)" yazısını görmelisiniz.

**❌ Eğer "failed" veya "activating (auto-restart)" görüyorsanız:**

**1. Logları kontrol edin:**
```bash
journalctl -u admin-panel -n 50 --no-pager
```

Bu komut son 50 log satırını gösterecektir. Hata mesajlarını burada görebilirsiniz.

**2. JAR dosyasının var olduğunu tekrar kontrol edin:**
```bash
ls -la /opt/admin-panel/admin_panel-0.0.1-SNAPSHOT.jar
```

**3. Java'nın çalıştığını kontrol edin:**
```bash
java -version
```

**4. JAR dosyasını manuel olarak çalıştırmayı deneyin:**
```bash
cd /opt/admin-panel
java -jar -Dspring.profiles.active=prod admin_panel-0.0.1-SNAPSHOT.jar
```

Bu komut hata mesajlarını gösterecektir. Hataları not edin ve düzeltin.

**5. Yaygın sorunlar:**

- **Veritabanı bağlantı hatası:** PostgreSQL'in çalıştığını kontrol edin: `systemctl status postgresql`
- **Port kullanımda:** Port 8080'in kullanımda olup olmadığını kontrol edin: `ss -tulpn | grep 8080` veya `netstat -tulpn | grep 8080` (net-tools kuruluysa)
- **JAR dosyası bozuk:** JAR dosyasını yeniden build edin (6.3 adımı)
- **Yetersiz izinler:** JAR dosyasının okunabilir olduğunu kontrol edin: `chmod 644 /opt/admin-panel/admin_panel-0.0.1-SNAPSHOT.jar`

**6. Sorunları düzelttikten sonra:**
```bash
systemctl restart admin-panel
systemctl status admin-panel
```

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
# Default site'ı kaldırın (yoksa hata vermez, normal)
rm /etc/nginx/sites-enabled/default 2>/dev/null || true

# Mevcut symbolic link'i kontrol edin ve varsa kaldırın
if [ -L /etc/nginx/sites-enabled/admin-panel ]; then
    rm /etc/nginx/sites-enabled/admin-panel
fi

# Yeni symbolic link oluşturun
ln -s /etc/nginx/sites-available/admin-panel /etc/nginx/sites-enabled/

# Nginx yapılandırmasını test edin
nginx -t

# Nginx'i yeniden başlatın
systemctl restart nginx
```

**⚠️ ÖNEMLİ:** Eğer `ln: failed to create symbolic link... File exists` hatası alırsanız:

```bash
# Mevcut link'i kaldırın
rm /etc/nginx/sites-enabled/admin-panel

# Yeniden oluşturun
ln -s /etc/nginx/sites-available/admin-panel /etc/nginx/sites-enabled/

# Test edin
nginx -t

# Yeniden başlatın
systemctl restart nginx
```

**Eğer `systemctl restart nginx` başarısız olursa:**

```bash
# Nginx durumunu kontrol edin
systemctl status nginx

# Hata loglarını görün
journalctl -xeu nginx.service -n 50 --no-pager

# Nginx yapılandırma dosyasını kontrol edin
cat /etc/nginx/sites-available/admin-panel

# Yapılandırma dosyasının var olduğundan emin olun
ls -la /etc/nginx/sites-available/admin-panel
```

**✅ Başarılı:** `systemctl status nginx` komutu `active (running)` göstermelidir.

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

**📁 İşlemlerin Yapılacağı Dosya:** `/etc/nginx/sites-available/admin-panel`

Certbot, SSL sertifikası kurulumu sırasında bu Nginx yapılandırma dosyasını **otomatik olarak güncelleyecektir**. Manuel düzenleme gerekmez.

### 10.1. Certbot Kurulumu

```bash
apt install -y certbot python3-certbot-nginx
```

### 10.2. SSL Sertifikası Alma

```bash
certbot --nginx -d suleymansecgin.com.tr -d www.suleymansecgin.com.tr
```

Certbot size sorular soracak:
- **Email adresinizi girin** (sertifika yenileme bildirimleri için)
- **Şartları kabul edin** (`A` yazın)
- **HTTP'den HTTPS'e yönlendirme:** `2` seçin (önerilen)

**✅ Başarılı:** SSL sertifikası otomatik olarak kurulacak ve Nginx config dosyası güncellenecek!

### 10.3. Nginx Config Dosyasını Kontrol Etme (Opsiyonel)

Certbot işlemi tamamlandıktan sonra, Nginx config dosyasının güncellendiğini görmek için:

```bash
cat /etc/nginx/sites-available/admin-panel
```

Bu komut dosyanın içeriğini gösterecektir. Certbot otomatik olarak:
- SSL sertifika yollarını ekler
- Port 443 (HTTPS) yapılandırmasını ekler
- HTTP'den HTTPS'e yönlendirme ekler (eğer seçtiyseniz)

**Not:** Certbot dosyayı otomatik güncellediği için manuel düzenleme yapmanıza gerek yoktur.

---

## ✅ KONTROL VE TEST

### Uygulama Durumu

```bash
systemctl status admin-panel
systemctl status nginx
systemctl status postgresql
```

**✅ Tüm servisler "active (running)" olmalıdır.**

**⚠️ PostgreSQL Durumu Kontrolü:**

Eğer PostgreSQL `active (exited)` görünüyorsa, bu normal olabilir. `postgresql.service` bir wrapper servistir. Gerçek PostgreSQL cluster servisini kontrol edin:

```bash
# PostgreSQL cluster servislerini listeleyin
systemctl list-units | grep postgresql
```

**Gerçek PostgreSQL servisini kontrol edin:**

```bash
# PostgreSQL versiyonunu öğrenin
psql --version
```

Genellikle şu servislerden biri çalışıyor olmalıdır:
- `postgresql@16-main.service` (PostgreSQL 16 için)
- `postgresql@15-main.service` (PostgreSQL 15 için)
- `postgresql@14-main.service` (PostgreSQL 14 için)

**PostgreSQL'in gerçekten çalıştığını kontrol edin:**

```bash
# PostgreSQL process'lerini kontrol edin
ps aux | grep postgres
```

Bu komut PostgreSQL process'lerini göstermelidir. Eğer process'ler çalışıyorsa, PostgreSQL çalışıyor demektir.

**PostgreSQL'e bağlanmayı test edin:**

```bash
sudo -u postgres psql -c "SELECT version();"
```

Bu komut PostgreSQL versiyonunu göstermelidir. Eğer hata alırsanız, PostgreSQL çalışmıyor demektir.

**Eğer PostgreSQL gerçekten çalışmıyorsa:**

```bash
# PostgreSQL cluster servisini bulun ve başlatın
systemctl list-units | grep postgresql
# Çıktıda görünen servis adını kullanın (örnek: postgresql@16-main)
systemctl start postgresql@16-main
systemctl enable postgresql@16-main
systemctl status postgresql@16-main
```

### Web Sitesinde İçerik Görünmüyorsa - Sorun Giderme

Eğer servisler çalışıyor ama web sitesinde hiçbir şey görünmüyorsa:

#### 1. Port 8080'in Açık Olduğunu Kontrol Edin

**Yöntem A: ss komutu (önerilen - modern Linux'ta varsayılan olarak gelir)**
```bash
ss -tulpn | grep 8080
```

**Yöntem B: netstat komutu (eğer kurulu değilse)**
```bash
# Önce netstat'ı kurun
apt install -y net-tools
# Sonra kullanın
netstat -tulpn | grep 8080
```

Bu komutlar şunu göstermelidir: `tcp LISTEN 0 128 0.0.0.0:8080 ... java`

**❌ Eğer "Port 8080 was already in use" hatası alıyorsanız:**

Port 8080'i kullanan process'i bulun ve durdurun:

**Yöntem A: ss komutu ile**
```bash
# Port 8080'i kullanan process'i bulun
ss -tulpn | grep 8080
# Çıktıda görünen PID'yi not edin (örnek: pid=12345)
# Process'i durdurun
kill -9 PID_NUMARASI
```

**Yöntem B: fuser komutu ile (en kolay)**
```bash
fuser -k 8080/tcp
```

**Yöntem C: lsof komutu ile**
```bash
# Önce lsof'u kurun (eğer yoksa)
apt install -y lsof
# Port 8080'i kullanan process'i bulun
lsof -i :8080
# Çıktıda görünen PID'yi not edin
# Process'i durdurun
kill -9 PID_NUMARASI
```

**Örnek:**
```bash
# Eğer PID 12345 ise
kill -9 12345
```

Sonra servisi yeniden başlatın:
```bash
systemctl restart admin-panel
```

Eğer hiçbir şey göstermiyorsa, Spring Boot uygulaması port 8080'de çalışmıyor demektir.

#### 2. Spring Boot Uygulamasının Loglarını Kontrol Edin

```bash
journalctl -u admin-panel -n 100 --no-pager
```

Hata mesajlarını kontrol edin. Özellikle:
- Veritabanı bağlantı hataları
- **Schema hatası:** "schema 'admin_panel' does not exist" - 3.3 adımını yapın
- Port kullanımda hatası - 1. adımı yapın
- Uygulama başlatma hataları

#### 3. Nginx Yapılandırmasını Kontrol Edin

```bash
cat /etc/nginx/sites-available/admin-panel
```

Dosyanın içeriği şöyle olmalı:
```nginx
server {
    listen 80;
    server_name suleymansecgin.com.tr www.suleymansecgin.com.tr;

    location / {
        proxy_pass http://localhost:8080;
        ...
    }
}
```

#### 4. Nginx'in Doğru Çalıştığını Kontrol Edin

```bash
nginx -t
```

Bu komut hata göstermemelidir.

```bash
systemctl restart nginx
```

#### 5. Nginx Loglarını Kontrol Edin

```bash
tail -f /var/log/nginx/error.log
```

Başka bir terminal penceresinde web sitesine gidin. Hata mesajları burada görünecektir.

#### 6. Spring Boot Uygulamasını Manuel Test Edin

```bash
curl http://localhost:8080
```

Bu komut Spring Boot uygulamasından yanıt döndürmelidir. Eğer hata alırsanız, uygulama çalışmıyor demektir.

#### 7. Nginx Reverse Proxy'yi Test Edin

```bash
curl http://localhost
```

Bu komut Nginx üzerinden Spring Boot uygulamasına erişmeyi test eder.

#### 8. DNS Ayarlarını Kontrol Edin

```bash
nslookup suleymansecgin.com.tr
```

Bu komut domain'in doğru IP adresine işaret ettiğini göstermelidir.

#### 9. Firewall Ayarlarını Kontrol Edin

```bash
ufw status
```

Port 80 ve 443'ün açık olduğundan emin olun:
```bash
ufw allow 80/tcp
ufw allow 443/tcp
```

#### 10. React Frontend Build Edilmiş mi Kontrol Edin

```bash
ls -la /opt/admin-panel/admin_panel/src/main/resources/static/
```

Bu klasörde `index.html` ve `assets/` klasörü olmalıdır. Eğer yoksa, 6.1 ve 6.2 adımlarını tekrar yapın.

### Log Kontrolü

**Canlı log takibi:**
```bash
journalctl -u admin-panel -f
```

**Son 100 log satırı:**
```bash
journalctl -u admin-panel -n 100 --no-pager
```

### Web Tarayıcıdan Test

- http://suleymansecgin.com.tr
- https://suleymansecgin.com.tr (SSL kurulumundan sonra)
- http://DROPLET_IP_ADRESI (doğrudan IP ile test)

**Not:** Eğer IP adresi ile çalışıyorsa ama domain ile çalışmıyorsa, DNS ayarları henüz yayılmamış olabilir.

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

Eğer repository adınız `admin-panel` ise:
```bash
cd /opt/admin-panel
git pull
cd admin_panel
```

Eğer repository adınız `suleymansecgin-proje` ise:
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
- Port 8080'in açık olduğunu kontrol edin: `ss -tulpn | grep 8080`

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
ss -tulpn | grep 8080
```

---

**Başarılar! 🎉**

Projeniz artık https://suleymansecgin.com.tr adresinde yayında olacak!

