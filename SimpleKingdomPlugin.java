import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

public class SimpleKingdomPlugin extends JavaPlugin implements Listener {
    
    @Override
    public void onEnable() {
        getLogger().info("§6✦ Kingdom Plugin Enabled! ✦");
        
        // Register events
        getServer().getPluginManager().registerEvents(this, this);
        
        // Save default config
        saveDefaultConfig();
        
        getLogger().info("§6✦ Kingdom Commands loaded successfully! ✦");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("§6✦ Kingdom Plugin Disabled! ✦");
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Custom join message
        event.setJoinMessage(ChatColor.GOLD + "⚔️ " + ChatColor.YELLOW + player.getName() + 
                           ChatColor.GOLD + " has entered the Kingdom! ⚔️");
        
        // Welcome message
        player.sendMessage(ChatColor.GOLD + "§6✦ Welcome to the Kingdom Server! ✦");
        player.sendMessage(ChatColor.YELLOW + "Type " + ChatColor.AQUA + "/kingdom" + 
                          ChatColor.YELLOW + " for kingdom commands!");
        player.sendMessage(ChatColor.YELLOW + "Type " + ChatColor.AQUA + "/coins" + 
                          ChatColor.YELLOW + " to check your coins!");
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        // Custom leave message
        event.setQuitMessage(ChatColor.GRAY + "🌾 " + ChatColor.GRAY + player.getName() + 
                           ChatColor.GRAY + " has left the Kingdom. 🌾");
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("kingdom")) {
            sender.sendMessage(ChatColor.GOLD + "§6✦ Kingdom Commands ✦");
            sender.sendMessage(ChatColor.YELLOW + "Available commands:");
            sender.sendMessage(ChatColor.AQUA + "/kingdom" + ChatColor.WHITE + " - Show this help");
            sender.sendMessage(ChatColor.AQUA + "/coins" + ChatColor.WHITE + " - Check your coins");
            sender.sendMessage(ChatColor.GREEN + "Welcome to the Medieval Kingdom!");
            return true;
        }
        
        if (command.getName().equalsIgnoreCase("coins")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                sender.sendMessage(ChatColor.GOLD + "💰 Coins: " + ChatColor.YELLOW + 
                                 "Counting your gold...");
                sender.sendMessage(ChatColor.YELLOW + "Gold nuggets: " + 
                                 ChatColor.GREEN + "0");
                sender.sendMessage(ChatColor.YELLOW + "Gold ingots: " + 
                                 ChatColor.GREEN + "0");
                sender.sendMessage(ChatColor.YELLOW + "Gold blocks: " + 
                                 ChatColor.GREEN + "0");
                sender.sendMessage(ChatColor.GOLD + "Total Value: " + 
                                 ChatColor.GREEN + "0 coins");
            } else {
                sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
            }
            return true;
        }
        
        return false;
    }
}
