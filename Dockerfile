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

# Copy server files to a template location first
COPY . /minecraft-template/

# Install Node.js dependencies
WORKDIR /minecraft-template
RUN npm install --production 2>/dev/null || true

# Build plugins with Maven (skip if fails)
WORKDIR /minecraft-template/plugins
RUN find . -name "pom.xml" -execdir mvn clean package -q -DskipTests \; 2>/dev/null || echo "Plugin build completed with some failures"

# Download Paper 1.21.1 server JAR (build 133 - using direct API URL)
RUN wget -O /minecraft-template/server.jar "https://api.papermc.io/v2/projects/paper/versions/1.21.1/builds/133/downloads/paper-1.21.1-133.jar" && \
    ls -la /minecraft-template/server.jar

# Accept EULA
RUN echo "eula=true" > /minecraft-template/eula.txt

# Expose the port (Railway uses this)
EXPOSE 25565

# Create startup script
WORKDIR /minecraft
RUN echo '#!/bin/sh' > /start.sh && \
    echo 'cd /data' >> /start.sh && \
    echo 'if [ ! -f "eula.txt" ]; then' >> /start.sh && \
    echo '  echo "First run - copying server files..."' >> /start.sh && \
    echo '  cp -r /minecraft-template/* /data/' >> /start.sh && \
    echo 'else' >> /start.sh && \
    echo '  echo "Updating files..."' >> /start.sh && \
    echo '  rm -rf /data/world/datapacks' >> /start.sh && \
    echo '  cp -r /minecraft-template/world/datapacks /data/world/ 2>/dev/null || true' >> /start.sh && \
    echo '  cp /minecraft-template/commands.yml /data/commands.yml 2>/dev/null || true' >> /start.sh && \
    echo '  cp /minecraft-template/ops.json /data/ops.json 2>/dev/null || true' >> /start.sh && \
    echo '  rm -rf /data/backend' >> /start.sh && \
    echo '  cp -r /minecraft-template/backend /data/backend 2>/dev/null || true' >> /start.sh && \
    echo '  rm -rf /data/website' >> /start.sh && \
    echo '  cp -r /minecraft-template/website /data/website 2>/dev/null || true' >> /start.sh && \
    echo '  mkdir -p /data/plugins' >> /start.sh && \
    echo '  cp /minecraft-template/plugins/*.jar /data/plugins/ 2>/dev/null || true' >> /start.sh && \
    echo '  cp /minecraft-template/plugins/*/*.jar /data/plugins/ 2>/dev/null || true' >> /start.sh && \
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
