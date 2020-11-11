package be4rjp.sclat.server;

import be4rjp.sclat.Main;
import be4rjp.sclat.Sclat;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.manager.PlayerStatusMgr;
import be4rjp.sclat.manager.SettingMgr;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static be4rjp.sclat.Main.conf;

public class EquipmentServerManager {
    
    public static List<String> commands = new ArrayList<>();
    
    public static void addEquipmentCommand(String command){
        commands.add(command);
    }
    
    public static void doCommands(){
        for(String cmd : commands) {
            String args[] = cmd.split(" ");
    
            switch (args[0]) {
                case "set": { //add [statusName] [number or name] [uuid]
                    if (args.length == 4) {
                        if (args[3].length() == 36) {
                            switch (args[1]) {
                                case "weapon":
                                    for (Player player : Main.getPlugin().getServer().getOnlinePlayers())
                                        if (player.getUniqueId().toString().equals(args[3]))
                                            DataMgr.getPlayerData(player).setWeaponClass(DataMgr.getWeaponClass(args[2]));
                                    break;
                                case "gear":
                                    for (Player player : Main.getPlugin().getServer().getOnlinePlayers())
                                        if (player.getUniqueId().toString().equals(args[3]) && Sclat.isNumber(args[2]))
                                            DataMgr.getPlayerData(player).setGearNumber(Integer.parseInt(args[2]));
                                    break;
                                case "rank":
                                    PlayerStatusMgr.setRank(args[3], Integer.parseInt(args[2]));
                                    break;
                                case "lv":
                                    PlayerStatusMgr.setLv(args[3], Integer.parseInt(args[2]));
                                    break;
                            }
                        }
                    }
                    break;
                }
            }
        }
        commands.clear();
    }
    
}
