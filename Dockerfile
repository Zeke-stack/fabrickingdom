FROM eclipse-temurin:21-jdk-alpine

# Set working directory
WORKDIR /app

# Install curl and wget
RUN apk update && apk add --no-cache curl wget

# Create server directory structure
RUN mkdir -p /app/server/world /app/server/world_nether /app/server/world_the_end /app/server/plugins /app/server/logs

# Download PaperMC server
RUN wget -O paper.jar https://api.papermc.io/v2/projects/paper/versions/1.21.1/builds/133/downloads/paper-1.21.1-133.jar

# Accept EULA
RUN echo "eula=true" > eula.txt

# Set server properties with auto-save configuration
RUN echo "server-port=25565" > server.properties && \
    echo "enable-rcon=true" >> server.properties && \
    echo "rcon.port=25575" >> server.properties && \
    echo "rcon.password=changeme" >> server.properties && \
    echo "motd=Kingdom Server - Medieval Roleplay" >> server.properties && \
    echo "difficulty=normal" >> server.properties && \
    echo "gamemode=survival" >> server.properties && \
    echo "level-type=default" >> server.properties && \
    echo "level-name=world" >> server.properties && \
    echo "view-distance=6" >> server.properties && \
    echo "simulation-distance=4" >> server.properties && \
    echo "entity-broadcast-range-percentage=50" >> server.properties && \
    echo "max-chained-neighbor-updates=500" >> server.properties && \
    echo "max-entity-collisions=2" >> server.properties

# Create KingdomCommands plugin JAR from resources
RUN mkdir -p /tmp/plugin && \
    echo "Manifest-Version: 1.0" > /tmp/plugin/META-INF/MANIFEST.MF && \
    echo "Main-Class: com.kingdom.commands.KingdomCommands" >> /tmp/plugin/META-INF/MANIFEST.MF && \
    cp plugins/KingdomCommands/src/main/resources/plugin.yml /tmp/plugin/ && \
    cp plugins/KingdomCommands/src/main/resources/config.yml /tmp/plugin/ && \
    cd /tmp/plugin && \
    jar cf KingdomCommands-1.0.0.jar * && \
    cp KingdomCommands-1.0.0.jar /app/server/plugins/ && \
    rm -rf /tmp/plugin

# Create startup script with auto-save
RUN echo '#!/bin/sh' > /app/start.sh && \
    echo 'cd /app/server' >> /app/start.sh && \
    echo 'echo "Starting Kingdom Server with auto-save..."' >> /app/start.sh && \
    echo 'java -Xms1G -Xmx2G -XX:+UseG1GC -XX:+ParallelRefProcEnabled -XX:MaxGCPauseMillis=200 -XX:+UnlockExperimentalVMOptions -XX:+DisableExplicitGC -XX:+AlwaysPreTouch -XX:G1NewSizePercent=30 -XX:G1MaxNewSizePercent=40 -XX:G1HeapRegionSize=8M -XX:G1ReservePercent=20 -XX:G1HeapWastePercent=5 -XX:G1MixedGCCountTarget=4 -XX:InitiatingHeapOccupancyPercent=15 -XX:G1MixedGCLiveThresholdPercent=90 -XX:G1RSetUpdatingPauseTimePercent=5 -XX:SurvivorRatio=32 -XX:+PerfDisableSharedMem -XX:MaxTenuringThreshold=1 -jar paper.jar nogui --nogui' >> /app/start.sh && \
    chmod +x /app/start.sh

# Expose ports
EXPOSE 25565 25575

# Start the server
CMD ["/app/start.sh"]
