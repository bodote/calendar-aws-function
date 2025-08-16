# Local Lambda Development with SAM + LocalStack

## Current Status

✅ **Working:**
- LocalStack S3 service running in Docker
- SAM CLI installed and building Lambda packages
- Spring Boot app connecting to LocalStack successfully
- Lambda function starting and Spring Boot initializing

## Notes

The setup now successfully connects the Lambda container to LocalStack over the shared Docker network. If you still observe cold-starts during local invocation, see the `--warm-containers` guidance below.
## What We're Trying to Accomplish

Run the Woodle Spring Boot application as a Lambda function locally using:
- **SAM Local** - AWS Lambda emulation environment
- **LocalStack** - AWS S3 emulation for storage
- **Docker Compose** - Container orchestration

This setup would allow testing the exact Lambda deployment configuration locally before deploying to AWS.

## Current Architecture

```
Host Machine
├── LocalStack Container (port 4566) - S3 emulation
├── SAM Lambda Container (port 3000) - Spring Boot app
└── Docker Network: calendar-aws-function_default
```

@@

## Files Involved

- `template.yml` - SAM Lambda function definition
- `env.json` - Environment variables for local Lambda
- `docker-compose.yml` - LocalStack container setup
- `application.yml` - Spring Boot S3 configuration with @Value injection
- `S3PollStorageService.java` - Service that connects to S3/LocalStack

## Commands for Testing

```bash
# Start LocalStack
docker-compose up -d localstack

# Build the SAM app
sam build

# Option A: Run full local API on the same Docker network as LocalStack (preferred)
# Note: Change the port if 3000 is busy (e.g. --port 3001)
sam local start-api \
  --docker-network calendar-aws-function_default \
  --env-vars env.json \
  --port 3000

# To reduce cold starts during local development, run with warm containers enabled:
sam local start-api \
  --docker-network calendar-aws-function_default \
  --env-vars env.json \
  --port 3000 \
  --warm-containers EAGER

# The env.json sets SPRING_PROFILES_ACTIVE=localstack so JTE dev mode is used
# and LocalStack endpoints are applied inside the Lambda container.

# Then test endpoints via API Gateway emulation
curl -s http://127.0.0.1:3000/schedule-event | head -n 20
curl -s -X POST http://127.0.0.1:3000/schedule-event \
  -H "Content-Type: application/x-www-form-urlencoded" \
  --data "yourName=Test&emailAddress=test%40example.com&activityTitle=Demo&description=Check+S3" -i

# Verify S3 write in LocalStack
aws --endpoint-url=http://localhost:4566 s3 ls s3://de.bas.bodo --recursive

# Option B: Single Lambda invocation with correct API Gateway proxy events
sam local invoke WoodleLambdaFunction \
  --env-vars env.json \
  --docker-network calendar-aws-function_default \
  --event events/get-schedule-event.json

sam local invoke WoodleLambdaFunction \
  --env-vars env.json \
  --docker-network calendar-aws-function_default \
  --event events/post-schedule-event.json
```

### Troubleshooting

- InvalidRequestEventException: Ensure you use an API Gateway proxy-shaped event. The minimal samples
  `api-test.json` and `simple-test.json` are not valid. Use files under `events/` created for this.
- Port already in use (3000):
  - Find process: `lsof -nP -iTCP:3000 -sTCP:LISTEN`
  - Stop it or use another port: add `--port 3001` to `sam local start-api` and call `http://127.0.0.1:3001`.
- LocalStack connectivity from Lambda: `env.json` sets `AWS_S3_ENDPOINT` to `http://woodle-localstack:4566`
  so the Lambda container can reach LocalStack by hostname over the shared Docker network.

### About --warm-containers

- **What it does**: `--warm-containers EAGER` tells SAM to create and keep warm containers for functions so invocations reuse the same runtime container instead of starting a fresh one for each request. This significantly reduces cold-start times for frameworks like Spring Boot.
- **Usage**: add `--warm-containers EAGER` to `sam local start-api` (or `sam local invoke`) as shown above.
- **Notes**: warm containers consume resources locally; you can use `DISABLED` (default) or `LAZY` as alternatives. `EAGER` starts containers at startup and keeps them ready.