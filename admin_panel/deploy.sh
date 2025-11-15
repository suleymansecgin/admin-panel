#!/bin/bash

# Admin Panel Deployment Script
# Bu script React frontend'i build edip Spring Boot static klasörüne kopyalar
# JAR dosyasını oluşturur, kopyalar ve servisi yeniden başlatır

set -e

echo "🚀 Deployment başlatılıyor..."

# Renkler
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Mevcut dizini kontrol et
CURRENT_DIR=$(pwd)
echo -e "${YELLOW}📂 Mevcut dizin: ${CURRENT_DIR}${NC}"

# React frontend build
echo -e "${YELLOW}📦 React frontend build ediliyor...${NC}"
cd admin_panel-react
npm install
npm run build

# Build çıktısını Spring Boot static klasörüne kopyala
echo -e "${YELLOW}📁 Build dosyaları kopyalanıyor...${NC}"
# Static klasörünü oluştur (yoksa)
mkdir -p ../src/main/resources/static
# Eski dosyaları temizle
rm -rf ../src/main/resources/static/*
# Yeni build dosyalarını kopyala
cp -r dist/* ../src/main/resources/static/
echo -e "${GREEN}✅ Build dosyaları başarıyla kopyalandı${NC}"

cd ..

# Maven wrapper'a execute permission ver
if [ -f "./mvnw" ]; then
    chmod +x ./mvnw
fi

# Spring Boot JAR dosyası oluştur
echo -e "${YELLOW}🔨 Spring Boot JAR dosyası oluşturuluyor...${NC}"
./mvnw clean package -DskipTests

# JAR dosyasını kopyala
echo -e "${YELLOW}📋 JAR dosyası kopyalanıyor...${NC}"
mkdir -p /opt/admin-panel

# JAR dosyasının var olduğunu kontrol et
if [ ! -f "target/admin_panel-0.0.1-SNAPSHOT.jar" ]; then
    echo -e "${RED}❌ HATA: JAR dosyası bulunamadı: target/admin_panel-0.0.1-SNAPSHOT.jar${NC}"
    exit 1
fi

# JAR dosyasını kopyala (verbose mode ile)
cp -v target/admin_panel-0.0.1-SNAPSHOT.jar /opt/admin-panel/

# Kopyalama başarılı mı kontrol et
if [ ! -f "/opt/admin-panel/admin_panel-0.0.1-SNAPSHOT.jar" ]; then
    echo -e "${RED}❌ HATA: JAR dosyası kopyalanamadı!${NC}"
    exit 1
fi

echo -e "${GREEN}✅ JAR dosyası başarıyla kopyalandı${NC}"
echo -e "${YELLOW}📅 JAR dosyası tarihi:$(ls -lh /opt/admin-panel/admin_panel-0.0.1-SNAPSHOT.jar | awk '{print $6, $7, $8}')${NC}"

# Servisi yeniden başlat
echo -e "${YELLOW}🔄 Servis yeniden başlatılıyor...${NC}"
systemctl stop admin-panel
sleep 2
systemctl start admin-panel

# Servis durumunu kontrol et
echo -e "${YELLOW}📊 Servis durumu kontrol ediliyor...${NC}"
sleep 2
systemctl status admin-panel --no-pager -l

echo -e "${GREEN}✅ Deployment tamamlandı!${NC}"
echo -e "${GREEN}JAR dosyası: /opt/admin-panel/admin_panel-0.0.1-SNAPSHOT.jar${NC}"

