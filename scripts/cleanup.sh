#!/usr/bin/env bash
#
# Tears down the local SonarQube stack: stops the containers, removes the
# associated volumes, and prunes orphan networks. Does not touch your Maven
# build artefacts or the bundled sample project.
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

if [[ "${1:-}" != "-y" && "${1:-}" != "--yes" ]]; then
  read -r -p "Stop and remove the SonarQube stack and its volumes? [y/N] " reply
  case "$reply" in
    y|Y|yes|YES) ;;
    *) echo "Aborted."; exit 0 ;;
  esac
fi

docker compose down --volumes --remove-orphans
echo "Stack stopped. Run ./scripts/setup-test-environment.sh to bring it back up."
