#!/bin/bash
set -e

# Couleurs
BLUE='\033[0;34m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${BLUE}🎨 Starting Frontend Development Server...${NC}"

# Aller au dossier frontend
cd "$(dirname "$0")/../frontend"

echo -e "${YELLOW}⚠️  Make sure the backend is running on http://localhost:8080${NC}"
echo -e "${GREEN}✅ Frontend will start on http://localhost:5173${NC}"
echo -e "${GREEN}✅ Vite proxy will redirect /api to http://localhost:8080/api${NC}"
echo ""

# Lancer Vite dev server
npm run dev
