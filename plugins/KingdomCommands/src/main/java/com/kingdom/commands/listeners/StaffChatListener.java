package com.kingdom.commands.listeners;

import com.kingdom.commands.KingdomCommands;
import com.kingdom.commands.StaffManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class StaffChatListener implements Listener {
    
    private final KingdomCommands plugin;
    private final StaffManager staffManager;
    
    public StaffChatListener(KingdomCommands plugin) {
        this.plugin = plugin;
        this.staffManager = plugin.getStaffManager();
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        
        // Add to chat history
        String message = ChatColor.GRAY + "[" + ChatColor.YELLOW + 
            new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date()) + 
            ChatColor.GRAY + "] " + player.getDisplayName() + ChatColor.WHITE + ": " + event.getMessage();
        staffManager.addToChatHistory(uuid, message);
        
        // Check if player is frozen
        if (staffManager.isFrozen(uuid)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "❄ " + ChatColor.WHITE + "You cannot chat while frozen!");
            return;
        }
        
        // Staff chat formatting
        StaffManager.StaffRank rank = staffManager.getStaffRank(uuid);
        if (rank != null) {
            // Format staff chat with colors
            String formattedMessage = rank.getPrefix() + ChatColor.WHITE + player.getName() + 
                ChatColor.GRAY + ": " + ChatColor.WHITE + event.getMessage();
            
            // Check for special staff commands in chat
            if (event.getMessage().startsWith("!")) {
                event.setCancelled(true);
                handleStaffCommand(player, event.getMessage().substring(1));
                return;
            }
            
            // Enhanced formatting for staff
            if (rank.ordinal() >= StaffManager.StaffRank.ADMINISTRATOR.ordinal()) {
                formattedMessage = rank.getPrefix() + ChatColor.WHITE + player.getName() + 
                    ChatColor.GRAY + ": " + ChatColor.AQUA + event.getMessage();
            }
            
            event.setFormat(formattedMessage);
        }
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        
        // Update staff display name
        staffManager.updatePlayerDisplayName(player);
        
        // Custom join messages for staff
        StaffManager.StaffRank rank = staffManager.getStaffRank(uuid);
        if (rank != null) {
            event.setJoinMessage(ChatColor.GOLD + "✦ " + rank.getPrefix() + 
                ChatColor.WHITE + player.getName() + ChatColor.GRAY + " has joined the kingdom.");
        } else {
            // Regular player join with colors
            event.setJoinMessage(ChatColor.GREEN + "➤ " + ChatColor.WHITE + player.getName() + 
                ChatColor.GRAY + " has entered the kingdom.");
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        
        // Custom quit messages for staff
        StaffManager.StaffRank rank = staffManager.getStaffRank(uuid);
        if (rank != null) {
            event.setQuitMessage(ChatColor.GOLD + "✦ " + rank.getPrefix() + 
                ChatColor.WHITE + player.getName() + ChatColor.GRAY + " has left the kingdom.");
        } else {
            // Regular player quit with colors
            event.setQuitMessage(ChatColor.RED + "➤ " + ChatColor.WHITE + player.getName() + 
                ChatColor.GRAY + " has left the kingdom.");
        }
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        
        // Prevent frozen players from moving
        if (staffManager.isFrozen(uuid)) {
            if (event.getFrom().getX() != event.getTo().getX() || 
                event.getFrom().getZ() != event.getTo().getZ()) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "❄ " + ChatColor.WHITE + "You cannot move while frozen!");
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        
        // Prevent frozen players from using commands
        if (staffManager.isFrozen(uuid)) {
            String command = event.getMessage().toLowerCase();
            if (!command.startsWith("/msg") && !command.startsWith("/reply") && 
                !command.startsWith("/help") && !command.startsWith("/rules")) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "❄ " + ChatColor.WHITE + "You cannot use commands while frozen!");
            }
        }
    }
    
    private void handleStaffCommand(Player player, String command) {
        String[] parts = command.split(" ");
        String cmd = parts[0];
        String[] args = parts.length > 1 ? java.util.Arrays.copyOfRange(parts, 1, parts.length) : new String[0];
        
        switch (cmd.toLowerCase()) {
            case "staffchat":
            case "sc":
                handleStaffChat(player, args);
                break;
            case "stafflist":
                handleStaffList(player);
                break;
            case "online":
                handleOnlineStaff(player);
                break;
            default:
                player.sendMessage(ChatColor.RED + "Unknown staff command: !" + cmd);
                break;
        }
    }
    
    private void handleStaffChat(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: !staffchat <message>");
            return;
        }
        
        String message = String.join(" ", args);
        StaffManager.StaffRank rank = staffManager.getStaffRank(player.getUniqueId());
        
        // Send to all staff
        for (Player staff : org.bukkit.Bukkit.getOnlinePlayers()) {
            if (staffManager.isStaff(staff.getUniqueId())) {
                staff.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "Staff Chat" + 
                    ChatColor.DARK_GRAY + "] " + (rank != null ? rank.getPrefix() : "") + 
                    ChatColor.WHITE + player.getName() + ChatColor.GRAY + ": " + ChatColor.AQUA + message);
            }
        }
    }
    
    private void handleStaffList(Player player) {
        java.util.Map<java.util.UUID, StaffManager.StaffRank> allStaff = staffManager.getAllStaff();
        player.sendMessage(ChatColor.GOLD + "✦ " + ChatColor.WHITE + "All Staff Members:");
        
        for (java.util.Map.Entry<java.util.UUID, StaffManager.StaffRank> entry : allStaff.entrySet()) {
            org.bukkit.entity.Player staffPlayer = org.bukkit.Bukkit.getPlayer(entry.getKey());
            String status = staffPlayer != null && staffPlayer.isOnline() ? 
                ChatColor.GREEN + "Online" : ChatColor.RED + "Offline";
            player.sendMessage(entry.getValue().getPrefix() + ChatColor.WHITE + 
                org.bukkit.Bukkit.getOfflinePlayer(entry.getKey()).getName() + 
                ChatColor.GRAY + " - " + status);
        }
    }
    
    private void handleOnlineStaff(Player player) {
        player.sendMessage(ChatColor.GOLD + "✦ " + ChatColor.WHITE + "Online Staff:");
        
        for (org.bukkit.entity.Player staff : org.bukkit.Bukkit.getOnlinePlayers()) {
            if (staffManager.isStaff(staff.getUniqueId())) {
                StaffManager.StaffRank rank = staffManager.getStaffRank(staff.getUniqueId());
                player.sendMessage(rank.getPrefix() + ChatColor.WHITE + staff.getName() + 
                    ChatColor.GRAY + " - " + ChatColor.GREEN + "Available");
            }
        }
    }
}
