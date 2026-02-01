package com.kingdom.commands.listeners;

import com.kingdom.commands.KingdomCommands;
import com.kingdom.commands.StaffManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class InventoryClickListener implements Listener {
    
    private final KingdomCommands plugin;
    private final StaffManager staffManager;
    
    public InventoryClickListener(KingdomCommands plugin) {
        this.plugin = plugin;
        this.staffManager = plugin.getStaffManager();
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();
        
        // Check if this is an inventory viewer
        if (inventory.getTitle() != null && inventory.getTitle().contains("Inventory:")) {
            event.setCancelled(true);
            
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || clicked.getType() == Material.AIR) {
                return;
            }
            
            // Extract target player name from inventory title
            String title = inventory.getTitle();
            String targetName = title.replace("Inventory: ", "").replace(ChatColor.DARK_GRAY.toString(), "").replace(ChatColor.WHITE.toString(), "");
            Player target = plugin.getServer().getPlayer(targetName);
            
            if (target == null) {
                player.sendMessage(ChatColor.RED + "Target player is no longer online.");
                player.closeInventory();
                return;
            }
            
            // Handle control item clicks
            if (clicked.hasItemMeta() && clicked.getItemMeta().hasDisplayName()) {
                String itemName = clicked.getItemMeta().getDisplayName();
                
                if (itemName.contains("Player Information")) {
                    refreshInventory(player, target);
                    player.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.WHITE + "Player information refreshed.");
                    
                } else if (itemName.contains("Freeze Player") || itemName.contains("Unfreeze Player")) {
                    if (staffManager.isFrozen(target.getUniqueId())) {
                        staffManager.unfreezePlayer(target, player);
                    } else {
                        staffManager.freezePlayer(target, player);
                    }
                    refreshInventory(player, target);
                    
                } else if (itemName.contains("Teleport to Player")) {
                    player.teleport(target.getLocation());
                    player.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.WHITE + "Teleported to " + target.getDisplayName());
                    
                } else if (itemName.contains("Bring Player")) {
                    target.teleport(player.getLocation());
                    target.sendMessage(ChatColor.AQUA + "✓ " + ChatColor.WHITE + "You have been brought to " + player.getDisplayName());
                    player.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.WHITE + "Brought " + target.getDisplayName() + " to you.");
                }
            }
            
            // Handle regular inventory slots (0-35 for main inventory, 45-48 for armor)
            int slot = event.getSlot();
            if (slot >= 0 && slot <= 35) {
                // Main inventory slot clicked
                handleInventorySlotClick(player, target, slot, clicked);
            } else if (slot >= 45 && slot <= 48) {
                // Armor slot clicked
                handleArmorSlotClick(player, target, slot - 45, clicked);
            }
        }
    }
    
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();
        
        // Prevent dragging in inventory viewers
        if (inventory.getTitle() != null && inventory.getTitle().contains("Inventory:")) {
            event.setCancelled(true);
        }
    }
    
    private void handleInventorySlotClick(Player player, Player target, int slot, ItemStack clicked) {
        if (clicked == null || clicked.getType() == Material.AIR) {
            return;
        }
        
        // Create item info menu
        Inventory itemInfo = plugin.getServer().createInventory(null, 27, 
            ChatColor.DARK_GRAY + "Item Info - Slot " + slot);
        
        // Display item information
        ItemStack infoItem = clicked.clone();
        itemInfo.setItemMeta(createItemInfoMeta(clicked));
        itemInfo.setItemMeta(createItemInfoMeta(clicked));
        itemInfo.setItemMeta(createItemInfoMeta(clicked));
        
        itemInfo.setItemMeta(createItemInfoMeta(clicked));
        itemInfo.setItemMeta(createItemInfoMeta(clicked));
        
        // Add control items
        ItemStack removeItem = new ItemStack(Material.BARRIER);
        ItemMeta removeMeta = removeItem.getItemMeta();
        removeMeta.setDisplayName(ChatColor.RED + "Remove Item");
        removeMeta.setLore(Arrays.asList(ChatColor.GRAY + "Click to remove this item from player's inventory"));
        removeItem.setItemMeta(removeMeta);
        
        ItemStack editAmount = new ItemStack(Material.ANVIL);
        ItemMeta editMeta = editAmount.getItemMeta();
        editMeta.setDisplayName(ChatColor.YELLOW + "Edit Amount");
        editMeta.setLore(Arrays.asList(ChatColor.GRAY + "Click to edit the amount of this item"));
        editAmount.setItemMeta(editMeta);
        
        itemInfo.setItem(11, infoItem);
        itemInfo.setItem(13, removeItem);
        itemInfo.setItem(15, editAmount);
        
        player.openInventory(itemInfo);
    }
    
    private void handleArmorSlotClick(Player player, Player target, int armorSlot, ItemStack clicked) {
        String[] slotNames = {"Helmet", "Chestplate", "Leggings", "Boots"};
        String slotName = armorSlot < slotNames.length ? slotNames[armorSlot] : "Unknown";
        
        player.sendMessage(ChatColor.GOLD + "✦ " + ChatColor.WHITE + slotName + ": " + 
            ChatColor.AQUA + (clicked != null ? clicked.getType().name() : "Empty"));
        
        if (clicked != null && clicked.hasItemMeta()) {
            ItemMeta meta = clicked.getItemMeta();
            if (meta.hasDisplayName()) {
                player.sendMessage(ChatColor.GRAY + "  Name: " + ChatColor.WHITE + meta.getDisplayName());
            }
            if (meta.hasLore()) {
                player.sendMessage(ChatColor.GRAY + "  Lore: " + ChatColor.WHITE + String.join(", ", meta.getLore()));
            }
            if (meta.hasEnchants()) {
                player.sendMessage(ChatColor.GRAY + "  Enchantments: " + ChatColor.WHITE + 
                    meta.getEnchants().keySet().toString());
            }
        }
    }
    
    private ItemMeta createItemInfoMeta(ItemStack item) {
        ItemMeta meta = item.getItemMeta().clone();
        List<String> lore = Arrays.asList(
            ChatColor.GRAY + "Type: " + ChatColor.WHITE + item.getType().name(),
            ChatColor.GRAY + "Amount: " + ChatColor.WHITE + item.getAmount(),
            ChatColor.GRAY + "Durability: " + ChatColor.WHITE + item.getDurability(),
            "",
            ChatColor.GREEN + "Left click to edit",
            ChatColor.RED + "Right click to remove"
        );
        meta.setLore(lore);
        return meta;
    }
    
    private void refreshInventory(Player player, Player target) {
        Inventory newInv = staffManager.createInventoryViewer(target, player);
        player.openInventory(newInv);
    }
}
