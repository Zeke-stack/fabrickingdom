package com.kingdom.commands.commands;

import com.kingdom.commands.KingdomCommands;
import com.kingdom.commands.StaffManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Sound;
import org.bukkit.Bukkit;

import java.util.Arrays;
import java.util.List;

public class KingdomCommandExecutor implements CommandExecutor {
    
    private final KingdomCommands plugin;
    
    public KingdomCommandExecutor(KingdomCommands plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }
        
        Player player = (Player) sender;
        
        switch (command.getName().toLowerCase()) {
            case "kingdom":
                return handleKingdomCommand(player, args);
            case "realm":
                return handleRealmCommand(player, args);
            case "royal":
                return handleRoyalCommand(player, args);
            case "herald":
                return handleHeraldCommand(player, args);
            case "decree":
                return handleDecreeCommand(player, args);
            case "proclaim":
                return handleProclaimCommand(player, args);
            case "summoncourt":
                return handleSummonCourtCommand(player, args);
            case "knight":
                return handleKnightCommand(player, args);
            case "noble":
                return handleNobleCommand(player, args);
            case "peasant":
                return handlePeasantCommand(player, args);
        }
        
        return false;
    }
    
    private boolean handleKingdomCommand(Player player, String[] args) {
        if (args.length == 0) {
            showKingdomInfo(player);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "info":
                showKingdomInfo(player);
                break;
            case "status":
                showKingdomStatus(player);
                break;
            case "laws":
                showKingdomLaws(player);
                break;
            case "ranks":
                showKingdomRanks(player);
                break;
            default:
                player.sendMessage(ChatColor.RED + "Usage: /kingdom <info|status|laws|ranks>");
                break;
        }
        
        return true;
    }
    
    private boolean handleRealmCommand(Player player, String[] args) {
        if (!plugin.getStaffManager().hasPermission(player.getUniqueId(), "administrator")) {
            player.sendMessage(ChatColor.RED + "Only nobility can manage the realm!");
            return true;
        }
        
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /realm <announce|lock|unlock|tax>");
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "announce":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /realm announce <message>");
                    return true;
                }
                String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                announceToKingdom(player, "Royal Decree", message, ChatColor.GOLD);
                break;
                
            case "lock":
                lockKingdom(player);
                break;
                
            case "unlock":
                unlockKingdom(player);
                break;
                
            case "tax":
                collectTaxes(player);
                break;
                
            default:
                player.sendMessage(ChatColor.RED + "Usage: /realm <announce|lock|unlock|tax>");
                break;
        }
        
        return true;
    }
    
    private boolean handleRoyalCommand(Player player, String[] args) {
        if (!plugin.getStaffManager().hasPermission(player.getUniqueId(), "administrator")) {
            player.sendMessage(ChatColor.RED + "Only royalty can use royal commands!");
            return true;
        }
        
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /royal <pardon|exile|decree|summon>");
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "pardon":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /royal pardon <player>");
                    return true;
                }
                pardonPlayer(player, args[1]);
                break;
                
            case "exile":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /royal exile <player> [reason]");
                    return true;
                }
                String reason = args.length > 2 ? String.join(" ", Arrays.copyOfRange(args, 2, args.length)) : "Treason against the crown";
                exilePlayer(player, args[1], reason);
                break;
                
            case "decree":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /royal decree <decree text>");
                    return true;
                }
                String decree = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                issueRoyalDecree(player, decree);
                break;
                
            case "summon":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /royal summon <player>");
                    return true;
                }
                summonToCourt(player, args[1]);
                break;
                
            default:
                player.sendMessage(ChatColor.RED + "Usage: /royal <pardon|exile|decree|summon>");
                break;
        }
        
        return true;
    }
    
    private boolean handleHeraldCommand(Player player, String[] args) {
        if (!plugin.getStaffManager().hasPermission(player.getUniqueId(), "moderator")) {
            player.sendMessage(ChatColor.RED + "Only heralds can make announcements!");
            return true;
        }
        
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /herald <message>");
            return true;
        }
        
        String message = String.join(" ", args);
        announceToKingdom(player, "Kingdom Herald", message, ChatColor.YELLOW);
        return true;
    }
    
    private boolean handleDecreeCommand(Player player, String[] args) {
        if (!plugin.getStaffManager().hasPermission(player.getUniqueId(), "administrator")) {
            player.sendMessage(ChatColor.RED + "Only nobility can issue decrees!");
            return true;
        }
        
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /decree <decree text>");
            return true;
        }
        
        String decree = String.join(" ", args);
        issueRoyalDecree(player, decree);
        return true;
    }
    
    private boolean handleProclaimCommand(Player player, String[] args) {
        if (!plugin.getStaffManager().hasPermission(player.getUniqueId(), "event_staff")) {
            player.sendMessage(ChatColor.RED + "Only staff can make proclamations!");
            return true;
        }
        
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /proclaim <message>");
            return true;
        }
        
        String message = String.join(" ", args);
        announceToKingdom(player, "Royal Proclamation", message, ChatColor.LIGHT_PURPLE);
        return true;
    }
    
    private boolean handleSummonCourtCommand(Player player, String[] args) {
        if (!plugin.getStaffManager().hasPermission(player.getUniqueId(), "administrator")) {
            player.sendMessage(ChatColor.RED + "Only nobility can summon the court!");
            return true;
        }
        
        announceToKingdom(player, "Court Summons", "All nobility are summoned to the royal court!", ChatColor.GOLD);
        
        // Teleport all staff to the player
        for (Player staff : Bukkit.getOnlinePlayers()) {
            if (plugin.getStaffManager().isStaff(staff.getUniqueId())) {
                staff.teleport(player.getLocation());
                staff.sendMessage(ChatColor.GOLD + "✦ " + ChatColor.WHITE + "You have been summoned to the royal court!");
            }
        }
        
        return true;
    }
    
    private boolean handleKnightCommand(Player player, String[] args) {
        if (!plugin.getStaffManager().hasPermission(player.getUniqueId(), "administrator")) {
            player.sendMessage(ChatColor.RED + "Only nobility can knight players!");
            return true;
        }
        
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /knight <player>");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Player not found: " + args[0]);
            return true;
        }
        
        knightPlayer(player, target);
        return true;
    }
    
    private boolean handleNobleCommand(Player player, String[] args) {
        if (!plugin.getStaffManager().hasPermission(player.getUniqueId(), "founder")) {
            player.sendMessage(ChatColor.RED + "Only the crown can grant nobility!");
            return true;
        }
        
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /noble <player>");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Player not found: " + args[0]);
            return true;
        }
        
        grantNobility(player, target);
        return true;
    }
    
    private boolean handlePeasantCommand(Player player, String[] args) {
        // This is a fun command for regular players
        showPeasantLife(player);
        return true;
    }
    
    private void showKingdomInfo(Player player) {
        plugin.getKingdomManager().showKingdomInfo(player);
    }
    
    private void showKingdomStatus(Player player) {
        player.sendMessage(ChatColor.GOLD + "✦ " + ChatColor.WHITE + "Kingdom Status " + ChatColor.GOLD + "✦");
        player.sendMessage(ChatColor.GREEN + "Peace: " + ChatColor.WHITE + "The realm is at peace");
        player.sendMessage(ChatColor.GREEN + "Prosperity: " + ChatColor.WHITE + "Trade flourishes");
        player.sendMessage(ChatColor.GREEN + "Defense: " + ChatColor.WHITE + "Walls are strong");
        player.sendMessage(ChatColor.YELLOW + "Current Events: " + ChatColor.WHITE + "Royal tournament planned");
    }
    
    private void showKingdomLaws(Player player) {
        plugin.getKingdomManager().showKingdomLaws(player);
    }
    
    private void showKingdomRanks(Player player) {
        plugin.getKingdomManager().showKingdomRanks(player);
    }
    
    private void announceToKingdom(Player sender, String title, String message, ChatColor color) {
        plugin.getKingdomManager().sendRoyalAnnouncement(title, message, color);
        sender.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.WHITE + "Announcement sent to the kingdom!");
    }
    
    private void lockKingdom(Player player) {
        // Implementation for locking the kingdom
        Bukkit.broadcastMessage(ChatColor.RED + "✦ " + ChatColor.WHITE + "The kingdom is now under lockdown!");
        player.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.WHITE + "Kingdom locked!");
    }
    
    private void unlockKingdom(Player player) {
        // Implementation for unlocking the kingdom
        Bukkit.broadcastMessage(ChatColor.GREEN + "✦ " + ChatColor.WHITE + "The kingdom lockdown has been lifted!");
        player.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.WHITE + "Kingdom unlocked!");
    }
    
    private void collectTaxes(Player player) {
        // Implementation for tax collection
        announceToKingdom(player, "Tax Collection", "All citizens must pay their taxes!", ChatColor.GOLD);
        player.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.WHITE + "Tax collection announced!");
    }
    
    private void pardonPlayer(Player player, String targetName) {
        Player target = Bukkit.getPlayer(targetName);
        if (target != null) {
            Bukkit.broadcastMessage(ChatColor.GREEN + "✦ " + ChatColor.WHITE + "His Majesty has pardoned " + target.getName() + "!");
            target.sendMessage(ChatColor.GOLD + "✦ " + ChatColor.WHITE + "You have been pardoned by the crown!");
        }
        player.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.WHITE + "Pardoned " + targetName);
    }
    
    private void exilePlayer(Player player, String targetName, String reason) {
        Player target = Bukkit.getPlayer(targetName);
        if (target != null) {
            Bukkit.broadcastMessage(ChatColor.RED + "✦ " + ChatColor.WHITE + target.getName() + " has been exiled for: " + reason);
            target.kickPlayer(ChatColor.RED + "You have been exiled from the kingdom!\n" + ChatColor.GRAY + "Reason: " + reason);
        }
        player.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.WHITE + "Exiled " + targetName);
    }
    
    private void issueRoyalDecree(Player player, String decree) {
        announceToKingdom(player, "Royal Decree", decree, ChatColor.GOLD);
        player.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.WHITE + "Royal decree issued!");
    }
    
    private void summonToCourt(Player player, String targetName) {
        Player target = Bukkit.getPlayer(targetName);
        if (target != null) {
            target.teleport(player.getLocation());
            target.sendMessage(ChatColor.GOLD + "✦ " + ChatColor.WHITE + "You have been summoned to the royal court!");
            player.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.WHITE + "Summoned " + targetName + " to court");
        }
    }
    
    private void knightPlayer(Player player, Player target) {
        plugin.getKingdomManager().knightPlayer(player, target);
    }
    
    private void grantNobility(Player player, Player target) {
        plugin.getKingdomManager().grantNobility(player, target);
    }
    
    private void showPeasantLife(Player player) {
        plugin.getKingdomManager().showPeasantLife(player);
    }
}
