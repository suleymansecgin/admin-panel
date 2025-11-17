# Admin Panel React Frontend

Bu proje, Admin Panel backend API'si için React tabanlı frontend uygulamasıdır.

## Özellikler

- ✅ Kullanıcı girişi (Login)
- ✅ Kullanıcı kaydı (Register)
- ✅ MFA (İki Faktörlü Kimlik Doğrulama) desteği
- ✅ JWT token yönetimi
- ✅ Protected routes (Korumalı sayfalar)
- ✅ Modern ve responsive tasarım
- ✅ Otomatik token yenileme

## Kurulum

```bash
npm install
```

## Çalıştırma

```bash
npm run dev
```

Uygulama `http://localhost:3000` adresinde çalışacaktır.

## Backend Bağlantısı

Backend API'si `http://localhost:8080` adresinde çalışmalıdır. Vite proxy yapılandırması sayesinde `/api` istekleri otomatik olarak backend'e yönlendirilir.

## Yapı

```
src/
├── components/       # React bileşenleri
├── context/          # Context API (AuthContext)
├── pages/            # Sayfa bileşenleri
│   ├── Login.jsx
│   ├── Register.jsx
│   └── Dashboard.jsx
└── services/         # API servisleri
    ├── api.js
    └── authService.js
```

## API Endpoints

- `POST /api/auth/login` - Giriş yap
- `POST /api/auth/register` - Kayıt ol
- `POST /api/auth/logout` - Çıkış yap
- `POST /api/auth/refresh` - Token yenile
- `POST /api/auth/mfa/setup` - MFA kurulumu
- `POST /api/auth/mfa/enable` - MFA etkinleştir
- `POST /api/auth/mfa/disable` - MFA devre dışı bırak

## Kullanım

1. Backend'i çalıştırın (port 8080)
2. Frontend'i çalıştırın: `npm run dev`
3. Tarayıcıda `http://localhost:3000` adresine gidin
4. Kayıt olun veya giriş yapın
