#!/usr/bin/env bash
# Installs Google Chrome and Firefox inside the devcontainer so that local
# (non-Grid) WebDriverManager runs have real browser binaries to drive,
# whichever TEST_BROWSER is set to.
#
# Without this, `mvn test -Dtest=LoginTest` with GRID_URL unset fails with
# "cannot find Chrome/Firefox binary" - the devcontainer previously only
# ever expected browsers to run inside the Docker Compose Grid nodes
# (selenium/node-chrome, selenium/node-firefox), which bundle their own.
#
# Codespaces/devcontainers have no display server, so HEADLESS must stay
# true for any locally-run test here - see .env.example.

set -euo pipefail

echo "Installing Google Chrome..."
wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | sudo apt-key add -
echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" \
  | sudo tee /etc/apt/sources.list.d/google-chrome.list > /dev/null

sudo apt-get update -qq
sudo apt-get install -y -qq google-chrome-stable

echo "Installing Firefox..."
# Debian/Ubuntu carry the ESR channel in the default repos rather than the
# rapid-release "firefox" package, which usually isn't packaged for apt at all.
sudo apt-get install -y -qq firefox-esr

google-chrome --version
firefox-esr --version