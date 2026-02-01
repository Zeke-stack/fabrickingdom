package com.kingdom.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class StaffManager {
    
    private final KingdomCommands plugin;
    private final Map<UUID, StaffRank> staffRanks = new ConcurrentHashMap<>();
    private final Map<UUID, Player> frozenPlayers = new ConcurrentHashMap<>();
    private final Map<UUID, List<String>> chatHistory = new ConcurrentHashMap<>();
    private final Map<UUID, Long> lastAction = new ConcurrentHashMap<>();
    
    public enum StaffRank {
        MODERATOR(ChatColor.GREEN + "[Mod] ", "§a[Mod] ", ChatColor.GREEN),
        ADMINISTRATOR(ChatColor.RED + "[Admin] ", "§c[Admin] ", ChatColor.RED),
        EVENT_STAFF(ChatColor.GOLD + "[Event] ", "§6[Event] ", ChatColor.GOLD),
        FOUNDER(ChatColor.DARK_PURPLE + "[Founder] ", "§5[Founder] ", ChatColor.DARK_PURPLE);
        
        private final String prefix;
        private final String rawPrefix;
        private final ChatColor color;
        
        StaffRank(String prefix, String rawPrefix, ChatColor color) {
            this.prefix = prefix;
            this.rawPrefix = rawPrefix;
            this.color = color;
        }
        
        public String getPrefix() { return prefix; }
        public String getRawPrefix() { return rawPrefix; }
        public ChatColor getColor() { return color; }
    }
    
    public StaffManager(KingdomCommands plugin) {
        this.plugin = plugin;
        startChatHistoryTask();
    }
    
    public void setStaffRank(UUID uuid, StaffRank rank) {
        staffRanks.put(uuid, rank);
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            updatePlayerDisplayName(player);
            player.sendMessage(ChatColor.GOLD + "✦ " + ChatColor.WHITE + "You are now " + rank.getPrefix() + ChatColor.WHITE + "!");
        }
    }
    
    public StaffRank getStaffRank(UUID uuid) {
        return staffRanks.getOrDefault(uuid, null);
    }
    
    public boolean isStaff(UUID uuid) {
        return staffRanks.containsKey(uuid);
    }
    
    public boolean hasPermission(UUID uuid, String permission) {
        StaffRank rank = getStaffRank(uuid);
        if (rank == null) return false;
        
        switch (permission) {
            case "moderator":
                return rank.ordinal() >= StaffRank.MODERATOR.ordinal();
            case "administrator":
                return rank.ordinal() >= StaffRank.ADMINISTRATOR.ordinal();
            case "event_staff":
                return rank.ordinal() >= StaffRank.EVENT_STAFF.ordinal();
            case "founder":
                return rank.ordinal() >= StaffRank.FOUNDER.ordinal();
            default:
                return false;
        }
    }
    
    public void updatePlayerDisplayName(Player player) {
        StaffRank rank = getStaffRank(player.getUniqueId());
        if (rank != null) {
            String displayName = rank.getPrefix() + ChatColor.WHITE + player.getName();
            player.setDisplayName(displayName);
            player.setPlayerListName(displayName);
        }
    }
    
    public void freezePlayer(Player target, Player staff) {
        frozenPlayers.put(target.getUniqueId(), target);
        target.sendMessage(ChatColor.RED + "❄ " + ChatColor.WHITE + "You have been frozen by " + staff.getDisplayName());
        staff.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.WHITE + "Frozen " + target.getName());
        
        // Send periodic freeze messages
        new BukkitRunnable() {
            @Override
            public void run() {
                if (frozenPlayers.containsKey(target.getUniqueId())) {
                    target.sendMessage(ChatColor.RED + "❄ " + ChatColor.WHITE + "You are frozen! " + ChatColor.GRAY + "Do not log out!");
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 20L, 100L); // Every 5 seconds
    }
    
    public void unfreezePlayer(Player target, Player staff) {
        frozenPlayers.remove(target.getUniqueId());
        target.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.WHITE + "You have been unfrozen by " + staff.getDisplayName());
        staff.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.WHITE + "Unfrozen " + target.getName());
    }
    
    public boolean isFrozen(UUID uuid) {
        return frozenPlayers.containsKey(uuid);
    }
    
    public void addToChatHistory(UUID uuid, String message) {
        chatHistory.computeIfAbsent(uuid, k -> new ArrayList<>()).add(message);
        List<String> history = chatHistory.get(uuid);
        if (history.size() > 50) { // Keep last 50 messages
            history.remove(0);
        }
    }
    
    public List<String> getChatHistory(UUID uuid) {
        return chatHistory.getOrDefault(uuid, new ArrayList<>());
    }
    
    private void startChatHistoryTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (isStaff(player.getUniqueId())) {
                        // Update staff action time
                        lastAction.put(player.getUniqueId(), System.currentTimeMillis());
                    }
                }
            }
        }.runTaskTimerAsynchronously(plugin, 0L, 6000L); // Every 5 minutes
    }
    
    public Inventory createInventoryViewer(Player target, Player staff) {
        String title = ChatColor.DARK_GRAY + "Inventory: " + ChatColor.WHITE + target.getName();
        Inventory inv = Bukkit.createInventory(null, 54, title);
        
        // Add player's inventory
        for (int i = 0; i < Math.min(36, target.getInventory().getSize()); i++) {
            ItemStack item = target.getInventory().getItem(i);
            if (item != null && item.getType() != Material.AIR) {
                inv.setItem(i, item.clone());
            }
        }
        
        // Add armor slots
        ItemStack[] armor = target.getInventory().getArmorContents();
        for (int i = 0; i < armor.length; i++) {
            if (armor[i] != null && armor[i].getType() != Material.AIR) {
                inv.setItem(45 + i, armor[i].clone());
            }
        }
        
        // Add control items
        addControlItems(inv, target, staff);
        
        return inv;
    }
    
    private void addControlItems(Inventory inv, Player target, Player staff) {
        // Info item
        ItemStack info = new ItemStack(Material.BOOK);
        ItemMeta meta = info.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Player Information");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Name: " + ChatColor.WHITE + target.getName());
        lore.add(ChatColor.GRAY + "Health: " + ChatColor.RED + String.format("%.1f", target.getHealth()) + "/20");
        lore.add(ChatColor.GRAY + "Food: " + ChatColor.GREEN + target.getFoodLevel() + "/20");
        lore.add(ChatColor.GRAY + "Location: " + ChatColor.AQUA + String.format("%.0f, %.0f, %.0f", 
            target.getLocation().getX(), target.getLocation().getY(), target.getLocation().getZ()));
        lore.add(ChatColor.GRAY + "World: " + ChatColor.YELLOW + target.getWorld().getName());
        lore.add("");
        lore.add(ChatColor.GREEN + "Click to refresh info");
        meta.setLore(lore);
        info.setItemMeta(meta);
        inv.setItem(49, info);
        
        // Freeze/Unfreeze item
        ItemStack freezeItem = new ItemStack(isFrozen(target.getUniqueId()) ? Material.LIME_WOOL : Material.RED_WOOL);
        ItemMeta freezeMeta = freezeItem.getItemMeta();
        freezeMeta.setDisplayName(isFrozen(target.getUniqueId()) ? 
            ChatColor.GREEN + "Unfreeze Player" : ChatColor.RED + "Freeze Player");
        freezeMeta.setLore(Arrays.asList(ChatColor.GRAY + "Click to " + 
            (isFrozen(target.getUniqueId()) ? "unfreeze" : "freeze") + " this player"));
        freezeItem.setItemMeta(freezeMeta);
        inv.setItem(48, freezeItem);
        
        // Teleport to player item
        ItemStack tpItem = new ItemStack(Material.ENDER_PEARL);
        ItemMeta tpMeta = tpItem.getItemMeta();
        tpMeta.setDisplayName(ChatColor.AQUA + "Teleport to Player");
        tpMeta.setLore(Arrays.asList(ChatColor.GRAY + "Click to teleport to this player"));
        tpItem.setItemMeta(tpMeta);
        inv.setItem(46, tpItem);
        
        // Bring player item
        ItemStack bringItem = new ItemStack(Material.COMPASS);
        ItemMeta bringMeta = bringItem.getItemMeta();
        bringMeta.setDisplayName(ChatColor.YELLOW + "Bring Player");
        bringMeta.setLore(Arrays.asList(ChatColor.GRAY + "Click to bring this player to you"));
        bringItem.setItemMeta(bringMeta);
        inv.setItem(47, bringItem);
    }
    
    public void sendHint(CommandSender sender, String message) {
        // Send italic gold hint
        String hint = ChatColor.GOLD + "" + ChatColor.ITALIC + message;
        
        if (sender instanceof Player) {
            Player player = (Player) sender;
            player.sendTitle("", hint, 10, 70, 20);
        }
        
        // Also send as tellraw
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), 
            "tellraw @a {\"text\":\"" + message + "\",\"italic\":true,\"color\":\"gold\"}");
    }
    
    public void sendAnnouncement(CommandSender sender, String message) {
        // Send bold red announcement
        String announcement = ChatColor.RED + "" + ChatColor.BOLD + message;
        
        // Send title to all players
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(announcement, "", 10, 100, 20);
        }
        
        // Also send as tellraw
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), 
            "tellraw @a {\"text\":\"" + message + "\",\"bold\":true,\"color\":\"red\"}");
    }
    
    public void sendChatAnnouncement(CommandSender sender, String message, boolean bold, boolean italic, ChatColor color) {
        StringBuilder json = new StringBuilder("{\"text\":\"");
        json.append(message).append("\",");
        
        if (bold) json.append("\"bold\":true,");
        if (italic) json.append("\"italic\":true,");
        if (color != null) {
            String colorName = getColorName(color);
            json.append("\"color\":\"").append(colorName).append("\",");
        }
        
        json.append("\"italic\":true}");
        
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw @a " + json.toString());
    }
    
    private String getColorName(ChatColor color) {
        switch (color) {
            case RED: return "red";
            case GOLD: return "gold";
            case GREEN: return "green";
            case AQUA: return "aqua";
            case YELLOW: return "yellow";
            case LIGHT_PURPLE: return "light_purple";
            case WHITE: return "white";
            case GRAY: return "gray";
            case DARK_GRAY: return "dark_gray";
            default: return "white";
        }
    }
    
    public Map<UUID, StaffRank> getAllStaff() {
        return new HashMap<>(staffRanks);
    }
    
    public void removeStaff(UUID uuid) {
        staffRanks.remove(uuid);
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            player.setDisplayName(player.getName());
            player.setPlayerListName(player.getName());
            player.sendMessage(ChatColor.RED + "✦ " + ChatColor.WHITE + "You are no longer staff.");
        }
    }
}
