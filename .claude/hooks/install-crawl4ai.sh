#!/bin/bash
# Idempotent: installs crawl4ai at session start (reuses preinstalled Chromium)
python3 -c "import crawl4ai" 2>/dev/null && exit 0
PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD=1 pip install -q --break-system-packages crawl4ai playwright==1.56.0 >&2
