# Postman ile Sipariş Verme ve Görüntüleme Yol Haritası

## 📋 Genel Bilgiler
- **Base URL**: `http://localhost:8080/api`
- **Port**: 8080
- **Authentication**: JWT Token (Bearer Token)
- **Content-Type**: `application/json`

---

## 🔐 ADIM 1: Giriş Yaparak Token Alma

### 1.1 Login Request Oluşturma

**Method**: `POST`  
**URL**: `http://localhost:8080/api/auth/login`

**Headers**:
```
Content-Type: application/json
```

**Body** (raw JSON):
```json
{
  "username": "kullanici_adi",
  "password": "sifre"
}
```

**Örnek Response**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "username": "kullanici_adi",
    "email": "email@example.com",
    "roles": [...]
  }
}
```

**⚠️ ÖNEMLİ**: Response'dan gelen `token` değerini kopyalayın. Bu token'ı tüm diğer isteklerde kullanacaksınız.

---

## 📦 ADIM 2: Mevcut Ürünleri Listeleme (Ürün ID'si Almak İçin)

### 2.1 Tüm Ürünleri Getirme

**Method**: `GET`  
**URL**: `http://localhost:8080/api/urunler`

**Headers**:
```
Authorization: Bearer {TOKEN_BURAYA}
Content-Type: application/json
```

**Not**: `{TOKEN_BURAYA}` yerine ADIM 1'de aldığınız token'ı yazın.

**Örnek Response**:
```json
[
  {
    "id": 1,
    "ad": "Ürün Adı",
    "aciklama": "Ürün Açıklaması",
    "fiyat": 99.99,
    "stok": 100,
    "barkod": "123456789",
    ...
  },
  {
    "id": 2,
    "ad": "Başka Ürün",
    ...
  }
]
```

**✅ Yapılacak**: Response'dan sipariş vermek istediğiniz ürünün `id` değerini not edin.

---

## 🏪 ADIM 3: Pazaryerlerini Listeleme (Pazaryeri ID'si Almak İçin)

### 3.1 Tüm Pazaryerlerini Getirme

**Method**: `GET`  
**URL**: `http://localhost:8080/api/pazaryerleri`

**Headers**:
```
Authorization: Bearer {TOKEN_BURAYA}
Content-Type: application/json
```

**Örnek Response**:
```json
[
  {
    "id": 1,
    "ad": "Trendyol",
    "aktif": true,
    ...
  },
  {
    "id": 2,
    "ad": "Hepsiburada",
    "aktif": true,
    ...
  }
]
```

**✅ Yapılacak**: Response'dan sipariş vermek istediğiniz pazaryerinin `id` değerini not edin.

---

## 🛒 ADIM 4: Sipariş Oluşturma (POST)

### 4.1 Yeni Sipariş Ekleme

**Method**: `POST`  
**URL**: `http://localhost:8080/api/siparisler`

**Headers**:
```
Authorization: Bearer {TOKEN_BURAYA}
Content-Type: application/json
```

**Body** (raw JSON):
```json
{
  "pazaryeriId": 1,
  "pazaryeriSiparisId": "SP-2024-001",
  "durum": "Yeni",
  "toplamTutar": 199.98,
  "siparisTarihi": "2024-01-15T10:30:00",
  "musteriAdi": "Ahmet Yılmaz",
  "musteriAdresi": "İstanbul, Kadıköy, Moda Caddesi No:123",
  "musteriTelefon": "05551234567",
  "musteriEmail": "ahmet@example.com",
  "kargoTakipNo": null,
  "kargoFirmasi": null,
  "notlar": "Acil teslimat",
  "siparisKalemleri": [
    {
      "urunId": 1,
      "miktar": 2,
      "birimFiyat": 99.99,
      "pazaryeriUrunKodu": "TRENDYOL-123"
    }
  ]
}
```

**Alan Açıklamaları**:
- `pazaryeriId`: ADIM 3'te aldığınız pazaryeri ID'si (zorunlu)
- `pazaryeriSiparisId`: Pazaryerinin verdiği sipariş numarası (zorunlu, benzersiz olmalı)
- `durum`: Sipariş durumu - "Yeni", "Hazırlanıyor", "Kargoya Verildi", "Teslim Edildi", "İptal" (zorunlu)
- `toplamTutar`: Toplam sipariş tutarı (zorunlu, pozitif sayı)
- `siparisTarihi`: ISO 8601 formatında tarih (zorunlu) - Format: `YYYY-MM-DDTHH:mm:ss`
- `musteriAdi`: Müşteri adı (opsiyonel)
- `musteriAdresi`: Müşteri adresi (opsiyonel)
- `musteriTelefon`: Müşteri telefonu (opsiyonel)
- `musteriEmail`: Müşteri email (opsiyonel)
- `kargoTakipNo`: Kargo takip numarası (opsiyonel)
- `kargoFirmasi`: Kargo firması (opsiyonel)
- `notlar`: Sipariş notları (opsiyonel)
- `siparisKalemleri`: Sipariş kalemleri listesi (zorunlu)
  - `urunId`: ADIM 2'de aldığınız ürün ID'si (zorunlu)
  - `miktar`: Ürün miktarı (zorunlu, pozitif sayı)
  - `birimFiyat`: Birim fiyat (zorunlu)
  - `pazaryeriUrunKodu`: Pazaryerindeki ürün kodu (opsiyonel)

**Örnek Response** (Başarılı):
```json
{
  "id": 1,
  "pazaryeriId": 1,
  "pazaryeriAdi": "Trendyol",
  "pazaryeriSiparisId": "SP-2024-001",
  "durum": "Yeni",
  "toplamTutar": 199.98,
  "siparisTarihi": "2024-01-15T10:30:00",
  "musteriAdi": "Ahmet Yılmaz",
  "musteriAdresi": "İstanbul, Kadıköy, Moda Caddesi No:123",
  "musteriTelefon": "05551234567",
  "musteriEmail": "ahmet@example.com",
  "kargoTakipNo": null,
  "kargoFirmasi": null,
  "notlar": "Acil teslimat",
  "siparisKalemleri": [
    {
      "id": 1,
      "urunId": 1,
      "urunAdi": "Ürün Adı",
      "miktar": 2,
      "birimFiyat": 99.99,
      "toplamFiyat": 199.98,
      "pazaryeriUrunKodu": "TRENDYOL-123"
    }
  ],
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

**✅ Yapılacak**: Response'dan gelen `id` değerini not edin (sipariş ID'si).

---

## 📋 ADIM 5: Siparişleri Listeleme (GET)

### 5.1 Tüm Siparişleri Getirme

**Method**: `GET`  
**URL**: `http://localhost:8080/api/siparisler`

**Headers**:
```
Authorization: Bearer {TOKEN_BURAYA}
Content-Type: application/json
```

**Örnek Response**:
```json
[
  {
    "id": 1,
    "pazaryeriId": 1,
    "pazaryeriAdi": "Trendyol",
    "pazaryeriSiparisId": "SP-2024-001",
    "durum": "Yeni",
    "toplamTutar": 199.98,
    "siparisTarihi": "2024-01-15T10:30:00",
    "musteriAdi": "Ahmet Yılmaz",
    ...
  },
  {
    "id": 2,
    ...
  }
]
```

---

## 🔍 ADIM 6: Belirli Bir Siparişi Görüntüleme

### 6.1 Sipariş Detayını Getirme

**Method**: `GET`  
**URL**: `http://localhost:8080/api/siparisler/{id}`

**Örnek**: `http://localhost:8080/api/siparisler/1`

**Headers**:
```
Authorization: Bearer {TOKEN_BURAYA}
Content-Type: application/json
```

**Örnek Response**: ADIM 4'teki response ile aynı format.

---

## 🔎 ADIM 7: Duruma Göre Siparişleri Filtreleme

### 7.1 Durum Filtresi ile Siparişleri Getirme

**Method**: `GET`  
**URL**: `http://localhost:8080/api/siparisler/durum/{durum}`

**Örnekler**:
- `http://localhost:8080/api/siparisler/durum/Yeni`
- `http://localhost:8080/api/siparisler/durum/Hazırlanıyor`
- `http://localhost:8080/api/siparisler/durum/Kargoya Verildi`
- `http://localhost:8080/api/siparisler/durum/Teslim Edildi`
- `http://localhost:8080/api/siparisler/durum/İptal`

**Headers**:
```
Authorization: Bearer {TOKEN_BURAYA}
Content-Type: application/json
```

---

## 🏪 ADIM 8: Pazaryerine Göre Siparişleri Filtreleme

### 8.1 Pazaryeri Filtresi ile Siparişleri Getirme

**Method**: `GET`  
**URL**: `http://localhost:8080/api/siparisler/pazaryeri/{pazaryeriId}`

**Örnek**: `http://localhost:8080/api/siparisler/pazaryeri/1`

**Headers**:
```
Authorization: Bearer {TOKEN_BURAYA}
Content-Type: application/json
```

---

## 🔄 ADIM 9: Sipariş Durumunu Güncelleme

### 9.1 Sipariş Durumu Güncelleme

**Method**: `PUT`  
**URL**: `http://localhost:8080/api/siparisler/{id}/durum`

**Örnek**: `http://localhost:8080/api/siparisler/1/durum`

**Headers**:
```
Authorization: Bearer {TOKEN_BURAYA}
Content-Type: application/json
```

**Body** (raw JSON):
```json
{
  "durum": "Hazırlanıyor"
}
```

**Geçerli Durumlar**:
- "Yeni"
- "Hazırlanıyor"
- "Kargoya Verildi"
- "Teslim Edildi"
- "İptal"

---

## 📝 Postman'de Hızlı Kullanım İpuçları

### 1. Environment Variables Kullanımı
Postman'de Environment oluşturup şu değişkenleri tanımlayın:
- `base_url`: `http://localhost:8080/api`
- `token`: (Login sonrası otomatik set edilecek)

Böylece URL'lerde `{{base_url}}/siparisler` şeklinde kullanabilirsiniz.

### 2. Token'ı Otomatik Set Etme
Login request'inin **Tests** sekmesine şunu ekleyin:
```javascript
if (pm.response.code === 200) {
    var jsonData = pm.response.json();
    pm.environment.set("token", jsonData.token);
}
```

Sonra diğer request'lerde Authorization header'ında:
```
Bearer {{token}}
```
şeklinde kullanabilirsiniz.

### 3. Collection Oluşturma
Tüm request'leri bir Collection altında toplayın:
- Auth (Login, Register, Refresh)
- Ürünler (GET, POST, PUT, DELETE)
- Pazaryerleri (GET, POST, PUT, DELETE)
- Siparişler (GET, POST, PUT)

---

## ⚠️ Hata Durumları ve Çözümleri

### 401 Unauthorized
- **Sebep**: Token eksik, geçersiz veya süresi dolmuş
- **Çözüm**: ADIM 1'i tekrar yaparak yeni token alın

### 403 Forbidden
- **Sebep**: Kullanıcının `ORDER_MANAGE` yetkisi yok
- **Çözüm**: Kullanıcıya gerekli yetkiyi verin

### 400 Bad Request
- **Sebep**: Request body'de eksik veya hatalı alan var
- **Çözüm**: Tüm zorunlu alanları kontrol edin

### 404 Not Found
- **Sebep**: Ürün ID veya Pazaryeri ID mevcut değil
- **Çözüm**: ADIM 2 ve 3'ü tekrar yaparak geçerli ID'leri kontrol edin

---

## ✅ Kontrol Listesi

Sipariş verme işlemi için:
- [ ] Login yapıldı ve token alındı
- [ ] Ürünler listelendi ve ürün ID'si alındı
- [ ] Pazaryerleri listelendi ve pazaryeri ID'si alındı
- [ ] POST request ile sipariş oluşturuldu
- [ ] GET request ile siparişler listelendi
- [ ] Oluşturulan sipariş görüntülendi

---

## 🎯 Özet: Hızlı Başlangıç

1. **Login**: `POST /api/auth/login` → Token al
2. **Ürünleri Listele**: `GET /api/urunler` → Ürün ID'sini not et
3. **Pazaryerlerini Listele**: `GET /api/pazaryerleri` → Pazaryeri ID'sini not et
4. **Sipariş Oluştur**: `POST /api/siparisler` → Sipariş ID'sini not et
5. **Siparişleri Listele**: `GET /api/siparisler` → Tüm siparişleri görüntüle
6. **Sipariş Detayı**: `GET /api/siparisler/{id}` → Belirli siparişi görüntüle

---

**Not**: Tüm endpoint'ler JWT token gerektirir. Her request'te `Authorization: Bearer {token}` header'ını eklemeyi unutmayın!

