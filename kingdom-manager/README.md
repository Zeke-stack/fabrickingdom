# 🏰 Kingdom Server Manager

A powerful Windows application for managing your Minecraft Kingdom Server with a dark, professional UI.

## Features

### 🖥️ **Server Console**
- Real-time server output monitoring
- Command execution with history
- Colored console output (green text on black)
- Auto-scrolling to latest messages

### 👥 **Player Management**
- Live player list with online status
- Quick actions: OP, DeOP, Kick, Ban
- Player coin balance viewing
- Player teleportation and gamemode changes

### 🎒 **Inventory Editor**
- Visual 36-slot inventory viewer
- Click-to-edit inventory slots
- Item and amount editing
- Real-time inventory updates
- Support for all Minecraft items

### ⚡ **Quick Commands**
- **Server Commands**: Restart, Save, Status, Weather, Time
- **Kingdom Commands**: Broadcast, Give Items, Gamemode, Teleport
- **Utility Commands**: World Info, Performance, Find Player, Custom Commands

### 🎨 **Dark Theme UI**
- Professional dark almost-black interface
- White text for high contrast
- Organized panel layout
- Responsive design

## Installation

### Option 1: Download Pre-built EXE
1. Download `KingdomServerManager.exe` from the releases
2. Run the executable - no installation required!

### Option 2: Build from Source
1. Clone this repository
2. Run `build.bat` to create the executable
3. Find the EXE in the `dist` folder

## Usage

1. **Launch the Application**
   - Double-click `KingdomServerManager.exe`
   - The app will automatically connect to your Kingdom Server

2. **Monitor Console**
   - View real-time server logs
   - Type commands and press Enter to execute

3. **Manage Players**
   - Select a player from the list
   - Use action buttons for quick moderation

4. **Edit Inventory**
   - Click on any inventory slot
   - Enter item name and amount
   - Click "Set Item" to apply changes

5. **Execute Commands**
   - Use the quick command buttons
   - Or enter custom commands in the console

## Server Configuration

The app connects to your Railway server by default:
- **IP**: `production-us-east4-eqdc4a.railway.app`
- **Port**: `25565`
- **RCON Port**: `25575`

To change the server connection, edit the `main.py` file before building.

## Requirements

- Windows 10 or higher
- .NET Framework (included with Windows)
- Internet connection for server communication

## Features in Detail

### Console Features
- ✅ Real-time log streaming
- ✅ Command history
- ✅ Auto-scroll
- ✅ Timestamp formatting
- ✅ Color-coded output

### Player Management
- ✅ Live player list
- ✅ Player statistics
- ✅ Quick moderation tools
- ✅ Coin balance viewing
- ✅ Teleportation controls

### Inventory Editor
- ✅ Visual inventory grid (36 slots)
- ✅ Item search and selection
- ✅ Stack size editing
- ✅ Real-time updates
- ✅ Item validation

### Command System
- ✅ 15+ built-in commands
- ✅ Custom command execution
- ✅ Batch operations
- ✅ Command history
- ✅ Confirmation dialogs

## Security

- Encrypted server communication
- Local authentication
- Secure RCON connection
- No data storage on client

## Support

For issues and feature requests:
- Create an issue on GitHub
- Join our Discord server
- Check the documentation

## License

This project is licensed under the MIT License - see the LICENSE file for details.

---

**Made with ❤️ for Kingdom Server Administrators**
