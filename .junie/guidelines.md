# Project Guidelines

## Project Overview
This is a Kotlin microservice built using the Ktor framework. It follows a standard layered architecture with routes, services, and models.

### Tech Stack
- **Language**: Kotlin 2.3.x (API version 2.3)
- **Framework**: Ktor 3.3.x
- **Build System**: Gradle 9.3.x (with Kotlin DSL)
- **Runtime**: JVM 25 (Java Toolchain configured)
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

## Build and Configuration
### Prerequisites
- JDK 25 is required to build and run the project.

### Build Commands
- **Assemble project**: `./gradlew assemble`
- **Full build and test**: `./gradlew build`
- **Clean project**: `./gradlew clean`
- **Run application**: `./gradlew run` or execute the `main` function in `Application.kt`. The server starts on port `8080`.

### Configuration
- Dependency versions and build settings are managed in `gradle.properties`.
- Logging is configured in `src/main/resources/logback.xml`.

## Testing Information
### Running Tests
- Execute all tests: `./gradlew test`
- Execute tests with coverage check: `./gradlew check`
- JaCoCo reports are generated in `build/reports/jacoco/test/html/index.html`.

### Adding New Tests
When adding new tests, use Ktor's `testApplication` to set up the environment.

#### Example Test
The following is a simple test case demonstrating how to test a route:

```kotlin
class DemoTest {
    @Test
    fun testHealthCheck() = testApplication {
        application {
            configureRouting()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Hello World!", bodyAsText())
        }
    }
}
```

### Coverage Requirements
The project enforces strict JaCoCo coverage limits:
- Instructions: 83%
- Branches: 89%
Failure to meet these thresholds will cause the `./gradlew check` task to fail.

## Development Guidelines
- **Code Style**: Follow the [Official Kotlin Style Guide](https://kotlinlang.org/docs/coding-conventions.html). This is enforced by `kotlin.code.style=official` in `gradle.properties`.
- **Architecture**:
    - **Services**: Contain business logic and should be independent of Ktor-specific classes where possible.
    - **Routes**: Define endpoints and map request/response data. Manual dependency injection is performed in `plugins/Routing.kt`.
    - **Models**: Simple Kotlin data classes. Use `@Serializable` for classes used in API requests/responses.
- **API Changes**:
    - Update relevant `.http` files in `src/test/resources/tests/` when modifying or adding endpoints.
- **Continuous Integration**:
    - The project uses GitHub Actions (defined in `.github/workflows/gradle.yaml`) for CI/CD, running build and tests on every push.
