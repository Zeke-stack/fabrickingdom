# 🚂 Railway Deployment Guide

## 🏰 Kingdom of Minecraftia - Railway Ready

This project is fully configured for Railway deployment with automatic building and deployment.

## 📋 Railway Configuration

### ✅ Pre-configured Files:
- `railway.toml` - Railway service configuration
- `Dockerfile` - Optimized container build
- `nixpacks.toml` - Build instructions
- `start.sh` - Server startup script

### 🚀 Deployment Settings:
- **Memory**: 4GB RAM (recommended for medieval plugin)
- **CPU**: 2000m (2 cores)
- **Storage**: 20GB persistent volume
- **Port**: 25565 (Minecraft default)
- **Health Check**: TCP port 25565

## 🛠️ Quick Deploy to Railway

### 1. Connect Repository
```bash
# Connect your GitHub repo to Railway
# Railway will auto-detect the configuration
```

### 2. Configure Environment Variables
Railway will automatically set these from `railway.toml`:
- `MEMORY_MIN=2G`
- `MEMORY_MAX=4G`
- `SERVER_NAME=Kingdom of Minecraftia`
- `MOTD=§6✦ Kingdom of Minecraftia §7- §eMedieval Roleplay §6✦`

### 3. Deploy
```bash
# Railway will automatically:
# 1. Build the KingdomCommands plugin using Maven
# 2. Download Paper 1.21.1 server
# 3. Install medieval resource pack
# 4. Start the optimized server
```

## 📦 Build Process

### Phase 1: Build
- Install Maven and Git
- Build KingdomCommands plugin from source
- Verify plugin compilation
- Prepare resource pack

### Phase 2: Deploy
- Download PaperMC 1.21.1
- Configure server.properties
- Install plugins and resources
- Start server with JVM optimizations

## 🔧 Railway Features

### ✅ Automatic Health Checks
- TCP health check on port 25565
- 30-second intervals
- 60-second startup grace period
- Auto-restart on failure

### ✅ Persistent Storage
- 20GB volume for world data
- Auto-save every 60 seconds
- Maximum 10 backups
- World persistence across deployments

### ✅ Optimized Performance
- Aikar's JVM flags applied
- G1GC garbage collector
- 4GB memory allocation
- Optimized view distance

## 🎮 Server Features on Railway

### 🏰 Medieval Plugin System
- KingdomCommands with 40+ commands
- Medieval ranks (King, Duke, Knight, etc.)
- Royal announcements and decrees
- Staff management system
- Coin economy

### 📦 Resource Pack
- Medieval language pack
- Custom item names
- Kingdom-themed GUI
- Royal styling

### ⚙️ Server Configuration
- Hard difficulty for kingdom experience
- 20 player slots
- PVP enabled for combat
- Online mode for authentication

## 🌐 Accessing Your Server

### Once Deployed:
1. **Server IP**: Provided by Railway
2. **Port**: 25565 (default)
3. **Connect**: `IP:25565` in Minecraft

### Example:
```
Server: minecraft.your-project.railway.app
Port: 25565
```

## 🔍 Monitoring & Logs

### Railway Dashboard:
- Real-time server status
- Resource usage monitoring
- Live server logs
- Deployment history

### Server Commands:
```bash
# Check server status
/kingdom info

# List plugins
/plugins

# Check online players
/list

# Server performance
/tps
```

## 🚨 Troubleshooting

### Common Issues:
1. **Build fails**: Check Maven dependencies
2. **Server won't start**: Verify memory allocation
3. **Plugin errors**: Check compilation logs
4. **Connection issues**: Verify port 25565 is open

### Railway Logs:
```bash
# View build logs
railway logs

# View server logs
railway logs --follow

# Check deployment status
railway status
```

## 🔄 Updates & Redeployment

### Automatic Updates:
- Push to GitHub → Auto-rebuild → Auto-redeploy
- Plugin changes trigger rebuild
- Configuration updates apply on restart

### Manual Redeploy:
```bash
# Trigger new deployment
railway up

# Restart server
railway restart
```

## 📊 Scaling Options

### Upgrade Resources:
```toml
# In railway.toml
[services.resources]
memory = "8192Mi"    # 8GB RAM
cpu = "4000m"        # 4 cores
```

### Add More Storage:
```toml
[services.volumes]
size = "50Gi"       # 50GB storage
```

## 🎯 Railway Optimizations

### ✅ Pre-configured:
- Docker multi-stage builds
- Maven dependency caching
- Health checks and monitoring
- Automatic restarts
- Persistent world data

### 🚀 Performance:
- Optimized JVM settings
- G1GC garbage collection
- Memory-efficient container
- Fast startup times

## 📞 Support

### Railway Documentation:
- [Railway Docs](https://docs.railway.app/)
- [Minecraft Guide](https://docs.railway.app/guides/deploy-minecraft)

### Kingdom Server:
- Check `GUARANTEED_SETUP.md` for full documentation
- All commands listed in plugin.yml
- Medieval theme fully configured

---

**🎉 Your medieval kingdom server is now Railway-ready!**

Simply push to GitHub and Railway will handle the rest. The server will automatically build, deploy, and start with all medieval features enabled.
