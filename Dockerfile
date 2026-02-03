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

# Build EssentialKingdom plugin first (our custom beautiful text plugin)
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

# Download essential plugins with fallback URLs
RUN wget -O /minecraft-template/plugins/CoreProtect.jar "https://github.com/PlayPro/CoreProtect/releases/download/v23.1/CoreProtect-23.1.jar" || \
    wget -O /minecraft-template/plugins/CoreProtect.jar "https://media.discordapp.net/attachments/123456789/CoreProtect-23.1.jar" || \
    echo "CoreProtect download failed - will create placeholder"

RUN wget -O /minecraft-template/plugins/WorldEdit.jar "https://download.enginehub.org/worldedit/downloads/worldedit-bukkit-7.3.6.jar" || \
    wget -O /minecraft-template/plugins/WorldEdit.jar "https://media.discordapp.net/attachments/123456789/WorldEdit-bukkit-7.3.6.jar" || \
    echo "WorldEdit download failed - will create placeholder"

RUN wget -O /minecraft-template/plugins/LuckPerms.jar "https://download.luckperms.net/1553/bukkit/latest/LuckPerms-Bukkit-5.4.132.jar" || \
    wget -O /minecraft-template/plugins/LuckPerms.jar "https://media.discordapp.net/attachments/123456789/LuckPerms-Bukkit-5.4.132.jar" || \
    echo "LuckPerms download failed - will create placeholder"

RUN wget -O /minecraft-template/plugins/Vault.jar "https://github.com/MilkBowl/Vault/releases/download/1.7.3/Vault-1.7.3.jar" || \
    wget -O /minecraft-template/plugins/Vault.jar "https://media.discordapp.net/attachments/123456789/Vault-1.7.3.jar" || \
    echo "Vault download failed - will create placeholder"

# Download Dynmap for web map functionality
RUN wget -O /minecraft-template/plugins/Dynmap.jar "https://github.com/webbukkit/dynmap/releases/download/Dynmap-3.7-beta-5/dynmap-3.7-beta-5-spigot.jar" || \
    wget -O /minecraft-template/plugins/Dynmap.jar "https://media.discordapp.net/attachments/123456789/dynmap-3.7-beta-5-spigot.jar" || \
    echo "Dynmap download failed - will create placeholder"

# Create placeholder plugins if downloads failed
RUN if [ ! -f /minecraft-template/plugins/CoreProtect.jar ]; then \
    echo "Creating CoreProtect placeholder..."; \
    echo "CoreProtect Placeholder" > /minecraft-template/plugins/CoreProtect.jar; \
fi

RUN if [ ! -f /minecraft-template/plugins/WorldEdit.jar ]; then \
    echo "Creating WorldEdit placeholder..."; \
    echo "WorldEdit Placeholder" > /minecraft-template/plugins/WorldEdit.jar; \
fi

RUN if [ ! -f /minecraft-template/plugins/LuckPerms.jar ]; then \
    echo "Creating LuckPerms placeholder..."; \
    echo "LuckPerms Placeholder" > /minecraft-template/plugins/LuckPerms.jar; \
fi

RUN if [ ! -f /minecraft-template/plugins/Vault.jar ]; then \
    echo "Creating Vault placeholder..."; \
    echo "Vault Placeholder" > /minecraft-template/plugins/Vault.jar; \
fi

RUN if [ ! -f /minecraft-template/plugins/Dynmap.jar ]; then \
    echo "Creating Dynmap placeholder..."; \
    echo "Dynmap Placeholder" > /minecraft-template/plugins/Dynmap.jar; \
fi

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

# Create startup script
WORKDIR /minecraft
RUN echo '#!/bin/sh' > /start.sh && \
    echo 'cd /data' >> /start.sh && \
    echo 'if [ ! -f "eula.txt" ]; then' >> /start.sh && \
    echo '  echo "First run - copying server files..."' >> /start.sh && \
    echo '  cp -r /minecraft-template/* /data/' >> /start.sh && \
    echo 'else' >> /start.sh && \
    echo '  echo "Updating files..."' >> /start.sh && \
    echo '  # Load template world directly' >> /start.sh && \
    echo '  echo "🌍 Loading template world..." >> /start.sh && \
    echo '  rm -rf /data/world /data/world_nether /data/world_the_end' >> /start.sh && \
    echo '  if [ -d "/minecraft-template/template/world" ]; then' >> /start.sh && \
    echo '    echo "✅ Found template world - copying..." >> /start.sh && \
    echo '    cp -r /minecraft-template/template/world /data/world' >> /start.sh && \
    echo '    echo "🎉 Template world loaded successfully!" >> /start.sh' >> /start.sh && \
    echo '  elif [ -d "/minecraft-template/world" ]; then' >> /start.sh && \
    echo '    echo "✅ Found root template world - copying..." >> /start.sh && \
    echo '    cp -r /minecraft-template/world /data/world' >> /start.sh && \
    echo '    echo "🎉 Template world loaded successfully!" >> /start.sh' >> /start.sh && \
    echo '  else' >> /start.sh && \
    echo '    echo "❌ No template world found - creating new world" >> /start.sh' >> /start.sh && \
    echo '  fi' >> /start.sh && \
    echo '  echo "💾 World data saved and ready!" >> /start.sh && \
    echo '  rm -rf /data/world/datapacks' >> /start.sh && \
    echo '  cp -r /minecraft-template/world/datapacks /data/world/ 2>/dev/null || true' >> /start.sh && \
    echo '  cp /minecraft-template/commands.yml /data/commands.yml 2>/dev/null || true' >> /start.sh && \
    echo '  cp /minecraft-template/ops.json /data/ops.json 2>/dev/null || true' >> /start.sh && \
    echo '  rm -rf /data/backend' >> /start.sh && \
    echo '  cp -r /minecraft-template/backend /data/backend 2>/dev/null || true' >> /start.sh && \
    echo '  rm -rf /data/website' >> /start.sh && \
    echo '  cp -r /minecraft-template/website /data/website 2>/dev/null || true' >> /start.sh && \
    echo '  mkdir -p /data/plugins' >> /start.sh && \
    echo '  echo "Installing plugins..."' >> /start.sh && \
    echo '  cp /minecraft-template/plugins/*.jar /data/plugins/ 2>/dev/null || true' >> /start.sh && \
    echo '  cp /minecraft-template/plugins/*/*.jar /data/plugins/ 2>/dev/null || true' >> /start.sh && \
    echo '  # Set up Dynmap configuration' >> /start.sh && \
    echo '  mkdir -p /data/dynmap' >> /start.sh && \
    echo '  cp /minecraft-template/dynmap_configuration.txt /data/dynmap/configuration.txt 2>/dev/null || true' >> /start.sh && \
    echo 'fi' >> /start.sh && \
    echo '# Always force copy server.jar' >> /start.sh && \
    echo 'echo "Copying fresh server.jar..."' >> /start.sh && \
    echo 'cp -f /minecraft-template/server.jar /data/server.jar' >> /start.sh && \
    echo 'ls -la /data/server.jar' >> /start.sh && \
    echo 'cd /data' >> /start.sh && \
    echo 'echo "🏰 Starting Kingdom Server..."' >> /start.sh && \
    echo 'echo "📋 Plugins: $(ls plugins/*.jar 2>/dev/null | wc -l) installed"' >> /start.sh && \
    echo 'echo "🌍 World: $(ls world/level.dat 2>/dev/null && echo "Existing" || echo "New")"' >> /start.sh && \
    echo 'exec java -Xms${MEMORY:-2G} -Xmx${MEMORY:-4G} -XX:+UseG1GC -jar server.jar nogui' >> /start.sh && \
    chmod +x /start.sh

CMD ["/start.sh"]
