// Configuração para Emulador de Terminal de Pagamento
// Salve estas configurações no seu perfil do Android Studio

/*
PASSO 1: Criar um novo Virtual Device
=====================================================
1. Abra Android Studio
2. Vá em: Device Manager > Create device
3. Use estas configurações:

HARDWARE:
--------
Device Name: Terminal Pagamento
Device Type: Phone
Screen Size: 5 polegadas (aproximado ao terminal real)
Resolution: 1280 x 720 (ou 720 x 1280)
Density: 160dpi (mdpi)

SYSTEM IMAGE:
-----------
API Level: 30+ (recomendado 30-33)
System on Chip: x86_64
Variant: Google APIs (para testar APIs de pagamento)

MEMORY & STORAGE:
----------------
RAM: 2048 MB (2GB)
VM heap: 512 MB
Internal Storage: 2 GB
SD Card: 1 GB

ADVANCED SETTINGS:
------------------
Graphics: Hardware - GLES 2.0
Keyboard: Habilitado
Rede: Habilitada
*/

// PASSO 2: Configurações Avançadas para Periféricos
/*
Para simular hardwares de terminal:

1. Leitor de Cartão (USB Serial):
   - Emular via Telnet console
   - Comando: device serial <dados>

2. Câmera:
   - Habilitada por padrão
   - Ou use webcam do computador

3. Impressora Térmica:
   - Simular via arquivo de saída
   - Gravar em /sdcard/printer_output.txt

4. Sensor Biométrico:
   - Simular via console do emulador
   - Comando: finger touch
   - Comando: finger remove
*/

// PASSO 3: Executar com Otimizações
/*
No terminal/PowerShell, execute:

cd C:\Maikel\Projects\ParkControl

# Build e instalação
.\gradlew installDebug

# Executar em modo debug
adb shell am start -D -N com.techmania.parkcontrol/com.techmania.parkcontrol.MainActivity
*/

