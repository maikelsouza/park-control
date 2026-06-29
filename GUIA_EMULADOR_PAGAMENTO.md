# 🖥️ Guia Completo: Emuladores para Máquinas de Pagamento

## 📊 Comparação de Emuladores

| Emulador | Velocidade | Compatibilidade | Periféricos | Custo |
|----------|------------|-----------------|-------------|-------|
| **Android Studio** | ⭐⭐⭐ | ✅ Excelente | ✅ Bom | Grátis |
| **Genymotion** | ⭐⭐⭐⭐ | ✅ Excelente | ✅ Ótimo | Grátis/Pago |
| **Android TV Emulator** | ⭐⭐ | ⚠️ Limitado | ❌ Limitado | Grátis |
| **Dispositivo Real** | ⭐⭐⭐⭐⭐ | ✅ Perfeito | ✅ Perfeito | $$ |

---

## 🚀 Passo-a-Passo: Android Studio Emulator

### **1. Criar Device Virtual**

```
Arquivo → Settings → Device Manager (ou Tools → Device Manager)
    ↓
+ Create device
    ↓
Phone → Selecione "Pixel 4a"
    ↓
API Level: 33 (Recomendado para máquinas de pagamento)
    ↓
Configurações Avançadas:
  - RAM: 2GB
  - VM Heap: 512MB
  - Internal Storage: 2GB
  - Enable Keyboard: ✓
  - Enable d-pad: ✓ (para navegação)
```

### **2. Executar Emulador**

**Opção A: Via Android Studio**
```
Device Manager → Play (botão ▶️)
```

**Opção B: Via Terminal PowerShell**
```powershell
# Listar dispositivos virtuais disponíveis
emulator -list-avds

# Iniciar emulador específico
emulator -avd "Terminal_Pagamento" -no-snapshot-load
```

### **3. Instalar e Executar Aplicação**

**Opção A: Via Android Studio (Mais Fácil)**
```
1. Run → Run 'app'
2. Selecione o emulador
3. Clique em "OK"
```

**Opção B: Via PowerShell (Script)**
```powershell
cd C:\Maikel\Projects\ParkControl
.\run_on_emulator.bat
```

**Opção C: Manual com ADB**
```powershell
# Compilar
.\gradlew clean build

# Instalar
adb install -r app\build\outputs\apk\debug\app-debug.apk

# Executar
adb shell am start -n com.techmania.parkcontrol/com.techmania.parkcontrol.MainActivity

# Ver logs em tempo real
adb logcat com.techmania.parkcontrol:V *:S
```

---

## 🔌 Simular Periféricos em Máquinas de Pagamento

### **1. Leitor de Cartão (NFC/Bluetooth)**

Para simular leitura de cartão via NFC:

```kotlin
// No seu código, adicione:
<!-- AndroidManifest.xml -->
<uses-permission android:name="android.permission.NFC" />

<!-- build.gradle.kts -->
implementation "androidx.nfc:nfc:1.1.1"
```

Para testar no emulador:
```powershell
# Conectar ao emulador
adb connect emulator-5554

# Enviar comando NFC simulado
adb shell am broadcast -a android.nfc.action.NDEF_DISCOVERED 
```

### **2. Impressora Térmica**

Para simular saída de impressão:

```kotlin
// Salvar em arquivo ao invés de imprimir
val printFile = File(context.filesDir, "receipt.txt")
printFile.writeText("seu conteúdo de impressão aqui")
```

### **3. Teclado Numérico**

O emulador já suporta entrada de teclado normal. Para simular teclado de PIN:

```powershell
# Via adb shell
adb shell input text "123456"
```

### **4. Disco de Batidas (Vibração)**

```kotlin
// Teste de vibração
val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
vibrator.vibrate(100) // 100ms
```

Para testar no emulador:
```powershell
adb shell service call vibrator_manager 1 i64 100
```

---

## 📋 Requisitos Mínimos para Máquinas de Pagamento

```gradle
android {
    compileSdk = 33
    
    defaultConfig {
        minSdk = 24              // Compatibilidade com terminals antigas
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
    }
    
    // Recursos necessários para terminal:
    useLibrary 'android.test.mock'
}

dependencies {
    // Pagamentos
    implementation "com.square.okhttp3:okhttp:4.10.0"
    
    // Comunicação USB (leitura de cartão)
    // implementation "com.felhr:usbserial:6.1.0"
    
    // NFC (cartão contactless)
    // implementation "androidx.nfc:nfc:1.1.1"
    
    // Biometria
    // implementation "androidx.biometric:biometric:1.1.0"
}
```

---

## 🔐 Considerações de Segurança Para Pagamentos

```kotlin
// 1. Nunca armazene dados sensíveis em plain text
preferences.encrypt()

// 2. Use certificados SSL pinning
val certificatePinner = CertificatePinner.Builder()
    .add("seu-dominio.com", "sha256/...")
    .build()

// 3. Implemente proteção com senha/biometria
BiometricPrompt()

// 4. Logs seguros (nunca registre dados de cartão)
Log.d("PAGAMENTO", "Transação iniciada")  // ✅ Correto
Log.d("PAGAMENTO", cardNumber)             // ❌ NUNCA FAÇA ISTO
```

---

## 💻 Atalhos Úteis do Emulador

| Ação | Comando |
|------|---------|
| Print Screen | Ctrl + S |
| Rotacionar | Ctrl + →/← |
| Fechar emulador | Alt + F4 |
| Modo Landscape | Numpad 7 |
| Modo Portrait | Numpad 9 |
| Volume +/- | Numpad + / - |

---

## 🛠️ Troubleshooting

### Problema: "Emulador muito lento"
```powershell
# Usar aceleração de hardware
emulator -avd Terminal_Pagamento -gpu auto -engine auto
```

### Problema: "ADB não encontra dispositivo"
```powershell
# Reiniciar ADB
adb kill-server
adb start-server
adb devices
```

### Problema: "App não instala"
```powershell
# Limpar cache e desinstalar versão anterior
adb shell pm uninstall com.techmania.parkcontrol
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

### Problema: "Erro de permissões"
```powershell
# Conceder permissões manualmente
adb shell pm grant com.techmania.parkcontrol android.permission.CAMERA
```

---

## ✅ Checklist para Desenvolvimento

- [ ] Criar device virtual (5", 1280x720)
- [ ] Configurar API 30+
- [ ] Testar entrada de dados (placa)
- [ ] Testar botões ENTRADA/SAÍDA
- [ ] Testar modo landscape
- [ ] Testar com conexão intermitente (DevTools)
- [ ] Testar com bateria baixa
- [ ] Validar performance em RAM 2GB
- [ ] Testar orientação automática desativada
- [ ] Preparar para produção (Release APK)

---

## 📞 Recomendações Finais

Para **máquina de pagamento real**, use:
- ✅ **Stone Pay**
- ✅ **Ingenico**
- ✅ **Sumup**
- ✅ **SumUp Air**

Cada uma tem SDKs próprios que você pode integrar com a aplicação.

