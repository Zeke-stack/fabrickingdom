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
COPY simple_start.sh /start.sh
RUN chmod +x /start.sh

CMD ["sh", "/start.sh"]
