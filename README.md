# Fabrickingdom - Kingdom Minecraft Server

A medieval-themed Minecraft server with custom commands and coin economy system.

## Features

### 🏰 Kingdom Commands
- `/kban <player> [reason]` - Ban players with medieval exile messages
- `/kkick <player> [reason]` - Kick players with royal removal messages  
- `/kop <player>` - Op players with royal promotion messages
- `/kdeop <player>` - Deop players with royal demotion messages

### 💰 Coin Economy
- **1 Gold Nugget = 1 Coin**
- **1 Gold Ingot = 9 Coins** 
- **1 Gold Block = 81 Coins**
- `/coins` - Shows total coins from inventory + ender chest
- `/balance` - Quick balance check
- Real-time coin display in tab list

### ✨ Medieval UI
- Beautiful bordered message boxes
- Kingdom-themed language and styling
- Custom join/leave messages
- Tab list with coin balance display

## Deployment

### Railway (Recommended)
1. Connect this repository to Railway
2. Railway will automatically build and deploy using the provided Dockerfile
3. Server will be available at the Railway URL

### Manual Setup
1. Ensure you have Java 21+ installed
2. Download PaperMC 1.21.1
3. Place `KingdomCommands-1.0.0.jar` in the `plugins/` folder
4. Start the server

## Configuration

The plugin creates a `config.yml` with customizable settings:
- Coin values and economy settings
- Message styling options
- Tab list configuration
- Command permissions

## Development

Built with:
- PaperMC 1.21.1
- Java 21
- Medieval-themed UI design
- Custom coin economy system

## License

MIT License - Feel free to use and modify for your own kingdom server!
