package business.api;

import business.main.SpigotPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class BusinessAPI {
    private Plugin plugin = SpigotPlugin.getPlugin(SpigotPlugin.class);

    public boolean isGUI(Player p) {
        if (p.getOpenInventory().getTitle().contains("Меню:")) {
            return true;
        } else {
            return false;
        }
    }

    public void payMoney(String s, int sum) {
        int balance = plugin.getConfig().getInt("business."+s+".balance");
        plugin.getConfig().set("business."+s+".balance", balance+sum);
    }
}