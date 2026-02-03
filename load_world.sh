#!/bin/bash
echo "🌍 Loading template world..."

# Remove existing world
rm -rf /data/world /data/world_nether /data/world_the_end

# Copy template world
if [ -d "/minecraft-template/template/world" ]; then
    echo "✅ Found template world - copying..."
    cp -r /minecraft-template/template/world /data/world
    echo "🎉 Template world loaded successfully!"
elif [ -d "/minecraft-template/world" ]; then
    echo "✅ Found root template world - copying..."
    cp -r /minecraft-template/world /data/world
    echo "🎉 Template world loaded successfully!"
else
    echo "❌ No template world found - creating new world"
fi

echo "💾 World data saved and ready!"
