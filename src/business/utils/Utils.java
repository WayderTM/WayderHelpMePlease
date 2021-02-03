package business.utils;

import business.enums.EnglishName;
import business.main.SpigotPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class Utils {
    private SpigotPlugin plugin;

    public Utils(SpigotPlugin plugin) {
        this.plugin = plugin;
    }

    public String getEnglishName(String rusname) {
        StringBuilder sb = new StringBuilder();
        for (char chr : rusname.toCharArray()) {
            if (getChar(String.valueOf(chr)) != null) sb.append(getChar(String.valueOf(chr)));
        }
        return sb.toString();
    }

    private String getChar(String chr) {
        for (EnglishName name : EnglishName.values()) {
            if (name.ruschar.equalsIgnoreCase(chr)) return name.engchar;
        }
        return null;
    }

    public String getBusinessName(Player p) {
        for (String s : plugin.getConfig().getConfigurationSection("business").getKeys(false)) {
            if (plugin.getConfig().getString("business."+s+".owner").equals(p.getName())) {
                return s;
            }
        }
        return null;
    }

    public boolean isInt(String s) {
        try {
            int i = Integer.parseInt(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public ItemStack getItem(String name, List<String> lore, int id, int durability) {
        ItemStack i = new ItemStack(Material.getMaterial(id), 1, (short) durability);
        ItemMeta meta = i.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        meta.setLore(lore);
        i.setItemMeta(meta);
        return i;
    }

    public ItemStack removeLore(ItemStack stack) {
        ItemMeta m = stack.getItemMeta();
        m.setLore(null);
        stack.setItemMeta(m);
        return stack;
    }
}