#!/bin/sh
echo "🌍 FORCING TEMPLATE WORLD TO LOAD..."
cd /data

# Remove existing world
rm -rf /data/world /data/world_nether /data/world_the_end

# Copy template world - try all possible locations
if [ -d "/minecraft-template/template/world" ]; then
    echo "✅ Found /minecraft-template/template/world - copying..."
    cp -r /minecraft-template/template/world /data/world
    echo "🎉 TEMPLATE WORLD LOADED SUCCESSFULLY!"
elif [ -d "/minecraft-template/world" ]; then
    echo "✅ Found /minecraft-template/world - copying..."
    cp -r /minecraft-template/world /data/world
    echo "🎉 TEMPLATE WORLD LOADED SUCCESSFULLY!"
else
    echo "❌ NO TEMPLATE WORLD FOUND - checking directories:"
    ls -la /minecraft-template/
    ls -la /minecraft-template/template/ 2>/dev/null || echo "No template directory"
fi

echo "💾 World data ready!"
