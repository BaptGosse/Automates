#!/bin/bash
set -e

# Couleurs
BLUE='\033[0;34m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${BLUE}🌐 Starting Web Application (Backend + Frontend)...${NC}"
echo ""

PROJECT_ROOT="$(dirname "$0")/.."

echo -e "${YELLOW}This will open 2 terminal windows:${NC}"
echo -e "  1. Backend on http://localhost:8080"
echo -e "  2. Frontend on http://localhost:5173"
echo ""

# Détecter le terminal ou tmux disponible
TERMINAL=""

# Vérifier tmux en premier (meilleure option)
if command -v tmux &> /dev/null; then
    echo -e "${GREEN}✅ Using tmux${NC}"

    # Créer une session tmux
    SESSION_NAME="automates-dev"

    # Tuer la session si elle existe déjà
    tmux has-session -t $SESSION_NAME 2>/dev/null && tmux kill-session -t $SESSION_NAME

    # Créer nouvelle session avec le backend
    tmux new-session -d -s $SESSION_NAME -n backend "cd $PROJECT_ROOT/backend && echo '🚀 Backend starting...' && mvn spring-boot:run"

    # Créer une fenêtre pour le frontend
    tmux new-window -t $SESSION_NAME -n frontend "cd $PROJECT_ROOT/frontend && sleep 10 && echo '🎨 Frontend starting...' && npm run dev"

    # Attacher à la session
    echo ""
    echo -e "${GREEN}🎉 Dev servers starting in tmux!${NC}"
    echo -e "${GREEN}   Backend:  http://localhost:8080${NC}"
    echo -e "${GREEN}   Frontend: http://localhost:5173${NC}"
    echo ""
    echo -e "${YELLOW}Tmux shortcuts:${NC}"
    echo -e "   Ctrl+B then 1  - Switch to backend window"
    echo -e "   Ctrl+B then 2  - Switch to frontend window"
    echo -e "   Ctrl+B then D  - Detach (keeps servers running)"
    echo -e "   Ctrl+C         - Stop current server"
    echo ""
    echo -e "${BLUE}Press Enter to attach to tmux session...${NC}"
    read

    tmux attach-session -t $SESSION_NAME
    exit 0
fi

# Sinon, essayer de trouver un terminal
if command -v kgx &> /dev/null; then
    TERMINAL="kgx"  # GNOME Console
elif command -v ghostty &> /dev/null; then
    TERMINAL="ghostty"
elif command -v gnome-terminal &> /dev/null; then
    TERMINAL="gnome-terminal"
elif command -v konsole &> /dev/null; then
    TERMINAL="konsole"
elif command -v xfce4-terminal &> /dev/null; then
    TERMINAL="xfce4-terminal"
elif command -v alacritty &> /dev/null; then
    TERMINAL="alacritty"
elif command -v kitty &> /dev/null; then
    TERMINAL="kitty"
elif command -v terminator &> /dev/null; then
    TERMINAL="terminator"
elif command -v xterm &> /dev/null; then
    TERMINAL="xterm"
else
    echo -e "${YELLOW}⚠️  No suitable terminal or tmux found.${NC}"
    echo ""
    echo -e "${BLUE}Install tmux for automatic management:${NC}"
    echo "  sudo pacman -S tmux  # Arch Linux"
    echo "  sudo apt install tmux # Debian/Ubuntu"
    echo ""
    echo -e "${YELLOW}Or run manually in 2 terminals:${NC}"
    echo ""
    echo "Terminal 1:"
    echo "  ./scripts/dev-backend.sh"
    echo ""
    echo "Terminal 2:"
    echo "  ./scripts/dev-frontend.sh"
    exit 1
fi

echo -e "${GREEN}✅ Using terminal: $TERMINAL${NC}"
echo ""

# Lancer le backend dans un nouveau terminal
if [ "$TERMINAL" = "kgx" ]; then
    kgx -- bash -c "cd $PROJECT_ROOT/backend && echo '🚀 Backend starting...' && mvn spring-boot:run; exec bash" &
elif [ "$TERMINAL" = "ghostty" ]; then
    ghostty -e bash -c "cd $PROJECT_ROOT/backend && echo '🚀 Backend starting...' && mvn spring-boot:run; exec bash" &
elif [ "$TERMINAL" = "gnome-terminal" ]; then
    gnome-terminal -- bash -c "cd $PROJECT_ROOT/backend && echo '🚀 Backend starting...' && mvn spring-boot:run; exec bash" &
elif [ "$TERMINAL" = "konsole" ]; then
    konsole -e bash -c "cd $PROJECT_ROOT/backend && echo '🚀 Backend starting...' && mvn spring-boot:run; exec bash" &
elif [ "$TERMINAL" = "xfce4-terminal" ]; then
    xfce4-terminal -e "bash -c 'cd $PROJECT_ROOT/backend && echo 🚀 Backend starting... && mvn spring-boot:run; exec bash'" &
elif [ "$TERMINAL" = "alacritty" ]; then
    alacritty -e bash -c "cd $PROJECT_ROOT/backend && echo '🚀 Backend starting...' && mvn spring-boot:run; exec bash" &
elif [ "$TERMINAL" = "kitty" ]; then
    kitty bash -c "cd $PROJECT_ROOT/backend && echo '🚀 Backend starting...' && mvn spring-boot:run; exec bash" &
elif [ "$TERMINAL" = "terminator" ]; then
    terminator -e "bash -c 'cd $PROJECT_ROOT/backend && echo 🚀 Backend starting... && mvn spring-boot:run; exec bash'" &
elif [ "$TERMINAL" = "xterm" ]; then
    xterm -e bash -c "cd $PROJECT_ROOT/backend && echo '🚀 Backend starting...' && mvn spring-boot:run; exec bash" &
fi

echo -e "${GREEN}✅ Backend terminal opened${NC}"
echo -e "${BLUE}⏳ Waiting 10 seconds for backend to start...${NC}"
sleep 10

# Lancer le frontend dans un nouveau terminal
if [ "$TERMINAL" = "kgx" ]; then
    kgx -- bash -c "cd $PROJECT_ROOT/frontend && echo '🎨 Frontend starting...' && npm run dev; exec bash" &
elif [ "$TERMINAL" = "ghostty" ]; then
    ghostty -e bash -c "cd $PROJECT_ROOT/frontend && echo '🎨 Frontend starting...' && npm run dev; exec bash" &
elif [ "$TERMINAL" = "gnome-terminal" ]; then
    gnome-terminal -- bash -c "cd $PROJECT_ROOT/frontend && echo '🎨 Frontend starting...' && npm run dev; exec bash" &
elif [ "$TERMINAL" = "konsole" ]; then
    konsole -e bash -c "cd $PROJECT_ROOT/frontend && echo '🎨 Frontend starting...' && npm run dev; exec bash" &
elif [ "$TERMINAL" = "xfce4-terminal" ]; then
    xfce4-terminal -e "bash -c 'cd $PROJECT_ROOT/frontend && echo 🎨 Frontend starting... && npm run dev; exec bash'" &
elif [ "$TERMINAL" = "alacritty" ]; then
    alacritty -e bash -c "cd $PROJECT_ROOT/frontend && echo '🎨 Frontend starting...' && npm run dev; exec bash" &
elif [ "$TERMINAL" = "kitty" ]; then
    kitty bash -c "cd $PROJECT_ROOT/frontend && echo '🎨 Frontend starting...' && npm run dev; exec bash" &
elif [ "$TERMINAL" = "terminator" ]; then
    terminator -e "bash -c 'cd $PROJECT_ROOT/frontend && echo 🎨 Frontend starting... && npm run dev; exec bash'" &
elif [ "$TERMINAL" = "xterm" ]; then
    xterm -e bash -c "cd $PROJECT_ROOT/frontend && echo '🎨 Frontend starting...' && npm run dev; exec bash" &
fi

echo -e "${GREEN}✅ Frontend terminal opened${NC}"
echo ""
echo -e "${GREEN}🎉 Web application is starting!${NC}"
echo -e "${GREEN}   Backend:  http://localhost:8080${NC}"
echo -e "${GREEN}   Frontend: http://localhost:5173${NC}"
echo ""
echo -e "${YELLOW}Press Ctrl+C in each terminal to stop the servers${NC}"
