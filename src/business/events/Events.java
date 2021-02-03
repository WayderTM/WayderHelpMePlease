package business.events;

import business.main.SpigotPlugin;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.block.Block;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.StringJoiner;
import java.util.UUID;

public class Events implements Listener {
    private SpigotPlugin plugin;

    public Events(SpigotPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Inventory i = e.getInventory();
        if (i.getName().contains("Меню:")) {
            Player p = (Player) e.getWhoClicked();
            String busname = i.getName().substring(i.getName().indexOf(":")+2);
            System.out.println(busname);
            int slot = e.getSlot()+1;
            e.setCancelled(true);
            if (plugin.getConfig().contains("business."+busname+".materials."+slot)) {
                if (e.getCurrentItem().getItemMeta().getDisplayName().toLowerCase().contains("пиво")) {
                    Bukkit.dispatchCommand(p, "beer");
                } else if (e.getCurrentItem().getItemMeta().getDisplayName().toLowerCase().contains("водка")) {
                    Bukkit.dispatchCommand(p,"vodka");
                } else if (e.getCurrentItem().getItemMeta().getDisplayName().toLowerCase().contains("текила")) {
                    Bukkit.dispatchCommand(p,"tequila");
                } else if (e.getCurrentItem().getItemMeta().getDisplayName().toLowerCase().contains("кока-кола")) {
                    Bukkit.dispatchCommand(p,"cocacola");
                } else if (e.getCurrentItem().getItemMeta().getDisplayName().toLowerCase().contains("пепси")) {
                    Bukkit.dispatchCommand(p,"pepsi");
                } else if (e.getCurrentItem().getItemMeta().getDisplayName().toLowerCase().contains("чай")) {
                    Bukkit.dispatchCommand(p,"tea");
                } else if (e.getCurrentItem().getItemMeta().getDisplayName().toLowerCase().contains("кофе")) {
                    Bukkit.dispatchCommand(p,"coffee");
                } else if (e.getCurrentItem().getItemMeta().getDisplayName().toLowerCase().contains("коктейль")) {
                    Bukkit.dispatchCommand(p,"coctail");
                } else if (e.getCurrentItem().getItemMeta().getDisplayName().toLowerCase().contains("энергетик")) {
                    Bukkit.dispatchCommand(p,"energydrink");
                } else {
                    p.closeInventory();
                    int sum = plugin.getConfig().getInt("business."+busname+".materials."+slot+".summa");
                    if (plugin.e.getBalance(p) >= sum) {
                        plugin.e.withdrawPlayer(p, sum);
                        int bal = plugin.getConfig().getInt("business."+busname+".balance");
                        plugin.getConfig().set("business."+busname+".balance", bal+sum);
                        plugin.saveConfig();
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lAbiz >> &eВы успешно купили " + e.getCurrentItem().getItemMeta().getDisplayName() + "&e за &a" + sum + "$!"));
                        p.getInventory().addItem(plugin.utils.removeLore(e.getCurrentItem()));
                    } else {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lAbiz >> &eНедостаточно средств!"));
                    }
                }
            }
        } if (i.getName().contains("Информация о бизнесе")) e.setCancelled(true);
    }

    @EventHandler
    public void onDamage(PlayerInteractEvent e) {
        //Action a = e.getAction();
        if (true) { // было лень удалять проверку поэтому пусть будет так кто против хый сысысте!!
            Player p = e.getPlayer();
            for (String s : plugin.getConfig().getConfigurationSection("business").getKeys(false)) {
                ArmorStand stand = (ArmorStand) Bukkit.getEntity(UUID.fromString(plugin.getConfig().getString("business."+s+".hologramuuid")));
                if (p.getLocation().distance(stand.getLocation()) <= 5 && p.isSneaking()) {
                    if (plugin.getConfig().getString("business."+s+".owner").equals("null") && plugin.utils.getBusinessName(p) == null) {
                        int sum = plugin.getConfig().getInt("business."+s+".sell");
                        if (plugin.e.getBalance(p) >= sum) {
                            plugin.e.withdrawPlayer(p, sum);
                            plugin.getConfig().set("business."+s+".owner", p.getName());
                            plugin.saveConfig();
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lAbiz >> &eПоздравляем вас с покупкой &7"+plugin.getConfig().getString("business."+s+".rusname")));
                            stand.setCustomName(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("hologram.ownbusiness").replace("%owner%", p.getName()).replace("%businessname%", plugin.getConfig().getString("business."+s+".rusname"))));
                        } else {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lAbiz >> &eУ вас недостаточно средств!"));
                        }
                    } else {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lAbiz >> &eУ этого бизнеса уже есть свой владелец или у вас уже есть свой бизнес!"));
                    }
                }
            }
        }
    }

    /*@EventHandler
    public void onKalian(PlayerInteractEvent e) {
        Action a = e.getAction();
        if (a == Action.RIGHT_CLICK_BLOCK) {
            Block b = e.getClickedBlock();
            if (b.getType().getId() == 379) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6&lAtlantWorld&7] &eВы начали курить!"));
                e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 120, 2));
            }
        }
    }*/
}