FROM eclipse-temurin:21-jdk-alpine

# Set working directory
WORKDIR /minecraft

# Install necessary tools including Node.js and Gradle
RUN apk add --no-cache curl jq nodejs npm wget unzip maven

# Set environment variables
ENV MEMORY=2G
ENV EULA=true
ENV RCON_PASSWORD=Kingdom2026SecureRCON!

# Create data directory for persistent storage
RUN mkdir -p /data
RUN mkdir -p /data/plugins

# Copy server files to a template location first (plugins excluded by .dockerignore)
COPY . /minecraft-template/

# Copy world loader script and make it executable
COPY load_world.sh /minecraft-template/load_world.sh
RUN chmod +x /minecraft-template/load_world.sh

# Install Node.js dependencies
WORKDIR /minecraft-template
RUN npm install --production 2>/dev/null || true

# Create working plugins directory and build EssentialKingdom
RUN mkdir -p /minecraft-template/plugins

# Build EssentialKingdom plugin (economy, ranks, admin commands)
WORKDIR /minecraft-template/plugins/EssentialKingdom
RUN mvn clean package -q -DskipTests && \
    cp target/*.jar /minecraft-template/plugins/ && \
    echo "EssentialKingdom plugin built successfully"

# Build EarthWrap plugin (world wrapping mechanics)
WORKDIR /minecraft-template/plugins/EarthWrap
RUN mvn clean package -q -DskipTests && \
    cp target/*.jar /minecraft-template/plugins/ && \
    echo "EarthWrap plugin built successfully"

# Build RealisticWorld plugin (removes monsters and NPCs)
WORKDIR /minecraft-template/plugins/RealisticWorld
RUN mvn clean package -q -DskipTests && \
    cp target/*.jar /minecraft-template/plugins/ && \
    echo "RealisticWorld plugin built successfully"

# Skip problematic plugin downloads - use only our custom plugins
RUN echo "Skipping external plugin downloads - using custom plugins only"

# Skip placeholder creation - using only our custom plugins
RUN echo "Custom plugins ready - EssentialKingdom, EarthWrap, RealisticWorld"

# List all plugins
RUN echo "=== PLUGINS INSTALLED ===" && \
    ls -la /minecraft-template/plugins/ && \
    echo "========================"

# Download Paper 1.21.1 server JAR (build 133 - using direct API URL)
RUN wget -O /minecraft-template/server.jar "https://api.papermc.io/v2/projects/paper/versions/1.21.1/builds/133/downloads/paper-1.21.1-133.jar" && \
    ls -la /minecraft-template/server.jar

# Accept EULA
RUN echo "eula=true" > /minecraft-template/eula.txt

# Expose the ports (Railway uses this)
EXPOSE 25565
EXPOSE 8123

# Copy simple startup script
RUN mkdir -p /app/server
WORKDIR /app/server

# Create startup script for Railway with FORCED world loading
RUN echo '#!/bin/sh' > /app/start.sh && \
    echo 'set -e' >> /app/start.sh && \
    echo 'MEMORY_MIN=${MEMORY_MIN:-2G}' >> /app/start.sh && \
    echo 'MEMORY_MAX=${MEMORY_MAX:-4G}' >> /app/start.sh && \
    echo 'cd /app/server' >> /app/start.sh && \
    echo '[ ! -f eula.txt ] && echo "eula=true" > eula.txt' >> /app/start.sh && \
    echo 'mkdir -p plugins' >> /app/start.sh && \
    echo 'cp /minecraft-template/plugins/*.jar plugins/ 2>/dev/null || true' >> /app/start.sh && \
    echo 'echo "🌍 FORCING world overwrite from template..."' >> /app/start.sh && \
    echo 'rm -rf world world_nether world_the_end' >> /app/start.sh && \
    echo 'if [ -d "/minecraft-template/template/world" ]; then cp -r /minecraft-template/template/world . && echo "✅ WORLD FORCEFULLY LOADED FROM TEMPLATE"; else echo "❌ No template world found"; fi' >> /app/start.sh && \
    echo 'cp /minecraft-template/server.properties . 2>/dev/null || true' >> /app/start.sh && \
    echo 'cp /minecraft-template/server.jar . 2>/dev/null || true' >> /app/start.sh && \
    echo 'exec java -Xms${MEMORY_MIN} -Xmx${MEMORY_MAX} -XX:+UseG1GC -XX:+ParallelRefProcEnabled -XX:MaxGCPauseMillis=200 -XX:+UnlockExperimentalVMOptions -XX:+DisableExplicitGC -XX:+AlwaysPreTouch -XX:G1NewSizePercent=30 -XX:G1MaxNewSizePercent=40 -XX:G1HeapRegionSize=8M -XX:G1ReservePercent=20 -XX:G1HeapWastePercent=5 -XX:G1MixedGCCountTarget=4 -XX:InitiatingHeapOccupancyPercent=15 -XX:G1MixedGCLiveThresholdPercent=90 -XX:G1RSetUpdatingPauseTimePercent=5 -XX:SurvivorRatio=32 -XX:+PerfDisableSharedMem -XX:MaxTenuringThreshold=1 -Dusing.aikars.flags=https://mcflags.emc.gs -Daikars.new.flags=true -jar server.jar nogui' >> /app/start.sh && \
    chmod +x /app/start.sh

EXPOSE 25565
EXPOSE 8123

CMD ["sh", "/app/start.sh"]
