package be4rjp.sclat.manager;

import be4rjp.sclat.Main;
import be4rjp.sclat.data.PlayerReturn;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class PlayerReturnManager {
    
    public static List<PlayerReturn> list = new ArrayList<>();
    
    public static boolean isReturned(String uuid){
        for(PlayerReturn pr : list){
            if(pr.getUUID().equals(uuid)){
                list.remove(pr);
                return true;
            }
        }
        return false;
    }
    
    public static void runRemoveTask(){
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    for (PlayerReturn pr : list)
                        if (!pr.getFlag())
                            list.remove(pr);
                }catch (Exception e){}
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 200);
    }
    
    public static void addPlayerReturn(String uuid){
        PlayerReturn pr = new PlayerReturn(uuid);
        list.add(pr);
    }
}
