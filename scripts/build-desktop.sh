#!/bin/bash
set -e

echo "🏗️  Building Desktop Application..."

# Couleurs pour l'output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Aller à la racine du projet
cd "$(dirname "$0")/.."
PROJECT_ROOT=$(pwd)

echo -e "${BLUE}📦 Step 1/4: Building backend JAR...${NC}"
cd "$PROJECT_ROOT/backend"
mvn clean package -DskipTests
JAR_FILE="target/automates-backend.jar"

if [ ! -f "$JAR_FILE" ]; then
    echo -e "${RED}❌ Error: JAR file not found at $JAR_FILE${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Backend JAR built successfully${NC}"

echo -e "${BLUE}📋 Step 2/4: Copying JAR to Tauri resources...${NC}"
mkdir -p "$PROJECT_ROOT/desktop/src-tauri/resources"
cp "$JAR_FILE" "$PROJECT_ROOT/desktop/src-tauri/resources/backend.jar"
echo -e "${GREEN}✅ JAR copied to Tauri resources${NC}"

echo -e "${BLUE}🎨 Step 3/4: Building frontend...${NC}"
cd "$PROJECT_ROOT/frontend"
npm run build

if [ ! -d "build" ]; then
    echo -e "${RED}❌ Error: Frontend build directory not found${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Frontend built successfully${NC}"

echo -e "${BLUE}🚀 Step 4/4: Building Tauri application...${NC}"
cd "$PROJECT_ROOT/desktop"

# S'assurer que Rust est disponible dans le PATH
if [ -f "$HOME/.cargo/env" ]; then
    source "$HOME/.cargo/env"
fi

npm run tauri build

echo -e "${GREEN}✅ Build complete!${NC}"
echo ""
echo -e "${BLUE}📦 Desktop app location:${NC}"
echo "  • Linux DEB: $PROJECT_ROOT/desktop/src-tauri/target/release/bundle/deb/"
echo "  • Linux AppImage: $PROJECT_ROOT/desktop/src-tauri/target/release/bundle/appimage/"
echo "  • Windows MSI: $PROJECT_ROOT/desktop/src-tauri/target/release/bundle/msi/"
echo ""
echo -e "${GREEN}🎉 Desktop application built successfully!${NC}"
