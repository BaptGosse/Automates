#!/bin/bash
set -e

# Couleurs
BLUE='\033[0;34m'
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${BLUE}🎨 Building Frontend (SvelteKit)...${NC}"
echo ""

PROJECT_ROOT="$(dirname "$0")/.."
cd "$PROJECT_ROOT/frontend"

# Vérifier que node_modules existe
if [ ! -d "node_modules" ]; then
    echo -e "${YELLOW}⚠️  node_modules not found. Running npm install...${NC}"
    npm install
fi

# Build avec SvelteKit
npm run build

BUILD_DIR="build"

if [ ! -d "$BUILD_DIR" ]; then
    echo -e "${RED}❌ Error: Build directory not found${NC}"
    exit 1
fi

BUILD_SIZE=$(du -sh "$BUILD_DIR" | cut -f1)
FILE_COUNT=$(find "$BUILD_DIR" -type f | wc -l)

echo ""
echo -e "${GREEN}✅ Frontend built successfully${NC}"
echo -e "${GREEN}   Directory: $BUILD_DIR${NC}"
echo -e "${GREEN}   Size: $BUILD_SIZE${NC}"
echo -e "${GREEN}   Files: $FILE_COUNT${NC}"
echo ""
echo -e "${BLUE}To preview the build:${NC}"
echo -e "   npm run preview"
