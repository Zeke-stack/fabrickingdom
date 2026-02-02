package com.kingdom.commands.commands;

import com.kingdom.commands.KingdomCommands;
import com.kingdom.commands.utils.KingdomManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class KingdomCommandExecutor implements CommandExecutor {
    
    private final KingdomCommands plugin;
    private final KingdomManager kingdomManager;
    
    public KingdomCommandExecutor(KingdomCommands plugin) {
        this.plugin = plugin;
        this.kingdomManager = plugin.getKingdomManager();
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
            case "kingdom":
                return handleKingdom(player, args);
            case "realm":
                return handleRealm(player, args);
            case "royal":
                return handleRoyal(player, args);
            case "herald":
                return handleHerald(player, args);
            case "decree":
                return handleDecree(player, args);
            case "proclaim":
                return handleProclaim(player, args);
            case "summoncourt":
                return handleSummonCourt(player, args);
            case "knight":
                return handleKnight(player, args);
            case "noble":
                return handleNoble(player, args);
            case "peasant":
                return handlePeasant(player, args);
        }
        
        return false;
    }
    
    private boolean handleKingdom(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage(ChatColor.GOLD + "✦ " + ChatColor.WHITE + "Kingdom Information:");
            player.sendMessage(ChatColor.GRAY + "  Ruler: " + ChatColor.YELLOW + "Kingdom Founder");
            player.sendMessage(ChatColor.GRAY + "  Population: " + ChatColor.GREEN + Bukkit.getOnlinePlayers().size());
            player.sendMessage(ChatColor.GRAY + "  Laws: " + ChatColor.AQUA + "Use /kingdom laws");
            player.sendMessage(ChatColor.GRAY + "  Ranks: " + ChatColor.AQUA + "Use /kingdom ranks");
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "info":
                player.sendMessage(ChatColor.GOLD + "✦ " + ChatColor.WHITE + "Kingdom Information:");
                player.sendMessage(ChatColor.GRAY + "  Welcome to the Kingdom! A place of honor and glory.");
                player.sendMessage(ChatColor.GRAY + "  Follow the laws and serve with loyalty.");
                break;
            case "status":
                player.sendMessage(ChatColor.GOLD + "✦ " + ChatColor.WHITE + "Kingdom Status:");
                player.sendMessage(ChatColor.GRAY + "  Status: " + ChatColor.GREEN + "Prosperous");
                player.sendMessage(ChatColor.GRAY + "  Treasury: " + ChatColor.YELLOW + "Full");
                player.sendMessage(ChatColor.GRAY + "  Military: " + ChatColor.RED + "Strong");
                break;
            case "laws":
                player.sendMessage(ChatColor.GOLD + "✦ " + ChatColor.WHITE + "Kingdom Laws:");
                player.sendMessage(ChatColor.GRAY + "  1. Respect all players and staff");
                player.sendMessage(ChatColor.GRAY + "  2. No griefing or stealing");
                player.sendMessage(ChatColor.GRAY + "  3. Follow chat rules");
                player.sendMessage(ChatColor.GRAY + "  4. Report bugs to staff");
                break;
            case "ranks":
                player.sendMessage(ChatColor.GOLD + "✦ " + ChatColor.WHITE + "Kingdom Ranks:");
                player.sendMessage(ChatColor.DARK_PURPLE + "  Founder - " + ChatColor.WHITE + "Kingdom ruler");
                player.sendMessage(ChatColor.RED + "  Administrator - " + ChatColor.WHITE + "Kingdom management");
                player.sendMessage(ChatColor.GOLD + "  Event Staff - " + ChatColor.WHITE + "Event coordination");
                player.sendMessage(ChatColor.GREEN + "  Moderator - " + ChatColor.WHITE + "Law enforcement");
                player.sendMessage(ChatColor.AQUA + "  Noble - " + ChatColor.WHITE + "Honored citizens");
                player.sendMessage(ChatColor.YELLOW + "  Knight - " + ChatColor.WHITE + "Kingdom protectors");
                player.sendMessage(ChatColor.GRAY + "  Peasant - " + ChatColor.WHITE + "Kingdom citizens");
                break;
            default:
                player.sendMessage(ChatColor.RED + "Usage: /kingdom <info|status|laws|ranks>");
                break;
        }
        return true;
    }
    
    private boolean handleRealm(Player player, String[] args) {
        if (!player.hasPermission("kingdom.staff.administrator")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /realm <announce|lock|unlock|tax>");
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "announce":
                String message = args.length > 1 ? String.join(" ", args).substring(8) : "Realm announcement";
                Bukkit.broadcastMessage(ChatColor.GOLD + "✦ [REALM] " + ChatColor.WHITE + message);
                player.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.WHITE + "Realm announcement sent.");
                break;
            case "lock":
                Bukkit.broadcastMessage(ChatColor.RED + "✦ [REALM] " + ChatColor.WHITE + "The realm is now locked!");
                player.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.WHITE + "Realm locked.");
                break;
            case "unlock":
                Bukkit.broadcastMessage(ChatColor.GREEN + "✦ [REALM] " + ChatColor.WHITE + "The realm is now open!");
                player.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.WHITE + "Realm unlocked.");
                break;
            case "tax":
                Bukkit.broadcastMessage(ChatColor.YELLOW + "✦ [REALM] " + ChatColor.WHITE + "Tax collection day!");
                player.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.WHITE + "Tax collection announced.");
                break;
            default:
                player.sendMessage(ChatColor.RED + "Usage: /realm <announce|lock|unlock|tax>");
                break;
        }
        return true;
    }
    
    private boolean handleRoyal(Player player, String[] args) {
        if (!player.hasPermission("kingdom.staff.administrator")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
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
                Player pardonTarget = Bukkit.getPlayer(args[1]);
                if (pardonTarget != null) {
                    Bukkit.broadcastMessage(ChatColor.GOLD + "✦ [ROYAL] " + ChatColor.WHITE + pardonTarget.getName() + " has been pardoned by " + player.getDisplayName());
                    player.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.WHITE + "Pardoned " + pardonTarget.getName());
                } else {
                    player.sendMessage(ChatColor.RED + "Player not found: " + args[1]);
                }
                break;
            case "exile":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /royal exile <player>");
                    return true;
                }
                Player exileTarget = Bukkit.getPlayer(args[1]);
                if (exileTarget != null) {
                    exileTarget.kickPlayer(ChatColor.RED + "You have been exiled from the kingdom!\n" + ChatColor.YELLOW + "By order of " + player.getName());
                    Bukkit.broadcastMessage(ChatColor.RED + "✦ [ROYAL] " + ChatColor.WHITE + exileTarget.getName() + " has been exiled by " + player.getDisplayName());
                    player.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.WHITE + "Exiled " + exileTarget.getName());
                } else {
                    player.sendMessage(ChatColor.RED + "Player not found: " + args[1]);
                }
                break;
            case "decree":
                String decree = args.length > 1 ? String.join(" ", args).substring(6) : "Royal decree";
                Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "✦ [ROYAL DECREE] " + ChatColor.WHITE + decree);
                player.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.WHITE + "Royal decree issued.");
                break;
            case "summon":
                Bukkit.broadcastMessage(ChatColor.GOLD + "✦ [ROYAL] " + ChatColor.WHITE + player.getDisplayName() + " summons all nobility to the court!");
                player.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.WHITE + "Nobility summoned.");
                break;
            default:
                player.sendMessage(ChatColor.RED + "Usage: /royal <pardon|exile|decree|summon>");
                break;
        }
        return true;
    }
    
    private boolean handleHerald(Player player, String[] args) {
        if (!player.hasPermission("kingdom.staff.moderator")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /herald <message>");
            return true;
        }
        
        String message = String.join(" ", args);
        Bukkit.broadcastMessage(ChatColor.AQUA + "✦ [HERALD] " + ChatColor.WHITE + message);
        player.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.WHITE + "Herald announcement sent.");
        return true;
    }
    
    private boolean handleDecree(Player player, String[] args) {
        if (!player.hasPermission("kingdom.staff.administrator")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /decree <decree text>");
            return true;
        }
        
        String decree = String.join(" ", args);
        Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "✦ [ROYAL DECREE] " + ChatColor.WHITE + decree);
        player.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.WHITE + "Royal decree issued.");
        return true;
    }
    
    private boolean handleProclaim(Player player, String[] args) {
        if (!player.hasPermission("kingdom.staff.event_staff")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /proclaim <message>");
            return true;
        }
        
        String message = String.join(" ", args);
        Bukkit.broadcastMessage(ChatColor.YELLOW + "✦ [PROCLAMATION] " + ChatColor.WHITE + message);
        player.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.WHITE + "Proclamation made.");
        return true;
    }
    
    private boolean handleSummonCourt(Player player, String[] args) {
        if (!player.hasPermission("kingdom.staff.administrator")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        
        Bukkit.broadcastMessage(ChatColor.GOLD + "✦ [ROYAL] " + ChatColor.WHITE + player.getDisplayName() + " summons all nobility to the court!");
        player.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.WHITE + "Court summoned.");
        return true;
    }
    
    private boolean handleKnight(Player player, String[] args) {
        if (!player.hasPermission("kingdom.staff.administrator")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
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
        
        Bukkit.broadcastMessage(ChatColor.YELLOW + "✦ [ROYAL] " + ChatColor.WHITE + target.getName() + " has been knighted by " + player.getDisplayName());
        target.sendMessage(ChatColor.YELLOW + "✦ " + ChatColor.WHITE + "You have been knighted! Serve the kingdom with honor!");
        target.setDisplayName(ChatColor.YELLOW + "[Sir] " + ChatColor.WHITE + target.getName());
        player.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.WHITE + "Knighted " + target.getName());
        return true;
    }
    
    private boolean handleNoble(Player player, String[] args) {
        if (!player.hasPermission("kingdom.staff.founder")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
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
        
        Bukkit.broadcastMessage(ChatColor.AQUA + "✦ [ROYAL] " + ChatColor.WHITE + target.getName() + " has been granted nobility by " + player.getDisplayName());
        target.sendMessage(ChatColor.AQUA + "✦ " + ChatColor.WHITE + "You have been granted nobility! Serve with wisdom!");
        target.setDisplayName(ChatColor.AQUA + "[Lord] " + ChatColor.WHITE + target.getName());
        player.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.WHITE + "Granted nobility to " + target.getName());
        return true;
    }
    
    private boolean handlePeasant(Player player, String[] args) {
        if (!player.hasPermission("kingdom.kingdom.peasant")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        
        player.sendMessage(ChatColor.GRAY + "✦ " + ChatColor.WHITE + "Peasant Life Information:");
        player.sendMessage(ChatColor.GRAY + "  As a peasant, you are the backbone of the kingdom.");
        player.sendMessage(ChatColor.GRAY + "  Work hard, follow the laws, and you may rise in rank.");
        player.sendMessage(ChatColor.GRAY + "  Current status: " + ChatColor.GREEN + "Loyal citizen");
        player.sendMessage(ChatColor.GRAY + "  Daily tasks: Farm, build, trade, and serve the kingdom.");
        return true;
    }
}
