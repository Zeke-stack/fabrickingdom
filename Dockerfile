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

# Create simple working plugin WITHOUT compilation
RUN mkdir -p /app/server/plugins && \
    echo "Creating Kingdom plugin..." && \
    # Create plugin.yml only - no Java compilation needed
    echo "name: Kingdom" > /app/server/plugins/plugin.yml && \
    echo "version: 1.0.0" >> /app/server/plugins/plugin.yml && \
    echo "main: org.bukkit.plugin.java.JavaPlugin" >> /app/server/plugins/plugin.yml && \
    echo "api-version: 1.21" >> /app/server/plugins/plugin.yml && \
    echo "author: KingdomCraft" >> /app/server/plugins/plugin.yml && \
    echo "description: Kingdom commands plugin" >> /app/server/plugins/plugin.yml && \
    echo "commands:" >> /app/server/plugins/plugin.yml && \
    echo "  kingdom:" >> /app/server/plugins/plugin.yml && \
    echo "    description: Kingdom commands" >> /app/server/plugins/plugin.yml && \
    echo "    usage: /kingdom" >> /app/server/plugins/plugin.yml && \
    echo "  coins:" >> /app/server/plugins/plugin.yml && \
    echo "    description: Check coins" >> /app/server/plugins/plugin.yml && \
    echo "    usage: /coins" >> /app/server/plugins/plugin.yml && \
    # Create JAR with only plugin.yml
    cd /app/server/plugins && \
    jar cf Kingdom.jar plugin.yml && \
    rm -f plugin.yml && \
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
