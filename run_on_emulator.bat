@echo off
REM Script para rodar a aplicação no emulador de terminal (Windows)

echo ==========================================
echo ParkControl - Emulador de Terminal
echo ==========================================
echo.

echo 📱 Procurando por emuladores/dispositivos conectados...
adb devices

echo.
echo 🔨 Compilando projeto...
call .\gradlew clean build -x lint --no-daemon

if %errorlevel% equ 0 (
    echo.
    echo ✅ Compilação bem-sucedida!
    echo.
    echo 📲 Instalando aplicação...
    adb install -r app\build\outputs\apk\debug\app-debug.apk

    if %errorlevel% equ 0 (
        echo.
        echo ✅ Instalação bem-sucedida!
        echo.
        echo 🚀 Iniciando aplicação...
        adb shell am start -n com.techmania.parkcontrol/com.techmania.parkcontrol.MainActivity

        echo.
        echo ✅ Aplicação iniciada no emulador!
        echo.
        timeout /t 3
    ) else (
        echo ❌ Erro na instalação
        pause
        exit /b 1
    )
) else (
    echo ❌ Erro na compilação
    pause
    exit /b 1
)

