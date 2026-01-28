#!/bin/bash
set -e

# Couleurs
BLUE='\033[0;34m'
GREEN='\033[0;32m'
NC='\033[0m'

echo -e "${BLUE}🌐 Building Web Application (Backend + Frontend)...${NC}"
echo ""

PROJECT_ROOT="$(dirname "$0")/.."

# Build backend
echo -e "${BLUE}📦 Step 1/2: Building backend JAR...${NC}"
"$PROJECT_ROOT/scripts/build-backend.sh"

echo ""

# Build frontend
echo -e "${BLUE}🎨 Step 2/2: Building frontend...${NC}"
"$PROJECT_ROOT/scripts/build-frontend.sh"

echo ""
echo -e "${GREEN}🎉 Web application built successfully!${NC}"
echo ""
echo -e "${BLUE}Deployment files:${NC}"
echo -e "   Backend JAR: backend/target/automates-backend.jar"
echo -e "   Frontend:    frontend/build/"
echo ""
echo -e "${BLUE}To deploy:${NC}"
echo -e "   1. Copy the JAR to your server"
echo -e "   2. Run: java -jar automates-backend.jar"
echo -e "   3. Serve the frontend/build/ directory with nginx/apache"
