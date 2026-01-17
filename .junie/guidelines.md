# Project Guidelines

## Project Overview
This is a Kotlin microservice built using the Ktor framework. It follows a standard layered architecture with routes, services, and models.

### Tech Stack
- **Language**: Kotlin 2.3.x
- **Framework**: Ktor 3.3.x
- **Build System**: Gradle 9.3.x
- **Runtime**: JVM 25
- **Server Engine**: Netty
- **Serialization**: Kotlinx Serialization (JSON)
- **Testing**: JUnit, Ktor Test Host
- **Coverage**: JaCoCo (Target: 83% instructions, 89% branches)

### Project Structure
- `src/main/kotlin/com/templatetasks/kotlin/ktor/`: Base package
    - `Application.kt`: Application entry point and module configuration.
    - `api/`: API response models and HTTP extensions.
    - `models/`: Data classes representing domain entities (e.g., `Customer`, `Order`).
    - `plugins/`: Ktor plugins for administration, routing, and serialization.
    - `routes/`: Definitions of API endpoints.
    - `service/`: Business logic layer.
- `src/main/resources/`: Configuration files (e.g., `logback.xml`).
- `src/test/kotlin/`: Unit and integration tests.
- `src/test/resources/tests/`: `.http` files for manual testing and API documentation.

## Development Guidelines
- **Code Style**: Follow standard Kotlin coding conventions.
- **Testing**:
    - Always add unit tests for new services and routes.
    - Run tests using `./gradlew test`.
    - Ensure JaCoCo coverage remains above the defined thresholds.
- **Building**:
    - Build the project using `./gradlew build`.
- **API Changes**:
    - Update relevant `.http` files in `src/test/resources/tests/` when modifying or adding endpoints.
