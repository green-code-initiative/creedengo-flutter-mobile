#!/usr/bin/env bash
#
# Verifies that the Creedengo Flutter plugin is installed and exposes the
# expected number of rules.
set -euo pipefail

SONAR_URL="${SONAR_URL:-http://localhost:9000}"
SONAR_LOGIN="${SONAR_LOGIN:-admin}"
SONAR_PASSWORD="${SONAR_PASSWORD:-admin}"
EXPECTED_RULES="${EXPECTED_RULES:-15}"
PLUGIN_KEY="creedengoflutter"
REPOSITORY_KEY="creedengo-dart"

if ! curl -fsS "$SONAR_URL/api/system/status" 2>/dev/null | grep -q '"status":"UP"'; then
  echo "SonarQube is not reachable at $SONAR_URL." >&2
  exit 1
fi

plugin_response="$(curl -fsS -u "$SONAR_LOGIN:$SONAR_PASSWORD" "$SONAR_URL/api/plugins/installed")"

if ! echo "$plugin_response" | grep -q "\"key\":\"$PLUGIN_KEY\""; then
  echo "Plugin '$PLUGIN_KEY' is not installed." >&2
  echo "Make sure the JAR is mounted into /opt/sonarqube/extensions/plugins/ and that SonarQube was restarted." >&2
  exit 1
fi

rules_response="$(curl -fsS -u "$SONAR_LOGIN:$SONAR_PASSWORD" \
  "$SONAR_URL/api/rules/search?repositories=$REPOSITORY_KEY&ps=500")"

if command -v jq >/dev/null 2>&1; then
  rule_count="$(echo "$rules_response" | jq -r '.total')"
else
  rule_count="$(echo "$rules_response" | grep -o '"total":[0-9]*' | head -1 | cut -d: -f2)"
fi

echo "Plugin '$PLUGIN_KEY' is installed."
echo "Repository '$REPOSITORY_KEY' exposes ${rule_count:-?} rules (expected: $EXPECTED_RULES)."

if [[ "${rule_count:-0}" != "$EXPECTED_RULES" ]]; then
  echo "Warning: rule count differs from expectations." >&2
  exit 1
fi
