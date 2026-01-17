[![GitHub Workflow Status](https://img.shields.io/github/actions/workflow/status/starichkov/kotlin-ktor-micro-service/gradle.yaml?style=for-the-badge)](https://github.com/starichkov/kotlin-ktor-micro-service/actions/workflows/gradle.yaml)
[![codecov](https://img.shields.io/codecov/c/github/starichkov/kotlin-ktor-micro-service?style=for-the-badge)](https://app.codecov.io/github/starichkov/kotlin-ktor-micro-service)
[![GitHub license](https://img.shields.io/github/license/starichkov/kotlin-ktor-micro-service?style=for-the-badge)](https://github.com/starichkov/kotlin-ktor-micro-service/blob/main/LICENSE.md)

# Kotlin Ktor Microservice

A demonstration of a Kotlin microservice built with the Ktor framework, showcasing a clean, layered architecture and modern development practices.

## Technical Information

| Piece of Tech | Version |
|---------------|---------|
| Kotlin        | 2.3.x   |
| Ktor          | 3.3.x   |
| Gradle        | 9.3.x   |
| JVM           | 25      |

## Requirements

- **JDK 25**: The project is configured to use Java 25 via Gradle Toolchain.
- **Docker**: (Optional) Required for building and running the application in a containerized environment.

## Setup and Run

### Running Locally

To start the application locally using Gradle:

```shell
./gradlew run
```

The server starts on port `8080`.

### Building the Project

- **Assemble**: `./gradlew assemble`
- **Install Distribution**: `./gradlew installDist` (creates an executable in `build/install/kotlin-ktor-micro-service`)

## Docker

You can build and run the application using Docker.

### Building and Running Locally

To build the Docker image locally, first ensure you have the application distribution ready:

```shell
./gradlew installDist
docker build -t ktor-app .
```

To run the container:

```shell
docker run -d -p 8080:8080 --name ktor-container ktor-app
```

To shut down the application gracefully and remove the container:

```shell
curl -s http://localhost:8080/ktor/application/shutdown
docker rm -f ktor-container
```

### Testing the Docker Setup

A script is provided to automate the build, run, health check, and shutdown process:

```shell
./scripts/test-docker.sh
```

You can skip the Gradle tests by setting `SKIP_TESTS=true`:

```shell
SKIP_TESTS=true ./scripts/test-docker.sh
```

## Scripts

- `./gradlew build`: Full build, including tests and coverage verification.
- `./gradlew test`: Runs unit and integration tests.
- `./gradlew check`: Executes all verification tasks, including JaCoCo coverage checks.
- `./gradlew wrapper --gradle-version 9.3.0`: Updates the Gradle Wrapper version.
- `./scripts/test-docker.sh`: Automates Docker build and verification.

## Environment Variables

- `JAVA_HOME`: Path to the JDK 25 installation (if not auto-detected).
- `SKIP_TESTS`: Used by `test-docker.sh` to bypass the test phase during build.

## API Endpoints

- `GET /`: Health check / Welcome message.
- `GET /json/kotlinx-serialization`: Example JSON response.
- `GET /customer`: List all customers.
- `GET /customer/{id}`: Get a specific customer.
- `POST /customer`: Create a new customer.
- `DELETE /customer/{id}`: Delete a customer.
- `GET /order`: List all orders.
- `GET /order/{number}`: Get a specific order.
- `GET /order/{number}/total`: Get the total for a specific order.
- `POST /order`: Create a new order.
- `DELETE /order/{number}`: Delete an order.
- `GET /ktor/application/shutdown`: Gracefully shut down the application.

Manual testing files (`.http`) are available in `src/test/resources/tests/`.

## Testing and Coverage

The project uses JUnit and Ktor Test Host for testing. JaCoCo is used to enforce code coverage.

**Coverage Thresholds:**
- Instructions: **83%**
- Branches: **89%**

To run tests and generate a coverage report:
```shell
./gradlew check
```
Reports are generated at `build/reports/jacoco/test/html/index.html`.

## Project Structure

```text
.
├── Dockerfile                  # Docker image configuration
├── build.gradle.kts           # Gradle build configuration
├── gradle.properties           # Version and property definitions
├── scripts/                    # Utility and automation scripts
│   └── test-docker.sh         # Docker verification script
├── src/
│   ├── main/
│   │   ├── kotlin/             # Application source code
│   │   │   └── com/templatetasks/kotlin/ktor/
│   │   │       ├── Application.kt      # Application entry point
│   │   │       ├── api/                # API response models and extensions
│   │   │       ├── models/             # Domain entities
│   │   │       ├── plugins/            # Ktor plugins configuration
│   │   │       ├── routes/             # API route definitions
│   │   │       └── service/            # Business logic implementation
│   │   └── resources/          # Configuration files (e.g., logback.xml)
│   └── test/
│       ├── kotlin/             # Unit and integration tests
│       └── resources/          # Test resources and .http files
└── LICENSE.md                  # Project license
```

## About TemplateTasks

TemplateTasks is a personal software development initiative by Vadim Starichkov, focused on sharing open-source libraries, services, and technical demos.

It operates independently and outside the scope of any employment.

All code is released under permissive open-source licenses. The legal structure may evolve as the project grows.

## License & Attribution

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE.md) file for details.

### Using This Project?

If you use this code in your own projects, attribution is required under the MIT License:

```text
Based on kotlin-ktor-micro-service by Vadim Starichkov, TemplateTasks
https://github.com/starichkov/kotlin-ktor-micro-service
```

**Copyright © 2026 Vadim Starichkov, TemplateTasks**
