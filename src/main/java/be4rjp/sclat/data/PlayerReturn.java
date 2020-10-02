package be4rjp.sclat.data;

import be4rjp.sclat.Main;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerReturn {
    
    private final String uuid;
    private final BukkitRunnable task;
    private boolean flag = true;
    
    public PlayerReturn(String uuid){
        this.uuid = uuid;
        
        this.task = new BukkitRunnable(){
            @Override
            public void run(){
                flag = false;
            }
        };
        this.task.runTaskLater(Main.getPlugin(), 400);
    }
    
    public boolean getFlag(){return this.flag;}
    
    public String getUUID(){return this.uuid;}
}
