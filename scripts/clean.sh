#!/bin/bash
set -e

# Couleurs
BLUE='\033[0;34m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${BLUE}🧹 Cleaning build artifacts...${NC}"
echo ""

PROJECT_ROOT="$(dirname "$0")/.."

clean_dir() {
    local dir=$1
    local name=$2
    
    if [ -d "$dir" ]; then
        echo -e "${YELLOW}Cleaning $name...${NC}"
        rm -rf "$dir"
        echo -e "${GREEN}✅ $name cleaned${NC}"
    else
        echo -e "${YELLOW}⚠️  $name not found (skipping)${NC}"
    fi
}

# Backend
echo -e "${BLUE}📦 Backend:${NC}"
clean_dir "$PROJECT_ROOT/backend/target" "Maven target/"

echo ""
echo -e "${BLUE}🎨 Frontend:${NC}"
clean_dir "$PROJECT_ROOT/frontend/build" "SvelteKit build/"
clean_dir "$PROJECT_ROOT/frontend/.svelte-kit" "SvelteKit cache"
clean_dir "$PROJECT_ROOT/frontend/node_modules" "node_modules (optional)"

echo ""
echo -e "${BLUE}🖥️  Desktop:${NC}"
clean_dir "$PROJECT_ROOT/desktop/src-tauri/target" "Rust target/"
clean_dir "$PROJECT_ROOT/desktop/src-tauri/resources/backend.jar" "Embedded JAR"
clean_dir "$PROJECT_ROOT/desktop/node_modules" "node_modules (optional)"

echo ""
echo -e "${BLUE}📱 Mobile:${NC}"
if [ -d "$PROJECT_ROOT/mobile" ]; then
    clean_dir "$PROJECT_ROOT/mobile/android/app/build" "Android build/"
    clean_dir "$PROJECT_ROOT/mobile/ios/App/build" "iOS build/"
    clean_dir "$PROJECT_ROOT/mobile/node_modules" "node_modules (optional)"
else
    echo -e "${YELLOW}⚠️  Mobile not configured yet${NC}"
fi

echo ""
echo -e "${GREEN}🎉 Cleaning complete!${NC}"
echo ""
echo -e "${BLUE}To rebuild everything:${NC}"
echo -e "   ./scripts/build-all.sh"
