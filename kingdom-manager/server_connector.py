import socket
import threading
import time
import json
from datetime import datetime

class MinecraftRCON:
    def __init__(self, host, port, password):
        self.host = host
        self.port = port
        self.password = password
        self.socket = None
        self.request_id = 1
        self.connected = False
        
    def connect(self):
        """Connect to RCON server"""
        try:
            self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            self.socket.settimeout(10)
            self.socket.connect((self.host, self.port))
            
            # Send authentication
            self.send_packet(3, self.password)
            response = self.read_packet()
            
            if response and response['id'] == -1:
                self.connected = False
                return False
                
            self.connected = True
            return True
            
        except Exception as e:
            print(f"RCON connection failed: {e}")
            self.connected = False
            return False
            
    def disconnect(self):
        """Disconnect from RCON server"""
        if self.socket:
            self.socket.close()
            self.connected = False
            
    def send_packet(self, type, data):
        """Send RCON packet"""
        if not self.socket:
            return None
            
        try:
            # Build packet
            payload = data.encode('utf-8') + b'\x00\x00'
            packet_length = len(payload) + 10
            
            # Create packet
            packet = bytearray()
            packet.extend(packet_length.to_bytes(4, 'little'))
            packet.extend(self.request_id.to_bytes(4, 'little'))
            packet.extend(type.to_bytes(4, 'little'))
            packet.extend(payload)
            
            self.socket.send(packet)
            self.request_id += 1
            
        except Exception as e:
            print(f"Failed to send packet: {e}")
            return None
            
    def read_packet(self):
        """Read RCON packet response"""
        if not self.socket:
            return None
            
        try:
            # Read packet length
            length_data = self.socket.recv(4)
            if not length_data:
                return None
                
            length = int.from_bytes(length_data, 'little')
            
            # Read remaining packet
            remaining = self.socket.recv(length)
            if not remaining:
                return None
                
            # Parse packet
            request_id = int.from_bytes(remaining[0:4], 'little')
            type = int.from_bytes(remaining[4:8], 'little')
            payload = remaining[8:-2].decode('utf-8')
            
            return {
                'id': request_id,
                'type': type,
                'payload': payload
            }
            
        except Exception as e:
            print(f"Failed to read packet: {e}")
            return None
            
    def command(self, command):
        """Execute RCON command"""
        if not self.connected:
            return "Not connected to server"
            
        try:
            self.send_packet(2, command)
            response = self.read_packet()
            
            if response:
                return response['payload']
            else:
                return "No response from server"
                
        except Exception as e:
            return f"Command failed: {e}"

class ServerMonitor:
    def __init__(self, rcon_client):
        self.rcon = rcon_client
        self.monitoring = False
        self.callbacks = []
        
    def add_callback(self, callback):
        """Add callback for server events"""
        self.callbacks.append(callback)
        
    def start_monitoring(self):
        """Start monitoring server"""
        self.monitoring = True
        thread = threading.Thread(target=self._monitor_loop, daemon=True)
        thread.start()
        
    def stop_monitoring(self):
        """Stop monitoring server"""
        self.monitoring = False
        
    def _monitor_loop(self):
        """Main monitoring loop"""
        while self.monitoring:
            try:
                # Get server info
                self._check_server_status()
                self._check_players()
                
                # Wait before next check
                time.sleep(5)
                
            except Exception as e:
                print(f"Monitor error: {e}")
                time.sleep(10)
                
    def _check_server_status(self):
        """Check server status"""
        try:
            # Get TPS
            tps_response = self.rcon.command("tps")
            if tps_response:
                self._notify_callbacks("server_status", {"tps": tps_response})
                
        except Exception as e:
            print(f"Status check error: {e}")
            
    def _check_players(self):
        """Check online players"""
        try:
            # Get player list
            list_response = self.rcon.command("list")
            if list_response:
                # Parse player list
                if "players online:" in list_response.lower():
                    parts = list_response.split(":")
                    if len(parts) > 1:
                        player_part = parts[1].strip()
                        players = [p.strip() for p in player_part.split(",")]
                        self._notify_callbacks("player_list", {"players": players})
                        
        except Exception as e:
            print(f"Player check error: {e}")
            
    def _notify_callbacks(self, event_type, data):
        """Notify all callbacks"""
        for callback in self.callbacks:
            try:
                callback(event_type, data)
            except Exception as e:
                print(f"Callback error: {e}")

class InventoryManager:
    def __init__(self, rcon_client):
        self.rcon = rcon_client
        
    def get_player_inventory(self, player_name):
        """Get player inventory"""
        try:
            # This would require a plugin to provide inventory data
            # For now, simulate with basic info
            return {
                "player": player_name,
                "slots": self._get_sample_inventory()
            }
        except Exception as e:
            print(f"Inventory error: {e}")
            return None
            
    def set_player_item(self, player_name, slot, item, amount):
        """Set item in player inventory"""
        try:
            command = f"give {player_name} {item} {amount}"
            response = self.rcon.command(command)
            return response
        except Exception as e:
            print(f"Set item error: {e}")
            return None
            
    def clear_player_slot(self, player_name, slot):
        """Clear player inventory slot"""
        try:
            # This would require a plugin to clear specific slots
            command = f"clear {player_name}"
            response = self.rcon.command(command)
            return response
        except Exception as e:
            print(f"Clear slot error: {e}")
            return None
            
    def _get_sample_inventory(self):
        """Get sample inventory data"""
        return [
            {"item": "air", "amount": 0},
            {"item": "diamond_sword", "amount": 1},
            {"item": "diamond_pickaxe", "amount": 1},
            {"item": "bread", "amount": 32},
            {"item": "torch", "amount": 64},
            {"item": "diamond", "amount": 8},
            {"item": "iron_ingot", "amount": 16},
            {"item": "gold_ingot", "amount": 12},
            {"item": "emerald", "amount": 5},
        ] + [{"item": "air", "amount": 0}] * 27

# Item database for inventory editor
ITEM_DATABASE = {
    "tools": {
        "wooden_sword": "⚔️",
        "stone_sword": "⚔️", 
        "iron_sword": "⚔️",
        "golden_sword": "⚔️",
        "diamond_sword": "⚔️",
        "netherite_sword": "⚔️",
        "wooden_pickaxe": "⛏️",
        "stone_pickaxe": "⛏️",
        "iron_pickaxe": "⛏️",
        "golden_pickaxe": "⛏️",
        "diamond_pickaxe": "⛏️",
        "netherite_pickaxe": "⛏️",
    },
    "weapons": {
        "bow": "🏹",
        "crossbow": "🏹",
        "trident": "🔱",
        "shield": "🛡️",
        "arrow": "➡️",
        "spectral_arrow": "➡️",
        "tipped_arrow": "➡️",
    },
    "armor": {
        "leather_helmet": "🎩",
        "iron_helmet": "🎩",
        "golden_helmet": "🎩",
        "diamond_helmet": "🎩",
        "netherite_helmet": "🎩",
        "leather_chestplate": "👔",
        "iron_chestplate": "👔",
        "golden_chestplate": "👔",
        "diamond_chestplate": "👔",
        "netherite_chestplate": "👔",
    },
    "food": {
        "bread": "🍞",
        "apple": "🍎",
        "golden_apple": "🍎",
        "carrot": "🥕",
        "potato": "🥔",
        "beef": "🥩",
        "porkchop": "🥩",
        "chicken": "🍗",
        "fish": "🐟",
        "cookie": "🍪",
        "cake": "🍰",
    },
    "blocks": {
        "dirt": "🟫",
        "stone": "⬜",
        "cobblestone": "🪨",
        "wood": "🪵",
        "planks": "🪵",
        "glass": "🪟",
        "sand": "🏖️",
        "gravel": "🪨",
        "coal_ore": "⬛",
        "iron_ore": "🔶",
        "gold_ore": "🟨",
        "diamond_ore": "💎",
        "emerald_ore": "💚",
    },
    "items": {
        "diamond": "💎",
        "emerald": "💚",
        "gold_ingot": "🟨",
        "iron_ingot": "🔶",
        "coal": "⬛",
        "redstone": "🔴",
        "lapis_lazuli": "🔵",
        "quartz": "⚪",
        "amethyst_shard": "🟣",
        "copper_ingot": "🟠",
    },
    "misc": {
        "torch": "🔦",
        "lantern": "🏮",
        "flint_and_steel": "🔥",
        "bucket": "🪣",
        "water_bucket": "💧",
        "lava_bucket": "🌋",
        "compass": "🧭",
        "clock": "🕐",
        "map": "🗺️",
        "book": "📖",
        "feather": "🪶",
        "paper": "📄",
    }
}

def get_item_emoji(item_name):
    """Get emoji for item name"""
    for category, items in ITEM_DATABASE.items():
        if item_name in items:
            return items[item_name]
    return "❓"

def search_items(query):
    """Search for items"""
    results = []
    query = query.lower()
    
    for category, items in ITEM_DATABASE.items():
        for item, emoji in items.items():
            if query in item.lower():
                results.append((item, emoji))
                
    return results[:20]  # Limit to 20 results
