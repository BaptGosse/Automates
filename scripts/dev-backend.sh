#!/bin/bash
set -e

# Couleurs
BLUE='\033[0;34m'
GREEN='\033[0;32m'
NC='\033[0m'

echo -e "${BLUE}🚀 Starting Backend Development Server...${NC}"

# Aller au dossier backend
cd "$(dirname "$0")/../backend"

echo -e "${GREEN}✅ Backend will start on http://localhost:8080${NC}"
echo -e "${GREEN}✅ API available at http://localhost:8080/api${NC}"
echo -e "${GREEN}✅ Health check: http://localhost:8080/actuator/health${NC}"
echo ""

# Lancer Spring Boot en mode dev
mvn spring-boot:run
