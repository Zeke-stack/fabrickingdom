import tkinter as tk
from tkinter import ttk, scrolledtext, messagebox, simpledialog
import socket
import threading
import json
import requests
import base64
from PIL import Image, ImageTk
import io
import time
import subprocess
import os
from datetime import datetime

class KingdomManager:
    def __init__(self):
        self.root = tk.Tk()
        self.root.title("🏰 Kingdom Server Manager")
        self.root.geometry("1200x800")
        self.root.configure(bg='#0a0a0a')
        
        # Server connection
        self.server_ip = "production-us-east4-eqdc4a.railway.app"
        self.server_port = 25565
        self.rcon_port = 25575
        self.rcon_password = "changeme"
        
        # Connection status
        self.connected = False
        self.socket = None
        
        # Player data
        self.players = {}
        self.selected_player = None
        
        # Setup UI
        self.setup_ui()
        self.setup_styles()
        
        # Start connection thread
        self.connect_to_server()
        
    def setup_styles(self):
        """Setup dark theme styles"""
        style = ttk.Style()
        style.theme_use('clam')
        
        # Configure dark theme
        style.configure('Dark.TFrame', background='#1a1a1a')
        style.configure('Dark.TLabel', background='#1a1a1a', foreground='#ffffff')
        style.configure('Dark.TButton', background='#2a2a2a', foreground='#ffffff')
        style.map('Dark.TButton', background=[('active', '#3a3a3a')])
        style.configure('Dark.TEntry', fieldbackground='#2a2a2a', foreground='#ffffff')
        style.configure('Dark.TCombobox', fieldbackground='#2a2a2a', foreground='#ffffff')
        
    def setup_ui(self):
        """Setup the main UI"""
        # Main container
        main_frame = tk.Frame(self.root, bg='#0a0a0a')
        main_frame.pack(fill=tk.BOTH, expand=True, padx=10, pady=10)
        
        # Top bar with connection status
        self.setup_top_bar(main_frame)
        
        # Main content area
        content_frame = tk.Frame(main_frame, bg='#0a0a0a')
        content_frame.pack(fill=tk.BOTH, expand=True, pady=(10, 0))
        
        # Left panel - Console
        self.setup_console_panel(content_frame)
        
        # Right panel - Player management and inventory
        self.setup_player_panel(content_frame)
        
        # Bottom panel - Commands
        self.setup_command_panel(main_frame)
        
    def setup_top_bar(self, parent):
        """Setup top connection bar"""
        top_frame = tk.Frame(parent, bg='#1a1a1a', height=50)
        top_frame.pack(fill=tk.X)
        top_frame.pack_propagate(False)
        
        # Title
        title_label = tk.Label(top_frame, text="🏰 Kingdom Server Manager", 
                              font=('Arial', 16, 'bold'), 
                              bg='#1a1a1a', fg='#ffffff')
        title_label.pack(side=tk.LEFT, padx=20, pady=10)
        
        # Connection status
        self.status_label = tk.Label(top_frame, text="🔴 Disconnected", 
                                   font=('Arial', 12), 
                                   bg='#1a1a1a', fg='#ff4444')
        self.status_label.pack(side=tk.RIGHT, padx=20, pady=10)
        
        # Server info
        self.server_info_label = tk.Label(top_frame, text=f"Server: {self.server_ip}:{self.server_port}", 
                                        font=('Arial', 10), 
                                        bg='#1a1a1a', fg='#cccccc')
        self.server_info_label.pack(side=tk.RIGHT, padx=20, pady=10)
        
    def setup_console_panel(self, parent):
        """Setup console panel"""
        console_frame = tk.Frame(parent, bg='#1a1a1a')
        console_frame.pack(side=tk.LEFT, fill=tk.BOTH, expand=True, padx=(0, 5))
        
        # Console title
        console_title = tk.Label(console_frame, text="📟 Server Console", 
                               font=('Arial', 12, 'bold'), 
                               bg='#1a1a1a', fg='#ffffff')
        console_title.pack(pady=(10, 5))
        
        # Console output
        self.console_output = scrolledtext.ScrolledText(console_frame, 
                                                       bg='#0a0a0a', fg='#00ff00',
                                                       font=('Consolas', 10),
                                                       height=20, width=50)
        self.console_output.pack(fill=tk.BOTH, expand=True, padx=10, pady=(0, 10))
        
        # Console input
        console_input_frame = tk.Frame(console_frame, bg='#1a1a1a')
        console_input_frame.pack(fill=tk.X, padx=10, pady=(0, 10))
        
        self.console_input = tk.Entry(console_input_frame, 
                                     bg='#2a2a2a', fg='#ffffff',
                                     font=('Arial', 10))
        self.console_input.pack(side=tk.LEFT, fill=tk.X, expand=True)
        self.console_input.bind('<Return>', self.send_console_command)
        
        send_btn = tk.Button(console_input_frame, text="Send", 
                           bg='#3a3a3a', fg='#ffffff',
                           command=self.send_console_command)
        send_btn.pack(side=tk.RIGHT, padx=(5, 0))
        
    def setup_player_panel(self, parent):
        """Setup player management panel"""
        player_frame = tk.Frame(parent, bg='#1a1a1a', width=400)
        player_frame.pack(side=tk.RIGHT, fill=tk.BOTH, expand=True, padx=(5, 0))
        player_frame.pack_propagate(False)
        
        # Player list
        player_title = tk.Label(player_frame, text="👥 Online Players", 
                               font=('Arial', 12, 'bold'), 
                               bg='#1a1a1a', fg='#ffffff')
        player_title.pack(pady=(10, 5))
        
        # Player listbox
        self.player_listbox = tk.Listbox(player_frame, 
                                        bg='#2a2a2a', fg='#ffffff',
                                        font=('Arial', 10),
                                        height=8)
        self.player_listbox.pack(fill=tk.X, padx=10, pady=(0, 10))
        self.player_listbox.bind('<<ListboxSelect>>', self.on_player_select)
        
        # Player actions
        actions_frame = tk.Frame(player_frame, bg='#1a1a1a')
        actions_frame.pack(fill=tk.X, padx=10, pady=(0, 10))
        
        tk.Button(actions_frame, text="👑 OP", bg='#3a3a3a', fg='#ffffff',
                 command=self.op_player).pack(side=tk.LEFT, padx=2)
        tk.Button(actions_frame, text="👤 DeOP", bg='#3a3a3a', fg='#ffffff',
                 command=self.deop_player).pack(side=tk.LEFT, padx=2)
        tk.Button(actions_frame, text="🚫 Kick", bg='#3a3a3a', fg='#ffffff',
                 command=self.kick_player).pack(side=tk.LEFT, padx=2)
        tk.Button(actions_frame, text="🔨 Ban", bg='#3a3a3a', fg='#ffffff',
                 command=self.ban_player).pack(side=tk.LEFT, padx=2)
        tk.Button(actions_frame, text="💰 Coins", bg='#3a3a3a', fg='#ffffff',
                 command=self.show_coins).pack(side=tk.LEFT, padx=2)
        
        # Inventory viewer
        inventory_title = tk.Label(player_frame, text="🎒 Inventory Editor", 
                                  font=('Arial', 12, 'bold'), 
                                  bg='#1a1a1a', fg='#ffffff')
        inventory_title.pack(pady=(20, 5))
        
        # Inventory grid
        self.inventory_frame = tk.Frame(player_frame, bg='#1a1a1a')
        self.inventory_frame.pack(padx=10, pady=(0, 10))
        
        self.inventory_slots = []
        for i in range(36):
            row = i // 9
            col = i % 9
            slot = tk.Label(self.inventory_frame, bg='#2a2a2a', fg='#ffffff',
                          width=4, height=2, relief=tk.RAISED, bd=1)
            slot.grid(row=row, column=col, padx=1, pady=1)
            slot.bind('<Button-1>', lambda e, idx=i: self.on_slot_click(idx))
            self.inventory_slots.append(slot)
        
        # Item editor
        editor_frame = tk.Frame(player_frame, bg='#1a1a1a')
        editor_frame.pack(fill=tk.X, padx=10, pady=(0, 10))
        
        tk.Label(editor_frame, text="Item:", bg='#1a1a1a', fg='#ffffff').pack(side=tk.LEFT)
        self.item_entry = tk.Entry(editor_frame, bg='#2a2a2a', fg='#ffffff', width=15)
        self.item_entry.pack(side=tk.LEFT, padx=5)
        
        tk.Label(editor_frame, text="Amount:", bg='#1a1a1a', fg='#ffffff').pack(side=tk.LEFT, padx=(10, 0))
        self.amount_entry = tk.Entry(editor_frame, bg='#2a2a2a', fg='#ffffff', width=5)
        self.amount_entry.pack(side=tk.LEFT, padx=5)
        self.amount_entry.insert(0, "1")
        
        tk.Button(editor_frame, text="Set Item", bg='#3a3a3a', fg='#ffffff',
                 command=self.set_item).pack(side=tk.LEFT, padx=5)
        tk.Button(editor_frame, text="Clear", bg='#3a3a3a', fg='#ffffff',
                 command=self.clear_slot).pack(side=tk.LEFT, padx=2)
        
    def setup_command_panel(self, parent):
        """Setup command panel"""
        command_frame = tk.Frame(parent, bg='#1a1a1a', height=150)
        command_frame.pack(fill=tk.X, pady=(10, 0))
        command_frame.pack_propagate(False)
        
        # Command title
        command_title = tk.Label(command_frame, text="⚡ Quick Commands", 
                                font=('Arial', 12, 'bold'), 
                                bg='#1a1a1a', fg='#ffffff')
        command_title.pack(pady=(10, 5))
        
        # Command buttons
        buttons_frame = tk.Frame(command_frame, bg='#1a1a1a')
        buttons_frame.pack(fill=tk.BOTH, expand=True, padx=10, pady=(0, 10))
        
        # Server commands
        server_frame = tk.Frame(buttons_frame, bg='#1a1a1a')
        server_frame.pack(side=tk.LEFT, fill=tk.BOTH, expand=True)
        
        tk.Label(server_frame, text="🖥️ Server", font=('Arial', 10, 'bold'),
                bg='#1a1a1a', fg='#ffffff').pack()
        
        tk.Button(server_frame, text="🔄 Restart", bg='#3a3a3a', fg='#ffffff',
                 command=self.restart_server).pack(fill=tk.X, pady=2)
        tk.Button(server_frame, text="💾 Save", bg='#3a3a3a', fg='#ffffff',
                 command=self.save_server).pack(fill=tk.X, pady=2)
        tk.Button(server_frame, text="📊 Status", bg='#3a3a3a', fg='#ffffff',
                 command=self.show_status).pack(fill=tk.X, pady=2)
        tk.Button(server_frame, text="🌍 Weather Clear", bg='#3a3a3a', fg='#ffffff',
                 command=self.clear_weather).pack(fill=tk.X, pady=2)
        tk.Button(server_frame, text="☀️ Time Day", bg='#3a3a3a', fg='#ffffff',
                 command=self.set_day).pack(fill=tk.X, pady=2)
        
        # Kingdom commands
        kingdom_frame = tk.Frame(buttons_frame, bg='#1a1a1a')
        kingdom_frame.pack(side=tk.LEFT, fill=tk.BOTH, expand=True, padx=(10, 0))
        
        tk.Label(kingdom_frame, text="🏰 Kingdom", font=('Arial', 10, 'bold'),
                bg='#1a1a1a', fg='#ffffff').pack()
        
        tk.Button(kingdom_frame, text="📢 Broadcast", bg='#3a3a3a', fg='#ffffff',
                 command=self.broadcast_message).pack(fill=tk.X, pady=2)
        tk.Button(kingdom_frame, text="🎮 Give Item", bg='#3a3a3a', fg='#ffffff',
                 command=self.give_item_command).pack(fill=tk.X, pady=2)
        tk.Button(kingdom_frame, text="⚔️ GameMode", bg='#3a3a3a', fg='#ffffff',
                 command=self.change_gamemode).pack(fill=tk.X, pady=2)
        tk.Button(kingdom_frame, text="🔮 Teleport", bg='#3a3a3a', fg='#ffffff',
                 command=self.teleport_player).pack(fill=tk.X, pady=2)
        tk.Button(kingdom_frame, text="🎁 Give All", bg='#3a3a3a', fg='#ffffff',
                 command=self.give_all_items).pack(fill=tk.X, pady=2)
        
        # Utility commands
        utility_frame = tk.Frame(buttons_frame, bg='#1a1a1a')
        utility_frame.pack(side=tk.LEFT, fill=tk.BOTH, expand=True, padx=(10, 0))
        
        tk.Label(utility_frame, text="🛠️ Utility", font=('Arial', 10, 'bold'),
                bg='#1a1a1a', fg='#ffffff').pack()
        
        tk.Button(utility_frame, text="🗺️ World Info", bg='#3a3a3a', fg='#ffffff',
                 command=self.world_info).pack(fill=tk.X, pady=2)
        tk.Button(utility_frame, text="📈 Performance", bg='#3a3a3a', fg='#ffffff',
                 command=self.show_performance).pack(fill=tk.X, pady=2)
        tk.Button(utility_frame, text="🔍 Find Player", bg='#3a3a3a', fg='#ffffff',
                 command=self.find_player).pack(fill=tk.X, pady=2)
        tk.Button(utility_frame, text="📋 Player List", bg='#3a3a3a', fg='#ffffff',
                 command=self.list_players).pack(fill=tk.X, pady=2)
        tk.Button(utility_frame, text="🎨 Custom CMD", bg='#3a3a3a', fg='#ffffff',
                 command=self.custom_command).pack(fill=tk.X, pady=2)
        
    def connect_to_server(self):
        """Connect to the Minecraft server"""
        def connect():
            try:
                # Simulate connection (in real app, would use RCON or server API)
                time.sleep(2)
                self.connected = True
                self.root.after(0, self.update_connection_status, True)
                self.root.after(0, self.log_to_console, "Connected to Kingdom Server successfully!")
                self.root.after(0, self.start_monitoring)
            except Exception as e:
                self.root.after(0, self.update_connection_status, False)
                self.root.after(0, self.log_to_console, f"Failed to connect: {str(e)}")
        
        threading.Thread(target=connect, daemon=True).start()
        
    def update_connection_status(self, connected):
        """Update connection status"""
        if connected:
            self.status_label.config(text="🟢 Connected", fg='#44ff44')
        else:
            self.status_label.config(text="🔴 Disconnected", fg='#ff4444')
        self.connected = connected
        
    def log_to_console(self, message):
        """Log message to console"""
        timestamp = datetime.now().strftime("%H:%M:%S")
        self.console_output.insert(tk.END, f"[{timestamp}] {message}\n")
        self.console_output.see(tk.END)
        
    def send_console_command(self, event=None):
        """Send command to console"""
        command = self.console_input.get().strip()
        if command:
            self.log_to_console(f"> {command}")
            # Simulate command execution
            self.root.after(100, lambda: self.log_to_console(f"Command executed: {command}"))
            self.console_input.delete(0, tk.END)
            
    def start_monitoring(self):
        """Start monitoring server"""
        def monitor():
            while self.connected:
                # Simulate server events
                time.sleep(5)
                if self.connected:
                    self.root.after(0, self.update_player_list)
        
        threading.Thread(target=monitor, daemon=True).start()
        
    def update_player_list(self):
        """Update player list"""
        # Simulate player list
        sample_players = ["GavinoFlores", "KingPlayer", "MedievalKnight", "CastleBuilder"]
        self.player_listbox.delete(0, tk.END)
        for player in sample_players:
            self.player_listbox.insert(tk.END, player)
            
    def on_player_select(self, event):
        """Handle player selection"""
        selection = self.player_listbox.curselection()
        if selection:
            self.selected_player = self.player_listbox.get(selection[0])
            self.log_to_console(f"Selected player: {self.selected_player}")
            self.load_player_inventory()
            
    def load_player_inventory(self):
        """Load player inventory"""
        # Simulate loading inventory
        sample_items = [
            ("diamond_sword", "⚔️"),
            ("diamond_pickaxe", "⛏️"),
            ("bread", "🍞"),
            ("torch", "🔦"),
            ("diamond", "💎"),
        ]
        
        for i, slot in enumerate(self.inventory_slots):
            if i < len(sample_items):
                item, emoji = sample_items[i]
                slot.config(text=f"{emoji}\n{item}", bg='#3a3a3a')
            else:
                slot.config(text="", bg='#2a2a2a')
                
    def on_slot_click(self, slot_index):
        """Handle inventory slot click"""
        self.log_to_console(f"Selected inventory slot: {slot_index}")
        
    def set_item(self):
        """Set item in selected slot"""
        item = self.item_entry.get().strip()
        amount = self.amount_entry.get().strip()
        if item and amount:
            self.log_to_console(f"Set item: {item} x{amount}")
            
    def clear_slot(self):
        """Clear selected slot"""
        self.log_to_console("Cleared inventory slot")
        
    def op_player(self):
        """OP selected player"""
        if self.selected_player:
            self.log_to_console(f"OP'd player: {self.selected_player}")
            
    def deop_player(self):
        """DeOP selected player"""
        if self.selected_player:
            self.log_to_console(f"DeOP'd player: {self.selected_player}")
            
    def kick_player(self):
        """Kick selected player"""
        if self.selected_player:
            reason = simpledialog.askstring("Kick Player", "Enter kick reason:")
            if reason:
                self.log_to_console(f"Kicked {self.selected_player}: {reason}")
                
    def ban_player(self):
        """Ban selected player"""
        if self.selected_player:
            reason = simpledialog.askstring("Ban Player", "Enter ban reason:")
            if reason:
                self.log_to_console(f"Banned {self.selected_player}: {reason}")
                
    def show_coins(self):
        """Show player coins"""
        if self.selected_player:
            self.log_to_console(f"{self.selected_player} has 150 gold coins")
            
    def restart_server(self):
        """Restart server"""
        if messagebox.askyesno("Restart Server", "Are you sure you want to restart the server?"):
            self.log_to_console("Restarting server...")
            
    def save_server(self):
        """Save server"""
        self.log_to_console("Saving world data...")
        
    def show_status(self):
        """Show server status"""
        status = f"""Server Status:
Players Online: {self.player_listbox.size()}
TPS: 20.0
Memory Usage: 1.2GB / 2.0GB
Uptime: 2h 34m
World Size: 125MB"""
        messagebox.showinfo("Server Status", status)
        
    def clear_weather(self):
        """Clear weather"""
        self.log_to_console("Weather set to clear")
        
    def set_day(self):
        """Set time to day"""
        self.log_to_console("Time set to day")
        
    def broadcast_message(self):
        """Broadcast message"""
        message = simpledialog.askstring("Broadcast", "Enter message to broadcast:")
        if message:
            self.log_to_console(f"Broadcast: {message}")
            
    def give_item_command(self):
        """Give item to player"""
        if self.selected_player:
            item = simpledialog.askstring("Give Item", "Enter item name:")
            if item:
                amount = simpledialog.askinteger("Give Item", "Enter amount:", initialvalue=1)
                if amount:
                    self.log_to_console(f"Gave {amount}x{item} to {self.selected_player}")
                    
    def change_gamemode(self):
        """Change player gamemode"""
        if self.selected_player:
            gamemodes = ["survival", "creative", "adventure", "spectator"]
            gamemode = simpledialog.askstring("Gamemode", f"Enter gamemode {gamemodes}:")
            if gamemode:
                self.log_to_console(f"Set {self.selected_player} to {gamemode} mode")
                
    def teleport_player(self):
        """Teleport player"""
        if self.selected_player:
            target = simpledialog.askstring("Teleport", "Enter target player or coordinates:")
            if target:
                self.log_to_console(f"Teleported {self.selected_player} to {target}")
                
    def give_all_items(self):
        """Give items to all players"""
        item = simpledialog.askstring("Give All", "Enter item to give all players:")
        if item:
            amount = simpledialog.askinteger("Give All", "Enter amount:", initialvalue=1)
            if amount:
                self.log_to_console(f"Gave {amount}x{item} to all players")
                
    def world_info(self):
        """Show world information"""
        info = f"""World Information:
World Name: world
World Type: Default
Difficulty: Normal
Game Mode: Survival
Spawn Point: (0, 64, 0)
World Border: 30000x30000
Structures: ON
PvP: ON"""
        messagebox.showinfo("World Info", info)
        
    def show_performance(self):
        """Show performance metrics"""
        perf = f"""Performance Metrics:
CPU Usage: 45%
Memory Usage: 60%
Network I/O: 2.5 MB/s
Disk I/O: 1.2 MB/s
Chunks Loaded: 1,245
Entities: 856
Tile Entities: 234"""
        messagebox.showinfo("Performance", perf)
        
    def find_player(self):
        """Find player"""
        player = simpledialog.askstring("Find Player", "Enter player name:")
        if player:
            self.log_to_console(f"Finding player: {player}")
            
    def list_players(self):
        """List all players"""
        players = "\n".join([self.player_listbox.get(i) for i in range(self.player_listbox.size())])
        messagebox.showinfo("Online Players", f"Players Online ({self.player_listbox.size()}):\n\n{players}")
        
    def custom_command(self):
        """Execute custom command"""
        command = simpledialog.askstring("Custom Command", "Enter command:")
        if command:
            self.log_to_console(f"Executing: {command}")
            
    def run(self):
        """Run the application"""
        self.root.mainloop()

if __name__ == "__main__":
    app = KingdomManager()
    app.run()
