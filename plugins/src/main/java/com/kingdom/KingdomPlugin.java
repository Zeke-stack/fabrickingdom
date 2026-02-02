package com.kingdom;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

public class KingdomPlugin extends JavaPlugin {
    
    @Override
    public void onEnable() {
        getLogger().info("Kingdom Plugin Enabled!");
        saveDefaultConfig();
    }
    
    @Override
    public void onDisable() {
        getLogger().info("Kingdom Plugin Disabled!");
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("kingdom")) {
            sender.sendMessage(ChatColor.GOLD + "✦ " + ChatColor.WHITE + "Welcome to the Kingdom!");
            sender.sendMessage(ChatColor.YELLOW + "Use " + ChatColor.GREEN + "/coins" + ChatColor.YELLOW + " to check your wealth!");
            return true;
        }
        
        if (command.getName().equalsIgnoreCase("coins")) {
            sender.sendMessage(ChatColor.GOLD + "✦ " + ChatColor.WHITE + "Your Coins: " + ChatColor.GREEN + "100");
            return true;
        }
        
        return false;
    }
}
