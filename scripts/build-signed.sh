#!/usr/bin/env bash
set -euo pipefail
mkdir -p release
KEYSTORE="${CHIPAPP_KEYSTORE:-$PWD/release/chipapp-release.jks}"
if [ ! -f "$KEYSTORE" ]; then
  keytool -genkeypair -v -keystore "$KEYSTORE" -storepass chipapp-build \
    -keypass chipapp-build -alias chipapp -keyalg RSA -keysize 2048 -validity 10000 \
    -dname "CN=ChipApp, OU=Mobile, O=ChipApp, L=Indonesia, C=ID"
fi
export CHIPAPP_KEYSTORE="$KEYSTORE"
export CHIPAPP_STORE_PASSWORD="${CHIPAPP_STORE_PASSWORD:-chipapp-build}"
export CHIPAPP_KEY_ALIAS="${CHIPAPP_KEY_ALIAS:-chipapp}"
export CHIPAPP_KEY_PASSWORD="${CHIPAPP_KEY_PASSWORD:-chipapp-build}"
GRADLE="${GRADLE:-gradle}"
"$GRADLE" :app:assembleRelease
cp app/build/outputs/apk/release/app-release.apk release/ChipApp-release.apk
echo "APK signed: release/ChipApp-release.apk"
