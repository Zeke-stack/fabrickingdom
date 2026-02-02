import com.kingdom.commands.commands.*;
import com.kingdom.commands.listeners.PlayerJoinLeaveListener;
import com.kingdom.commands.listeners.CoinEconomyListener;
import com.kingdom.commands.listeners.TabListListener;
import com.kingdom.commands.listeners.AutoSaveListener;
import com.kingdom.commands.listeners.StaffChatListener;
import com.kingdom.commands.listeners.InventoryClickListener;
import com.kingdom.commands.utils.CoinManager;
import com.kingdom.commands.utils.MessageUtils;
import com.kingdom.commands.utils.KingdomManager;
import org.bukkit.plugin.java.JavaPlugin;

public class KingdomCommands extends JavaPlugin {
    
    private CoinManager coinManager;
    private AutoSaveListener autoSaveListener;
    private StaffManager staffManager;
    private StaffCommands staffCommands;
    private KingdomCommandExecutor kingdomCommandExecutor;
    private KingdomManager kingdomManager;
    
    @Override
    public void onEnable() {
        getLogger().info("§6✦ Kingdom Commands has been enabled! ✦");
        
        // Initialize staff manager
        this.staffManager = new StaffManager(this);
        this.staffCommands = new StaffCommands(this);
        this.kingdomCommandExecutor = new KingdomCommandExecutor(this);
        
        // Initialize kingdom manager
        this.kingdomManager = new KingdomManager(this);
        
        // Initialize coin manager
        this.coinManager = new CoinManager(this);
        
        // Register commands
        getCommand("kban").setExecutor(new BanCommand(this));
        getCommand("kkick").setExecutor(new KickCommand(this));
        getCommand("kop").setExecutor(new OpCommand(this));
        getCommand("kdeop").setExecutor(new DeopCommand(this));
        getCommand("coins").setExecutor(new CoinsCommand(this));
        getCommand("balance").setExecutor(new BalanceCommand(this));
        
        // Register staff commands
        registerStaffCommands();
        
        // Register events
        getServer().getPluginManager().registerEvents(new PlayerJoinLeaveListener(this), this);
        getServer().getPluginManager().registerEvents(new CoinEconomyListener(this), this);
        getServer().getPluginManager().registerEvents(new TabListListener(this), this);
        getServer().getPluginManager().registerEvents(new StaffChatListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(this), this);
        
        // Initialize auto-save
        this.autoSaveListener = new AutoSaveListener(this);
        getServer().getPluginManager().registerEvents(autoSaveListener, this);
        
        // Save default config
        saveDefaultConfig();
        
        // Initialize tab list updater
        TabListListener tabListListener = new TabListListener(this);
        tabListListener.startTabUpdater();
        
        getLogger().info("§6✦ Staff system initialized with " + staffManager.getAllStaff().size() + " staff members!");
        getLogger().info("§6✦ Kingdom system initialized with " + kingdomManager.getAllPlayerRanks().size() + " ranked citizens!");
        
        // Load existing kingdom data
        kingdomManager.loadKingdomData();
    }
    
    private void registerStaffCommands() {
        // Register all staff commands
        String[] staffCommands = {
            "spectate", "survival", "to", "bring", "kick", "freeze", "invsee", "history",
            "ban", "pardon", "ipban", "vanish", "hint", "announce", "chatannounce",
            "give", "summon", "reload", "restart", "stop", "gamemode", "kill", "banlist",
            "audit", "op", "deop", "staff"
        };
        
        for (String cmd : staffCommands) {
            getCommand(cmd).setExecutor(this.staffCommands);
        }
        
        // Register kingdom commands
        String[] kingdomCommands = {
            "kingdom", "realm", "royal", "herald", "decree", "proclaim", 
            "summoncourt", "knight", "noble", "peasant"
        };
        
        for (String cmd : kingdomCommands) {
            getCommand(cmd).setExecutor(this.kingdomCommandExecutor);
        }
    }
    
    public StaffManager getStaffManager() {
        return staffManager;
    }
    
    public CoinManager getCoinManager() {
        return coinManager;
    }
    
    @Override
    public void onDisable() {
        getLogger().info("§6✦ Kingdom Commands has been disabled! ✦");
        
        // Stop auto-save
        if (autoSaveListener != null) {
            autoSaveListener.stopAutoSave();
        }
        
        // Final save before shutdown
        getServer().getScheduler().runTask(this, () -> {
            getServer().getWorlds().forEach(world -> world.save());
            getServer().getOnlinePlayers().forEach(player -> player.saveData());
            
            // Save kingdom data
            if (kingdomManager != null) {
                kingdomManager.saveKingdomData();
            }
        });
    }
    
    public KingdomManager getKingdomManager() {
        return kingdomManager;
    }
    
    public String getPrefix() {
        return getConfig().getString("prefix", "§6[§e✦§6Kingdom§e✦§6] §r");
    }
}
