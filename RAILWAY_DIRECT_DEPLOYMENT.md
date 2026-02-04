# 🚂 Railway Direct Deployment Guide

## ✅ Fixed Issues

### 1. **GitHub Dependency Removed**
- ❌ **Old**: Procfile referenced Heroku deployment path `/app/server`
- ✅ **New**: Direct Railway deployment with Docker build
- ❌ **Old**: `simple_start.sh` used `/data` paths
- ✅ **New**: Updated to use `/app/server` paths for Railway

### 2. **World Loading Fixed**
- ❌ **Old**: Server was generating new world with Minecraft seed even when template existed
- ✅ **New**: Startup script properly loads `template/world` before server starts
- **Key Fix**: World is copied during container startup BEFORE server JAR runs
- **Benefit**: Players always get the custom Earth world, not random seed

### 3. **Server.properties Corrected**
- Fixed typo: `harcore` → `hardcore`
- Fixed typo: `ticks-per.animal-spawns` → `ticks-per-animal-spawns`
- Fixed typo: `ticks-per.monster-spawns` → `ticks-per-monster-spawns`

---

## 🚀 How to Deploy to Railway

### **Step 1: Connect Your Repository**
```bash
# In Railway Dashboard:
1. Create new project
2. Select "GitHub" as source
3. Connect your fabrickingdom repository
4. Railway auto-detects Dockerfile
```

### **Step 2: Configure Environment (Optional)**
Railway will use these defaults, but you can customize in Railway Dashboard:
```
MEMORY_MIN=2G
MEMORY_MAX=4G
```

### **Step 3: Deploy**
- Push to your GitHub repository
- Railway automatically:
  1. ✅ Detects Dockerfile
  2. ✅ Builds server JAR
  3. ✅ Builds plugins (EssentialKingdom, EarthWrap, RealisticWorld)
  4. ✅ Copies template world
  5. ✅ Starts server with proper configuration

---

## 📋 Deployment Flow

```
GitHub Push
    ↓
Railway Detects Dockerfile
    ↓
Build Phase:
  - Alpine JDK 21 image
  - Download Paper 1.21.1 server
  - Build 3 custom plugins with Maven
  - Copy template world to build context
    ↓
Run Phase (/app/start.sh):
  - Accept EULA
  - Copy server.jar to /app/server
  - LOAD TEMPLATE WORLD from /minecraft-template/template/world
  - Copy plugins (*.jar)
  - Start Java with Aikar's optimized flags
    ↓
Server Running on Railway
```

---

## 🌍 World Loading Verification

### **What Happens on First Start:**
```
✅ eula.txt created (auto-accept)
✅ Plugins installed (3 custom plugins)
✅ World loaded from template/world
✅ level.dat found - existing world confirmed
✅ Java started with 2G-4G memory
```

### **What Happens on Restart:**
```
✅ Server directory already exists (/app/server)
✅ World directory already exists
✅ Plugins already installed
✅ Server resumes with saved world data
```

---

## 🔧 Server Configuration

### **Memory Settings**
- **Default**: 2GB min, 4GB max
- **Railway CPU Credits**: ~2 credits per hour
- **Environment Variables**:
  ```
  MEMORY_MIN=2G    # Minimum heap
  MEMORY_MAX=4G    # Maximum heap
  ```

### **World Settings**
- **World Size**: 30,000 x 30,000 blocks (Earth illusion)
- **Seed**: `EarthKingdom2024` (fallback only if template missing)
- **Difficulty**: HARD
- **Max Players**: 20
- **Game Mode**: SURVIVAL
- **PVP**: Enabled

### **Plugins**
1. **EssentialKingdom** - Economy & ranks system
2. **EarthWrap** - World wrapping mechanics
3. **RealisticWorld** - No monster spawning

---

## ⚡ Performance Optimizations

Server uses Aikar's optimized JVM flags:
```
-XX:+UseG1GC                    # Garbage collector
-XX:+ParallelRefProcEnabled     # Multi-threaded GC
-XX:MaxGCPauseMillis=200        # GC pause time
-XX:G1HeapRegionSize=8M         # Optimal region size
-XX:G1ReservePercent=20         # Reserve memory
```

**Expected Performance**:
- TPS: 15-20 (with 2-4 players)
- CPU: ~40-60% usage
- Memory: 2.5GB-3.5GB used

---

## 🆘 Troubleshooting

### **World Not Loading**
```bash
# Check Railway logs for:
"✅ World loaded from template"

# If missing:
1. Verify template/world/level.dat exists in repo
2. Check Docker build logs for copy errors
3. Restart Railway deployment
```

### **Plugins Not Installed**
```bash
# Check logs for:
"✅ Plugins installed: 3"

# If fewer than 3:
1. Check Maven build logs (EssentialKingdom, EarthWrap, RealisticWorld)
2. Verify pom.xml files are valid
3. Clear Railway build cache and rebuild
```

### **Java Out of Memory**
```bash
# Increase in Railway Dashboard:
MEMORY_MAX=6G    # Up from 4G
```

### **Server Port Not Responding**
```bash
# Railway might need port mapping:
1. Port 25565 (Minecraft)
2. Port 8123 (Dynmap, if enabled)
3. Check firewall not blocking
```

---

## 📊 Monitoring

### **Railway Dashboard Shows:**
- ✅ Build logs (compilation status)
- ✅ Runtime logs (server startup)
- ✅ CPU/Memory usage
- ✅ Network I/O

### **Server Logs Show:**
```
🏰 Starting Kingdom Server on Railway...
✅ Plugins installed: 3
✅ World loaded from template
🚀 Starting Kingdom Server...
```

---

## 🔄 Updates

### **To Update Plugins:**
1. Edit plugin source in `/plugins/PluginName/`
2. Push to GitHub
3. Railway auto-rebuilds and redeploys
4. Maven recompiles plugins

### **To Update World:**
1. Update `/template/world/` folder
2. Push to GitHub
3. For existing deployments, delete world in Railway > Deploy > Remove Data
4. Restart deployment to reload template

---

## 📝 Important Files

| File | Purpose |
|------|---------|
| `Dockerfile` | Build configuration (no GitHub reference) |
| `Procfile` | Startup command for Railway |
| `simple_start.sh` | World loading & server startup script |
| `server.properties` | Server configuration |
| `template/world/` | Pre-built world (loaded on startup) |
| `plugins/*/` | Custom plugins with Maven build |

---

## ✨ Next Steps

1. ✅ Push changes to GitHub
2. ✅ Railway detects new Dockerfile
3. ✅ Build starts automatically
4. ✅ Plugins compile with Maven
5. ✅ Server starts with template world
6. ✅ Players can join!

**No GitHub webhooks needed. No manual deployment. Pure Railway magic!** 🚂✨
