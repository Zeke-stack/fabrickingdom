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

public class OpCommand implements CommandExecutor, TabCompleter {
    
    private final KingdomCommands plugin;
    
    public OpCommand(KingdomCommands plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("kingdom.commands.op")) {
            sender.sendMessage(MessageUtils.colorize("&cYou don't have permission to use this royal command!"));
            return true;
        }
        
        if (args.length != 1) {
            sender.sendMessage(MessageUtils.colorize("&6Usage: /kop <player>"));
            return true;
        }
        
        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);
        
        if (target == null) {
            sender.sendMessage(MessageUtils.colorize("&cThat player is not in the kingdom!"));
            return true;
        }
        
        if (target.isOp()) {
            sender.sendMessage(MessageUtils.colorize("&e" + target.getName() + " &fis already a noble of the realm!"));
            return true;
        }
        
        target.setOp(true);
        
        // Create beautiful op notification
        List<String> notificationLines = new ArrayList<>();
        notificationLines.add("Player &e" + target.getName() + "&f has been promoted to nobility!");
        notificationLines.add("They now wield the power of the realm!");
        notificationLines.add("Promoted by: &e" + sender.getName());
        
        MessageUtils.sendMedievalBox(Bukkit.getConsoleSender(), "✦ Royal Promotion ✦", notificationLines);
        
        // Notify all online players with permission
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("kingdom.commands.op")) {
                MessageUtils.sendMedievalBox(player, "✦ Royal Promotion ✦", notificationLines);
            }
        }
        
        // Send message to the promoted player
        List<String> playerLines = new ArrayList<>();
        playerLines.add("You have been promoted to nobility!");
        playerLines.add("You now wield the power of the realm!");
        playerLines.add("Use your powers wisely, noble one!");
        
        MessageUtils.sendMedievalBox(target, "✦ Royal Promotion ✦", playerLines);
        
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
