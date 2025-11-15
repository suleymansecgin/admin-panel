# 🚀 Hızlı Güncelleme Kılavuzu

Artık her değişiklikte tüm adımları manuel yapmanıza gerek yok! Tek komutla her şeyi yapabilirsiniz.

---

## 📋 İki Yöntem

### Yöntem 1: Sadece Build ve Deploy (Git pull yapmadan)

Eğer değişiklikleri zaten sunucuda yaptıysanız veya manuel olarak git pull yaptıysanız:

```bash
cd /opt/admin-panel/admin_panel
./deploy.sh
```

Bu script şunları yapar:
1. ✅ React frontend'i build eder
2. ✅ Build çıktısını Spring Boot static klasörüne kopyalar
3. ✅ Spring Boot JAR dosyasını oluşturur
4. ✅ JAR'ı `/opt/admin-panel/` klasörüne kopyalar
5. ✅ Servisi yeniden başlatır
6. ✅ Servis durumunu kontrol eder

---

### Yöntem 2: Git Pull + Build + Deploy (Hepsi birlikte)

Eğer değişiklikleri yerel bilgisayarınızda yaptıysanız ve Git'e push ettiyseniz:

```bash
cd /opt/admin-panel/admin_panel
./update.sh
```

Bu script şunları yapar:
1. ✅ Git'ten güncellemeleri çeker (`git pull`)
2. ✅ `deploy.sh` script'ini çalıştırır (yukarıdaki tüm adımlar)

---

## 🔧 Script'leri Çalıştırılabilir Yapma

**İlk kez kullanıyorsanız**, script'lere çalıştırma izni vermeniz gerekiyor:

```bash
cd /opt/admin-panel/admin_panel
chmod +x deploy.sh
chmod +x update.sh
```

---

## 📝 Kullanım Senaryoları

### Senaryo 1: Yerel bilgisayarda değişiklik yaptınız

1. **Yerel bilgisayarınızda:**
   ```bash
   git add .
   git commit -m "Değişiklik açıklaması"
   git push
   ```

2. **Sunucuda:**
   ```bash
   cd /opt/admin-panel/admin_panel
   ./update.sh
   ```

### Senaryo 2: Sunucuda direkt değişiklik yaptınız

```bash
cd /opt/admin-panel/admin_panel
# Dosyaları düzenleyin (nano, vim, vs.)
./deploy.sh
```

---

## ⚠️ Önemli Notlar

1. **Script'lerin çalışması için doğru dizinde olmalısınız:**
   - `/opt/admin-panel/admin_panel/` dizininde olmalısınız
   - Script'ler bu dizinden çalışacak şekilde tasarlandı

2. **İlk kullanımda:**
   - `chmod +x deploy.sh` ve `chmod +x update.sh` komutlarını çalıştırın

3. **Hata durumunda:**
   - Script'ler hata verirse, hata mesajını okuyun
   - Genellikle `npm install` veya `npm run build` hataları olabilir
   - `node_modules` klasörünü silip `npm install` yapmayı deneyin

---

## 🐛 Sorun Giderme

### Script çalışmıyor: "Permission denied"

```bash
chmod +x deploy.sh
chmod +x update.sh
```

### npm install hatası

```bash
cd admin_panel-react
rm -rf node_modules package-lock.json
npm install
```

### Build hatası

```bash
cd admin_panel-react
npm run build
# Hata mesajlarını kontrol edin
```

### Servis başlamıyor

```bash
systemctl status admin-panel
journalctl -u admin-panel -n 50
```

---

## ✅ Başarı Kontrolü

Script başarıyla tamamlandığında:

1. ✅ "Deployment tamamlandı!" mesajını görmelisiniz
2. ✅ Servis durumu "active (running)" olmalı
3. ✅ Web sitesinde değişiklikler görünmeli

---

**Artık tek komutla her şeyi yapabilirsiniz! 🎉**

