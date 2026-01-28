#!/bin/bash
set -e

# Couleurs
BLUE='\033[0;34m'
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${BLUE}🧪 Running tests...${NC}"
echo ""

PROJECT_ROOT="$(dirname "$0")/.."
ERRORS=0

# Backend tests
echo -e "${BLUE}📦 Backend tests:${NC}"
cd "$PROJECT_ROOT/backend"
if mvn test; then
    echo -e "${GREEN}✅ Backend tests passed${NC}"
else
    echo -e "${RED}❌ Backend tests failed${NC}"
    ERRORS=$((ERRORS + 1))
fi

# Frontend tests (si configuré)
echo ""
echo -e "${BLUE}🎨 Frontend tests:${NC}"
cd "$PROJECT_ROOT/frontend"
if [ -f "package.json" ] && grep -q "\"test\"" package.json; then
    if npm test; then
        echo -e "${GREEN}✅ Frontend tests passed${NC}"
    else
        echo -e "${RED}❌ Frontend tests failed${NC}"
        ERRORS=$((ERRORS + 1))
    fi
else
    echo -e "${YELLOW}⚠️  No frontend tests configured${NC}"
fi

# Desktop tests (si configuré)
echo ""
echo -e "${BLUE}🖥️  Desktop tests:${NC}"
cd "$PROJECT_ROOT/desktop/src-tauri"
if [ -f "Cargo.toml" ]; then
    export PATH="$HOME/.cargo/bin:$PATH"
    if cargo test; then
        echo -e "${GREEN}✅ Desktop tests passed${NC}"
    else
        echo -e "${RED}❌ Desktop tests failed${NC}"
        ERRORS=$((ERRORS + 1))
    fi
else
    echo -e "${YELLOW}⚠️  No desktop tests configured${NC}"
fi

# Résumé
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
if [ $ERRORS -eq 0 ]; then
    echo -e "${GREEN}✅ All tests passed!${NC}"
else
    echo -e "${RED}❌ $ERRORS test suite(s) failed${NC}"
fi
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

if [ $ERRORS -gt 0 ]; then
    exit 1
fi

exit 0
