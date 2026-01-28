#!/bin/bash
set -e

# Couleurs
BLUE='\033[0;34m'
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${BLUE}📦 Building Backend JAR...${NC}"
echo ""

PROJECT_ROOT="$(dirname "$0")/.."
cd "$PROJECT_ROOT/backend"

# Build avec Maven
mvn clean package -DskipTests

JAR_FILE="target/automates-backend.jar"

if [ ! -f "$JAR_FILE" ]; then
    echo -e "${RED}❌ Error: JAR file not found${NC}"
    exit 1
fi

JAR_SIZE=$(du -h "$JAR_FILE" | cut -f1)
echo ""
echo -e "${GREEN}✅ Backend JAR built successfully${NC}"
echo -e "${GREEN}   File: $JAR_FILE${NC}"
echo -e "${GREEN}   Size: $JAR_SIZE${NC}"
echo ""
echo -e "${BLUE}To run the JAR:${NC}"
echo -e "   java -jar $JAR_FILE"
