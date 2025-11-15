# 🔧 Sunucuda Dizin Hatası - Çözüm

## ❌ HATA:
```bash
cd admin_panel/admin_panel-react
# Hata: No such file or directory
```

## ✅ ÇÖZÜM:

Şu anda `/opt/admin-panel/admin_panel/` dizinindesiniz. 

**Doğru komut:**
```bash
cd admin_panel-react
```

**YANLIŞ komut:**
```bash
cd admin_panel/admin_panel-react  # ❌ Bu yanlış!
```

---

## 📋 ADIM ADIM ÇÖZÜM:

### 1. Mevcut dizini kontrol edin:
```bash
pwd
```
Bu komut şunu göstermeli: `/opt/admin-panel/admin_panel`

### 2. Dizin yapısını kontrol edin:
```bash
ls -la
```

Bu komut şunları göstermelidir:
- `admin_panel-react/` (klasör)
- `src/` (klasör)
- `pom.xml` (dosya)
- `mvnw` (dosya)
- vb.

### 3. admin_panel-react dizinine gidin:
```bash
cd admin_panel-react
```

### 4. Dizinin doğru olduğunu kontrol edin:
```bash
pwd
```
Bu komut şunu göstermeli: `/opt/admin-panel/admin_panel/admin_panel-react`

### 5. package.json dosyasının var olduğunu kontrol edin:
```bash
ls -la package.json
```

### 6. npm install yapın:
```bash
npm install
```

### 7. Build yapın:
```bash
npm run build
```

---

## ⚠️ ÖNEMLİ NOT:

**Sunucuda `npm run dev` çalıştırmaya GEREK YOK!**

Sunucuda sadece:
1. `npm install` - Paketleri yükle
2. `npm run build` - Production build oluştur
3. Build çıktısını Spring Boot static klasörüne kopyala
4. Spring Boot uygulamasını yeniden başlat

**`npm run dev` sadece yerel geliştirme için kullanılır!**

---

## 🚀 TAM GÜNCELLEME SÜRECİ (Sunucuda):

```bash
# 1. Proje dizinine git
cd /opt/admin-panel/admin_panel

# 2. Git'ten güncellemeleri çek
git pull

# 3. React dizinine git
cd admin_panel-react

# 4. Paketleri yükle
npm install

# 5. Build yap
npm run build

# 6. Ana dizine geri dön
cd ..

# 7. Static klasörünü temizle
rm -rf src/main/resources/static/*

# 8. Build çıktısını kopyala
cp -r admin_panel-react/dist/* src/main/resources/static/

# 9. Spring Boot JAR oluştur
./mvnw clean package -DskipTests

# 10. JAR'ı kopyala
cp target/admin_panel-0.0.1-SNAPSHOT.jar /opt/admin-panel/

# 11. Servisi yeniden başlat
systemctl restart admin-panel

# 12. Durumu kontrol et
systemctl status admin-panel
```

