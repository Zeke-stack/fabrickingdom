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

# Copy server files
COPY server.properties /app/server/
COPY plugins/KingdomPlugin.jar /app/server/plugins/
COPY resources /app/server/
COPY start.sh /app/start.sh

# Make script executable
RUN chmod +x /app/start.sh

# Expose ports
EXPOSE 25565

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD netstat -an | grep :25565 || exit 1

# Start the server
CMD ["/app/start.sh"]
