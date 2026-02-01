package com.kingdom.commands.commands;

import com.kingdom.commands.KingdomCommands;
import com.kingdom.commands.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class KickCommand implements CommandExecutor, TabCompleter {
    
    private final KingdomCommands plugin;
    
    public KickCommand(KingdomCommands plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("kingdom.commands.kick")) {
            sender.sendMessage(MessageUtils.colorize("&cYou don't have permission to use this royal command!"));
            return true;
        }
        
        if (args.length < 1) {
            sender.sendMessage(MessageUtils.colorize("&6Usage: /kkick <player> [reason]"));
            return true;
        }
        
        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);
        
        if (target == null) {
            sender.sendMessage(MessageUtils.colorize("&cThat player is not in the kingdom!"));
            return true;
        }
        
        if (target.hasPermission("kingdom.commands.kick") && !sender.isOp()) {
            sender.sendMessage(MessageUtils.colorize("&cYou cannot kick a fellow noble!"));
            return true;
        }
        
        String reason = args.length > 1 ? String.join(" ", args).substring(targetName.length() + 1) : "";
        
        // Create beautiful kick notification
        List<String> notificationLines = new ArrayList<>();
        notificationLines.add("Player &e" + target.getName() + "&f has been removed!");
        notificationLines.add("Reason: &e" + (reason.isEmpty() ? "No reason provided" : reason));
        notificationLines.add("Executed by: &e" + sender.getName());
        
        MessageUtils.sendMedievalBox(Bukkit.getConsoleSender(), "✦ Royal Removal ✦", notificationLines);
        
        // Notify all online players with permission
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("kingdom.commands.kick")) {
                MessageUtils.sendMedievalBox(player, "✦ Royal Removal ✦", notificationLines);
            }
        }
        
        // Kick the player with custom message
        target.kickPlayer(MessageUtils.getKickMessage(reason));
        
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            // Tab complete for player names
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(player.getName());
                }
            }
        }
        
        return completions;
    }
}
