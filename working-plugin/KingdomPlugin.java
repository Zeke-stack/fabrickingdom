import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class KingdomPlugin extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        getLogger().info("Kingdom Plugin Enabled!");
        getServer().getPluginManager().registerEvents(this, this);
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.setJoinMessage("[Kingdom] " + player.getName() + " has joined!");
        player.sendMessage("Welcome to the Kingdom Server!");
        player.sendMessage("Type /kingdom for commands!");
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("kingdom")) {
            sender.sendMessage("Kingdom Commands:");
            sender.sendMessage("/kingdom - Show this help");
            sender.sendMessage("/coins - Check your coins");
            return true;
        }
        if (command.getName().equalsIgnoreCase("coins")) {
            sender.sendMessage("Coins: 0");
            return true;
        }
        return false;
    }
}
