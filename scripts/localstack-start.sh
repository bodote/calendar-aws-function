#!/bin/bash

# Start LocalStack with Docker Compose
docker-compose up -d localstack

# Wait for LocalStack to be ready
echo "Waiting for LocalStack to start..."
until curl -s http://localhost:4566/_localstack/health > /dev/null 2>&1; do
    echo "LocalStack not ready yet, waiting..."
    sleep 2
done

echo "LocalStack is ready!"

echo "LocalStack S3 setup complete!"
echo "Note: S3 bucket will be created automatically by the application when needed."