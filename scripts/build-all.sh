#!/bin/bash
set -e

# Couleurs
BLUE='\033[0;34m'
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo ""
echo "╔════════════════════════════════════════════════════════════╗"
echo "║                                                            ║"
echo "║     🏗️  Building ALL Automates Applications 🏗️           ║"
echo "║                                                            ║"
echo "╚════════════════════════════════════════════════════════════╝"
echo ""

PROJECT_ROOT="$(dirname "$0")/.."
START_TIME=$(date +%s)

# Compteur d'erreurs
ERRORS=0

# Fonction pour exécuter une commande et gérer les erreurs
run_build() {
    local name=$1
    local script=$2
    
    echo ""
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo -e "${BLUE}Building: $name${NC}"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    
    if [ -f "$script" ]; then
        if "$script"; then
            echo -e "${GREEN}✅ $name: SUCCESS${NC}"
        else
            echo -e "${RED}❌ $name: FAILED${NC}"
            ERRORS=$((ERRORS + 1))
        fi
    else
        echo -e "${YELLOW}⚠️  $name: Script not found (skipping)${NC}"
    fi
}

# 1. Backend
run_build "Backend JAR" "$PROJECT_ROOT/scripts/build-backend.sh"

# 2. Frontend
run_build "Frontend Web" "$PROJECT_ROOT/scripts/build-frontend.sh"

# 3. Desktop
run_build "Desktop (Tauri)" "$PROJECT_ROOT/scripts/build-desktop.sh"

# 4. Mobile (si disponible)
if [ -d "$PROJECT_ROOT/mobile" ]; then
    echo ""
    echo -e "${YELLOW}📱 Mobile builds require manual steps:${NC}"
    echo -e "   Android: ./scripts/build-mobile-android.sh"
    echo -e "   iOS:     ./scripts/build-mobile-ios.sh"
else
    echo ""
    echo -e "${YELLOW}⚠️  Mobile not yet configured (Phase 4)${NC}"
fi

# Résumé
END_TIME=$(date +%s)
DURATION=$((END_TIME - START_TIME))

echo ""
echo "╔════════════════════════════════════════════════════════════╗"
echo "║                     BUILD SUMMARY                          ║"
echo "╚════════════════════════════════════════════════════════════╝"
echo ""

if [ $ERRORS -eq 0 ]; then
    echo -e "${GREEN}✅ All builds completed successfully!${NC}"
else
    echo -e "${RED}❌ $ERRORS build(s) failed${NC}"
fi

echo ""
echo -e "${BLUE}⏱️  Total build time: ${DURATION}s${NC}"
echo ""

echo -e "${BLUE}📦 Build artifacts:${NC}"
echo "   Backend:  backend/target/automates-backend.jar"
echo "   Frontend: frontend/build/"
echo "   Desktop:  desktop/src-tauri/target/release/bundle/"
echo ""

if [ $ERRORS -gt 0 ]; then
    exit 1
fi

exit 0
