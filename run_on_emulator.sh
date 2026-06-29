#!/bin/bash
# Script para rodar a aplicação no emulador de terminal
# Use este script em Git Bash ou WSL no Windows

echo "=========================================="
echo "ParkControl - Emulador de Terminal"
echo "=========================================="
echo ""

# Verificar se há emulador conectado
echo "📱 Procurando por emuladores/dispositivos conectados..."
adb devices

echo ""
echo "🔨 Compilando projeto..."
./gradlew clean build

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Compilação bem-sucedida!"
    echo ""
    echo "📲 Instalando aplicação..."
    adb install -r app/build/outputs/apk/debug/app-debug.apk

    if [ $? -eq 0 ]; then
        echo ""
        echo "✅ Instalação bem-sucedida!"
        echo ""
        echo "🚀 Iniciando aplicação..."
        adb shell am start -n com.techmania.parkcontrol/com.techmania.parkcontrol.MainActivity

        echo ""
        echo "✅ Aplicação iniciada no emulador!"
        echo ""
    else
        echo "❌ Erro na instalação"
        exit 1
    fi
else
    echo "❌ Erro na compilação"
    exit 1
fi

