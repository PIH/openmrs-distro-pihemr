#!/usr/bin/env bash
# At this point, all this does is verify that the frontend application looks as expected
# and then add the config-file line to the import map.

set -euo pipefail
set +x
test -f "$1/index.html"
test -f "$1/importmap.json"
npx --yes json-merger --pretty "$1/importmap.json" importmap-overrides.json --output "$1/importmap.json.tmp"
mv "$1/importmap.json.tmp" "$1/importmap.json"
