package com.kingdom.commands.commands;

import com.kingdom.commands.KingdomCommands;
import com.kingdom.commands.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CoinsCommand implements CommandExecutor {
    
    private final KingdomCommands plugin;
    
    public CoinsCommand(KingdomCommands plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageUtils.colorize("&cThis command can only be used by players!"));
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("kingdom.commands.coins")) {
            player.sendMessage(MessageUtils.colorize("&cYou don't have permission to use this royal command!"));
            return true;
        }
        
        showBalance(player);
        return true;
    }
    
    private void showBalance(Player player) {
        int totalCoins = plugin.getCoinManager().countAllGold(player);
        
        player.sendMessage(MessageUtils.getMedievalBorder());
        player.sendMessage(MessageUtils.centerText("&e✦ Royal Treasury ✦", 58));
        player.sendMessage(MessageUtils.getMedievalSeparator());
        player.sendMessage(MessageUtils.centerText("&fYour Total Coins: " + plugin.getCoinManager().formatCoins(totalCoins), 58));
        player.sendMessage(MessageUtils.centerText("&7(Gold in inventory & ender chest)", 58));
        player.sendMessage(MessageUtils.getMedievalBorderEnd());
    }
}
