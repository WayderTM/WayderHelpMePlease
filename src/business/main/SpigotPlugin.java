package business.main;

import business.commands.Abiz;
import business.commands.Bizsdelka;
import business.events.Events;
import business.utils.Utils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;

public class SpigotPlugin extends JavaPlugin {
    public Utils utils = new Utils(this);
    public Economy e;
    public HashMap<Player, HashMap<Player, Integer>> sdelka = new HashMap<>();

    private void EconomyInit() {
        RegisteredServiceProvider<Economy> reg = Bukkit.getServicesManager().getRegistration(Economy.class);
        e = reg.getProvider();
    }

    public void onEnable() {
        EconomyInit();
        getCommand("abiz").setExecutor(new Abiz(this));
        getCommand("bizsdelka").setExecutor(new Bizsdelka(this));
        Bukkit.getPluginManager().registerEvents(new Events(this), this);
        File config = new File(getDataFolder() + File.separator + "config.yml");
        if (!config.exists()) {
            getConfig().options().copyDefaults(true);
            saveDefaultConfig();
        }
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            for (String s : getConfig().getConfigurationSection("business").getKeys(false)) {
                OfflinePlayer plr = Bukkit.getOfflinePlayer(getConfig().getString("business."+s+".owner"));
                if (plr.getPlayer().hasPermission("business.vipnalog")) {
                    int procent = (int) (getConfig().getInt("business."+s+".balance")*0.05);
                    int nalog = getConfig().getInt("business."+s+".nalog");
                    getConfig().set("business."+s+".nalog", nalog+procent);
                    saveConfig();
                } else {
                    if (plr.getPlayer().hasPermission("business.nonalog")) return;
                    int procent = (int) (getConfig().getInt("business."+s+".balance")*0.10);
                    int nalog = getConfig().getInt("business."+s+".nalog");
                    getConfig().set("business."+s+".nalog", nalog+procent);
                    saveConfig();
                }
                if (getConfig().getInt("business."+s+".nalog") > 50000) {
                    getConfig().set("business."+s+".owner", "null");
                    getConfig().set("business."+s+".nalog", 0);
                    saveConfig();
                }
            }
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&f[&bНалоговая служба&f] На все бизнесы был начислен налог!"));
        }, (20L*60)*60, (20L*60)*60);
    }
}