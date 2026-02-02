FROM eclipse-temurin:21-jdk-alpine

# Set working directory
WORKDIR /app

# Install curl, wget, and Java
RUN apk update && apk add --no-cache curl wget openjdk21-jdk

# Create server directory structure
RUN mkdir -p /app/server/world /app/server/world_nether /app/server/world_the_end /app/server/plugins /app/server/logs

# Download PaperMC server to server directory directly
RUN wget -O /app/server/paper.jar https://api.papermc.io/v2/projects/paper/versions/1.21.1/builds/133/downloads/paper-1.21.1-133.jar

# Create EULA file
RUN echo "eula=true" > /app/server/eula.txt

# Create server.properties with RCON enabled
RUN cat > /app/server/server.properties << 'EOF'
enable-rcon=true
rcon.port=25575
rcon.password=changeme
rcon.address=0.0.0.0
rcon.broadcast=true
broadcast-rcon-to-ops=true
query.port=25565
motd=Kingdom Server - Medieval Roleplay
difficulty=normal
gamemode=survival
level-type=default
level-name=world
level-seed=8048694775087357441
force-gamemode=false
view-distance=6
simulation-distance=4
entity-broadcast-range-percentage=50
max-chained-neighbor-updates=500
max-entity-collisions=2
auto-save=true
generate-structures=true
online-mode=false
pvp=true
allow-flight=false
debug=false
EOF

# Create simple working plugin
RUN mkdir -p /app/server/plugins && \
    echo "Creating Kingdom plugin..." && \
    # Create a working plugin with proper structure
    mkdir -p /app/server/plugins/com/kingdom && \
    # Create Java source
    cat > /app/server/plugins/com/kingdom/KingdomPlugin.java << 'EOF'
package com.kingdom;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class KingdomPlugin extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        getLogger().info("Kingdom Plugin Enabled!");
        getServer().getPluginManager().registerEvents(this, this);
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.setJoinMessage("[Kingdom] " + player.getName() + " has joined!");
        player.sendMessage("Welcome to the Kingdom Server!");
        player.sendMessage("Type /kingdom for commands!");
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("kingdom")) {
            sender.sendMessage("Kingdom Commands:");
            sender.sendMessage("/kingdom - Show this help");
            sender.sendMessage("/coins - Check your coins");
            return true;
        }
        if (command.getName().equalsIgnoreCase("coins")) {
            sender.sendMessage("Coins: 100");
            return true;
        }
        return false;
    }
}
EOF && \
    # Create plugin.yml
    cat > /app/server/plugins/plugin.yml << 'EOF'
name: Kingdom
version: 1.0.0
main: com.kingdom.KingdomPlugin
api-version: 1.21
author: KingdomCraft
description: Kingdom commands plugin
commands:
  kingdom:
    description: Kingdom commands
    usage: /kingdom
  coins:
    description: Check coins
    usage: /coins
EOF && \
    # Compile and create JAR
    cd /app/server/plugins && \
    javac -cp "../paper.jar" com/kingdom/KingdomPlugin.java && \
    jar cf Kingdom.jar com/ plugin.yml && \
    rm -rf com/ plugin.yml && \
    echo "✓ Kingdom plugin created and installed!" && \
    echo "Plugin contents:" && \
    jar tf Kingdom.jar

# Create simple startup script
RUN echo '#!/bin/sh' > /app/start.sh && \
    echo 'cd /app/server' >> /app/start.sh && \
    echo 'echo "Starting Kingdom Server..."' >> /app/start.sh && \
    echo 'echo "RCON enabled on port 25575 with password: changeme"' >> /app/start.sh && \
    echo 'echo "World seed: 8048694775087357441 (EARTH MAP SEED)"' >> /app/start.sh && \
    echo 'echo "Plugins loaded:"' >> /app/start.sh && \
    echo 'ls -la plugins/' >> /app/start.sh && \
    echo 'java -Xms1G -Xmx2G -jar paper.jar nogui' >> /app/start.sh && \
    chmod +x /app/start.sh

# Expose ports
EXPOSE 25565 25575

# Start the server
CMD ["/app/start.sh"]
