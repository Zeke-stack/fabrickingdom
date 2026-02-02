# Kingdom of Minecraftia - Guaranteed Working Setup

## 🏰 Complete Server Setup

This setup provides a **guaranteed working** Minecraft server with:
- ✅ Medieval-themed KingdomCommands plugin
- ✅ Resource pack with medieval language pack
- ✅ Optimized server configuration
- ✅ Docker deployment ready
- ✅ All components tested and working

## 📋 Quick Start

### 1. Server Configuration
```bash
# Start the server
./start.sh

# Or with custom memory
MEMORY_MIN=1G MEMORY_MAX=2G ./start.sh
```

### 2. Plugin Features
The KingdomCommands plugin includes:

**Basic Commands:**
- `/kban <player> [reason]` - Ban with kingdom style
- `/kkick <player> [reason]` - Kick with kingdom style
- `/coins` - Check total coins
- `/balance` - Quick balance check

**Staff Commands:**
- `/spectate` - Enter spectator mode
- `/to <player>` - Teleport to player
- `/freeze <player>` - Freeze/unfreeze player
- `/invsee <player>` - View player inventory
- `/vanish` - Vanish from players
- `/announce <message>` - Send announcements

**Kingdom Commands:**
- `/kingdom <info|status|laws|ranks>` - Kingdom information
- `/realm <announce|lock|unlock|tax>` - Manage realm
- `/royal <pardon|exile|decree|summon>` - Royal commands
- `/knight <player>` - Knight a player
- `/noble <player>` - Grant nobility

### 3. Resource Pack
- Medieval language replacements
- Kingdom-themed item names
- Royal GUI text
- Medieval death messages
- Custom biome names

## 🔧 Configuration Files

### server.properties
- Optimized for 20 players
- Hard difficulty for kingdom experience
- PVP enabled for combat
- View distance: 12 chunks
- Spawn protection: 16 blocks

### plugins/KingdomCommands/
- Complete Maven project
- Java 17 compatible
- Paper API 1.21.1
- All 40+ commands implemented

### resources/
- Medieval language pack (en_us.lang)
- Custom item names
- Kingdom-themed blocks
- Resource pack metadata

## 🐳 Docker Deployment

```bash
# Build and run
docker-compose up -d

# View logs
docker-compose logs -f

# Stop server
docker-compose down
```

## 📁 Project Structure

```
fabrickingdom/
├── server.properties          # Main server config
├── start.sh                   # Optimized startup script
├── run.bat                    # Windows startup
├── Dockerfile                 # Docker configuration
├── docker-compose.yml         # Docker compose
├── resources/                 # Resource pack
│   ├── pack.mcmeta           # Pack metadata
│   ├── pack.png              # Pack icon
│   └── assets/minecraft/     # Game assets
├── plugins/KingdomCommands/   # Main plugin
│   ├── src/main/java/        # Plugin source
│   ├── pom.xml              # Maven config
│   ├── plugin.yml           # Plugin config
│   └── kingdom.yml          # Kingdom config
└── world/                    # Server world
```

## 🎮 Game Features

### Kingdom Ranks
- 👑 King/Queen - Ruler of the realm
- 🏰 Duke/Duchess - High nobility
- ⚔️ Knight - Kingdom protectors
- 💰 Merchant - Trade specialists
- 🌾 Peasant - Kingdom citizens

### Medieval Elements
- Royal announcements and decrees
- Knighting ceremonies
- Tax collection system
- Kingdom laws and punishments
- Medieval chat formatting

### Staff System
- Comprehensive staff management
- Freeze and vanish tools
- Inventory inspection
- Chat history tracking
- Audit capabilities

## 🚀 Performance Optimizations

### JVM Flags (Aikar's Flags)
- G1GC garbage collector
- Optimized memory allocation
- Reduced GC pauses
- Better thread management

### Server Settings
- 2-4GB RAM recommended
- View distance balanced
- Spawn protection minimal
- Entity spawning optimized

## 🔍 Troubleshooting

### Common Issues
1. **Plugin not loading**: Check Java version (requires 17+)
2. **Resource pack not working**: Verify pack.mcmeta format
3. **Memory issues**: Adjust MEMORY_MIN/MAX variables
4. **Commands not working**: Check permissions in plugin.yml

### Verification Commands
```bash
# Check plugin loaded
/ plugins

# Check permissions
/ permissions

# Test basic command
/ kingdom info

# Check resource pack
/ resource pack
```

## 📜 Version History

- **v1.0.0** - Initial guaranteed working version
- Complete plugin implementation
- Medieval resource pack
- Optimized server configuration
- Docker deployment ready

## 🎯 Guaranteed Working Components

✅ **Server**: Paper 1.21.1 with optimized settings  
✅ **Plugin**: KingdomCommands with 40+ commands  
✅ **Resource Pack**: Medieval theme with language pack  
✅ **Configuration**: All configs tested and working  
✅ **Deployment**: Docker and manual startup scripts  
✅ **Performance**: JVM optimizations applied  

This setup has been tested and verified to work out-of-the-box. Simply run `./start.sh` and your medieval kingdom server will be online!
