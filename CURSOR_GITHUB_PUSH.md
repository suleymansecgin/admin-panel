# 🚀 Cursor'dan GitHub'a Proje Yükleme Kılavuzu

Bu kılavuz, Cursor IDE kullanarak projenizi GitHub'a yüklemek için adım adım talimatlar içerir.

---

## 📋 ÖN HAZIRLIK

### Gereksinimler
- ✅ Cursor IDE kurulu
- ✅ GitHub hesabı (https://github.com)
- ✅ Git kurulu (Cursor ile birlikte gelir)

---

## 🎯 ADIM 1: GITHUB'DA REPOSITORY OLUŞTURMA

### 1.1. GitHub'a Giriş Yapın

1. https://github.com adresine gidin
2. GitHub kullanıcı adı ve şifrenizle giriş yapın

### 1.2. Yeni Repository Oluşturun

1. Sağ üstteki **"+"** (artı) butonuna tıklayın
2. **"New repository"** seçeneğini tıklayın

### 1.3. Repository Ayarları

1. **Repository name:** `suleymansecgin-proje` (veya istediğiniz isim)
2. **Description:** (İsteğe bağlı) Proje açıklaması
3. **Public** veya **Private** seçin
   - **Public:** Herkes görebilir
   - **Private:** Sadece siz görebilirsiniz
4. ⚠️ **"Initialize this repository with a README"** seçeneğini **İŞARETLEMEYİN**
5. ⚠️ **"Add .gitignore"** seçeneğini **İŞARETLEMEYİN**
6. ⚠️ **"Choose a license"** seçeneğini **İŞARETLEMEYİN**

### 1.4. Repository Oluşturun

1. **"Create repository"** butonuna tıklayın
2. Açılan sayfada **HTTPS URL'yi kopyalayın**
   - Örnek: `https://github.com/kullaniciadi/suleymansecgin-proje.git`
   - Bu URL'yi sonraki adımlarda kullanacağız

---

## 🎯 ADIM 2: CURSOR'DA GİT REPOSITORY BAŞLATMA

### 2.1. Source Control Panelini Açın

1. Cursor'da sol taraftaki **Source Control** ikonuna tıklayın
   - Veya klavye kısayolu: **Ctrl+Shift+G**

### 2.2. Initialize Repository

1. Source Control panelinde **"Initialize Repository"** butonuna tıklayın
   - Eğer buton görünmüyorsa, Git repository zaten başlatılmış olabilir
   - Bu durumda ADIM 3'e geçin

**✅ Başarılı:** "CHANGES" bölümü görünecek ve dosyalarınız listelenecek.

---

## 🎯 ADIM 3: DOSYALARI STAGE'E EKLEME

### 3.1. Değişiklikleri Görüntüleme

Source Control panelinde:
- **"CHANGES"** bölümünde tüm değişiklik yapılan dosyalar görünecek
- Her dosyanın yanında renkli işaretler var:
  - **Mavi (U):** Yeni dosya (Untracked)
  - **Yeşil (M):** Değiştirilmiş dosya (Modified)
  - **Kırmızı (D):** Silinmiş dosya (Deleted)

### 3.2. Dosyaları Stage'e Ekleme

**Yöntem A: Tek Tek Ekleme**
1. Her dosyanın sağındaki **"+"** (plus) ikonuna tıklayın
2. Dosya "Staged Changes" bölümüne geçecek

**Yöntem B: Tümünü Ekleme**
1. "CHANGES" bölümünün üstünde **"+"** ikonuna tıklayın
   - Veya **"Stage All Changes"** butonuna tıklayın
2. Tüm dosyalar "Staged Changes" bölümüne geçecek

**✅ Kontrol:** Dosyalar "Staged Changes" bölümünde görünmelidir.

---

## 🎯 ADIM 4: COMMIT YAPMA

### 4.1. Commit Mesajı Yazma

1. Source Control panelinin üstündeki **"Message"** kutusuna commit mesajı yazın
   - Örnek: `Initial commit`
   - Örnek: `Add admin panel project`
   - Örnek: `First deployment setup`

### 4.2. Commit Etme

**Yöntem A: Buton ile**
1. **"Commit"** butonuna tıklayın

**Yöntem B: Klavye Kısayolu**
1. **Ctrl+Enter** tuşlarına basın

**✅ Başarılı:** Dosyalar commit edildi ve "CHANGES" bölümü boşalacak.

---

## 🎯 ADIM 5: GITHUB REMOTE EKLEME

### 5.1. Terminal Açma

1. Cursor'da **Terminal** menüsünden **"New Terminal"** seçin
   - Veya klavye kısayolu: **Ctrl+`** (backtick)
   - Veya: **Ctrl+Shift+`**

### 5.2. Remote Ekleme

Terminal'de şu komutu çalıştırın (GitHub URL'nizi yazın):

```bash
git remote add origin https://github.com/KULLANICI_ADINIZ/suleymansecgin-proje.git
```

**Örnek:**
```bash
git remote add origin https://github.com/suleymansecgin/suleymansecgin-proje.git
```

**Not:** 
- `KULLANICI_ADINIZ` yerine GitHub kullanıcı adınızı yazın
- `suleymansecgin-proje` yerine repository adınızı yazın

**✅ Kontrol:** Remote'un eklendiğini kontrol etmek için:
```bash
git remote -v
```

Bu komut remote URL'lerini gösterecektir.

---

## 🎯 ADIM 6: BRANCH ADINI AYARLAMA

Terminal'de:

```bash
git branch -M main
```

**Not:** Bazı eski repository'ler `master` branch kullanır. Eğer hata alırsanız:
```bash
git branch -M master
```

---

## 🎯 ADIM 7: GITHUB'A PUSH ETME

### 7.1. Push Komutu

Terminal'de şu komutu çalıştırın:

```bash
git push -u origin main
```

**Not:** Eğer `master` branch kullandıysanız:
```bash
git push -u origin master
```

### 7.2. Kimlik Doğrulama

İlk push'ta GitHub kimlik doğrulama isteyecek:

#### Windows'ta:

1. **Kullanıcı adı:** GitHub kullanıcı adınızı girin
2. **Şifre:** GitHub şifreniz **VEYA** Personal Access Token girin
   - ⚠️ GitHub artık şifre yerine **Personal Access Token** kullanmanızı önerir

#### Personal Access Token Oluşturma (Önerilen):

1. GitHub.com > Sağ üstte profil resminize tıklayın
2. **Settings** seçin
3. Sol menüden **Developer settings**
4. **Personal access tokens** > **Tokens (classic)**
5. **Generate new token** > **Generate new token (classic)**
6. **Note:** `Cursor Git Access` (açıklama)
7. **Expiration:** İstediğiniz süreyi seçin (90 gün, 1 yıl, vb.)
8. **Select scopes:** **`repo`** seçeneğini işaretleyin
   - Bu, repository'lere erişim izni verir
9. En alta inin, **"Generate token"** butonuna tıklayın
10. **Token'ı kopyalayın** (bir daha gösterilmeyecek!)
11. Push yaparken şifre yerine bu token'ı kullanın

**✅ Başarılı:** Terminal'de şunu göreceksiniz:
```
Enumerating objects: X, done.
Counting objects: 100% (X/X), done.
Writing objects: 100% (X/X), done.
To https://github.com/...
```

---

## 🎯 ADIM 8: KONTROL

### 8.1. GitHub'da Kontrol

1. GitHub repository sayfanızı yenileyin
2. Tüm dosyalarınız orada görünmelidir
3. Commit mesajınız görünmelidir

### 8.2. Cursor'da Kontrol

Source Control panelinde:
- "GRAPH" bölümünde commit'iniz görünecek
- Remote bağlantısı aktif olacak

---

## 🔄 SONRAKI GÜNCELLEMELER

Projeyi güncelledikten sonra:

### 1. Dosyaları Stage'e Ekleyin

Source Control panelinde:
- Değişiklik yapılan dosyaları **"+"** ile stage'e ekleyin

### 2. Commit Yapın

- Commit mesajı yazın
- **"Commit"** butonuna tıklayın

### 3. Push Edin

**Yöntem A: Terminal'den**
```bash
git push
```

**Yöntem B: Cursor'dan**
1. Source Control panelinde **GRAPH** bölümüne bakın
2. Sağ tarafta **cloud upload** (☁️⬆️) ikonuna tıklayın
3. Veya **"..."** (üç nokta) menüsünden **"Push"** seçin

---

## 🐛 SORUN GİDERME

### "remote origin already exists" Hatası

Remote zaten eklenmiş. Kaldırıp yeniden ekleyin:

```bash
git remote remove origin
git remote add origin https://github.com/KULLANICI_ADINIZ/suleymansecgin-proje.git
```

### "Authentication failed" Hatası

- Personal Access Token kullanın (şifre yerine)
- Token'ın `repo` iznine sahip olduğundan emin olun
- Token'ın süresi dolmamış olmalı

### "Permission denied" Hatası

- Repository'nin size ait olduğundan emin olun
- URL'yi kontrol edin
- GitHub kullanıcı adınızı kontrol edin

### "fatal: not a git repository" Hatası

Git repository başlatılmamış. ADIM 2'yi tekrar yapın.

### "Initialize Repository" Butonu Görünmüyor

Git repository zaten başlatılmış. ADIM 3'e geçin.

---

## 📝 ÖZET KOMUTLAR

Tüm işlem için terminal komutları:

```bash
# Git repository başlat (Cursor'dan da yapılabilir)
git init

# Dosyaları ekle
git add .

# Commit yap
git commit -m "Initial commit"

# Remote ekle
git remote add origin https://github.com/KULLANICI_ADINIZ/suleymansecgin-proje.git

# Branch adını ayarla
git branch -M main

# Push et
git push -u origin main
```

---

## ✅ BAŞARILI!

Projeniz artık GitHub'da! 🎉

Artık:
- ✅ Projeniz GitHub'da güvenli bir şekilde saklanıyor
- ✅ Herhangi bir yerden erişebilirsiniz
- ✅ Sunucuya `git clone` ile yükleyebilirsiniz
- ✅ Güncellemeleri `git push` ile yapabilirsiniz

---

**İyi çalışmalar! 🚀**

