package be4rjp.sclat.manager;

import be4rjp.sclat.Main;
import be4rjp.sclat.data.ServerStatus;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

import static be4rjp.sclat.Main.conf;

public class ServerStatusManager {
    public static Inventory inv = Bukkit.createInventory(null, 9, "Server List");
    
    public static List<ServerStatus> serverList = new ArrayList<>();
    
    public static BukkitRunnable task;
    
    public static void setupServerStatusGUI(){
        for (String server : conf.getServers().getConfigurationSection("Servers").getKeys(false)) {
            String serverName = conf.getServers().getString("Servers." + server + ".Server");
            String displayName = conf.getServers().getString("Servers." + server + ".DisplayName");
            int maxPlayer = conf.getServers().getInt("Servers." + server + ".MaxPlayer");
            String host = conf.getServers().getString("Servers." + server + ".Host");
            int port = conf.getServers().getInt("Servers." + server + ".Port");
            int period = conf.getServers().getInt("Servers." + server + ".Period");
            
            ServerStatus ss = new ServerStatus(serverName, displayName, host, port, maxPlayer, period);
            serverList.add(ss);
        }
        
        task = new BukkitRunnable() {
            @Override
            public void run() {
                inv.clear();
                int i = 0;
                for(ServerStatus ss : serverList){
                    Material mt = Material.LIME_STAINED_GLASS;
                    if(ss.getRunningMatch()) mt = Material.YELLOW_STAINED_GLASS;
                    if(ss.getPlayerCount() >= ss.getMaxPlayer()) mt = Material.RED_STAINED_GLASS;
                    if(!ss.isOnline() || ss.getRestartingServer()) mt = Material.IRON_BARS;
                    
                    ItemStack is = new ItemStack(mt);
                    ItemMeta itemMeta = is.getItemMeta();
                    itemMeta.setDisplayName(ss.getDisplayName());
                    List<String> role = new ArrayList<>();
                    if(ss.getRestartingServer()){
                        role.add("");
                        role.add("§r§7[Status]   §eRESTARTING...");
                    }else {
                        if (ss.isOnline()) {
                            role.add("");
                            role.add("§r§7[Players] §r§a" + ss.getPlayerCount() + "§r§7 / " + ss.getMaxPlayer());
                            role.add("");
                            role.add("§r§7[Status]   §aONLINE");
                            role.add("");
                            role.add("§r§7[Match]    " + (ss.getRunningMatch() ? "§cACTIVE" : "§aINACTIVE"));
                        } else {
                            role.add("");
                            role.add("§r§7[Status]   §cOFFLINE");
                        }
                    }
                    itemMeta.setLore(role);
                    is.setItemMeta(itemMeta);
                    
                    inv.setItem(i, is);
                    i++;
                }
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 40);
    }
    
    public static void openServerList(Player player){player.openInventory(inv);}
    
    public static void stopTask(){task.cancel();}
    
    
}
