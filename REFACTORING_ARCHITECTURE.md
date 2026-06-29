# Arquitetura Refatorizada - Repositório Compartilhado

## Visão Geral

O projeto foi refatorado para implementar o padrão **repositório compartilhado em `core`** para gerenciar a configuração de estacionamento (ParkingConfig), permitindo que múltiplas features (`settings` e `parking`) acessem os mesmos dados sem acoplamento direto.

## Estrutura de Módulos

```
ParkControl/
├── app/
│   └── src/main/java/com/parkcontrol/
│       ├── core/
│       │   ├── domain/
│       │   │   ├── model/
│       │   │   │   └── ParkingConfig.kt (configuração compartilhada)
│       │   │   ├── repository/
│       │   │   │   └── ParkingConfigRepository.kt (interface)
│       │   │   └── usecase/
│       │   │       ├── GetParkingConfigUseCase.kt (leitura)
│       │   │       └── SaveParkingConfigUseCase.kt (escrita)
│       │   ├── data/
│       │   │   └── repository/
│       │   │       └── ParkingConfigRepositoryImpl.kt (implementação com DataStore)
│       │   ├── di/
│       │   │   └── CoreDependencies.kt (factory de dependências)
│       │   └── datastore/
│       │       └── SettingsDataStore.kt (abstraçãodatastore)
│       │
│       ├── features/
│       │   ├── parking/
│       │   │   ├── domain/
│       │   │   │   ├── model/ParkingModels.kt (usa ParkingConfig de core)
│       │   │   │   └── usecase/CalculateParkingPriceUseCase.kt
│       │   │   └── ui/
│       │   │       └── ParkingViewModel.kt (consome GetParkingConfigUseCase)
│       │   │
│       │   └── settings/
│       │       ├── domain/
│       │       │   └── usecase/SettingsInteractor.kt (orquestra use cases de core)
│       │       └── ui/
│       │           ├── SettingsViewModel.kt (gerencia injeção de core)
│       │           └── SettingsScreen.kt
```

## Clean Architecture Grid

| Camada | Responsabilidade | Exemplo |
|--------|-----------------|---------|
| **Domain** | Entidades, interfaces, regras de negócio | `ParkingConfig`, `ParkingConfigRepository` (interface), `GetParkingConfigUseCase` |
| **Data** | Implementações, persistência, DataStore | `ParkingConfigRepositoryImpl`, `SettingsLocalDataSourceImpl` |
| **Presentation** | UI, ViewModels, estado | `ParkingViewModel`, `SettingsViewModel`, Composables |

## Fluxo de Dados

### Cenário 1: Feature Parking Lê Configuração (Read)

```
ParkingViewModel
    ↓ (usa)
GetParkingConfigUseCase (de core)
    ↓ (chama)
ParkingConfigRepository (interface, de core/domain)
    ↓ (implementação em)
ParkingConfigRepositoryImpl (de core/data)
    ↓ (persiste em)
DataStore (via SettingsLocalDataSourceImpl)
```

Os valores de `ParkingConfig` são observados em tempo real via `Flow<ParkingConfig>`.

### Cenário 2: Feature Settings Escreve Configuração (Write)

```
SettingsViewModel
    ↓ (usa)
SettingsInteractor
    ↓ (chama)
SaveParkingConfigUseCase (de core)
    ↓ (chama)
ParkingConfigRepository (interface)
    ↓ (implementação)
ParkingConfigRepositoryImpl
    ↓ (escreve em)
DataStore
```

## Principais Componentes

### 1. **ParkingConfig** (`core/domain/model/`)
Modelo de dados unificado compartilhado entre features:

```kotlin
data class ParkingConfig(
    val first30MinutesPrice: Double = 5.00,
    val pricePerHour: Double = 7.00,
    val toleranceMinutes: Int = 15
)
```

### 2. **ParkingConfigRepository** (`core/domain/repository/`)
Interface que define contratos para acesso à configuração:

```kotlin
interface ParkingConfigRepository {
    fun observeParkingConfig(): Flow<ParkingConfig>
    suspend fun saveParkingConfig(config: ParkingConfig)
    suspend fun saveFirst30MinutesPrice(price: Double)
    suspend fun saveHourlyRate(rate: Double)
    suspend fun saveToleranceMinutes(minutes: Int)
}
```

### 3. **ParkingConfigRepositoryImpl** (`core/data/repository/`)
Implementação real usando DataStore para persistência:

```kotlin
class ParkingConfigRepositoryImpl(context: Context) : ParkingConfigRepository {
    // Lê do DataStore e emite mudanças via Flow
    // Escreve valores individuais ou completos no DataStore
}
```

### 4. **Use Cases** (`core/domain/usecase/`)

- **GetParkingConfigUseCase**: Expõe o repositório como observable stream
- **SaveParkingConfigUseCase**: Permite salvar configuração da parking

Ambos são compartilhados e podem ser usados por qualquer feature.

### 5. **CoreDependencies** (`core/di/`)
Factory simples para criação de instâncias compartilhadas (singleton pattern):

```kotlin
object CoreDependencies {
    fun getParkingConfigRepository(context: Context): ParkingConfigRepository
    fun createGetParkingConfigUseCase(context: Context): GetParkingConfigUseCase
    fun createSaveParkingConfigUseCase(context: Context): SaveParkingConfigUseCase
}
```

## Como Usar em Uma Nova Feature

Se outra feature precisar acessar a configuração de parking:

### Leitura (como em Parking):

```kotlin
class MuvaFeatureViewModel(application: Application) : AndroidViewModel(application) {
    private val getParkingConfigUseCase by lazy {
        CoreDependencies.createGetParkingConfigUseCase(application)
    }
    
    init {
        viewModelScope.launch {
            getParkingConfigUseCase().collect { config ->
                // Use os valores: config.first30MinutesPrice, config.pricePerHour, etc
            }
        }
    }
}
```

### Escrita (como em Settings):

```kotlin
class NovaFeatureInteractor(
    getParkingConfigUseCase: GetParkingConfigUseCase,
    saveParkingConfigUseCase: SaveParkingConfigUseCase
) {
    fun salvarConfig(novoPreco: Double) {
        // Use saveParkingConfigUseCase para persistir
    }
}
```

## Benefícios da Refatoração

✅ **Separação de Responsabilidades**: Cada feature é responsável por sua UI/domain, aquivo compartilhado em core

✅ **Sem Acoplamento entre Features**: Parking e Settings não dependem uma da outra

✅ **Single Source of Truth**: Um único repositório gerencia ParkingConfig

✅ **Testabilidade**: Use cases e repositório podem ser testados isoladamente

✅ **Reutilização**: Qualquer feature pode injetar o mesmo repositório

✅ **Manutenibilidade**: Se a estrutura de dados mudar, muda em um lugar (ParkingConfig em core)

## Próximos Passos (Melhorias Futuras)

1. **Adicionar Hilt para DI**: Remover CoreDependencies manual e usar anotações @Inject
2. **Módulos Gradle Separados**: Separar core, parking, settings em sub-modules `:core`, `:feature-parking`, `:feature-settings`
3. **Testes Unitários**: Adicionar testes para use cases e repositório em core
4. **Erro Handling**: Implementar sealed classes para tratamento de erros
5. **Caching**: Adicionar estratégia de cache em memória no repositório

## Migração de Código Antigo

- ❌ Antes: `ParkingViewModel` acessava `SettingsInteractor` direto
- ✅ Depois: `ParkingViewModel` injeta `GetParkingConfigUseCase` de core

- ❌ Antes: `ParkingConfig` estava em `features/parking/domain/model`
- ✅ Depois: `ParkingConfig` centralizado em `core/domain/model`

---

**Data:** Junho 2026  
**Arquitetura:** MVVM + Clean Architecture  
**Persistent Storage:** Jetpack DataStore (Preferences)  
**Async:** Coroutines + Flow

