package com.kingdom;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EssentialKingdom extends JavaPlugin implements Listener {
    
    @Override
    public void onEnable() {
        getLogger().info(ChatColor.GOLD + "✦ Essential Kingdom Plugin Enabled! ✦");
        getServer().getPluginManager().registerEvents(this, this);
        saveDefaultConfig();
        
        // Initialize config with default values
        getConfig().addDefault("kingdom.name", "Minecraftia");
        getConfig().addDefault("kingdom.motd", "Welcome to the Kingdom!");
        getConfig().options().copyDefaults(true);
        saveConfig();
    }
    
    @Override
    public void onDisable() {
        getLogger().info(ChatColor.GOLD + "✦ Essential Kingdom Plugin Disabled! ✦");
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String kingdomName = getConfig().getString("kingdom.name", "Minecraftia");
        
        // Welcome message with beautiful formatting
        event.setJoinMessage(null); // Remove default join message
        getServer().broadcastMessage(
            ChatColor.GOLD + "✦ " + ChatColor.WHITE + player.getName() + 
            ChatColor.YELLOW + " has arrived in the Kingdom of " + 
            ChatColor.AQUA + kingdomName + ChatColor.GOLD + " ✦"
        );
        
        // Personal welcome
        player.sendMessage(ChatColor.GOLD + "✦✦✦✦✦✦✦✦✦✦✦✦✦✦✦");
        player.sendMessage(ChatColor.YELLOW + "  Welcome to " + ChatColor.AQUA + kingdomName + ChatColor.YELLOW + "!");
        player.sendMessage(ChatColor.WHITE + "  Use " + ChatColor.GREEN + "/kingdom" + ChatColor.WHITE + " for commands");
        player.sendMessage(ChatColor.WHITE + "  Use " + ChatColor.GREEN + "/coins" + ChatColor.WHITE + " to check wealth");
        player.sendMessage(ChatColor.GOLD + "✦✦✦✦✦✦✦✦✦✦✦✦✦✦✦");
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        event.setQuitMessage(null); // Remove default quit message
        getServer().broadcastMessage(
            ChatColor.GRAY + "◇ " + ChatColor.WHITE + player.getName() + 
            ChatColor.GRAY + " has left the Kingdom ◇"
        );
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String kingdomName = getConfig().getString("kingdom.name", "Minecraftia");
        
        switch (command.getName().toLowerCase()) {
            case "kingdom":
                sender.sendMessage(ChatColor.GOLD + "✦✦✦✦✦✦✦✦✦✦✦✦✦✦✦");
                sender.sendMessage(ChatColor.AQUA + "      Kingdom of " + kingdomName);
                sender.sendMessage(ChatColor.GOLD + "✦✦✦✦✦✦✦✦✦✦✦✦✦✦✦");
                sender.sendMessage(ChatColor.YELLOW + "Ranks: " + ChatColor.WHITE + "Peasant, Knight, Noble, King");
                sender.sendMessage(ChatColor.YELLOW + "Commands: " + ChatColor.GREEN + "/coins, /rank, /online");
                sender.sendMessage(ChatColor.GRAY + "Total Wealth: " + ChatColor.GREEN + "10,000 Gold Coins");
                sender.sendMessage(ChatColor.GOLD + "✦✦✦✦✦✦✦✦✦✦✦✦✦✦✦");
                return true;
                
            case "coins":
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    sender.sendMessage(ChatColor.GOLD + "✦✦✦✦✦✦✦✦✦✦✦✦✦✦✦");
                    sender.sendMessage(ChatColor.YELLOW + "    Royal Treasury");
                    sender.sendMessage(ChatColor.GOLD + "✦✦✦✦✦✦✦✦✦✦✦✦✦✦✦");
                    sender.sendMessage(ChatColor.WHITE + "Gold Coins: " + ChatColor.GREEN + "100");
                    sender.sendMessage(ChatColor.WHITE + "Silver Coins: " + ChatColor.GRAY + "500");
                    sender.sendMessage(ChatColor.WHITE + "Rank: " + ChatColor.AQUA + "Peasant");
                    sender.sendMessage(ChatColor.GRAY + "Work hard to increase your wealth!");
                    sender.sendMessage(ChatColor.GOLD + "✦✦✦✦✦✦✦✦✦✦✦✦✦✦✦");
                } else {
                    sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
                }
                return true;
                
            case "rank":
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    sender.sendMessage(ChatColor.GOLD + "✦ " + ChatColor.WHITE + "Rank System");
                    sender.sendMessage(ChatColor.YELLOW + "Current Rank: " + ChatColor.AQUA + "Peasant");
                    sender.sendMessage(ChatColor.GRAY + "Next Rank: Knight (500 coins needed)");
                    sender.sendMessage(ChatColor.WHITE + "Rank Benefits:");
                    sender.sendMessage(ChatColor.GREEN + "  • Access to special areas");
                    sender.sendMessage(ChatColor.GREEN + "  • Better trading rates");
                    sender.sendMessage(ChatColor.GREEN + "  • Unique cosmetics");
                }
                return true;
                
            case "online":
                int onlineCount = getServer().getOnlinePlayers().size();
                sender.sendMessage(ChatColor.GOLD + "✦ " + ChatColor.WHITE + "Citizens Online: " + ChatColor.GREEN + onlineCount);
                if (onlineCount > 0) {
                    sender.sendMessage(ChatColor.YELLOW + "Players in the Kingdom:");
                    for (Player player : getServer().getOnlinePlayers()) {
                        sender.sendMessage(ChatColor.WHITE + "  • " + player.getName());
                    }
                }
                return true;
                
            case "knight":
                if (sender.hasPermission("kingdom.admin")) {
                    if (args.length == 1) {
                        String targetName = args[0];
                        sender.sendMessage(ChatColor.GOLD + "✦ " + ChatColor.WHITE + "Knighting Ceremony!");
                        sender.sendMessage(ChatColor.YELLOW + targetName + " has been knighted!");
                        sender.sendMessage(ChatColor.GRAY + "Usage: /knight <player>");
                    } else {
                        sender.sendMessage(ChatColor.RED + "Usage: /knight <player>");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Only royalty can perform knighting ceremonies!");
                }
                return true;
        }
        return false;
    }
}
