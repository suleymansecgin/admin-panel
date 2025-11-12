@echo off
REM Admin Panel Deployment Script (Windows)
REM Bu script React frontend'i build edip Spring Boot static klasörüne kopyalar

echo 🚀 Deployment başlatılıyor...

REM React frontend build
echo 📦 React frontend build ediliyor...
cd admin_panel-react
call npm install
call npm run build

REM Build çıktısını Spring Boot static klasörüne kopyala
echo 📁 Build dosyaları kopyalanıyor...
if exist ..\src\main\resources\static (
    rmdir /s /q ..\src\main\resources\static
)
mkdir ..\src\main\resources\static
xcopy /E /I /Y dist\* ..\src\main\resources\static\

cd ..

REM Spring Boot JAR dosyası oluştur
echo 🔨 Spring Boot JAR dosyası oluşturuluyor...
call mvnw.cmd clean package -DskipTests

echo ✅ Deployment tamamlandı!
echo JAR dosyası: target\admin_panel-0.0.1-SNAPSHOT.jar

pause

