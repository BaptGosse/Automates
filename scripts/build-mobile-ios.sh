#!/bin/bash
set -e

# Couleurs
BLUE='\033[0;34m'
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${BLUE}📱 Building iOS Application...${NC}"
echo ""

# Vérifier que nous sommes sur macOS
if [ "$(uname)" != "Darwin" ]; then
    echo -e "${RED}❌ Error: iOS builds require macOS${NC}"
    exit 1
fi

PROJECT_ROOT="$(dirname "$0")/.."

# Vérifier que mobile/ existe
if [ ! -d "$PROJECT_ROOT/mobile" ]; then
    echo -e "${RED}❌ Error: mobile/ directory not found${NC}"
    echo -e "${YELLOW}   Run Phase 4 setup first (Capacitor)${NC}"
    exit 1
fi

# Vérifier que Xcode est installé
if ! command -v xcodebuild &> /dev/null; then
    echo -e "${RED}❌ Error: Xcode not installed${NC}"
    echo -e "${YELLOW}   Install Xcode from the App Store${NC}"
    exit 1
fi

# Build frontend avec variable d'environnement production
echo -e "${BLUE}🎨 Step 1/3: Building frontend for mobile...${NC}"
cd "$PROJECT_ROOT/frontend"
VITE_API_URL=${VITE_API_URL:-"https://automates-api.yourdomain.com"} npm run build

echo ""
echo -e "${BLUE}🔄 Step 2/3: Syncing Capacitor...${NC}"
cd "$PROJECT_ROOT/mobile"
npx cap sync ios

echo ""
echo -e "${BLUE}🏗️  Step 3/3: Building iOS IPA...${NC}"
echo -e "${YELLOW}   Opening Xcode...${NC}"
echo -e "${YELLOW}   In Xcode: Product → Archive${NC}"

npx cap open ios

echo ""
echo -e "${GREEN}✅ iOS project opened in Xcode${NC}"
echo -e "${BLUE}   Follow these steps in Xcode:${NC}"
echo -e "   1. Configure Signing & Capabilities"
echo -e "   2. Product → Archive"
echo -e "   3. Distribute to App Store or TestFlight"
echo ""
echo -e "${GREEN}   Archive will be in: ~/Library/Developer/Xcode/Archives/${NC}"
