FROM eclipse-temurin:21-jdk-alpine

# Set working directory
WORKDIR /app

# Install curl and wget
RUN apk update && apk add --no-cache curl wget

# Create server directory structure
RUN mkdir -p /app/server/world /app/server/world_nether /app/server/world_the_end /app/server/plugins /app/server/logs

# Download PaperMC server to a temporary location first
RUN wget -O /tmp/paper.jar https://api.papermc.io/v2/projects/paper/versions/1.21.1/builds/133/downloads/paper-1.21.1-133.jar

# Create a minimal KingdomCommands plugin JAR
RUN mkdir -p /tmp/plugin/META-INF && \
    echo "Manifest-Version: 1.0" > /tmp/plugin/META-INF/MANIFEST.MF && \
    echo "Created-By: KingdomCommands" >> /tmp/plugin/META-INF/MANIFEST.MF && \
    echo "name: KingdomCommands" > /tmp/plugin/plugin.yml && \
    echo "version: '1.0.0'" >> /tmp/plugin/plugin.yml && \
    echo "main: com.kingdom.commands.KingdomCommands" >> /tmp/plugin/plugin.yml && \
    echo "api-version: 1.21" >> /tmp/plugin/plugin.yml && \
    echo "author: KingdomCraft" >> /tmp/plugin/plugin.yml && \
    echo "description: A kingdom-themed Minecraft server commands plugin" >> /tmp/plugin/plugin.yml && \
    echo "commands:" >> /tmp/plugin/plugin.yml && \
    echo "  kban:" >> /tmp/plugin/plugin.yml && \
    echo "    description: Ban a player with kingdom style" >> /tmp/plugin/plugin.yml && \
    echo "    usage: /kban <player> [reason]" >> /tmp/plugin/plugin.yml && \
    echo "    permission: kingdom.commands.ban" >> /tmp/plugin/plugin.yml && \
    echo "  kkick:" >> /tmp/plugin/plugin.yml && \
    echo "    description: Kick a player with kingdom style" >> /tmp/plugin/plugin.yml && \
    echo "    usage: /kkick <player> [reason]" >> /tmp/plugin/plugin.yml && \
    echo "    permission: kingdom.commands.kick" >> /tmp/plugin/plugin.yml && \
    echo "  kop:" >> /tmp/plugin/plugin.yml && \
    echo "    description: Op a player with kingdom style" >> /tmp/plugin/plugin.yml && \
    echo "    usage: /kop <player>" >> /tmp/plugin/plugin.yml && \
    echo "    permission: kingdom.commands.op" >> /tmp/plugin/plugin.yml && \
    echo "  kdeop:" >> /tmp/plugin/plugin.yml && \
    echo "    description: Deop a player with kingdom style" >> /tmp/plugin/plugin.yml && \
    echo "    usage: /kdeop <player>" >> /tmp/plugin/plugin.yml && \
    echo "    permission: kingdom.commands.deop" >> /tmp/plugin/plugin.yml && \
    echo "  coins:" >> /tmp/plugin/plugin.yml && \
    echo "    description: Check your total coins" >> /tmp/plugin/plugin.yml && \
    echo "    usage: /coins" >> /tmp/plugin/plugin.yml && \
    echo "    permission: kingdom.commands.coins" >> /tmp/plugin/plugin.yml && \
    echo "  balance:" >> /tmp/plugin/plugin.yml && \
    echo "    description: Quick balance check" >> /tmp/plugin/plugin.yml && \
    echo "    usage: /balance" >> /tmp/plugin/plugin.yml && \
    echo "    permission: kingdom.commands.balance" >> /tmp/plugin/plugin.yml && \
    echo "permissions:" >> /tmp/plugin/plugin.yml && \
    echo "  kingdom.commands.*:" >> /tmp/plugin/plugin.yml && \
    echo "    description: All kingdom commands" >> /tmp/plugin/plugin.yml && \
    echo "    children:" >> /tmp/plugin/plugin.yml && \
    echo "      kingdom.commands.ban: true" >> /tmp/plugin/plugin.yml && \
    echo "      kingdom.commands.kick: true" >> /tmp/plugin/plugin.yml && \
    echo "      kingdom.commands.op: true" >> /tmp/plugin/plugin.yml && \
    echo "      kingdom.commands.deop: true" >> /tmp/plugin/plugin.yml && \
    echo "      kingdom.commands.coins: true" >> /tmp/plugin/plugin.yml && \
    echo "      kingdom.commands.balance: true" >> /tmp/plugin/plugin.yml && \
    echo "  kingdom.commands.ban:" >> /tmp/plugin/plugin.yml && \
    echo "    description: Allow banning players" >> /tmp/plugin/plugin.yml && \
    echo "    default: op" >> /tmp/plugin/plugin.yml && \
    echo "  kingdom.commands.kick:" >> /tmp/plugin/plugin.yml && \
    echo "    description: Allow kicking players" >> /tmp/plugin/plugin.yml && \
    echo "    default: op" >> /tmp/plugin/plugin.yml && \
    echo "  kingdom.commands.op:" >> /tmp/plugin/plugin.yml && \
    echo "    description: Allow opping players" >> /tmp/plugin/plugin.yml && \
    echo "    default: op" >> /tmp/plugin/plugin.yml && \
    echo "  kingdom.commands.deop:" >> /tmp/plugin/plugin.yml && \
    echo "    description: Allow deopping players" >> /tmp/plugin/plugin.yml && \
    echo "    default: op" >> /tmp/plugin/plugin.yml && \
    echo "  kingdom.commands.coins:" >> /tmp/plugin/plugin.yml && \
    echo "    description: Allow using coin commands" >> /tmp/plugin/plugin.yml && \
    echo "    default: true" >> /tmp/plugin/plugin.yml && \
    echo "  kingdom.commands.balance:" >> /tmp/plugin/plugin.yml && \
    echo "    description: Allow checking balance" >> /tmp/plugin/plugin.yml && \
    echo "    default: true" >> /tmp/plugin/plugin.yml && \
    echo "prefix: \"&6[&e✦&6Kingdom&e✦&6] &r\"" > /tmp/plugin/config.yml && \
    echo "coin_economy:" >> /tmp/plugin/config.yml && \
    echo "  enabled: true" >> /tmp/plugin/config.yml && \
    echo "  gold_nugget_value: 1" >> /tmp/plugin/config.yml && \
    echo "  gold_ingot_value: 9" >> /tmp/plugin/config.yml && \
    echo "  gold_block_value: 81" >> /tmp/plugin/config.yml && \
    echo "  count_ender_chest: true" >> /tmp/plugin/config.yml && \
    echo "auto-save:" >> /tmp/plugin/config.yml && \
    echo "  enabled: true" >> /tmp/plugin/config.yml && \
    echo "  interval: 60" >> /tmp/plugin/config.yml && \
    echo "  notify-admins: false" >> /tmp/plugin/config.yml && \
    cd /tmp/plugin && \
    jar cf KingdomCommands-1.0.0.jar * && \
    cp KingdomCommands-1.0.0.jar /app/server/plugins/ && \
    rm -rf /tmp/plugin

# Create startup script with auto-save
RUN echo '#!/bin/sh' > /app/start.sh && \
    echo 'cd /app/server' >> /app/start.sh && \
    echo 'echo "Starting Kingdom Server with auto-save..."' >> /app/start.sh && \
    echo '# Copy paper.jar to server directory if it doesnt exist' >> /app/start.sh && \
    echo 'if [ ! -f "paper.jar" ]; then' >> /app/start.sh && \
    echo '    cp /tmp/paper.jar .' >> /app/start.sh && \
    echo 'fi' >> /app/start.sh && \
    echo '# Create EULA file if it doesnt exist' >> /app/start.sh && \
    echo 'if [ ! -f "eula.txt" ]; then' >> /app/start.sh && \
    echo '    echo "eula=true" > eula.txt' >> /app/start.sh && \
    echo 'fi' >> /app/start.sh && \
    echo '# Create server properties if they dont exist' >> /app/start.sh && \
    echo 'if [ ! -f "server.properties" ]; then' >> /app/start.sh && \
    echo '    echo "server-port=25565" > server.properties' >> /app/start.sh && \
    echo '    echo "enable-rcon=true" >> server.properties' >> /app/start.sh && \
    echo '    echo "rcon.port=25575" >> server.properties' >> /app/start.sh && \
    echo '    echo "rcon.password=changeme" >> server.properties' >> /app/start.sh && \
    echo '    echo "motd=Kingdom Server - Medieval Roleplay" >> server.properties' >> /app/start.sh && \
    echo '    echo "difficulty=normal" >> server.properties' >> /app/start.sh && \
    echo '    echo "gamemode=survival" >> server.properties' >> /app/start.sh && \
    echo '    echo "level-type=default" >> server.properties' >> /app/start.sh && \
    echo '    echo "level-name=world" >> server.properties' >> /app/start.sh && \
    echo '    echo "view-distance=6" >> server.properties' >> /app/start.sh && \
    echo '    echo "simulation-distance=4" >> server.properties' >> /app/start.sh && \
    echo '    echo "entity-broadcast-range-percentage=50" >> server.properties' >> /app/start.sh && \
    echo '    echo "max-chained-neighbor-updates=500" >> server.properties' >> /app/start.sh && \
    echo '    echo "max-entity-collisions=2" >> server.properties' >> /app/start.sh && \
    echo 'fi' >> /app/start.sh && \
    echo 'java -Xms1G -Xmx2G -XX:+UseG1GC -XX:+ParallelRefProcEnabled -XX:MaxGCPauseMillis=200 -XX:+UnlockExperimentalVMOptions -XX:+DisableExplicitGC -XX:+AlwaysPreTouch -XX:G1NewSizePercent=30 -XX:G1MaxNewSizePercent=40 -XX:G1HeapRegionSize=8M -XX:G1ReservePercent=20 -XX:G1HeapWastePercent=5 -XX:G1MixedGCCountTarget=4 -XX:InitiatingHeapOccupancyPercent=15 -XX:G1MixedGCLiveThresholdPercent=90 -XX:G1RSetUpdatingPauseTimePercent=5 -XX:SurvivorRatio=32 -XX:+PerfDisableSharedMem -XX:MaxTenuringThreshold=1 -jar paper.jar nogui --nogui' >> /app/start.sh && \
    chmod +x /app/start.sh

# Expose ports
EXPOSE 25565 25575

# Start the server
CMD ["/app/start.sh"]
