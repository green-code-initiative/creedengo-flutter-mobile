#!/usr/bin/env bash
#
# Runs sonar-scanner against the bundled flutter-test-project against the local
# SonarQube instance started by setup-test-environment.sh.
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

SONAR_URL="${SONAR_URL:-http://localhost:9000}"
SONAR_LOGIN="${SONAR_LOGIN:-admin}"
SONAR_PASSWORD="${SONAR_PASSWORD:-admin}"
PROJECT_DIR="${PROJECT_DIR:-flutter-test-project}"

if ! curl -fsS "$SONAR_URL/api/system/status" 2>/dev/null | grep -q '"status":"UP"'; then
  echo "SonarQube is not reachable at $SONAR_URL." >&2
  echo "Start it with: ./scripts/setup-test-environment.sh" >&2
  exit 1
fi

if [[ ! -d "$PROJECT_DIR" ]]; then
  echo "Sample project '$PROJECT_DIR' not found." >&2
  exit 1
fi

NETWORK="$(docker compose ps --format '{{.Service}} {{.Networks}}' | awk '/^sonarqube /{print $2; exit}')"
if [[ -z "${NETWORK:-}" ]]; then
  echo "Could not determine the docker compose network. Is the stack running?" >&2
  exit 1
fi

docker run --rm \
  --network "$NETWORK" \
  -v "$ROOT_DIR/$PROJECT_DIR:/usr/src" \
  sonarsource/sonar-scanner-cli:5 \
  -Dsonar.projectKey=flutter-test-creedengo \
  -Dsonar.projectName="Flutter Test Creedengo" \
  -Dsonar.projectVersion=1.0 \
  -Dsonar.sources=lib \
  -Dsonar.sourceEncoding=UTF-8 \
  -Dsonar.host.url="http://sonarqube:9000" \
  -Dsonar.login="$SONAR_LOGIN" \
  -Dsonar.password="$SONAR_PASSWORD"

echo
echo "Analysis complete. Open: $SONAR_URL/dashboard?id=flutter-test-creedengo"
