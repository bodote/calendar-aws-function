Woodle on AWS Lambda - Deploy Guide

Prerequisites
- AWS account with access keys configured (`aws configure`)
- AWS CLI v2 and SAM CLI installed
- Docker installed (for SAM build images)
- Java 21 toolchain (project compiles to 21)

One-time setup
1) Clone repo and install deps
   - `git clone ... && cd calendar-aws-function`
   - `./mvnw -v` (verify Maven Wrapper works)

2) Configure AWS CLI
   - `aws configure` and set:
     - AWS Access Key ID / Secret Access Key
     - Default region: eu-central-1

3) Bootstrap SAM managed bucket (first deploy only)
   - Run `sam deploy --guided` once
   - Prompts:
     - Stack name: woodle
     - Region: eu-central-1
     - Confirm changeset: Y
     - Allow SAM CLI IAM role creation: Y
     - Save arguments to samconfig.toml: Y

Build and deploy
1) Run tests and package
   - `./mvnw clean test`
   - `./mvnw -DskipTests package`

2) Build SAM artifacts
   - `sam build --no-cached`
   - Flag `--no-cached` ensures a fresh container build

3) Deploy non-interactively
   - `sam deploy --no-confirm-changeset --stack-name woodle --region eu-central-1 --capabilities CAPABILITY_IAM`
   - Options:
     - `--no-confirm-changeset`: Skip interactive review step
     - `--stack-name`: CloudFormation stack name
     - `--region`: Target AWS region
     - `--capabilities CAPABILITY_IAM`: Allow IAM updates (Lambda role policies)

Post-deploy
1) Find API base URL
   - `aws cloudformation describe-stacks --stack-name woodle --region eu-central-1 --query "Stacks[0].Outputs" --output table`
   - Use the `Prod` stage URL, e.g. `https://.../Prod`

2) Test endpoints
   - Root (redirects to index.html):
     - `curl -i https://<api>/Prod/`
   - Create new event (302 to step2/uuid):
     - `curl -i -X POST https://<api>/Prod/schedule-event -H 'Content-Type: application/x-www-form-urlencoded' --data 'yourName=Tester&emailAddress=test@example.com&activityTitle=Demo&description=desc'`

3) Check logs
   - `aws logs describe-log-streams --log-group-name /aws/lambda/woodle-WoodleLambdaFunction-<suffix> --order-by LastEventTime --descending --limit 1 --region eu-central-1`
   - `aws logs get-log-events --log-group-name /aws/lambda/woodle-WoodleLambdaFunction-<suffix> --log-stream-name <latestStream> --limit 100 --region eu-central-1`

Important configuration
- Runtime: java21; Architecture: arm64
- Stage path `/Prod` is required on API Gateway REST API (default stage)
- S3 bucket and region are injected via environment (`template.yml`)
- Lambda role has S3CrudPolicy for your bucket

Stage-aware redirects
- The REST API default stage `Prod` introduces a stage path (`/Prod`). With AWS Serverless Java Container, the stage is not visible in `HttpServletRequest` path.
- Solution implemented:
  - The Lambda environment exposes `API_STAGE` (now parameterized via `ApiStageName` in `template.yml`).
  - `WoodleFormsController` builds absolute redirect URLs using:
    - scheme from `X-Forwarded-Proto` (fallback to `request.isSecure()`)
    - host from `Host`
    - stage from `API_STAGE`
  - This guarantees correct redirects for both `/Prod` and `/Prod/`.

Change stage name
- Edit `template.yml` parameter `ApiStageName` (default `Prod`).
- Deploy with a different stage value:
  - `sam deploy --no-confirm-changeset --stack-name woodle --region eu-central-1 --capabilities CAPABILITY_IAM --parameter-overrides ApiStageName=v1`

Alternative options
- Use an HTTP API with `$default` stage (no path segment) or a custom domain with base path mapping to eliminate the stage from paths.

Troubleshooting
- 403 Forbidden on POST schedule-event:
  - Ensure Lambda role allows `s3:PutObject` on your bucket
- 502 timeout during cold start:
  - Retry; enable warm containers in local dev, not applicable in API GW
- Static assets missing under `/Prod`:
  - Use relative paths in templates (no leading `/`)


