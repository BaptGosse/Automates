#!/bin/bash
set -e

# Couleurs
BLUE='\033[0;34m'
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${BLUE}📱 Building Android Application...${NC}"
echo ""

PROJECT_ROOT="$(dirname "$0")/.."

# Vérifier que mobile/ existe
if [ ! -d "$PROJECT_ROOT/mobile" ]; then
    echo -e "${RED}❌ Error: mobile/ directory not found${NC}"
    echo -e "${YELLOW}   Run Phase 4 setup first (Capacitor)${NC}"
    exit 1
fi

# Build frontend avec variable d'environnement production
echo -e "${BLUE}🎨 Step 1/3: Building frontend for mobile...${NC}"
cd "$PROJECT_ROOT/frontend"
VITE_API_URL=${VITE_API_URL:-"https://automates-api.yourdomain.com"} npm run build

echo ""
echo -e "${BLUE}🔄 Step 2/3: Syncing Capacitor...${NC}"
cd "$PROJECT_ROOT/mobile"
npx cap sync android

echo ""
echo -e "${BLUE}🏗️  Step 3/3: Building Android APK...${NC}"
echo -e "${YELLOW}   Opening Android Studio...${NC}"
echo -e "${YELLOW}   In Android Studio: Build → Generate Signed Bundle / APK${NC}"

npx cap open android

echo ""
echo -e "${GREEN}✅ Android project opened in Android Studio${NC}"
echo -e "${BLUE}   Follow these steps in Android Studio:${NC}"
echo -e "   1. Build → Generate Signed Bundle / APK"
echo -e "   2. Select APK or Bundle"
echo -e "   3. Create or select keystore"
echo -e "   4. Build release version"
echo ""
echo -e "${GREEN}   APK will be in: mobile/android/app/build/outputs/apk/release/${NC}"
