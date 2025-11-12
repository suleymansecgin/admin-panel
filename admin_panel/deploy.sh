#!/bin/bash

# Admin Panel Deployment Script
# Bu script React frontend'i build edip Spring Boot static klasörüne kopyalar

set -e

echo "🚀 Deployment başlatılıyor..."

# Renkler
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# React frontend build
echo -e "${YELLOW}📦 React frontend build ediliyor...${NC}"
cd admin_panel-react
npm install
npm run build

# Build çıktısını Spring Boot static klasörüne kopyala
echo -e "${YELLOW}📁 Build dosyaları kopyalanıyor...${NC}"
rm -rf ../src/main/resources/static/*
cp -r dist/* ../src/main/resources/static/

cd ..

# Spring Boot JAR dosyası oluştur
echo -e "${YELLOW}🔨 Spring Boot JAR dosyası oluşturuluyor...${NC}"
./mvnw clean package -DskipTests

echo -e "${GREEN}✅ Deployment tamamlandı!${NC}"
echo -e "${GREEN}JAR dosyası: target/admin_panel-0.0.1-SNAPSHOT.jar${NC}"

