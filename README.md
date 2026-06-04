# finio-transactions

KMP transactions module for the Finio platform. Encapsulates all transaction logic — API calls, local cache with SQLDelight, and transaction state — published to Maven for consumption by `finio-app`.

## Stack

- **Language**: Kotlin Multiplatform
- **HTTP**: Ktor Client 3.1.3
- **Serialization**: kotlinx.serialization 1.8.1
- **Coroutines**: kotlinx.coroutines 1.10.2
- **Local database**: SQLDelight 2.0.2
- **DI**: Koin 4.0.0
- **Publication**: GitHub Packages (Maven)
- **CI/CD**: Bitrise

## Targets

| Target | Status |
|--------|--------|
| Android | ✅ |
| iOS Arm64 | ✅ |
| iOS Simulator Arm64 | ✅ |

## Module structure

```
shared/src/
  commonMain/
    kotlin/dev/finio/transactions/
      data/
        dto/                          ← API request and response DTOs
        mapper/                       ← DTO → domain model mappers
        remote/                       ← Ktor API calls
        repository/                   ← Repository implementation
      di/                             ← Koin module definition
      domain/
        model/                        ← Transaction, TransactionType, TransactionCategory
        repository/                   ← Repository interface
      presentation/                   ← ViewModel with StateFlow
    sqldelight/dev/finio/transactions/
      Transaction.sq                  ← SQLDelight schema
  androidMain/
    kotlin/dev/finio/transactions/    ← Android SQLDelight driver
  iosMain/
    kotlin/dev/finio/transactions/    ← iOS SQLDelight driver
```

## API endpoints

All endpoints are served by `finio-api` deployed on Railway.

| Method | Route | Description | Auth |
|--------|-------|-------------|------|
| POST | `/transactions` | Create transaction | ✓ |
| GET | `/transactions` | List transactions (with filters) | ✓ |
| GET | `/transactions/summary` | Summary by type | ✓ |
| PUT | `/transactions/:id` | Update transaction | ✓ |
| DELETE | `/transactions/:id` | Delete transaction | ✓ |

## Maven artifacts

Published to GitHub Packages under `dev.finio` group:

| Artifact | Description |
|----------|-------------|
| `finio-transactions-android` | Android AAR |
| `finio-transactions-iosarm64` | iOS Arm64 klib |
| `finio-transactions-iossimulatorarm64` | iOS Simulator Arm64 klib |
| `finio-transactions-kmp` | KMP metadata |

## CI/CD

| Trigger | Workflow | Action |
|---------|----------|--------|
| Push to `main` | `ci` | Compiles Android AAR + iOS Arm64 |
| Any tag (e.g. `1.0.0`) | `release` | Publishes all artifacts to GitHub Packages |

## Build

```bash
# Compile all targets
./gradlew :shared:assemble

# Publish to local Maven (~/.m2)
./gradlew :shared:publishToMavenLocal

# Publish to GitHub Packages (requires GITHUB_ACTOR and GITHUB_TOKEN)
./gradlew :shared:publish
```

## Key versions

```toml
kotlin = "2.3.21"
agp = "9.0.1"
ktor = "3.1.3"
koin = "4.0.0"
sqldelight = "2.0.2"
kotlinx-coroutines = "1.10.2"
kotlinx-serialization = "1.8.1"
```