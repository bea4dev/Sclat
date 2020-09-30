package be4rjp.sclat.data;

import be4rjp.sclat.Main;
import be4rjp.sclat.MineStat;
import org.bukkit.scheduler.BukkitRunnable;

public class ServerStatus {
    
    private final String serverName;
    private final String displayName;
    private final String host;
    private final int port;
    private final int period;
    private final BukkitRunnable task;
    private final int maxPlayer;
    
    private int playerCount = 0;
    private boolean online = false;
    
    public ServerStatus(String serverName, String displayName, String host, int port, int maxPlayer, int period){
        this.serverName = serverName;
        this.displayName = displayName;
        this.host = host;
        this.port = port;
        this.period = period;
        this.maxPlayer = maxPlayer;
        
        this.task = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    MineStat ms = new MineStat(host, port);
                    playerCount = Integer.parseInt(ms.getCurrentPlayers());
                    online = ms.isServerUp();
                }catch (Exception e){
                    online = false;
                }
            }
        };
        task.runTaskTimerAsynchronously(Main.getPlugin(), 0, this.period);
    }
    
    public int getPlayerCount(){return this.playerCount;}
    
    public int getMaxPlayer(){return this.maxPlayer;}
    
    public String getServerName(){return this.serverName;}
    
    public String getDisplayName(){return this.displayName;}
    
    public boolean isOnline(){return this.online;}
    
    public void stopTask(){this.task.cancel();}
    
}
