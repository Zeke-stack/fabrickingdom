package com.kingdom;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

public class SimpleKingdom extends JavaPlugin {
    
    @Override
    public void onEnable() {
        getLogger().info("§6✦ Simple Kingdom Plugin Enabled! ✦");
        saveDefaultConfig();
    }
    
    @Override
    public void onDisable() {
        getLogger().info("§6✦ Simple Kingdom Plugin Disabled! ✦");
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (command.getName().toLowerCase()) {
            case "kingdom":
                sender.sendMessage(ChatColor.GOLD + "✦ " + ChatColor.WHITE + "Welcome to the Kingdom of Minecraftia!");
                sender.sendMessage(ChatColor.YELLOW + "Ranks: " + ChatColor.AQUA + "Peasant, Knight, Noble, King");
                sender.sendMessage(ChatColor.YELLOW + "Use " + ChatColor.GREEN + "/coins" + ChatColor.YELLOW + " to check your wealth!");
                return true;
                
            case "coins":
                sender.sendMessage(ChatColor.GOLD + "✦ " + ChatColor.WHITE + "Your Royal Treasury:");
                sender.sendMessage(ChatColor.YELLOW + "Gold Coins: " + ChatColor.GREEN + "100");
                sender.sendMessage(ChatColor.GRAY + "Work hard to increase your wealth!");
                return true;
                
            case "knight":
                if (sender.hasPermission("kingdom.admin")) {
                    sender.sendMessage(ChatColor.GOLD + "✦ " + ChatColor.WHITE + "Knighting ceremony ready!");
                    sender.sendMessage(ChatColor.GRAY + "Usage: /knight <player>");
                } else {
                    sender.sendMessage(ChatColor.RED + "Only royalty can perform knighting ceremonies!");
                }
                return true;
        }
        return false;
    }
}
