#!/bin/bash

# Script de démarrage rapide pour développement
# Lance le backend et le frontend en parallèle

set -e

echo "🚀 Démarrage de l'application Automates..."

# Vérifier que nous sommes dans le bon dossier
if [ ! -d "../backend" ] || [ ! -d "../frontend" ]; then
    echo "❌ Erreur: Exécutez ce script depuis la racine du projet"
    exit 1
fi

echo ""
echo "📦 Backend Spring Boot: http://localhost:8080"
echo "🎨 Frontend Svelte: http://localhost:5173"
echo ""
echo "Appuyez sur Ctrl+C pour arrêter les deux services"
echo ""

# Fonction pour nettoyer les processus enfants
cleanup() {
    echo ""
    echo "🛑 Arrêt des services..."
    pkill -P $$
    exit 0
}

trap cleanup INT TERM

# Démarrer le backend en arrière-plan
echo "🔄 Démarrage du backend..."
cd backend
mvn spring-boot:run > ../logs/backend.log 2>&1 &
BACKEND_PID=$!
cd ..

# Attendre que le backend démarre
echo "⏳ Attente du démarrage du backend..."
sleep 5

# Démarrer le frontend en arrière-plan
echo "🔄 Démarrage du frontend..."
cd frontend
npm run dev > ../logs/frontend.log 2>&1 &
FRONTEND_PID=$!
cd ..

echo ""
echo "✅ Services démarrés!"
echo "   Backend PID: $BACKEND_PID"
echo "   Frontend PID: $FRONTEND_PID"
echo ""

# Suivre les logs
tail -f logs/backend.log logs/frontend.log
