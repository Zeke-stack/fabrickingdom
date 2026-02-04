#!/bin/sh
set -e

echo "🏰 Starting Kingdom Server on Railway..."

MEMORY_MIN=${MEMORY_MIN:-2G}
MEMORY_MAX=${MEMORY_MAX:-4G}

cd /app/server

echo "📋 Setup: Loading configuration..."
[ ! -f eula.txt ] && echo "eula=true" > eula.txt

echo "📦 Setup: Preparing plugins..."
mkdir -p plugins
cp /minecraft-template/plugins/*.jar plugins/ 2>/dev/null || true
echo "✅ Plugins installed: $(ls plugins/*.jar 2>/dev/null | wc -l)"

echo "🌍 Setup: Loading world template..."
if [ ! -d "world" ]; then
  if [ -d "/minecraft-template/template/world" ]; then
    echo "✅ Found template world - loading..."
    cp -r /minecraft-template/template/world .
    echo "🎉 EARTH WORLD LOADED FROM TEMPLATE!"
  else
    echo "⚠️  No template world found - server will generate new world with seed"
  fi
else
  echo "✅ World already exists - using existing world"
fi

echo "📋 Setup: Copying server JAR..."
cp -f /minecraft-template/server.jar .

echo "🚀 Starting Kingdom Server..."
echo "Memory: $MEMORY_MIN - $MEMORY_MAX"
echo "World: $([ -f world/level.dat ] && echo "Loaded" || echo "Will be generated")"
echo "Plugins: $(ls plugins/*.jar 2>/dev/null | wc -l) installed"

exec java -Xms$MEMORY_MIN -Xmx$MEMORY_MAX \
  -XX:+UseG1GC \
  -XX:+ParallelRefProcEnabled \
  -XX:MaxGCPauseMillis=200 \
  -XX:+UnlockExperimentalVMOptions \
  -XX:+DisableExplicitGC \
  -XX:+AlwaysPreTouch \
  -XX:G1NewSizePercent=30 \
  -XX:G1MaxNewSizePercent=40 \
  -XX:G1HeapRegionSize=8M \
  -XX:G1ReservePercent=20 \
  -XX:G1HeapWastePercent=5 \
  -XX:G1MixedGCCountTarget=4 \
  -XX:InitiatingHeapOccupancyPercent=15 \
  -XX:G1MixedGCLiveThresholdPercent=90 \
  -XX:G1RSetUpdatingPauseTimePercent=5 \
  -XX:SurvivorRatio=32 \
  -XX:+PerfDisableSharedMem \
  -XX:MaxTenuringThreshold=1 \
  -Dusing.aikars.flags=https://mcflags.emc.gs \
  -Daikars.new.flags=true \
  -jar server.jar nogui
