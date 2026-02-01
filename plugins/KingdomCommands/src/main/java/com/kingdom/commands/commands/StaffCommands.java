package com.kingdom.commands.commands;

import com.kingdom.commands.KingdomCommands;
import com.kingdom.commands.StaffManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class StaffCommands implements CommandExecutor, TabCompleter {
    
    private final KingdomCommands plugin;
    private final StaffManager staffManager;
    
    public StaffCommands(KingdomCommands plugin) {
        this.plugin = plugin;
        this.staffManager = plugin.getStaffManager();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }
        
        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        
        switch (command.getName().toLowerCase()) {
            case "spectate":
                return handleSpectate(player, args);
            case "survival":
                return handleSurvival(player, args);
            case "to":
                return handleTo(player, args);
            case "bring":
                return handleBring(player, args);
            case "kick":
                return handleKick(player, args);
            case "freeze":
                return handleFreeze(player, args);
            case "invsee":
                return handleInvsee(player, args);
            case "history":
                return handleHistory(player, args);
            case "ban":
                return handleBan(player, args);
            case "pardon":
                return handlePardon(player, args);
            case "ipban":
                return handleIpban(player, args);
            case "vanish":
                return handleVanish(player, args);
            case "hint":
                return handleHint(player, args);
            case "announce":
                return handleAnnounce(player, args);
            case "chatannounce":
                return handleChatAnnounce(player, args);
            case "give":
                return handleGive(player, args);
            case "summon":
                return handleSummon(player, args);
            case "reload":
                return handleReload(player, args);
            case "restart":
                return handleRestart(player, args);
            case "stop":
                return handleStop(player, args);
            case "gamemode":
                return handleGamemode(player, args);
            case "kill":
                return handleKill(player, args);
            case "banlist":
                return handleBanlist(player, args);
            case "audit":
                return handleAudit(player, args);
            case "op":
                return handleOp(player, args);
            case "deop":
                return handleDeop(player, args);
            case "staff":
                return handleStaff(player, args);
        }
        
        return false;
    }
    
    private boolean handleSpectate(Player player, String[] args) {
        if (!staffManager.hasPermission(player.getUniqueId(), "moderator")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        
        player.setGameMode(GameMode.SPECTATOR);
        player.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.WHITE + "You are now in spectator mode.");
        return true;
    }
    
    private boolean handleSurvival(Player player, String[] args) {
        if (!staffManager.hasPermission(player.getUniqueId(), "moderator")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        
        player.setGameMode(GameMode.SURVIVAL);
        player.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.WHITE + "You are now in survival mode.");
        return true;
    }
    
    private boolean handleTo(Player player, String[] args) {
        if (!staffManager.hasPermission(player.getUniqueId(), "moderator")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /to <player>");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Player not found: " + args[0]);
            return true;
        }
        
        player.teleport(target.getLocation());
        player.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.WHITE + "Teleported to " + target.getDisplayName());
        return true;
    }
    
    private boolean handleBring(Player player, String[] args) {
        if (!staffManager.hasPermission(player.getUniqueId(), "moderator")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /bring <player>");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Player not found: " + args[0]);
            return true;
        }
        
        target.teleport(player.getLocation());
        target.sendMessage(ChatColor.AQUA + "✓ " + ChatColor.WHITE + "You have been brought to " + player.getDisplayName());
        player.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.WHITE + "Brought " + target.getDisplayName() + " to you.");
        return true;
    }
    
    private boolean handleKick(Player player, String[] args) {
        if (!staffManager.hasPermission(player.getUniqueId(), "moderator")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /kick <player> [reason]");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Player not found: " + args[0]);
            return true;
        }
        
        String reason = args.length > 1 ? String.join(" ", Arrays.copyOfRange(args, 1, args.length)) : "No reason provided";
        
        target.kickPlayer(ChatColor.RED + "You have been kicked!\n" + ChatColor.GRAY + "Reason: " + reason + "\n" + ChatColor.YELLOW + "Kicked by: " + player.getName());
        
        Bukkit.broadcastMessage(ChatColor.RED + "✦ " + ChatColor.WHITE + target.getName() + " was kicked by " + player.getDisplayName() + ChatColor.GRAY + " (" + reason + ")");
        player.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.WHITE + "Kicked " + target.getName());
        return true;
    }
    
    private boolean handleFreeze(Player player, String[] args) {
        if (!staffManager.hasPermission(player.getUniqueId(), "moderator")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /freeze <player>");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Player not found: " + args[0]);
            return true;
        }
        
        if (staffManager.isFrozen(target.getUniqueId())) {
            staffManager.unfreezePlayer(target, player);
        } else {
            staffManager.freezePlayer(target, player);
        }
        return true;
    }
    
    private boolean handleInvsee(Player player, String[] args) {
        if (!staffManager.hasPermission(player.getUniqueId(), "moderator")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /invsee <player>");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Player not found: " + args[0]);
            return true;
        }
        
        Inventory inv = staffManager.createInventoryViewer(target, player);
        player.openInventory(inv);
        player.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.WHITE + "Opening " + target.getName() + "'s inventory.");
        return true;
    }
    
    private boolean handleHistory(Player player, String[] args) {
        if (!staffManager.hasPermission(player.getUniqueId(), "moderator")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /history <player>");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Player not found: " + args[0]);
            return true;
        }
        
        List<String> history = staffManager.getChatHistory(target.getUniqueId());
        if (history.isEmpty()) {
            player.sendMessage(ChatColor.GRAY + "No chat history found for " + target.getName());
            return true;
        }
        
        player.sendMessage(ChatColor.GOLD + "✦ " + ChatColor.WHITE + "Chat History for " + target.getName() + ":");
        for (String msg : history) {
            player.sendMessage(ChatColor.GRAY + "  " + msg);
        }
        return true;
    }
    
    private boolean handleBan(Player player, String[] args) {
        if (!staffManager.hasPermission(player.getUniqueId(), "administrator")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /ban <player> <time> <reason>");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Player not found: " + args[0]);
            return true;
        }
        
        String time = args[1];
        String reason = args.length > 2 ? String.join(" ", Arrays.copyOfRange(args, 2, args.length)) : "No reason provided";
        
        target.ban(ChatColor.RED + "You have been banned!\n" + ChatColor.GRAY + "Time: " + time + "\nReason: " + reason + "\n" + ChatColor.YELLOW + "Banned by: " + player.getName());
        target.kickPlayer(ChatColor.RED + "You have been banned!\n" + ChatColor.GRAY + "Time: " + time + "\nReason: " + reason + "\n" + ChatColor.YELLOW + "Banned by: " + player.getName());
        
        Bukkit.broadcastMessage(ChatColor.RED + "✦ " + ChatColor.WHITE + target.getName() + " was banned by " + player.getDisplayName() + ChatColor.GRAY + " (" + time + " - " + reason + ")");
        player.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.WHITE + "Banned " + target.getName());
        return true;
    }
    
    private boolean handlePardon(Player player, String[] args) {
        if (!staffManager.hasPermission(player.getUniqueId(), "administrator")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /pardon <player>");
            return true;
        }
        
        Bukkit.getBanList(org.bukkit.BanList.Type.NAME).pardon(args[0]);
        player.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.WHITE + "Pardoned " + args[0]);
        return true;
    }
    
    private boolean handleIpban(Player player, String[] args) {
        if (!staffManager.hasPermission(player.getUniqueId(), "administrator")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /ipban <player> <time> <reason>");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Player not found: " + args[0]);
            return true;
        }
        
        String time = args[1];
        String reason = args.length > 2 ? String.join(" ", Arrays.copyOfRange(args, 2, args.length)) : "No reason provided";
        
        String ip = target.getAddress().getAddress().getHostAddress();
        Bukkit.getBanList(org.bukkit.BanList.Type.IP).addBan(ip, reason, null, player.getName());
        
        target.ban(ChatColor.RED + "You have been IP banned!\n" + ChatColor.GRAY + "Time: " + time + "\nReason: " + reason + "\n" + ChatColor.YELLOW + "IP Banned by: " + player.getName());
        target.kickPlayer(ChatColor.RED + "You have been IP banned!\n" + ChatColor.GRAY + "Time: " + time + "\nReason: " + reason + "\n" + ChatColor.YELLOW + "IP Banned by: " + player.getName());
        
        Bukkit.broadcastMessage(ChatColor.RED + "✦ " + ChatColor.WHITE + target.getName() + " was IP banned by " + player.getDisplayName() + ChatColor.GRAY + " (" + time + " - " + reason + ")");
        player.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.WHITE + "IP banned " + target.getName());
        return true;
    }
    
    private boolean handleVanish(Player player, String[] args) {
        if (!staffManager.hasPermission(player.getUniqueId(), "administrator")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        
        // Toggle vanish (simplified - would need vanish plugin for full functionality)
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.hasPermission("kingdom.staff")) {
                p.hidePlayer(plugin, player);
            }
        }
        
        player.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.WHITE + "You are now vanished from regular players.");
        return true;
    }
    
    private boolean handleHint(Player player, String[] args) {
        if (!staffManager.hasPermission(player.getUniqueId(), "administrator")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /hint <message>");
            return true;
        }
        
        String message = String.join(" ", args);
        staffManager.sendHint(player, message);
        player.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.WHITE + "Hint sent: " + ChatColor.GOLD + ChatColor.ITALIC + message);
        return true;
    }
    
    private boolean handleAnnounce(Player player, String[] args) {
        if (!staffManager.hasPermission(player.getUniqueId(), "administrator")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /announce <message>");
            return true;
        }
        
        String message = String.join(" ", args);
        staffManager.sendAnnouncement(player, message);
        player.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.WHITE + "Announcement sent: " + ChatColor.RED + ChatColor.BOLD + message);
        return true;
    }
    
    private boolean handleChatAnnounce(Player player, String[] args) {
        if (!staffManager.hasPermission(player.getUniqueId(), "administrator")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /chatannounce <message>");
            return true;
        }
        
        String message = String.join(" ", args);
        staffManager.sendChatAnnouncement(player, message, true, true, ChatColor.GOLD);
        player.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.WHITE + "Chat announcement sent.");
        return true;
    }
    
    private boolean handleGive(Player player, String[] args) {
        if (!staffManager.hasPermission(player.getUniqueId(), "event_staff")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /give <item> <quantity>");
            return true;
        }
        
        try {
            Material material = Material.matchMaterial(args[0].toUpperCase());
            if (material == null) {
                player.sendMessage(ChatColor.RED + "Invalid item: " + args[0]);
                return true;
            }
            
            int quantity = Integer.parseInt(args[1]);
            ItemStack item = new ItemStack(material, quantity);
            player.getInventory().addItem(item);
            player.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.WHITE + "Given " + quantity + "x " + material.name());
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Invalid quantity: " + args[1]);
        }
        return true;
    }
    
    private boolean handleSummon(Player player, String[] args) {
        if (!staffManager.hasPermission(player.getUniqueId(), "event_staff")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /summon <entity>");
            return true;
        }
        
        player.performCommand("minecraft:summon " + args[0]);
        player.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.WHITE + "Summoned " + args[0]);
        return true;
    }
    
    private boolean handleReload(Player player, String[] args) {
        if (!staffManager.hasPermission(player.getUniqueId(), "event_staff")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        
        Bukkit.reload();
        player.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.WHITE + "Server reloaded.");
        return true;
    }
    
    private boolean handleRestart(Player player, String[] args) {
        if (!staffManager.hasPermission(player.getUniqueId(), "event_staff")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        
        Bukkit.broadcastMessage(ChatColor.RED + "✦ " + ChatColor.WHITE + "Server restarting in 10 seconds!");
        
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.spigot().restart();
            }
        }.runTaskLater(plugin, 200L); // 10 seconds
        return true;
    }
    
    private boolean handleStop(Player player, String[] args) {
        if (!staffManager.hasPermission(player.getUniqueId(), "event_staff")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        
        Bukkit.shutdown();
        return true;
    }
    
    private boolean handleGamemode(Player player, String[] args) {
        if (!staffManager.hasPermission(player.getUniqueId(), "event_staff")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /gamemode <mode> [player]");
            return true;
        }
        
        GameMode mode;
        switch (args[0].toLowerCase()) {
            case "survival": case "0": mode = GameMode.SURVIVAL; break;
            case "creative": case "1": mode = GameMode.CREATIVE; break;
            case "adventure": case "2": mode = GameMode.ADVENTURE; break;
            case "spectator": case "3": mode = GameMode.SPECTATOR; break;
            default:
                player.sendMessage(ChatColor.RED + "Invalid gamemode: " + args[0]);
                return true;
        }
        
        Player target = args.length > 1 ? Bukkit.getPlayer(args[1]) : player;
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Player not found: " + (args.length > 1 ? args[1] : "yourself"));
            return true;
        }
        
        target.setGameMode(mode);
        target.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.WHITE + "Gamemode set to " + mode.name());
        if (target != player) {
            player.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.WHITE + "Set " + target.getName() + "'s gamemode to " + mode.name());
        }
        return true;
    }
    
    private boolean handleKill(Player player, String[] args) {
        if (!staffManager.hasPermission(player.getUniqueId(), "event_staff")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        
        if (args.length == 0) {
            player.setHealth(0);
            player.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.WHITE + "Killed yourself.");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Player not found: " + args[0]);
            return true;
        }
        
        target.setHealth(0);
        player.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.WHITE + "Killed " + target.getName());
        return true;
    }
    
    private boolean handleBanlist(Player player, String[] args) {
        if (!staffManager.hasPermission(player.getUniqueId(), "event_staff")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        
        player.performCommand("minecraft:banlist");
        return true;
    }
    
    private boolean handleAudit(Player player, String[] args) {
        if (!staffManager.hasPermission(player.getUniqueId(), "founder")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /audit <player>");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Player not found: " + args[0]);
            return true;
        }
        
        // Show audit information
        player.sendMessage(ChatColor.DARK_PURPLE + "✦ " + ChatColor.WHITE + "Audit Log for " + target.getName() + ":");
        player.sendMessage(ChatColor.GRAY + "  Location: " + target.getLocation().getX() + ", " + target.getLocation().getY() + ", " + target.getLocation().getZ());
        player.sendMessage(ChatColor.GRAY + "  World: " + target.getWorld().getName());
        player.sendMessage(ChatColor.GRAY + "  Health: " + target.getHealth() + "/20");
        player.sendMessage(ChatColor.GRAY + "  Food: " + target.getFoodLevel() + "/20");
        player.sendMessage(ChatColor.GRAY + "  Gamemode: " + target.getGameMode().name());
        player.sendMessage(ChatColor.GRAY + "  IP: " + target.getAddress().getAddress().getHostAddress());
        
        StaffManager.StaffRank rank = staffManager.getStaffRank(target.getUniqueId());
        if (rank != null) {
            player.sendMessage(ChatColor.GRAY + "  Staff Rank: " + rank.getPrefix());
        }
        
        return true;
    }
    
    private boolean handleOp(Player player, String[] args) {
        if (!staffManager.hasPermission(player.getUniqueId(), "founder")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /op <player>");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Player not found: " + args[0]);
            return true;
        }
        
        target.setOp(true);
        player.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.WHITE + "Opped " + target.getName());
        return true;
    }
    
    private boolean handleDeop(Player player, String[] args) {
        if (!staffManager.hasPermission(player.getUniqueId(), "founder")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /deop <player>");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Player not found: " + args[0]);
            return true;
        }
        
        target.setOp(false);
        player.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.WHITE + "Deopped " + target.getName());
        return true;
    }
    
    private boolean handleStaff(Player player, String[] args) {
        if (!player.isOp()) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /staff <add|remove|list> <player> [rank]");
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "add":
                if (args.length < 3) {
                    player.sendMessage(ChatColor.RED + "Usage: /staff add <player> <rank>");
                    return true;
                }
                
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    player.sendMessage(ChatColor.RED + "Player not found: " + args[1]);
                    return true;
                }
                
                StaffManager.StaffRank rank;
                try {
                    rank = StaffManager.StaffRank.valueOf(args[2].toUpperCase());
                } catch (IllegalArgumentException e) {
                    player.sendMessage(ChatColor.RED + "Invalid rank. Available ranks: MODERATOR, ADMINISTRATOR, EVENT_STAFF, FOUNDER");
                    return true;
                }
                
                staffManager.setStaffRank(target.getUniqueId(), rank);
                player.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.WHITE + "Added " + target.getName() + " as " + rank.getPrefix());
                break;
                
            case "remove":
                Player removeTarget = Bukkit.getPlayer(args[1]);
                if (removeTarget == null) {
                    player.sendMessage(ChatColor.RED + "Player not found: " + args[1]);
                    return true;
                }
                
                staffManager.removeStaff(removeTarget.getUniqueId());
                player.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.WHITE + "Removed " + removeTarget.getName() + " from staff.");
                break;
                
            case "list":
                Map<UUID, StaffManager.StaffRank> allStaff = staffManager.getAllStaff();
                player.sendMessage(ChatColor.GOLD + "✦ " + ChatColor.WHITE + "Online Staff:");
                for (Map.Entry<UUID, StaffManager.StaffRank> entry : allStaff.entrySet()) {
                    Player staffPlayer = Bukkit.getPlayer(entry.getKey());
                    if (staffPlayer != null) {
                        player.sendMessage(entry.getValue().getPrefix() + ChatColor.WHITE + staffPlayer.getName());
                    }
                }
                break;
                
            default:
                player.sendMessage(ChatColor.RED + "Usage: /staff <add|remove|list> <player> [rank]");
                break;
        }
        
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            // Player name completions
            for (Player player : Bukkit.getOnlinePlayers()) {
                completions.add(player.getName());
            }
        }
        
        if (command.getName().equalsIgnoreCase("staff") && args.length == 2) {
            if (args[0].equalsIgnoreCase("add")) {
                for (StaffManager.StaffRank rank : StaffManager.StaffRank.values()) {
                    completions.add(rank.name());
                }
            }
        }
        
        return completions.stream()
            .filter(completion -> completion.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
            .collect(Collectors.toList());
    }
}
