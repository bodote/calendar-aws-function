#!/bin/bash

echo "Cleaning up LocalStack data..."

# Stop LocalStack
docker-compose down

# Remove LocalStack data volume
rm -rf ./tmp/localstack

echo "LocalStack cleanup complete!"