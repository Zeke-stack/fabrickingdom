FROM eclipse-temurin:21-jdk-alpine

# Set working directory
WORKDIR /app

# Install curl, wget, and Java
RUN apk update && apk add --no-cache curl wget openjdk21-jdk git maven

# Create server directory structure
RUN mkdir -p /app/server/world /app/server/world_nether /app/server/world_the_end /app/server/plugins /app/server/logs /app/server/resources

# Download PaperMC server to server directory directly
RUN wget -O /app/server/paper.jar https://api.papermc.io/v2/projects/paper/versions/1.21.1/builds/133/downloads/paper-1.21.1-133.jar

# Create EULA file
RUN echo "eula=true" > /app/server/eula.txt

# Copy optimized server.properties
COPY server.properties /app/server/server.properties

# Build and install KingdomCommands plugin (with multiple fallbacks)
COPY plugins/KingdomCommands /tmp/KingdomCommands
COPY plugins/SimpleKingdom /tmp/SimpleKingdom
COPY plugins/BasicKingdom /tmp/BasicKingdom
RUN echo "🏰 Building KingdomCommands plugin..." && \
    if cd /tmp/KingdomCommands && mvn clean package -q -DskipTests 2>/dev/null; then \
        echo "✅ KingdomCommands built successfully!" && \
        cp target/KingdomCommands-1.0.0.jar /app/server/plugins/ && \
        echo "✓ KingdomCommands plugin installed!"; \
    elif cd /tmp/SimpleKingdom && mvn clean package -q 2>/dev/null; then \
        echo "⚠️ KingdomCommands failed, using SimpleKingdom fallback..." && \
        cp target/SimpleKingdom-1.0.0.jar /app/server/plugins/ && \
        echo "✓ SimpleKingdom fallback plugin installed!"; \
    else \
        echo "⚠️ SimpleKingdom failed, using BasicKingdom fallback..." && \
        cd /tmp/BasicKingdom && \
        mkdir -p target/classes && \
        javac -cp "/app/server/paper.jar" -d target/classes src/main/java/com/kingdom/BasicKingdom.java && \
        jar cf target/BasicKingdom.jar -C src/main/resources . -C target/classes . && \
        cp target/BasicKingdom.jar /app/server/plugins/ && \
        echo "✓ BasicKingdom ultra-fallback plugin installed!"; \
    fi && \
    ls -la /app/server/plugins/

# Copy resource pack
COPY resources /app/server/resources
RUN echo "✓ Medieval resource pack installed!"

# Create optimized startup script
RUN echo '#!/bin/sh' > /app/start.sh && \
    echo 'echo "🏰 Starting Kingdom of Minecraftia Server..."' >> /app/start.sh && \
    echo 'cd /app/server' >> /app/start.sh && \
    echo 'echo "📋 Server Configuration:"' >> /app/start.sh && \
    echo 'echo "  Memory: ${MEMORY_MIN:-2G} - ${MEMORY_MAX:-4G}"' >> /app/start.sh && \
    echo 'echo "  World: world"' >> /app/start.sh && \
    echo 'echo "  Plugins: $(ls plugins/ | wc -l) installed"' >> /app/start.sh && \
    echo 'echo "✅ Kingdom server ready!"' >> /app/start.sh && \
    echo 'exec java -Xms${MEMORY_MIN:-2G} -Xmx${MEMORY_MAX:-4G} -XX:+UseG1GC -XX:+ParallelRefProcEnabled -XX:MaxGCPauseMillis=200 -XX:+UnlockExperimentalVMOptions -XX:+DisableExplicitGC -XX:+AlwaysPreTouch -XX:G1NewSizePercent=30 -XX:G1MaxNewSizePercent=40 -XX:G1HeapRegionSize=8M -XX:G1ReservePercent=20 -XX:G1HeapWastePercent=5 -XX:G1MixedGCCountTarget=4 -XX:InitiatingHeapOccupancyPercent=15 -XX:G1MixedGCLiveThresholdPercent=90 -XX:G1RSetUpdatingPauseTimePercent=5 -XX:SurvivorRatio=32 -XX:+PerfDisableSharedMem -XX:MaxTenuringThreshold=1 -Dusing.aikars.flags=https://mcflags.emc.gs -Daikars.new.flags=true -jar paper.jar nogui' >> /app/start.sh && \
    chmod +x /app/start.sh && \
    ls -la /app/start.sh

# Create nixpacks.toml for Railway
RUN echo '[build]' > /app/nixpacks.toml && \
    echo 'builder = "NIXPACKS"' >> /app/nixpacks.toml && \
    echo '' >> /app/nixpacks.toml && \
    echo '[phases.build]' >> /app/nixpacks.toml && \
    echo 'cmds = ["echo \"Building Kingdom Server...\""]' >> /app/nixpacks.toml && \
    echo '' >> /app/nixpacks.toml && \
    echo '[start]' >> /app/nixpacks.toml && \
    echo 'cmd = "cd /app/server && /app/start.sh"' >> /app/nixpacks.toml

# Expose ports
EXPOSE 25565

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD netstat -an | grep :25565 || exit 1

# Start the server
CMD ["/app/start.sh"]
