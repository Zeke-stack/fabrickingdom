FROM openjdk:21-jdk-slim

# Set working directory
WORKDIR /app

# Install curl and wget
RUN apt-get update && apt-get install -y curl wget && rm -rf /var/lib/apt/lists/*

# Create server directory
RUN mkdir -p /app/server

# Download PaperMC server
RUN wget -O paper.jar https://api.papermc.io/v2/projects/paper/versions/1.21.1/builds/133/downloads/paper-1.21.1-133.jar

# Accept EULA
RUN echo "eula=true" > eula.txt

# Set server properties
RUN echo "server-port=25565" > server.properties && \
    echo "enable-rcon=true" >> server.properties && \
    echo "rcon.port=25575" >> server.properties && \
    echo "rcon.password=changeme" >> server.properties && \
    echo "motd=Kingdom Server - Medieval Roleplay" >> server.properties && \
    echo "difficulty=normal" >> server.properties && \
    echo "gamemode=survival" >> server.properties && \
    echo "level-type=default" >> server.properties

# Create plugins directory
RUN mkdir -p /app/server/plugins

# Copy KingdomCommands plugin
COPY plugins/KingdomCommands-1.0.0.jar /app/server/plugins/

# Expose ports
EXPOSE 25565 25575

# Set up startup script
RUN echo '#!/bin/bash\n\
cd /app/server\n\
java -Xms1G -Xmx2G -jar paper.jar nogui' > /app/start.sh && \
chmod +x /app/start.sh

# Start the server
CMD ["/app/start.sh"]
