# Railway Volume Setup for Minecraft Server

## Volume Configuration

The Railway deployment is configured with a persistent volume at:

**Mount Path:** `/app/server`

This volume contains all critical server data:
- World files (`/app/server/world/`, `/app/server/world_nether/`, `/app/server/world_the_end/`)
- Player data and statistics
- Plugin configurations
- Server logs
- KingdomCommands plugin data

## Volume Size
- **10GB** allocated storage
- **Automatically persists** across deployments and restarts

## Auto-Save Configuration

The server is configured for maximum data safety:

### Built-in Auto-Save:
- **Saves every 60 seconds** automatically
- **No timeout or lag** - uses optimized async saving
- **Final save on shutdown** before server stops

### Performance Optimizations:
- **G1GC Garbage Collector** with 200ms max pause time
- **Reduced view distance** (6 chunks) for better performance
- **Optimized entity settings** to prevent lag
- **Async world saving** to prevent stuttering

### Railway Volume Benefits:
- **Instant persistence** - data saved immediately to volume
- **No data loss** on container restarts
- **Automatic backups** through Railway's volume system
- **High-speed storage** with Railway's infrastructure

## Accessing Volume Data

If you need to access server files:
1. Go to your Railway project
2. Click on the service
3. Use the "Exec" tab to access the container
4. Navigate to `/app/server` to view all files

## Backup Strategy

The auto-save system ensures:
- **Continuous protection** with 60-second intervals
- **Zero downtime** during saves
- **Complete world preservation** including player inventories
- **Plugin state persistence** for KingdomCommands data

Your kingdom server data is completely safe with this setup!
