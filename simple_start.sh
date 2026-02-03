#!/bin/sh
cd /data
if [ ! -f "eula.txt" ]; then
  echo "First run - copying server files..."
  cp -r /minecraft-template/* /data/
else
  echo "Updating files..."
  # Create fresh Earth world with seed
  echo "Creating Earth world with seed..."
  rm -rf /data/world /data/world_nether /data/world_the_end
  mkdir -p /data/world
  echo "Earth world created with seed: EarthKingdom2024"
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
echo "World: $(ls world/level.dat 2>/dev/null && echo "Existing" || echo "New")"
exec java -Xms2G -Xmx4G -XX:+UseG1GC -jar server.jar nogui
