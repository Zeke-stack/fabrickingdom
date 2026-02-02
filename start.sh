#!/bin/bash
set -e

echo "🏰 Starting Kingdom Server..."

# Set memory limits from environment variables
MEMORY_MIN=${MEMORY_MIN:-2G}
MEMORY_MAX=${MEMORY_MAX:-4G}

# Ensure we're in the server directory
cd /app/server

echo "📋 Server Configuration:"
echo "  Memory: $MEMORY_MIN - $MEMORY_MAX"
echo "  World: world"
echo "  Plugins: $(ls plugins/ 2>/dev/null | wc -l) installed"
echo "  Plugin list: $(ls plugins/ 2>/dev/null || echo 'No plugins found')"

echo "🚀 Starting Paper server with optimized settings..."
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
  -jar paper.jar nogui

