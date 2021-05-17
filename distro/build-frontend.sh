#!/usr/bin/env bash
set -euo pipefail
set +x
npx --yes openmrs@${APP_SHELL_VERSION} build --spa-path \${SPA_PATH} --api-url \${API_URL} --target "$1"
test -f "$1/index.html"
npx --yes openmrs@${APP_SHELL_VERSION} assemble --mode config --target "$1"
test -f "$1/importmap.json"
npx --yes json-merger --pretty "$1/importmap.json" importmap-overrides.json --output "$1/importmap.json.tmp"
mv "$1/importmap.json.tmp" "$1/importmap.json"
