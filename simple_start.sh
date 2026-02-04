#!/bin/sh
cd /data
if [ ! -f "eula.txt" ]; then
  echo "First run - copying server files..."
  cp -r /minecraft-template/* /data/
  # Force Earth world on first run
  echo "🌍 LOADING EARTH WORLD TEMPLATE..."
  rm -rf /data/world /data/world_nether /data/world_the_end
  if [ -d "/minecraft-template/template/world" ]; then
    echo "✅ Found template world - copying..."
    cp -r /minecraft-template/template/world /data/world
    echo "🎉 EARTH WORLD LOADED!"
  elif [ -d "/minecraft-template/world" ]; then
    echo "✅ Found root template world - copying..."
    cp -r /minecraft-template/world /data/world
    echo "🎉 EARTH WORLD LOADED!"
  else
    echo "❌ No template world found - creating with Earth seed"
    mkdir -p /data/world
  fi
  echo "EARTH_WORLD_LOADED=true" > /data/.earth_world_loaded
else
  echo "Updating files - checking Earth world status..."
  # Force Earth world load if not loaded yet
  if [ ! -f "/data/.earth_world_loaded" ]; then
    echo "🌍 Earth world not loaded - forcing load now..."
    rm -rf /data/world /data/world_nether /data/world_the_end
    if [ -d "/minecraft-template/template/world" ]; then
      echo "✅ Found template world - copying..."
      cp -r /minecraft-template/template/world /data/world
      echo "🎉 EARTH WORLD LOADED!"
    elif [ -d "/minecraft-template/world" ]; then
      echo "✅ Found root template world - copying..."
      cp -r /minecraft-template/world /data/world
      echo "🎉 EARTH WORLD LOADED!"
    else
      echo "❌ No template world found"
    fi
    echo "EARTH_WORLD_LOADED=true" > /data/.earth_world_loaded
  else
    echo "✅ Earth world already loaded - preserving data"
  fi
  cp /minecraft-template/commands.yml /data/commands.yml 2>/dev/null || true
  cp /minecraft-template/ops.json /data/ops.json 2>/dev/null || true
  rm -rf /data/backend
  cp -r /minecraft-template/backend /data/backend 2>/dev/null || true
  rm -rf /data/website
  cp -r /minecraft-template/website /data/website 2>/dev/null || true
  mkdir -p /data/plugins
  echo "Installing plugins..."
  cp /minecraft-template/plugins/*.jar /data/plugins/ 2>/dev/null || true
  cp /minecraft-template/plugins/*/*.jar /data/plugins/ 2>/dev/null || true
  mkdir -p /data/dynmap
  cp /minecraft-template/dynmap_configuration.txt /data/dynmap/configuration.txt 2>/dev/null || true
fi
echo "Copying fresh server.jar..."
cp -f /minecraft-template/server.jar /data/server.jar
ls -la /data/server.jar
cd /data
echo "Starting Kingdom Server..."
echo "Plugins: $(ls plugins/*.jar 2>/dev/null | wc -l) installed"
echo "World: $(ls world/level.dat 2>/dev/null && echo "Existing Earth World" || echo "New World")"
exec java -Xms2G -Xmx4G -XX:+UseG1GC -jar server.jar nogui
