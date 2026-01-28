#!/bin/bash
set -e

# Couleurs
BLUE='\033[0;34m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${BLUE}🖥️  Starting Desktop Application (Tauri)...${NC}"
echo ""

PROJECT_ROOT="$(dirname "$0")/.."

# Vérifier que Rust est disponible
if ! command -v cargo &> /dev/null; then
    echo -e "${RED}❌ Error: Rust/Cargo not found in PATH${NC}"
    echo -e "${YELLOW}   Run: source \$HOME/.cargo/env${NC}"
    exit 1
fi

# Vérifier que le JAR backend existe
JAR_PATH="$PROJECT_ROOT/desktop/src-tauri/resources/backend.jar"
if [ ! -f "$JAR_PATH" ]; then
    echo -e "${YELLOW}⚠️  Backend JAR not found. Building it now...${NC}"
    cd "$PROJECT_ROOT/backend"
    mvn clean package -DskipTests
    mkdir -p "$PROJECT_ROOT/desktop/src-tauri/resources"
    cp target/automates-backend.jar "$PROJECT_ROOT/desktop/src-tauri/resources/backend.jar"
    echo -e "${GREEN}✅ Backend JAR prepared${NC}"
fi

echo -e "${GREEN}✅ Backend JAR: $(du -h "$JAR_PATH" | cut -f1)${NC}"
echo ""

# Vérifier que Java est installé
if ! command -v java &> /dev/null; then
    echo -e "${RED}❌ Error: Java not found${NC}"
    echo -e "${YELLOW}   Install: sudo apt install openjdk-21-jdk${NC}"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -n 1)
echo -e "${GREEN}✅ Java: $JAVA_VERSION${NC}"
echo ""

echo -e "${BLUE}🚀 Launching Tauri development mode...${NC}"
echo -e "${GREEN}   This will:${NC}"
echo -e "${GREEN}   1. Start Vite dev server (frontend) on http://localhost:5173${NC}"
echo -e "${GREEN}   2. Compile and launch Tauri app${NC}"
echo -e "${GREEN}   3. Tauri will auto-start the backend Java on a random port${NC}"
echo ""

cd "$PROJECT_ROOT/desktop"

# S'assurer que Rust est dans le PATH
export PATH="$HOME/.cargo/bin:$PATH"

npm run tauri dev
