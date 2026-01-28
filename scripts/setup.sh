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
echo "║         🚀 Automates Project Setup 🚀                     ║"
echo "║                                                            ║"
echo "╚════════════════════════════════════════════════════════════╝"
echo ""

PROJECT_ROOT="$(dirname "$0")/.."

# Fonction pour vérifier une commande
check_command() {
    local cmd=$1
    local name=$2
    local install_hint=$3
    
    if command -v "$cmd" &> /dev/null; then
        local version=$($cmd --version 2>&1 | head -n 1)
        echo -e "${GREEN}✅ $name: $version${NC}"
        return 0
    else
        echo -e "${RED}❌ $name not found${NC}"
        if [ -n "$install_hint" ]; then
            echo -e "${YELLOW}   Install: $install_hint${NC}"
        fi
        return 1
    fi
}

echo -e "${BLUE}🔍 Checking prerequisites...${NC}"
echo ""

MISSING=0

# Java
check_command "java" "Java" "sudo apt install openjdk-21-jdk" || MISSING=$((MISSING + 1))

# Maven
check_command "mvn" "Maven" "sudo apt install maven" || MISSING=$((MISSING + 1))

# Node.js
check_command "node" "Node.js" "Install from https://nodejs.org" || MISSING=$((MISSING + 1))

# npm
check_command "npm" "npm" "Comes with Node.js" || MISSING=$((MISSING + 1))

# Rust (optionnel pour desktop)
echo ""
echo -e "${BLUE}Optional (for Desktop):${NC}"
if check_command "cargo" "Rust/Cargo" "curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh"; then
    export PATH="$HOME/.cargo/bin:$PATH"
else
    echo -e "${YELLOW}⚠️  Rust not found. Desktop build will not work.${NC}"
fi

echo ""
if [ $MISSING -gt 0 ]; then
    echo -e "${RED}❌ $MISSING required tool(s) missing. Please install them first.${NC}"
    exit 1
fi

echo -e "${GREEN}✅ All required tools are installed!${NC}"
echo ""

# Installer les dépendances
echo -e "${BLUE}📦 Installing dependencies...${NC}"
echo ""

# Backend dependencies (Maven les télécharge automatiquement)
echo -e "${BLUE}Backend: Maven dependencies will be downloaded on first build${NC}"

# Frontend dependencies
echo -e "${BLUE}Frontend: Installing npm packages...${NC}"
cd "$PROJECT_ROOT/frontend"
npm install
echo -e "${GREEN}✅ Frontend dependencies installed${NC}"

# Desktop dependencies
if command -v cargo &> /dev/null; then
    echo ""
    echo -e "${BLUE}Desktop: Installing npm packages...${NC}"
    cd "$PROJECT_ROOT/desktop"
    npm install
    echo -e "${GREEN}✅ Desktop dependencies installed${NC}"
fi

echo ""
echo "╔════════════════════════════════════════════════════════════╗"
echo "║                    SETUP COMPLETE! 🎉                      ║"
echo "╚════════════════════════════════════════════════════════════╝"
echo ""

echo -e "${BLUE}Next steps:${NC}"
echo ""
echo -e "${GREEN}Development:${NC}"
echo "   Web:     ./scripts/dev-web.sh"
echo "   Desktop: ./scripts/dev-desktop.sh"
echo ""
echo -e "${GREEN}Build:${NC}"
echo "   All:     ./scripts/build-all.sh"
echo "   Web:     ./scripts/build-web.sh"
echo "   Desktop: ./scripts/build-desktop.sh"
echo ""
echo -e "${GREEN}Other:${NC}"
echo "   Test:    ./scripts/test.sh"
echo "   Clean:   ./scripts/clean.sh"
echo ""
