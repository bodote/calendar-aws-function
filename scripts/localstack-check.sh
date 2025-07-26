#!/bin/bash

echo "=== LocalStack Status ==="
docker-compose ps localstack

echo ""
echo "=== LocalStack Health ==="
curl -s http://localhost:4566/_localstack/health | jq '.' 2>/dev/null || curl -s http://localhost:4566/_localstack/health

echo ""
echo "=== S3 Buckets ==="
aws --endpoint-url=http://localhost:4566 s3 ls

echo ""
echo "=== S3 Bucket Contents (de.bas.bodo) ==="
aws --endpoint-url=http://localhost:4566 s3 ls s3://de.bas.bodo --recursive