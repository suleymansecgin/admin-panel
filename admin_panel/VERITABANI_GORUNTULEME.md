# 📊 Veritabanını Terminalde Görüntüleme Kılavuzu

Bu kılavuz, PostgreSQL veritabanındaki kullanıcı kayıtlarını ve diğer verileri terminal üzerinden görüntülemenize yardımcı olur.

---

## 🔌 PostgreSQL'e Bağlanma

### Yöntem 1: postgres kullanıcısı ile (Önerilen)

```bash
sudo -u postgres psql
```

### Yöntem 2: Direkt veritabanına bağlanma

**Development (Yerel):**
```bash
sudo -u postgres psql -d postgres
```

**Production (Sunucu):**
```bash
sudo -u postgres psql -d admin_panel_db
```

---

## 📋 Temel Komutlar

### 1. Veritabanlarını Listeleme

```sql
\l
```

veya

```sql
\list
```

### 2. Veritabanına Geçme

**Development:**
```sql
\c postgres
```

**Production:**
```sql
\c admin_panel_db
```

### 3. Schema'ya Geçme

```sql
SET search_path TO admin_panel;
```

veya

```sql
\dn
```

Tüm schema'ları görmek için.

### 4. Tabloları Listeleme

```sql
\dt
```

veya belirli bir schema'daki tabloları görmek için:

```sql
\dt admin_panel.*
```

### 5. Tablo Yapısını Görme

```sql
\d users
```

veya

```sql
\d+ users
```

(daha detaylı bilgi için)

---

## 👥 Kullanıcı Kayıtlarını Görüntüleme

### Tüm Kullanıcıları Listeleme

```sql
SELECT * FROM admin_panel.users;
```

### Sadece Önemli Bilgileri Görme

```sql
SELECT 
    id, 
    username, 
    email, 
    role, 
    created_at 
FROM admin_panel.users;
```

### Kullanıcı Sayısını Öğrenme

```sql
SELECT COUNT(*) FROM admin_panel.users;
```

### Belirli Bir Kullanıcıyı Bulma

**Kullanıcı adına göre:**
```sql
SELECT * FROM admin_panel.users WHERE username = 'kullanici_adi';
```

**Email'e göre:**
```sql
SELECT * FROM admin_panel.users WHERE email = 'email@example.com';
```

**ID'ye göre:**
```sql
SELECT * FROM admin_panel.users WHERE id = 1;
```

### Son Kayıt Olan Kullanıcıları Görme

```sql
SELECT 
    id, 
    username, 
    email, 
    role, 
    created_at 
FROM admin_panel.users 
ORDER BY created_at DESC 
LIMIT 10;
```

### Kullanıcıları Role'e Göre Gruplama

```sql
SELECT 
    role, 
    COUNT(*) as kullanici_sayisi 
FROM admin_panel.users 
GROUP BY role;
```

---

## 🔍 Gelişmiş Sorgular

### Bugün Kayıt Olan Kullanıcılar

```sql
SELECT 
    id, 
    username, 
    email, 
    created_at 
FROM admin_panel.users 
WHERE DATE(created_at) = CURRENT_DATE;
```

### Son 7 Gün İçinde Kayıt Olan Kullanıcılar

```sql
SELECT 
    id, 
    username, 
    email, 
    created_at 
FROM admin_panel.users 
WHERE created_at >= CURRENT_DATE - INTERVAL '7 days'
ORDER BY created_at DESC;
```

### Şifre Uzunluğunu Kontrol Etme (Hash uzunluğu)

```sql
SELECT 
    username, 
    email, 
    LENGTH(password) as sifre_hash_uzunlugu 
FROM admin_panel.users;
```

---

## 🗑️ Veritabanı İşlemleri (Dikkatli Kullanın!)

### Kullanıcı Silme

**⚠️ DİKKAT: Bu işlem geri alınamaz!**

**Belirli bir kullanıcıyı silme:**
```sql
DELETE FROM admin_panel.users WHERE id = 1;
```

**Kullanıcı adına göre silme:**
```sql
DELETE FROM admin_panel.users WHERE username = 'kullanici_adi';
```

**Email'e göre silme:**
```sql
DELETE FROM admin_panel.users WHERE email = 'email@example.com';
```

### Kullanıcı Güncelleme

**Role güncelleme:**
```sql
UPDATE admin_panel.users 
SET role = 'ADMIN' 
WHERE username = 'kullanici_adi';
```

**Email güncelleme:**
```sql
UPDATE admin_panel.users 
SET email = 'yeni_email@example.com' 
WHERE username = 'kullanici_adi';
```

---

## 📊 Tablo İstatistikleri

### Toplam Kayıt Sayısı

```sql
SELECT COUNT(*) as toplam_kullanici FROM admin_panel.users;
```

### Role Dağılımı

```sql
SELECT 
    role, 
    COUNT(*) as sayi,
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM admin_panel.users), 2) as yuzde
FROM admin_panel.users 
GROUP BY role;
```

### Günlük Kayıt İstatistikleri

```sql
SELECT 
    DATE(created_at) as tarih,
    COUNT(*) as kayit_sayisi
FROM admin_panel.users
GROUP BY DATE(created_at)
ORDER BY tarih DESC;
```

---

## 🔐 Güvenlik Notları

1. **Şifreler Hash'lenmiş:** `password` sütununda şifreler BCrypt ile hash'lenmiş olarak saklanır. Düz metin şifre görüntülenemez.

2. **Sadece Okuma:** Sorguları çalıştırırken dikkatli olun. DELETE ve UPDATE komutlarını kullanmadan önce mutlaka yedek alın.

3. **Production'da Dikkat:** Production ortamında veritabanı işlemleri yaparken çok dikkatli olun.

---

## 🚪 PostgreSQL'den Çıkış

```sql
\q
```

veya

```sql
exit
```

---

## 📝 Hızlı Referans

### Tam Akış (Baştan Sona)

```bash
# 1. PostgreSQL'e bağlan
sudo -u postgres psql

# 2. Veritabanına geç (Production için)
\c admin_panel_db

# 3. Schema'ya geç
SET search_path TO admin_panel;

# 4. Kullanıcıları listele
SELECT id, username, email, role, created_at FROM users;

# 5. Çıkış yap
\q
```

### Tek Satırda Sorgu Çalıştırma

**Kullanıcıları listele:**
```bash
sudo -u postgres psql -d admin_panel_db -c "SELECT id, username, email, role FROM admin_panel.users;"
```

**Kullanıcı sayısını öğren:**
```bash
sudo -u postgres psql -d admin_panel_db -c "SELECT COUNT(*) FROM admin_panel.users;"
```

---

## 🐛 Sorun Giderme

### "database does not exist" Hatası

Veritabanı adını kontrol edin:
```sql
\l
```

### "schema does not exist" Hatası

Schema'nın var olduğunu kontrol edin:
```sql
\dn
```

Eğer schema yoksa, uygulama ilk çalıştığında otomatik oluşturulur. Uygulamayı bir kez çalıştırın.

### "relation does not exist" Hatası

Tablo adını kontrol edin:
```sql
\dt admin_panel.*
```

### Bağlantı Hatası

PostgreSQL'in çalıştığını kontrol edin:
```bash
systemctl status postgresql
```

---

## 💡 İpuçları

1. **Tablo Tamamlama:** PostgreSQL'de tablo adlarını yazarken `Tab` tuşuna basarak otomatik tamamlayabilirsiniz.

2. **Komut Geçmişi:** Yukarı ok tuşu ile önceki komutları görebilirsiniz.

3. **Çıktıyı Dosyaya Kaydetme:**
   ```sql
   \o kullanicilar.txt
   SELECT * FROM admin_panel.users;
   \o
   ```

4. **Daha Okunabilir Çıktı:**
   ```sql
   \x  -- Genişletilmiş görünüm (aç/kapat)
   ```

---

**Başarılar! 🎉**

