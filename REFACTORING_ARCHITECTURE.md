# Arquitetura do Park Control

## Visão Geral

O projeto segue **MVVM + Clean Architecture**, organizado em um módulo `core` (compartilhado) e múltiplos
módulos de `features` independentes. O `core` concentra modelos, repositórios, use cases, persistência
(Room + DataStore), navegação e componentes de UI reutilizados por todas as features, evitando acoplamento
direto entre elas.

## Estrutura de Módulos

```
ParkControl/
├── app/
│   └── src/main/java/com/parkcontrol/
│       ├── core/
│       │   ├── data/
│       │   │   ├── database/AppDatabase.kt (Room: clientes mensalistas, veículos, registros de parking, convênios)
│       │   │   └── repository/
│       │   │       ├── ParkingConfigRepositoryImpl.kt (DataStore)
│       │   │       └── ParkingLotInfoRepositoryImpl.kt (DataStore)
│       │   ├── datastore/SettingsDataStore.kt
│       │   ├── di/CoreDependencies.kt (factory/singleton de repositórios e use cases)
│       │   ├── domain/
│       │   │   ├── model/
│       │   │   │   ├── ParkingConfig.kt (preços, tolerância, chave Pix)
│       │   │   │   └── ParkingLotInfo.kt (cadastro do estacionamento)
│       │   │   ├── repository/ (ParkingConfigRepository, ParkingLotInfoRepository)
│       │   │   └── usecase/ (Get/SaveParkingConfigUseCase, Get/SaveParkingLotInfoUseCase)
│       │   ├── navigation/
│       │   │   ├── AppNavigation.kt (NavHost com todas as rotas do app)
│       │   │   ├── AppRoutes.kt (rotas tipadas por feature)
│       │   │   └── AppDrawerScaffold.kt (menu lateral/drawer)
│       │   ├── ui/
│       │   │   ├── common/BrazilianStates.kt
│       │   │   ├── masks/ (Currency, Phone, Plate, ZipCode)
│       │   │   └── theme/ (Color, Theme, Type)
│       │   └── utils/
│       │       ├── EmailValidation.kt
│       │       ├── PixPayloadBuilder.kt (payload Pix "Copia e Cola" com CRC16)
│       │       └── QrCodeGenerator.kt
│       │
│       ├── features/
│       │   ├── home/ui/HomeScreen.kt (tela inicial/dashboard)
│       │   ├── parking/ (registro de entrada/saída, cálculo de preço, pagamento via Pix e ticket em QR-Code)
│       │   ├── parkingLot/ (cadastro dos dados do estacionamento)
│       │   ├── settings/ (preços, tolerância e chave Pix)
│       │   ├── monthlyCustomers/ (clientes mensalistas e seus veículos — Room)
│       │   ├── agreements/ (convênios/descontos — Room)
│       │   └── about/ui/AboutScreen.kt
│       │
│       └── MainActivity.kt
```

## Clean Architecture Grid

| Camada           | Responsabilidade                                                      | Exemplo                                                                                                 |
| ---------------- | ---------------------------------------------------------------------| -------------------------------------------------------------------------------------------------------|
| **Domain**       | Entidades, interfaces, regras de negócio                              | `ParkingConfig`, `ParkingLotInfo`, `Agreement`, `MonthlyCustomer`, repositórios (interfaces), use cases |
| **Data**         | Implementações, persistência (Room/DataStore), mappers entity↔domain  | `ParkingConfigRepositoryImpl`, `AgreementRepositoryImpl`, `MonthlyCustomerRepositoryImpl`, `AppDatabase` |
| **Presentation** | UI, ViewModels, estado, navegação                                     | `ParkingViewModel`, `SettingsViewModel`, `ActiveAgreementsScreen`, `AppNavigation`                      |

## Persistência

Duas estratégias convivem no projeto, conforme a natureza do dado:

- **Jetpack DataStore (Preferences)**: usado para configurações simples e singleton (um único registro),
  como `ParkingConfig` e `ParkingLotInfo`.
- **Room (SQLite)**: usado para dados relacionais/listas que crescem com o uso — `MonthlyCustomerEntity`,
  `CustomerVehicleEntity`, `ParkingRecordEntity` e `AgreementEntity`, todos centralizados em `AppDatabase`
  (`core/data/database/AppDatabase.kt`). Cada feature expõe seu próprio DAO, mapper (`*Mapper.kt`) e
  repositório, mas a instância do banco é única e gerenciada por `CoreDependencies`.

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
DataStore
```

Os valores de `ParkingConfig` são observados em tempo real via `Flow<ParkingConfig>`.

### Cenário 2: Feature Settings Escreve Configuração (Write)

```
SettingsViewModel
    ↓ (usa)
SaveParkingConfigUseCase (de core)
    ↓ (chama)
ParkingConfigRepository (interface)
    ↓ (implementação)
ParkingConfigRepositoryImpl
    ↓ (escreve em)
DataStore
```

### Cenário 3: Feature com Dados Relacionais (ex.: Convênios, Clientes Mensalistas)

```
ActiveAgreementsViewModel / MonthlyCustomerViewModel
    ↓ (usa)
Use Cases da própria feature (Get/Save/Update/Delete/Activate/Inactivate)
    ↓ (chama)
Repository (interface, na própria feature)
    ↓ (implementação)
*RepositoryImpl (usa DAO + Mapper)
    ↓ (persiste em)
AppDatabase (Room), instância compartilhada via CoreDependencies
```

### Cenário 4: Pagamento Pix na Saída do Veículo

```
ParkingViewModel.registrarSaida()
    ↓
RegisterParkingExitUseCase (calcula valor final, aplicando convênio/desconto manual)
    ↓
PixPayloadBuilder (monta payload BR Code/EMV com chave Pix de ParkingConfig + CRC16)
    ↓
QrCodeGenerator (gera bitmap do QR-Code)
    ↓
PixQrCodeDialog (exibe QR-Code + valor + chave Pix para o cliente escanear)
```

Funcionalidade 100% offline, sem gateway de pagamento ou servidor externo.

## Principais Componentes

### 1. **ParkingConfig** (`core/domain/model/`)
Modelo de configuração compartilhado entre `settings` e `parking`:

```kotlin
data class ParkingConfig(
    val first30MinutesPrice: Double = 5.00,
    val pricePerHour: Double = 7.00,
    val toleranceMinutes: Int = 15,
    val pixKey: String = ""
)
```

### 2. **ParkingLotInfo** (`core/domain/model/`)
Cadastro único do estabelecimento (nome, endereço, telefone, total de vagas), usado pela feature
`parkingLot` e exibido/compartilhado por outras telas (ex.: ticket de saída).

### 3. **Repositórios e Use Cases de `core`**
Interfaces (`ParkingConfigRepository`, `ParkingLotInfoRepository`) com implementações baseadas em
DataStore, expostas via use cases (`GetParkingConfigUseCase`, `SaveParkingConfigUseCase`,
`GetParkingLotInfoUseCase`, `SaveParkingLotInfoUseCase`). Qualquer feature pode reutilizá-los sem
depender da implementação concreta.

### 4. **AppDatabase** (`core/data/database/`)
Banco Room único do app, reunindo as entidades de todas as features que precisam de persistência
relacional (`MonthlyCustomerEntity`, `CustomerVehicleEntity`, `ParkingRecordEntity`, `AgreementEntity`).
Garante índices únicos de telefone/e-mail para clientes mensalistas via callback de criação/abertura.

### 5. **CoreDependencies** (`core/di/`)
Factory central (singleton manual, sem Hilt) que cria e reutiliza instâncias de `AppDatabase`,
repositórios e use cases — de `core` e das features — para todas as ViewModels:

```kotlin
object CoreDependencies {
    fun getParkingConfigRepository(context: Context): ParkingConfigRepository
    fun getParkingLotInfoRepository(context: Context): ParkingLotInfoRepository
    fun getMonthlyCustomerRepository(context: Context): MonthlyCustomerRepository
    fun getCustomerVehicleRepository(context: Context): CustomerVehicleRepository
    fun getParkingRepository(context: Context): ParkingRepository
    fun getAgreementRepository(context: Context): AgreementRepository
    fun createGetParkingConfigUseCase(context: Context): GetParkingConfigUseCase
    fun createSaveParkingConfigUseCase(context: Context): SaveParkingConfigUseCase
    // ...demais factories de use cases por feature
}
```

### 6. **Navegação** (`core/navigation/`)
- `AppRoutes`: rotas tipadas (sealed class) por feature, incluindo rotas com argumentos
  (ex.: `MonthlyCustomerForm`, `CustomerVehicles`, `AgreementForm`).
- `AppNavigation`: `NavHost` único com todos os `composable()`; distingue navegação vinda do drawer
  (`navigateFromDrawer`, usa `popUpTo`/`saveState`/`restoreState` para não empilhar instâncias
  duplicadas de uma mesma tela) de navegação "para frente" (`navigateForward`, ex.: abrir um
  formulário a partir de uma listagem).
- `AppDrawerScaffold`: menu lateral (drawer) com os itens de navegação do app.

### 7. **Pix e QR-Code** (`core/utils/`, `features/parking/ui/`)
- `PixPayloadBuilder`: monta o payload Pix "Copia e Cola" (BR Code/EMV) com cálculo de CRC16.
- `QrCodeGenerator`: gera o bitmap do QR-Code a partir do payload.
- `PixQrCodeDialog` e `TicketQrCodeDialog` (feature `parking`): exibem o QR-Code de pagamento Pix e o
  ticket de saída, respectivamente.

### 8. **Features com persistência própria (Room)**
- **`monthlyCustomers`**: clientes mensalistas e seus veículos (`MonthlyCustomerEntity`,
  `CustomerVehicleEntity`), com use cases de CRUD + ativar/inativar cliente.
- **`agreements`**: convênios/descontos aplicáveis no cálculo do preço de saída, com use cases de
  CRUD + ativar/inativar.
- **`parking`**: registros de entrada/saída (`ParkingRecordEntity`), cálculo de preço
  (`CalculateParkingPriceUseCase`), checagem de veículo já estacionado
  (`CheckVehicleActiveParkingUseCase`) e registro de saída (`RegisterParkingExitUseCase`).

Cada uma dessas features segue o mesmo padrão: `domain` (model, repository interface, use cases) e
`data` (entity, dao, mapper, repository impl), com a instância do `AppDatabase` fornecida por
`CoreDependencies`.

## Como Usar em Uma Nova Feature

### Reutilizar algo de `core` (ex.: ler `ParkingConfig`):

```kotlin
class MinhaFeatureViewModel(application: Application) : AndroidViewModel(application) {
    private val getParkingConfigUseCase by lazy {
        CoreDependencies.createGetParkingConfigUseCase(application)
    }

    init {
        viewModelScope.launch {
            getParkingConfigUseCase().collect { config ->
                // Use os valores: config.first30MinutesPrice, config.pricePerHour, config.pixKey
            }
        }
    }
}
```

### Criar uma nova feature com persistência própria (padrão Room):

1. Defina `domain/model`, `domain/repository` (interface) e `domain/usecase` na feature.
2. Defina `data/local/entity`, `data/local/dao`, `data/local/mapper` e `data/repository/*Impl`.
3. Adicione a entidade e o DAO em `AppDatabase` (`core/data/database/AppDatabase.kt`).
4. Exponha o repositório e os use cases em `CoreDependencies`.
5. Adicione a rota em `AppRoutes` e o `composable()` em `AppNavigation`.

## Benefícios da Arquitetura

✅ **Separação de Responsabilidades**: cada feature cuida de sua UI/domain; o que é compartilhado vive em `core`

✅ **Sem Acoplamento entre Features**: Parking, Settings, Agreements e Monthly Customers não dependem uma da outra

✅ **Single Source of Truth**: um único `AppDatabase` (Room) e repositórios únicos de `core` (DataStore)

✅ **Testabilidade**: use cases e repositórios podem ser testados isoladamente, independente da UI

✅ **Reutilização**: qualquer feature pode injetar os mesmos repositórios/use cases via `CoreDependencies`

✅ **Manutenibilidade**: mudanças de schema/modelo ficam centralizadas (`ParkingConfig`, `AppDatabase`)

## Próximos Passos (Melhorias Futuras)

1. **Adicionar Hilt para DI**: remover `CoreDependencies` manual e usar anotações `@Inject`
2. **Módulos Gradle Separados**: separar `core` e cada feature em sub-módulos Gradle
   (`:core`, `:feature-parking`, `:feature-settings`, `:feature-agreements`, `:feature-monthlyCustomers`)
3. **Testes Unitários**: ampliar cobertura de testes para use cases e repositórios
4. **Migrações de Banco**: substituir `fallbackToDestructiveMigration` por migrações versionadas do Room
5. **Erro Handling**: implementar sealed classes para tratamento de erros nos repositórios
6. **Caching**: adicionar estratégia de cache em memória onde fizer sentido

---

**Última atualização:** Novembro 2026
**Arquitetura:** MVVM + Clean Architecture
**Persistência:** Jetpack DataStore (Preferences) + Room (SQLite)
**Async:** Coroutines + Flow
**Navegação:** Jetpack Navigation Compose

