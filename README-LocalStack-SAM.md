# Local Lambda Development with SAM + LocalStack

## Current Status

✅ **Working:**
- LocalStack S3 service running in Docker
- SAM CLI installed and building Lambda packages
- Spring Boot app connecting to LocalStack successfully
- Lambda function starting and Spring Boot initializing

❌ **Issue:** 
Lambda container cannot reach LocalStack due to Docker networking configuration

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

## The Problem

The Lambda container environment variables are set to:
```json
{
  "AWS_S3_ENDPOINT": "http://woodle-localstack:4566"
}
```

But the Spring Boot application in the Lambda is still trying to connect to `localhost:4566` instead of using the environment variables from `env.json`.

## Next Steps to Resolve

1. **Debug environment variable injection** - Verify env.json variables reach the Spring application
2. **Fix container networking** - Ensure Lambda container can resolve `woodle-localstack` hostname  
3. **Alternative approach** - Use host networking or bridge configuration
4. **Fallback option** - Continue using Option 1 (Spring Boot + LocalStack) for development

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

# Build and test Lambda (current approach)
sam build
DOCKER_HOST=unix:///Users/$(whoami)/.docker/run/docker.sock sam local invoke WoodleLambdaFunction --env-vars env.json --docker-network calendar-aws-function_default --event api-test.json
```