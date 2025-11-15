#!/bin/bash

# Admin Panel Hızlı Güncelleme Script
# Git'ten güncellemeleri çeker, build yapar ve servisi yeniden başlatır

set -e

echo "🚀 Hızlı güncelleme başlatılıyor..."

# Renkler
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Git'ten güncellemeleri çek
echo -e "${YELLOW}📥 Git'ten güncellemeler çekiliyor...${NC}"
git pull

# Deploy script'ini çalıştır
echo -e "${YELLOW}🔨 Deploy script'i çalıştırılıyor...${NC}"
./deploy.sh

echo -e "${GREEN}✅ Güncelleme tamamlandı!${NC}"

