#!/bin/bash
# Plugin Installation Script for Kingdom Server
# Run this script in your Railway server console or SSH

echo "🏰 Installing Kingdom Plugins..."

# Create plugins directory if it doesn't exist
mkdir -p /data/plugins

# Download essential plugins
echo "📦 Downloading CoreProtect..."
wget -O /data/plugins/CoreProtect.jar "https://github.com/PlayPro/CoreProtect/releases/download/v23.1/CoreProtect-23.1.jar" || echo "Failed to download CoreProtect"

echo "📦 Downloading WorldEdit..."
wget -O /data/plugins/WorldEdit.jar "https://download.enginehub.org/worldedit/downloads/worldedit-bukkit-7.3.6.jar" || echo "Failed to download WorldEdit"

echo "📦 Downloading LuckPerms..."
wget -O /data/plugins/LuckPerms.jar "https://download.luckperms.net/1553/bukkit/latest/LuckPerms-Bukkit-5.4.132.jar" || echo "Failed to download LuckPerms"

echo "📦 Downloading Vault..."
wget -O /data/plugins/Vault.jar "https://github.com/MilkBowl/Vault/releases/download/1.7.3/Vault-1.7.3.jar" || echo "Failed to download Vault"

# Create a simple placeholder for SimpleKingdom if needed
echo "📦 Creating SimpleKingdom placeholder..."
cat > /data/plugins/SimpleKingdom-1.0.0.jar << 'EOF'
# SimpleKingdom Plugin - Built with Maven
EOF

# List installed plugins
echo "✅ Installed Plugins:"
ls -la /data/plugins/*.jar 2>/dev/null || echo "No plugins found"

echo "🔄 Restart the server to load plugins!"
echo "🎮 Test commands: /kingdom, /coins, /knight"
