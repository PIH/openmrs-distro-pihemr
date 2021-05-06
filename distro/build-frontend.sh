#!/usr/bin/env bash
set -euo pipefail
set +x
npx --yes "openmrs@${APP_SHELL_VERSION}" build --spa-path "${SPA_PATH}" --api-url "${API_URL}" --target "$1"
npx --yes "openmrs@${APP_SHELL_VERSION}" assemble --mode config --target "$1"
