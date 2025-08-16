sam local start-api \
  --docker-network calendar-aws-function_default \
  --env-vars env.json \
  --port 3000 \
  --warm-containers EAGER