package com.kingdom.commands.commands;

import com.kingdom.commands.KingdomCommands;
import com.kingdom.commands.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BalanceCommand implements CommandExecutor {
    
    private final KingdomCommands plugin;
    
    public BalanceCommand(KingdomCommands plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageUtils.colorize("&cThis command can only be used by players!"));
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("kingdom.commands.balance")) {
            player.sendMessage(MessageUtils.colorize("&cYou don't have permission to use this royal command!"));
            return true;
        }
        
        int totalCoins = plugin.getCoinManager().countAllGold(player);
        
        player.sendMessage(MessageUtils.colorize("&6✦ Your Balance: " + plugin.getCoinManager().formatCoins(totalCoins)));
        
        return true;
    }
}
