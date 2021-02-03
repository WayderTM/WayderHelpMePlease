package business.commands;

import business.main.SpigotPlugin;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.*;

public class Abiz implements CommandExecutor {
    private SpigotPlugin plugin;

    public Abiz(SpigotPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if (args.length == 2) {
                if (args[0].equals("reset") && sender.hasPermission("business.admin")) {
                    String busname = args[1];
                    if (plugin.getConfig().contains("business."+busname)) {
                        plugin.getConfig().set("business."+busname+".owner", "null");
                        plugin.getConfig().set("business."+busname+".nalog", 0);
                        plugin.saveConfig();
                        ArmorStand stand = (ArmorStand) Bukkit.getEntity(UUID.fromString(plugin.getConfig().getString("business."+busname+".hologramuuid")));
                        stand.setCustomName(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("hologram.buybusiness").replace("%sell%", String.valueOf(plugin.getConfig().getInt("business."+busname+".sell"))).replace("%businessname%", plugin.getConfig().getString("business."+busname+".rusname"))));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lAbiz >> &eБизнес очищен!"));
                        return true;
                    } else {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cБизнеса не существует!"));
                        return true;
                    }
                } else if (args[0].equals("take")) {
                    Player p = (Player) sender;
                    if (plugin.utils.isInt(args[1])) {
                        int sum = Integer.parseInt(args[1]);
                        if (plugin.utils.getBusinessName(p) != null) {
                            String busname = plugin.utils.getBusinessName(p);
                            if (plugin.getConfig().getInt("business."+busname+".balance") < sum) {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lAbiz >> &eНедостаточно средств! Баланс: &a"+plugin.getConfig().getInt("business."+busname+".balance")));
                                return true;
                            } else {
                                int balance = plugin.getConfig().getInt("business."+busname+".balance");
                                plugin.getConfig().set("business."+busname+".balance", balance-sum);
                                plugin.saveConfig();
                                plugin.e.depositPlayer(p, sum);
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lAbiz >> &eВы успешно сняли деньги с баланса бизнеса! Баланс: &a" + (balance-sum)));
                                return true;
                            }
                        } else {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cУ вас нет своего бизнеса!"));
                            return true;
                        }
                    } else {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cВы ввели не число!"));
                        return true;
                    }
                } else if (args[0].equals("open")) {
                    Player p = (Player) sender;
                    String busname = args[1];
                    if (plugin.getConfig().contains("business."+args[1])) {
                        Inventory i = Bukkit.createInventory(null, 54, "Меню: " + busname);
                        for (String material : plugin.getConfig().getConfigurationSection("business."+busname+".materials").getKeys(false)) {
                            int seller = plugin.getConfig().getInt("business."+busname+".materials."+material+".summa");
                            i.addItem(plugin.utils.getItem(plugin.getConfig().getString("business."+busname+".materials." + material + ".name"), Arrays.asList("", ChatColor.GRAY + "Цена: " + seller), plugin.getConfig().getInt("business."+busname+".materials." + material + ".id"), plugin.getConfig().getInt("business."+busname+".materials." + material + ".durability")));
                        }
                        i.addItem(plugin.utils.getItem("&eВладелец: &a"+(plugin.getConfig().getString("business."+busname+".owner").equals("null") ? "&cНет" : plugin.getConfig().getString("business."+busname+".owner")), null, 160, 5));
                        p.openInventory(i);
                        return true;
                    } else {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cУ вас нет своего бизнеса!"));
                        return true;
                    }
                }
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lAbiz >> &eВы ввели команду не по форме! Список команд - /abiz info"));
                return true;
            } else if (args.length == 1) {
                if (args[0].equals("info")) {
                    List<String> info = plugin.getConfig().getStringList("info");
                    for (String s : info) sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
                    return true;
                } else if (args[0].equals("sellgos")) {
                    Player p = (Player) sender;
                    String busname = plugin.utils.getBusinessName(p);
                    if (busname == null) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cВы не владеете бизнесом!"));
                        return true;
                    } else {
                        int sum = plugin.getConfig().getInt("business."+busname+".sell");
                        plugin.getConfig().set("business."+busname+".owner", "null");
                        plugin.saveConfig();
                        plugin.e.depositPlayer(p, sum/2);
                        ArmorStand stand = (ArmorStand) Bukkit.getEntity(UUID.fromString(plugin.getConfig().getString("business."+busname+".hologramuuid")));
                        stand.setCustomName(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("hologram.buybusiness").replace("%sell%", plugin.getConfig().getString("business."+busname+".sell")).replace("%businessname%", plugin.getConfig().getString("business."+busname+".rusname"))));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lAbiz >> &eВы продали бизнес государству и получили 50% (%sum%)".replace("%sum%", String.valueOf((sum/2)))));
                        return true;
                    }
                } else if (args[0].equals("paytax")) {
                    Player p = (Player) sender;
                    if (plugin.utils.getBusinessName(p) != null) {
                        String busname = plugin.utils.getBusinessName(p);
                        if (plugin.e.getBalance(p) >= plugin.getConfig().getInt("business."+busname+".nalog")) {
                            plugin.e.withdrawPlayer(p, plugin.getConfig().getInt("business."+busname+".nalog"));
                            plugin.getConfig().set("business."+busname+".nalog", 0);
                            plugin.saveConfig();
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lAbiz >> &eВы успешно оплатили налог!"));
                            return true;
                        } else {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lAbiz >> &eНедостаточно средств! Налог: &a"+plugin.getConfig().getInt("business."+busname+".nalog")));
                            return true;
                        }
                    } else {
                        p.sendMessage(ChatColor.RED + "У вас нет бизнеса!");
                        return true;
                    }
                } else if (args[0].equals("mybiz")) {
                    Player p = (Player) sender;
                    if (plugin.utils.getBusinessName(p) != null) {
                        String busname = plugin.utils.getBusinessName(p);
                        Inventory i = Bukkit.createInventory(null, 9, "Информация о бизнесе");
                        i.setItem(3, plugin.utils.getItem("&eБаланс", Arrays.asList("", ChatColor.GRAY + String.valueOf(plugin.getConfig().getInt("business."+busname+".balance")) + "$"), Material.STAINED_CLAY.getId(), (byte) 4));
                        i.setItem(4, plugin.utils.getItem("&bНалог", Arrays.asList("", ChatColor.GRAY + String.valueOf(plugin.getConfig().getInt("business."+busname+".nalog")) + "/50000$"), Material.STAINED_CLAY.getId(), (byte) 3));
                        i.setItem(5, plugin.utils.getItem("&dПредметы", Arrays.asList("", ChatColor.GRAY + String.valueOf(plugin.getConfig().getConfigurationSection("business."+busname+".materials").getKeys(false).size()) + " штук"), Material.STAINED_CLAY.getId(), (byte) 6));
                        p.openInventory(i);
                        return true;
                    } else {
                        sender.sendMessage(ChatColor.RED + "Вы не владеете бизнесом!");
                        return true;
                    }
                }
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lAbiz >> &eВы ввели команду не по форме! Список команд - /abiz info"));
                return true;
            } else if (args.length == 3) {
                if (args[0].equals("create") && sender.hasPermission("business.admin")) {
                    Player p = (Player) sender;
                    String rusname = args[1];
                    String engname = plugin.utils.getEnglishName(rusname);
                    int sell = Integer.parseInt(args[2]);
                    ArmorStand stand = p.getWorld().spawn(p.getLocation(), ArmorStand.class);
                    stand.setCustomName(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("hologram.buybusiness").replace("%sell%", String.valueOf(sell)).replace("%businessname%", rusname)));
                    stand.setCustomNameVisible(true);
                    stand.setGravity(false);
                    stand.setVisible(false);
                    plugin.getConfig().set("business."+engname+".rusname", rusname);
                    plugin.getConfig().set("business."+engname+".sell", sell);
                    plugin.getConfig().set("business."+engname+".owner", "null");
                    plugin.getConfig().set("business."+engname+".balance", 0);
                    plugin.getConfig().set("business."+engname+".nalog", 0);
                    plugin.getConfig().set("business."+engname+".hologram.world", p.getWorld().getName());
                    plugin.getConfig().set("business."+engname+".hologram.x", p.getLocation().getX());
                    plugin.getConfig().set("business."+engname+".hologram.y", p.getLocation().getY());
                    plugin.getConfig().set("business."+engname+".hologram.z", p.getLocation().getZ());
                    plugin.getConfig().set("business."+engname+".hologramuuid", stand.getUniqueId().toString());
                    plugin.getConfig().createSection("business."+engname+".materials");
                    plugin.saveConfig();
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lAbiz >> &eБизнес успешно создан! Английское название - &7" + engname));
                    return true;
                } else if (args[0].equals("sell")) {
                    Player buyer = Bukkit.getPlayer(args[1]);
                    Player p = (Player) sender;
                    if (buyer != null) {
                        if (plugin.utils.isInt(args[2]) && plugin.utils.getBusinessName(p) != null) {
                            int sum = Integer.parseInt(args[2]);
                            if (plugin.e.getBalance(buyer) >= sum) {
                                TextComponent accept = new TextComponent(ChatColor.translateAlternateColorCodes('&', "&a&l[&aКУПИТЬ&a&l]"));
                                TextComponent deny = new TextComponent(ChatColor.translateAlternateColorCodes('&', "&c&l[&cОТКАЗАТЬСЯ&c&l]"));
                                accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bizsdelka accept"));
                                deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bizsdelka deny"));
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lAbiz >> &eВы успешно предложили игроку " + buyer.getName() + " купить Ваш бизнес."));
                                buyer.sendMessage("");
                                buyer.sendMessage("");
                                buyer.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lAbiz >> &eИгрок " + sender.getName() + " предлагает Вам купить его бизнес за " + sum + "$"));
                                buyer.sendMessage("");
                                buyer.spigot().sendMessage(accept);
                                buyer.sendMessage("");
                                buyer.spigot().sendMessage(deny);
                                buyer.sendMessage("");
                                buyer.sendMessage("");
                                HashMap<Player, Integer> map = new HashMap<>();
                                map.put(buyer, sum);
                                plugin.sdelka.put(p, map);
                                Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
                                    if (plugin.sdelka.containsKey(p)) plugin.sdelka.remove(p);
                                }, 20L*15);
                                return true;
                            } else {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cУ игрока недостаточно средств!"));
                                return true;
                            }
                        } else {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cВы ввели не число или бизнеса не существует!"));
                            return true;
                        }
                    } else {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cИгрок не в сети!"));
                        return true;
                    }
                }
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lAbiz >> &eВы ввели команду не по форме! Список команд - /abiz info"));
                return true;
            } else if (args.length >= 5) {
                if (args[0].equals("addmaterial") && sender.hasPermission("business.admin")) {
                    if (plugin.getConfig().contains("business."+args[1])) {
                        String busname = args[1];
                        if (args[2].contains(":")) {
                            int durability = Integer.parseInt(args[2].substring(args[2].indexOf(":")+1));
                            int id = Integer.parseInt(args[2].substring(0, args[2].indexOf(":")));
                            int slot = 1;
                            for (String s : plugin.getConfig().getConfigurationSection("business."+busname+".materials").getKeys(false)) {
                                slot = Integer.parseInt(s);
                            }
                            if (slot != 1) slot++;
                            StringJoiner sj = new StringJoiner(" ");
                            for (int i = 4; i < args.length; i++) {
                                sj.add(args[i]);
                            }
                            plugin.getConfig().set("business."+busname+".materials."+slot+".name", sj.toString());
                            plugin.getConfig().set("business."+busname+".materials."+slot+".id", id);
                            plugin.getConfig().set("business."+busname+".materials."+slot+".durability", durability);
                            plugin.getConfig().set("business."+busname+".materials."+slot+".summa", Integer.parseInt(args[3]));
                            plugin.saveConfig();
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lAbiz >> &eПредмет успешно добавлен!"));
                            return true;
                        } else {
                            int id = Integer.parseInt(args[2]);
                            int slot = 0;
                            for (String s : plugin.getConfig().getConfigurationSection("business."+busname+".materials").getKeys(false)) {
                                slot = Integer.parseInt(s);
                            }
                            slot++;
                            StringJoiner sj = new StringJoiner(" ");
                            for (int i = 4; i < args.length; i++) {
                                sj.add(args[i]);
                            }
                            plugin.getConfig().set("business."+busname+".materials."+slot+".name", sj.toString());
                            plugin.getConfig().set("business."+busname+".materials."+slot+".id", id);
                            plugin.getConfig().set("business."+busname+".materials."+slot+".durability", 0);
                            plugin.getConfig().set("business."+busname+".materials."+slot+".summa", Integer.parseInt(args[3]));
                            plugin.saveConfig();
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lAbiz >> &eПредмет успешно добавлен!"));
                            return true;
                        }
                    } else {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cБизнеса не существует!"));
                        return true;
                    }
                }
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lAbiz >> &eВы ввели команду не по форме! Список команд - /abiz info"));
                return true;
            }
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lAbiz >> &eВы ввели команду не по форме! Список команд - /abiz info"));
            return true;
        } else {
            sender.sendMessage(ChatColor.RED + "Только для игроков!");
            return true;
        }
    }
}