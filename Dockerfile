FROM eclipse-temurin:21-jdk-alpine

# Set working directory
WORKDIR /app

# Install curl, wget, and Java
RUN apk update && apk add --no-cache curl wget openjdk21-jdk

# Create server directory structure
RUN mkdir -p /app/server/world /app/server/world_nether /app/server/world_the_end /app/server/plugins /app/server/logs

# Download PaperMC server to a temporary location first
RUN wget -O /tmp/paper.jar https://api.papermc.io/v2/projects/paper/versions/1.21.1/builds/133/downloads/paper-1.21.1-133.jar

# Create working plugin JAR - SIMPLE VERSION
RUN mkdir -p /app/server/plugins && \
    echo "Creating Kingdom plugin..." && \
    # Create Java source
    printf 'import org.bukkit.plugin.java.JavaPlugin;\nimport org.bukkit.entity.Player;\nimport org.bukkit.event.EventHandler;\nimport org.bukkit.event.Listener;\nimport org.bukkit.event.player.PlayerJoinEvent;\nimport org.bukkit.command.Command;\nimport org.bukkit.command.CommandSender;\n\npublic class KingdomPlugin extends JavaPlugin implements Listener {\n    @Override\n    public void onEnable() {\n        getLogger().info("Kingdom Plugin Enabled!");\n        getServer().getPluginManager().registerEvents(this, this);\n    }\n    @EventHandler\n    public void onPlayerJoin(PlayerJoinEvent event) {\n        Player player = event.getPlayer();\n        event.setJoinMessage("[Kingdom] " + player.getName() + " has joined!");\n        player.sendMessage("Welcome to the Kingdom Server!");\n        player.sendMessage("Type /kingdom for commands!");\n    }\n    @Override\n    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {\n        if (command.getName().equalsIgnoreCase("kingdom")) {\n            sender.sendMessage("Kingdom Commands:");\n            sender.sendMessage("/kingdom - Show this help");\n            sender.sendMessage("/coins - Check your coins");\n            return true;\n        }\n        if (command.getName().equalsIgnoreCase("coins")) {\n            sender.sendMessage("Coins: 0");\n            return true;\n        }\n        return false;\n    }\n}' > /app/server/plugins/KingdomPlugin.java && \
    # Create plugin.yml
    printf 'name: Kingdom\nversion: 1.0.0\nmain: KingdomPlugin\napi-version: 1.21\nauthor: KingdomCraft\ndescription: Kingdom commands plugin\ncommands:\n  kingdom:\n    description: Kingdom commands\n    usage: /kingdom\n  coins:\n    description: Check coins\n    usage: /coins\n' > /app/server/plugins/plugin.yml && \
    # Compile and create JAR
    cd /app/server/plugins && \
    javac -cp "/app/paper.jar" KingdomPlugin.java 2>/dev/null && \
    jar cf Kingdom.jar KingdomPlugin.class plugin.yml && \
    rm -f KingdomPlugin.java plugin.yml && \
    echo "✓ Kingdom plugin created and installed!" && \
    echo "Plugin contents:" && \
    jar tf Kingdom.jar

# Create startup script with auto-save
RUN echo '#!/bin/sh' > /app/start.sh && \
    echo 'cd /app/server' >> /app/start.sh && \
    echo 'echo "Starting Kingdom Server with auto-save..."' >> /app/start.sh && \
    echo 'echo "Volume mount path: /app/server"' >> /app/start.sh && \
    echo 'echo "Current working directory: $(pwd)"' >> /app/start.sh && \
    echo '# Wait for volume to be properly mounted' >> /app/start.sh && \
    echo 'echo "Checking volume mount..."' >> /app/start.sh && \
    echo 'for i in $(seq 1 30); do' >> /app/start.sh && \
    echo '    if [ -d "/app/server" ]; then' >> /app/start.sh && \
    echo '        echo "Volume mounted successfully! (attempt $i)"' >> /app/start.sh && \
    echo '        break' >> /app/start.sh && \
    echo '    fi' >> /app/start.sh && \
    echo '    echo "Waiting for volume mount... (attempt $i/30)"' >> /app/start.sh && \
    echo '    sleep 2' >> /app/start.sh && \
    echo 'done' >> /app/start.sh && \
    echo '# Final check' >> /app/start.sh && \
    echo 'if [ ! -d "/app/server" ]; then' >> /app/start.sh && \
    echo '    echo "ERROR: Volume failed to mount after 60 seconds!"' >> /app/start.sh && \
    echo '    echo "Creating emergency directory..."' >> /app/start.sh && \
    echo '    mkdir -p /app/server' >> /app/start.sh && \
    echo 'fi' >> /app/start.sh && \
    echo 'echo "Volume contents:"' >> /app/start.sh && \
    echo 'ls -la /app/server' >> /app/start.sh && \
    echo '# Check if world exists in volume' >> /app/start.sh && \
    echo 'if [ -d "world" ]; then' >> /app/start.sh && \
    echo '    echo "✓ Found existing world in volume!"' >> /app/start.sh && \
    echo '    echo "World files:"' >> /app/start.sh && \
    echo '    ls -la world/ | head -10' >> /app/start.sh && \
    echo '    echo "World region files:"' >> /app/start.sh && \
    echo '    ls -la world/region/ 2>/dev/null | head -5 || echo "No region files yet"' >> /app/start.sh && \
    echo '    echo "World level.dat:"' >> /app/start.sh && \
    echo '    ls -la world/level.dat 2>/dev/null || echo "No level.dat found"' >> /app/start.sh && \
    echo '    echo "Using existing world - players will spawn in SAME location!"' >> /app/start.sh && \
    echo '    echo "IMPORTANT: This should be the SAME world as before!"' >> /app/start.sh && \
    echo 'else' >> /app/start.sh && \
    echo '    echo "⚠ WARNING: No world found in volume!"' >> /app/start.sh && \
    echo '    echo "⚠ This will create a NEW world!"' >> /app/start.sh && \
    echo '    echo "⚠ Players will spawn in a NEW location!"' >> /app/start.sh && \
    echo '    echo "⚠ This is NOT the same world as before!"' >> /app/start.sh && \
    echo 'fi' >> /app/start.sh && \
    echo '# Clean up any leftover lock files from previous runs' >> /app/start.sh && \
    echo 'echo "Cleaning up lock files..."' >> /app/start.sh && \
    echo 'rm -f world/session.lock 2>/dev/null || true' >> /app/start.sh && \
    echo 'rm -f world_nether/session.lock 2>/dev/null || true' >> /app/start.sh && \
    echo 'rm -f world_the_end/session.lock 2>/dev/null || true' >> /app/start.sh && \
    echo 'rm -f session.lock 2>/dev/null || true' >> /app/start.sh && \
    echo '# Copy paper.jar to server directory if it doesnt exist' >> /app/start.sh && \
    echo 'if [ ! -f "paper.jar" ]; then' >> /app/start.sh && \
    echo '    echo "Copying paper.jar..."' >> /app/start.sh && \
    echo '    cp /tmp/paper.jar .' >> /app/start.sh && \
    echo 'fi' >> /app/start.sh && \
    echo '# Copy EULA file if it doesnt exist' >> /app/start.sh && \
    echo 'if [ ! -f "eula.txt" ]; then' >> /app/start.sh && \
    echo '    echo "Copying eula.txt..."' >> /app/start.sh && \
    echo '    cp /tmp/eula.txt .' >> /app/start.sh && \
    echo 'fi' >> /app/start.sh && \
    echo '# Create server properties if they dont exist' >> /app/start.sh && \
    echo 'if [ ! -f "server.properties" ]; then' >> /app/start.sh && \
    echo '    echo "Creating server.properties..."' >> /app/start.sh && \
    echo '    cat > server.properties << EOF' >> /app/start.sh && \
    echo 'enable-rcon=true' >> /app/start.sh && \
    echo 'rcon.port=25575' >> /app/start.sh && \
    echo 'rcon.password=changeme' >> /app/start.sh && \
    echo 'rcon.address=0.0.0.0' >> /app/start.sh && \
    echo 'rcon.broadcast=true' >> /app/start.sh && \
    echo 'broadcast-rcon-to-ops=true' >> /app/start.sh && \
    echo 'query.port=25565' >> /app/start.sh && \
    echo 'motd=Kingdom Server - Medieval Roleplay' >> /app/start.sh && \
    echo 'difficulty=normal' >> /app/start.sh && \
    echo 'gamemode=survival' >> /app/start.sh && \
    echo 'level-type=default' >> /app/start.sh && \
    echo 'level-name=world' >> /app/start.sh && \
    echo 'level-seed=8048694775087357441' >> /app/start.sh && \
    echo 'force-gamemode=false' >> /app/start.sh && \
    echo 'view-distance=6' >> /app/start.sh && \
    echo 'simulation-distance=4' >> /app/start.sh && \
    echo 'entity-broadcast-range-percentage=50' >> /app/start.sh && \
    echo 'max-chained-neighbor-updates=500' >> /app/start.sh && \
    echo 'max-entity-collisions=2' >> /app/start.sh && \
    echo 'auto-save=true' >> /app/start.sh && \
    echo 'generate-structures=true' >> /app/start.sh && \
    echo 'online-mode=false' >> /app/start.sh && \
    echo 'pvp=true' >> /app/start.sh && \
    echo 'allow-flight=false' >> /app/start.sh && \
    echo 'debug=false' >> /app/start.sh && \
    echo 'EOF' >> /app/start.sh && \
    echo 'fi' >> /app/start.sh && \
    echo '# List files to debug' >> /app/start.sh && \
    echo 'echo "Current directory contents:"' >> /app/start.sh && \
    echo 'ls -la' >> /app/start.sh && \
    echo 'echo "=== STARTING MINECRAFT SERVER ==="' >> /app/start.sh && \
    echo 'echo "Server will auto-save every 60 seconds to persistent volume"' >> /app/start.sh && \
    echo 'echo "RCON enabled on port 25575 with password: changeme"' >> /app/start.sh && \
    echo 'echo "World seed: 8048694775087357441 (EARTH MAP SEED)"' >> /app/start.sh && \
    echo 'echo "Plugins loaded:"' >> /app/start.sh && \
    echo 'ls -la plugins/' >> /app/start.sh && \
    echo '# Start server with auto-save every 60 seconds' >> /app/start.sh && \
    echo 'java -Xms1G -Xmx2G -XX:+UseG1GC -XX:+ParallelRefProcEnabled -XX:MaxGCPauseMillis=200 -XX:+UnlockExperimentalVMOptions -XX:+DisableExplicitGC -XX:+AlwaysPreTouch -XX:G1NewSizePercent=30 -XX:G1MaxNewSizePercent=40 -XX:G1HeapRegionSize=8M -XX:G1ReservePercent=20 -XX:G1HeapWastePercent=5 -XX:G1MixedGCCountTarget=4 -XX:InitiatingHeapOccupancyPercent=15 -XX:G1MixedGCLiveThresholdPercent=90 -XX:G1RSetUpdatingPauseTimePercent=5 -XX:SurvivorRatio=32 -XX:+PerfDisableSharedMem -XX:MaxTenuringThreshold=1 -jar paper.jar nogui --nogui &' >> /app/start.sh && \
    echo 'SERVER_PID=$!' >> /app/start.sh && \
    echo 'echo "Server started with PID: $SERVER_PID"' >> /app/start.sh && \
    echo '# Wait for server to fully start and RCON to be available' >> /app/start.sh && \
    echo 'echo "Waiting for server to fully start (30 seconds)..."' >> /app/start.sh && \
    echo 'sleep 30' >> /app/start.sh && \
    echo 'echo "Checking if server is running..."' >> /app/start.sh && \
    echo 'if kill -0 $SERVER_PID 2>/dev/null; then' >> /app/start.sh && \
    echo '    echo "✓ Server is running successfully!"' >> /app/start.sh && \
    echo '    echo "Testing RCON connection..."' >> /app/start.sh && \
    echo '    echo "list" > /proc/$SERVER_PID/fd/0 2>/dev/null && echo "✓ RCON is working!" || echo "⚠ RCON test failed - will retry"' >> /app/start.sh && \
    echo '    echo "Plugins loaded:"' >> /app/start.sh && \
    echo '    echo "plugins" > /proc/$SERVER_PID/fd/0 2>/dev/null || echo "Could not list plugins"' >> /app/start.sh && \
    echo 'else' >> /app/start.sh && \
    echo '    echo "✗ Server failed to start!"' >> /app/start.sh && \
    echo '    exit 1' >> /app/start.sh && \
    echo 'fi' >> /app/start.sh && \
    echo 'echo "Starting auto-save loop..."' >> /app/start.sh && \
    echo '# Auto-save every 60 seconds' >> /app/start.sh && \
    echo 'SAVE_COUNT=0' >> /app/start.sh && \
    echo 'while kill -0 $SERVER_PID 2>/dev/null; do' >> /app/start.sh && \
    echo '    sleep 60' >> /app/start.sh && \
    echo '    SAVE_COUNT=$((SAVE_COUNT + 1))' >> /app/start.sh && \
    echo '    echo "[$SAVE_COUNT] Auto-saving world data to volume..."' >> /app/start.sh && \
    echo '    if [ -n "$SERVER_PID" ]; then' >> /app/start.sh && \
    echo '        echo "save-all" > /proc/$SERVER_PID/fd/0 2>/dev/null && echo "Save command sent successfully" || echo "Failed to send save command"' >> /app/start.sh && \
    echo '        sleep 5' >> /app/start.sh && \
    echo '        echo "save-off" > /proc/$SERVER_PID/fd/0 2>/dev/null || true' >> /app/start.sh && \
    echo '        echo "save-on" > /proc/$SERVER_PID/fd/0 2>/dev/null || true' >> /app/start.sh && \
    echo '    fi' >> /app/start.sh && \
    echo '    echo "✓ World saved to persistent volume (save #$SAVE_COUNT)"' >> /app/start.sh && \
    echo '    echo "Volume size: $(du -sh . 2>/dev/null | cut -f1 || echo "unknown")"' >> /app/start.sh && \
    echo '    echo "World files: $(find world -name "*.mca" 2>/dev/null | wc -l) region files"' >> /app/start.sh && \
    echo 'done' >> /app/start.sh && \
    echo 'echo "Server process ended, cleaning up..."' >> /app/start.sh && \
    echo 'wait $SERVER_PID' >> /app/start.sh && \
    chmod +x /app/start.sh

# Expose ports
EXPOSE 25565 25575

# Start the server
CMD ["/app/start.sh"]
