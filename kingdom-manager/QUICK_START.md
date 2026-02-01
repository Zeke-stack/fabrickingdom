# 🚀 Quick Start Guide

## Building the EXE

### Step 1: Install Dependencies
```bash
pip install pyinstaller pillow requests
```

### Step 2: Create Icon (Optional)
```bash
python create_icon.py
```

### Step 3: Build the Application
```bash
# Option 1: Use the batch file
build.bat

# Option 2: Use Python directly
python build_exe.py

# Option 3: Manual PyInstaller command
pyinstaller --onefile --windowed --name=KingdomServerManager --icon=icon.ico main.py
```

### Step 4: Find Your EXE
The built executable will be in the `dist` folder:
```
dist/KingdomServerManager.exe
```

## Running the Application

1. **Double-click** `KingdomServerManager.exe`
2. **Wait for connection** to your Kingdom Server
3. **Start managing** your server!

## First Time Setup

1. **Server Connection**: The app automatically connects to your Railway server
2. **Player List**: View online players in the right panel
3. **Console**: Monitor server activity in the left panel
4. **Commands**: Use quick command buttons at the bottom

## Basic Usage

### 🎮 **Player Management**
- Click a player name to select them
- Use action buttons: OP, DeOP, Kick, Ban, Coins
- View and edit their inventory

### 🎒 **Inventory Editor**
- Click any inventory slot to select it
- Enter item name and amount
- Click "Set Item" to apply changes
- Use "Clear" to empty the slot

### 📟 **Console Commands**
- Type commands in the input box
- Press Enter to execute
- View results in the console output

### ⚡ **Quick Commands**
- **Server**: Restart, Save, Status, Weather, Time
- **Kingdom**: Broadcast, Give Items, Gamemode, Teleport  
- **Utility**: World Info, Performance, Find Player

## Troubleshooting

### **Connection Issues**
- Check your internet connection
- Verify server IP in `main.py`
- Ensure RCON is enabled on server

### **Build Issues**
- Install all requirements: `pip install -r requirements.txt`
- Run as Administrator if needed
- Check Python version (3.7+ required)

### **UI Issues**
- Ensure Windows display scaling is 100%
- Update graphics drivers
- Run in compatibility mode if needed

## Server Configuration

### RCON Setup (Server Side)
Add to `server.properties`:
```properties
enable-rcon=true
rcon.port=25575
rcon.password=your_secure_password
```

### Plugin Requirements
For full functionality, install these plugins on your server:
- KingdomCommands (included with your server)
- An inventory management plugin (for inventory editor)

## Support

- **Documentation**: Check README.md
- **Issues**: Report on GitHub
- **Community**: Join our Discord

---

**Ready to manage your Kingdom Server like a pro! 🏰**
