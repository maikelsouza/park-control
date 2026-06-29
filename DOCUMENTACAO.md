# ParkControl - Sistema de Estacionamento

## 📱 Visão Geral
Aplicativo Android desenvolvido em Kotlin com Jetpack Compose para gerenciar entrada e saída de veículos em um estacionamento.

## 🏗️ Estrutura do Projeto

### Pacotes Criados

#### 1. `model/` - Modelos de Dados
- **ParkingModels.kt**: Define as data classes utilizadas na aplicação
  - `ParkingRecord`: Armazena informações de um registro de estacionamento
    - `licensePlate`: Placa do veículo
    - `entryTime`: Horário de entrada
    - `exitTime`: Horário de saída (opcional)
    - `status`: Status do veículo (ESTACIONADO ou FINALIZADO)
  - `ParkingConfig`: Configurações de preço do estacionamento
    - `first30MinutesPrice`: Valor para os primeiros 30 minutos
    - `pricePerHour`: Valor por hora após 30 minutos

#### 2. `viewmodel/` - Gerenciamento de Estado
- **ParkingViewModel.kt**: ViewModel que gerencia o estado da aplicação
  - Controla a lista de registros de estacionamento
  - Gerencia as configurações de preço
  - Fornece funções para entrada e saída de veículos

#### 3. `ui/screens/` - Telas da Aplicação
- **ParkingScreen.kt**: Componente raiz que define as abas
  - **ParkingEntryScreen**: Tela de entrada/saída (primeira aba)
    - Campo para inserir placa do veículo
    - Display visual simulando uma placa Brazilian
    - Botões de ENTRADA (azul) e SAÍDA (verde)
    - Exibição do último registro
  - **ConfigurationScreen**: Tela de configurações de valores (segunda aba)
    - Campos para configurar preços
    - Resumo dos valores
    - Botão para salvar configurações

## 🎨 Recursos Visuais

### Aba 1: Estacionamento
- **Campo de Entrada**: Digite a placa do veículo
- **Display da Placa**: Visualização em tempo real da placa formatada
- **Botões de Ação**:
  - 🚗 ENTRADA (Azul): Registra entrada do veículo
  - 🚗 SAÍDA (Verde): Registra saída do veículo
- **Último Registro**: Mostra informações do último registro (placa, data/hora, status)

### Aba 2: Configurações
- **Primeiros 30 Minutos**: Campo para definir valor fixo (padrão R$ 5,00)
- **Após a Primeira Hora**: Campo para definir valor por hora (padrão R$ 7,00)
- **Resumo**: Visualização dos valores configurados
- **Botão Salvar**: Persiste as configurações

## 💻 Funcionalidades Principais

### Registro de Entrada
1. Digite a placa do veículo
2. Clique em "ENTRADA"
3. O sistema registra automaticamente a data e hora

### Registro de Saída
1. Clique em "SAÍDA" para registrar saída do último veículo
2. O sistema registra automaticamente a data e hora de saída

### Gestão de Configurações
1. Acesse a aba "Configurações de Valores"
2. Modifique os valores desejados
3. Clique em "SALVAR CONFIGURAÇÕES" para aplicar

## 🔧 Dependências Principais

- **Jetpack Compose**: Framework UI moderno para Android
- **Lifecycle ViewModel**: Gerenciamento de estado
- **Material Design 3**: Design system atualizado
- **Java Time API**: Para manipulação de datas e horas

## 📋 Requisitos do Sistema

- **Android API Nível 26+** (Android 8.0)
- **Kotlin 2.2.10**
- **Gradle 9.4.1**

## 🚀 Como Executar

```bash
# Compilar o projeto
./gradlew build

# Executar no emulador/dispositivo
./gradlew installDebug
```

## 📝 Formato de Data/Hora

As datas e horas são exibidas no formato brasileiro: `dd/MM/yyyy HH:mm`

Exemplo: `25/05/2026 09:15`

## 🎯 Funcionalidades Futuras Possíveis

- [ ] Persistência de dados em banco de dados local (Room)
- [ ] Histórico completo de registros
- [ ] Cálculo automático de valores a pagar
- [ ] Relatórios de uso
- [ ] Sincronização em nuvem
- [ ] Autenticação de usuários
- [ ] Listagem de todos os veículos estacionados

## 📱 Próximos Passos

- [ ] Finalizar a gestão de clientes por status:
  - Clientes ativos
  - Clientes inativos

- [ ] Adicionar filtros na listagem de clientes:
  - Pesquisa por placa
  - Pesquisa por nome

