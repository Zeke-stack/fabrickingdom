# 🏰 Kingdom Server Staff Management Setup

## 📋 Overview
Complete staff management system with colored text, custom teams, and comprehensive permissions for your Kingdom Server.

## 🎨 **Color System**
- **Green** [§a]: Moderator team
- **Red** [§c]: Administrator team  
- **Gold** [§6]: Event staff team
- **Dark Purple** [§5]: Founder team
- **Italic Gold** [§6§o]: Hint messages
- **Bold Red** [§c§l]: Announcements

## 👥 **Staff Teams & Permissions**

### 🟢 **Moderator Team** `[Mod]`
**Permissions:**
- `/spectate` - Enter spectator mode
- `/survival` - Enter survival mode
- `/to <player>` - Teleport to player
- `/bring <player>` - Bring player to you
- `/kick <player>` - Kick players
- `/freeze <player>` - Freeze/unfreeze players
- `/invsee <player>` - View player inventory
- `/history <player>` - View chat history

### 🔴 **Administrator Team** `[Admin]`
**All Moderator permissions PLUS:**
- `/ban <player> <time> <reason>` - Ban with time and reason
- `/pardon <player>` - Pardon banned players
- `/ipban <player> <time> <reason>` - IP ban players
- `/vanish` - Vanish from server list
- `/hint <message>` - Send italic gold hint messages
- `/announce <message>` - Send bold red announcements
- `/chatannounce <message>` - Send formatted chat announcements

### 🟡 **Event Staff Team** `[Event]`
**All Administrator permissions PLUS:**
- All CoreProtect commands
- All ItemBlocker commands
- `/give <item> <quantity>` - Give items to self only
- `/summon <entity>` - Summon entities
- `/reload` - Reload server
- `/restart` - Restart server
- `/stop` - Stop server
- `/gamemode <mode> [player]` - Change gamemode
- `/kill [player]` - Kill players
- `/banlist` - View ban list

### 🟣 **Founder Team** `[Founder]`
**All Event Staff permissions PLUS:**
- All Minecraft commands
- `/op <player>` - OP players
- `/deop <player>` - DeOP players
- `/audit <player>` - View complete player audit logs

## 🎒 **Inventory Viewer Features**
- **Visual 36-slot inventory display**
- **Armor slots (helmet, chestplate, leggings, boots)**
- **Interactive controls:**
  - 📖 Player Information (health, location, world)
  - ❄️ Freeze/Unfreeze toggle
  - 🌟 Teleport to player
  - 🧭 Bring player to you
- **Click items to view details**
- **Remove items from inventory**
- **Edit item quantities**

## 💬 **Chat Features**
- **Colored staff prefixes**
- **Staff chat** (`!staffchat <message>` or `!sc <message>`)
- **Chat history tracking** (last 50 messages)
- **Freeze prevention** (frozen players can't chat)
- **Custom join/leave messages**
- **Enhanced formatting for higher ranks**

## 🔧 **Setup Commands**

### Add Staff Members
```bash
/staff add <player> MODERATOR
/staff add <player> ADMINISTRATOR  
/staff add <player> EVENT_STAFF
/staff add <player> FOUNDER
```

### Remove Staff
```bash
/staff remove <player>
```

### View Staff
```bash
/staff list          # All staff members
/stafflist           # Detailed staff list with online status
!online              # Online staff only
```

## 🎮 **Special Features**

### Freeze System
- Players cannot move when frozen
- Periodic reminder messages
- Visual indicators (red wool block)
- Prevents command usage while frozen

### Hint & Announce System
```bash
/hint "Welcome to the Kingdom!"
# Sends: §6§oWelcome to the Kingdom! (italic gold)

/announce "Server restart in 5 minutes!"  
# Sends: §c§lServer restart in 5 minutes! (bold red)

/chatannounce "Event starting soon!"
# Sends: Formatted chat announcement
```

### Inventory Management
- Real-time inventory viewing
- Item information display
- Quick actions (teleport, bring, freeze)
- Armor slot management
- Item removal and editing

## 🔍 **Audit System**
Founder-only comprehensive player auditing:
```bash
/audit <player>
# Shows:
# - Current location and world
# - Health and food levels  
# - Gamemode
# - IP address
# - Staff rank
# - Recent activity
```

## 📊 **Chat History**
- Automatically tracks last 50 messages per player
- Staff can view any player's chat history
- Timestamped entries
- Searchable through staff commands

## 🛡️ **Security Features**
- Permission-based command access
- Frozen player restrictions
- Vanish system for administrators
- IP banning capabilities
- Audit logging for founders

## 🚀 **Quick Start**

1. **Add your first staff member:**
   ```bash
   /op <your_name>
   /staff add <your_name> FOUNDER
   ```

2. **Add moderators:**
   ```bash
   /staff add <mod_name> MODERATOR
   ```

3. **Test commands:**
   ```bash
   /hint "System is ready!"
   /announce "Staff system activated!"
   /invsee <player>
   ```

## 📝 **Examples**

### Moderator Daily Tasks
```bash
/freeze <suspicious_player>
/invsee <suspicious_player>
/history <suspicious_player>
/kick <rule_breaker> "Griefing"
/to <player_needing_help>
```

### Administrator Management
```bash
/ban <griefer> 7d "Griefing spawn"
/ipban <hacker> permanent "Hacking"
/vanish
/announce "Maintenance in 10 minutes"
/hint "New rules available at /warp rules"
```

### Event Staff Operations
```bash
/give diamond 64
/summon zombie
/gamemode creative <event_winner>
/reload
/announce "Event starting now!"
```

## 🎯 **Tips**
- Use `!staffchat` for private staff communication
- Freeze players before investigating
- Check chat history for rule violations
- Use vanish for monitoring
- Audit players for suspicious activity
- Set up regular staff meetings

---

**Your Kingdom Server now has a professional staff management system with beautiful colored text and comprehensive features! 🏰✨**
