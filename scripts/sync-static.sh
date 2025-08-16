#!/usr/bin/env bash
set -euo pipefail

BUCKET="${1:-}"
REGION="${2:-eu-central-1}"

if [[ -z "$BUCKET" ]]; then
  echo "Usage: $0 <bucket-name> [region]" >&2
  exit 1
fi

SRC_DIR="$(cd "$(dirname "$0")/.." && pwd)/src/main/resources/static"
if [[ ! -d "$SRC_DIR" ]]; then
  echo "Static assets directory not found: $SRC_DIR" >&2
  exit 1
fi

echo "Syncing static assets from $SRC_DIR to s3://$BUCKET/"
aws s3 sync "$SRC_DIR" "s3://$BUCKET/" --region "$REGION" --delete
echo "Done."


