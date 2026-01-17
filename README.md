[![GitHub Workflow Status](https://img.shields.io/github/actions/workflow/status/starichkov/kotlin-ktor-micro-service/gradle.yaml?style=for-the-badge)](https://github.com/starichkov/kotlin-ktor-micro-service/actions/workflows/gradle.yaml)
[![codecov](https://img.shields.io/codecov/c/github/starichkov/kotlin-ktor-micro-service?style=for-the-badge)](https://app.codecov.io/github/starichkov/kotlin-ktor-micro-service)
[![GitHub license](https://img.shields.io/github/license/starichkov/kotlin-ktor-micro-service?style=for-the-badge)](https://github.com/starichkov/kotlin-ktor-micro-service/blob/main/LICENSE.md)

# kotlin-ktor-micro-service

## Technical information

| Piece of tech | Version |
|---------------|---------|
| Kotlin        | 2.3.x   |
| Ktor          | 3.3.x   |
| Gradle        | 9.3.x   |
| JVM           | 25      |     

## Migration guides

Official migration guide to [Ktor 3](https://ktor.io/docs/migrating-3.html).

### Gradle

#### How to update a Gradle Wrapper version

```shell
./gradlew wrapper --gradle-version 9.3.0
```

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

## About TemplateTasks

TemplateTasks is a personal software development initiative by Vadim Starichkov, focused on sharing open-source libraries, services, and technical demos.

It operates independently and outside the scope of any employment.

All code is released under permissive open-source licenses. The legal structure may evolve as the project grows.

## License & Attribution

This project is licensed under the **MIT License** - see the [LICENSE](https://github.com/starichkov/kotlin-ktor-micro-service/blob/main/LICENSE.md) file for details.

### Using This Project?

If you use this code in your own projects, attribution is required under the MIT License:

```
Based on kotlin-ktor-micro-service by Vadim Starichkov, TemplateTasks

https://github.com/starichkov/kotlin-ktor-micro-service
```

**Copyright © 2026 Vadim Starichkov, TemplateTasks**
