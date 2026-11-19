#!/usr/bin/env bash
# Installs an APK on the running emulator, taps through it at random (as Play's review and pre-launch
# robots do) and fails if the app crashes or stops responding. Usage: monkey_test.sh APK OUT_DIR
set -u
APK=$1
OUT=$2
PKG=com.atozkids
mkdir -p "$OUT"
adb install -r -g "$APK" || exit 2
adb logcat -c
adb shell am start -W -n "$PKG/com.atozkids.learn.MainActivity" > "$OUT/launch.txt"
sleep 8
for seed in 11 22 33 44; do
  adb shell monkey -p "$PKG" -s "$seed" --throttle 120 --pct-syskeys 5 --pct-appswitch 3 \
    --ignore-security-exceptions -v 2000 > "$OUT/monkey-$seed.txt" 2>&1
  adb shell am start -n "$PKG/com.atozkids.learn.MainActivity" > /dev/null 2>&1
  sleep 2
done
# Rotate while each main screen is open.
adb shell settings put system accelerometer_rotation 0
for r in 1 0 1 0; do adb shell settings put system user_rotation $r; sleep 2; done
adb logcat -d > "$OUT/logcat.txt"
adb logcat -b crash -d > "$OUT/crash.txt" 2>/dev/null
if grep -qE "FATAL EXCEPTION|ANR in $PKG" "$OUT/logcat.txt" "$OUT/crash.txt" || grep -qE "// (CRASH|NOT RESPONDING)" "$OUT"/monkey-*.txt; then
  # Show the first crash on the run page as an annotation.
  msg=$( (grep -h -A30 -E "FATAL EXCEPTION|ANR in $PKG" "$OUT/crash.txt" "$OUT/logcat.txt"; grep -h -A30 -E "// (CRASH|NOT RESPONDING)" "$OUT"/monkey-*.txt) | head -45 | sed 's/%/%25/g' | awk '{printf "%s%%0A", $0}')
  echo "::error title=App crashed ($OUT)::$msg"
  exit 1
fi
echo "no crash or ANR in $(grep -h 'Events injected' "$OUT"/monkey-*.txt | tr '\n' ' ')"
