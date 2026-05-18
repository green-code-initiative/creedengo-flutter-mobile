#!/usr/bin/env bash
#
# Boots the local test environment: builds the plugin, starts SonarQube and
# PostgreSQL via docker compose, then waits for SonarQube to report status UP.
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

PLUGIN_JAR="target/creedengo-flutter-1.0.0-SNAPSHOT.jar"
SONAR_URL="http://localhost:9000"
MAX_ATTEMPTS=60
SLEEP_SECONDS=5

require() {
  command -v "$1" >/dev/null 2>&1 || { echo "Missing required command: $1" >&2; exit 1; }
}

require docker
require mvn

if ! docker compose version >/dev/null 2>&1; then
  echo "docker compose plugin is required (Docker 20.10+)." >&2
  exit 1
fi

if [[ ! -f "$PLUGIN_JAR" ]]; then
  echo "Building plugin..."
  mvn -B -ntp -DskipTests package
fi

echo "Stopping any previous SonarQube stack..."
docker compose down --remove-orphans >/dev/null 2>&1 || true

echo "Starting SonarQube and PostgreSQL..."
docker compose up -d sonarqube-db sonarqube

echo "Waiting for SonarQube at $SONAR_URL ..."
attempt=0
until curl -fsS "$SONAR_URL/api/system/status" 2>/dev/null | grep -q '"status":"UP"'; do
  attempt=$((attempt + 1))
  if (( attempt >= MAX_ATTEMPTS )); then
    echo "SonarQube did not become healthy within $((MAX_ATTEMPTS * SLEEP_SECONDS))s." >&2
    echo "Run 'docker compose logs sonarqube' to inspect the logs." >&2
    exit 1
  fi
  printf '.'
  sleep "$SLEEP_SECONDS"
done
echo

cat <<EOF

SonarQube is ready.

  URL:       $SONAR_URL
  Login:     admin / admin (change it on first login)

Next steps:
  ./scripts/verify-plugin.sh   # confirm the plugin is loaded
  ./scripts/run-analysis.sh    # analyse the bundled sample project
EOF
