package business.commands;

import business.main.SpigotPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Bizsdelka implements CommandExecutor {
    private SpigotPlugin plugin;

    public Bizsdelka(SpigotPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player buyer = (Player) sender;
            if (args[0].equals("accept")) {
                for (Map.Entry<Player, HashMap<Player, Integer>> entry : plugin.sdelka.entrySet()) {
                    if (entry.getValue().containsKey(buyer) && plugin.utils.getBusinessName(buyer) == null) {
                        Player p = entry.getKey();
                        String busname = plugin.utils.getBusinessName(p);
                        int sum = entry.getValue().get(buyer);
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lAbiz >> &eИгрок " + buyer.getName() + " &aсогласился &eкупить ваш бизнес за " + sum + "$."));
                        buyer.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lAbiz >> &eПоздравляем вас с покупкой &7"+plugin.getConfig().getString("business."+busname+".rusname")));
                        plugin.e.depositPlayer(p, sum);
                        plugin.e.withdrawPlayer(buyer, sum);
                        plugin.getConfig().set("business."+busname+".owner", buyer.getName());
                        plugin.saveConfig();
                        plugin.sdelka.remove(p);
                        ArmorStand stand = (ArmorStand) Bukkit.getEntity(UUID.fromString(plugin.getConfig().getString("business."+busname+".hologramuuid")));
                        stand.setCustomName(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("hologram.ownbusiness").replace("%owner%", buyer.getName()).replace("%businessname%", plugin.getConfig().getString("business."+busname+".rusname"))));
                        return true;
                    }
                }
            } else if (args[0].equals("deny")) {
                for (Map.Entry<Player, HashMap<Player, Integer>> entry : plugin.sdelka.entrySet()) {
                    if (entry.getValue().containsKey(buyer)) {
                        Player p = entry.getKey();
                        String busname = plugin.utils.getBusinessName(p);
                        int sum = entry.getValue().get(buyer);
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lAbiz >> &eИгрок " + buyer.getName() + " &cотказался &eкупить ваш бизнес за " + sum + "$."));
                        buyer.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lAbiz >> &eВы отказались от предложения игрока " + p.getName()));
                        plugin.sdelka.remove(p);
                        return true;
                    }
                }
            }
            return false;
        } else return false;
    }
}