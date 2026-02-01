import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

public class SimpleKingdomPlugin extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        getLogger().info("Kingdom Plugin Enabled!");
        getServer().getPluginManager().registerEvents(this, this);
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.setJoinMessage(ChatColor.GOLD + " [Kingdom] " + player.getName() + " has joined!");
        player.sendMessage(ChatColor.GOLD + "Welcome to the Kingdom Server!");
        player.sendMessage(ChatColor.YELLOW + "Type /kingdom for commands!");
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("kingdom")) {
            sender.sendMessage(ChatColor.GOLD + "Kingdom Commands:");
            sender.sendMessage(ChatColor.YELLOW + "/kingdom - Show this help");
            sender.sendMessage(ChatColor.YELLOW + "/coins - Check your coins");
            return true;
        }
        if (command.getName().equalsIgnoreCase("coins")) {
            sender.sendMessage(ChatColor.GOLD + "Coins: 0");
            return true;
        }
        return false;
    }
}
