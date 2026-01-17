FROM eclipse-temurin:25-jre-noble
WORKDIR /app

# Copy the pre-built application from the build directory
COPY build/install/kotlin-ktor-micro-service /app

# Expose the port the app runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["/app/bin/kotlin-ktor-micro-service"]
