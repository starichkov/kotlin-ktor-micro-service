#!/bin/bash
set -e

# Configuration
IMAGE_NAME="ktor-app-local"
CONTAINER_NAME="ktor-container-local"
PORT=8080
MAX_RETRIES=10
SLEEP_TIME=2

echo "Building application..."
if [ "$SKIP_TESTS" = "true" ]; then
    echo "Skipping tests as requested..."
    ./gradlew installDist -x test
else
    ./gradlew build installDist
fi

echo "Building Docker image..."
docker build -t "$IMAGE_NAME" .

# Cleanup old container if exists
if docker ps -a --format '{{.Names}}' | grep -q "^$CONTAINER_NAME$"; then
    echo "Removing existing container..."
    docker rm -f "$CONTAINER_NAME"
fi

echo "Starting Docker container..."
docker run -d -p "$PORT:$PORT" --name "$CONTAINER_NAME" "$IMAGE_NAME"

echo "Running health check..."
RETRY_COUNT=0
HEALTHY=false

until [ "$RETRY_COUNT" -ge "$MAX_RETRIES" ]; do
    if curl -s "http://localhost:$PORT/" | grep -q "Hello World!"; then
        echo "Health check passed!"
        HEALTHY=true
        break
    fi
    RETRY_COUNT=$((RETRY_COUNT+1))
    echo "Waiting for app to start... ($RETRY_COUNT/$MAX_RETRIES)"
    sleep "$SLEEP_TIME"
done

if [ "$HEALTHY" = false ]; then
    echo "Health check failed after $MAX_RETRIES retries."
    echo "Container logs:"
    docker logs "$CONTAINER_NAME"
    echo "Cleaning up..."
    docker rm -f "$CONTAINER_NAME"
    exit 1
fi

echo "Shutting down application gracefully..."
curl -s "http://localhost:$PORT/ktor/application/shutdown" || true

# Wait for container to stop
echo "Waiting for container to stop..."
sleep 2

echo "Removing container..."
docker rm -f "$CONTAINER_NAME"

echo "Docker test completed successfully!"
