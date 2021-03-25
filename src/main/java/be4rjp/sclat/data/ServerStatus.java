package be4rjp.sclat.data;

import be4rjp.sclat.Main;
import be4rjp.sclat.MineStat;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class ServerStatus {
    
    private final String serverName;
    private final String displayName;
    private final String host;
    private final int port;
    private final int period;
    private final BukkitRunnable task;
    private final int maxPlayer;
    private final BukkitRunnable task2;
    private final Block sign;
    private final String info;
    
    private int playerCount = 0;
    private boolean online = false;
    private boolean runningMatch = false;
    private boolean restartingServer = false;
    private String mapName = "";
    private boolean maintenance = false;
    private List<String> uuidList;
    private long waitingEndTime = 0;
    private long matchStartTime = 0;
    
    private MatchServerRunnable matchServerRunnable;
    
    public ServerStatus(String serverName, String displayName, String host, int port, int maxPlayer, int period, Block sign, String info){
        this.serverName = serverName;
        this.displayName = displayName;
        this.host = host;
        this.port = port;
        this.period = period;
        this.maxPlayer = maxPlayer;
        this.sign = sign;
        this.info = info;
        this.uuidList = new ArrayList<>();
        
        this.matchServerRunnable = new MatchServerRunnable(this);
        
        this.task = new BukkitRunnable() {
            @Override
            public void run() {
                if(maintenance){
                    online = false;
                }else {
                    try {
                        MineStat ms = new MineStat(host, port);
                        playerCount = Integer.parseInt(ms.getCurrentPlayers());
                        online = ms.isServerUp();
                        if (!online) {
                            runningMatch = false;
                        }
                    } catch (Exception e) {
                        online = false;
                        runningMatch = false;
                    }
                }
            }
        };
        task.runTaskTimerAsynchronously(Main.getPlugin(), 0, this.period);
        
        this.task2 = new BukkitRunnable() {
            @Override
            public void run() {
                try{
                    if(sign.getType().toString().contains("SIGN")){
                        Sign signState = (Sign) sign.getState();
                        signState.setLine(0, displayName);
                        if(online){
                            signState.setLine(1, "§a" + playerCount + " / " + maxPlayer);
                            if(runningMatch)
                                signState.setLine(2, "§cIN MATCH");
                            else
                                signState.setLine(2, "§aINACTIVE");
                            signState.setLine(3, "§b" + mapName);
                        }else{
                            signState.setLine(1, maintenance ? "§cMAINTENANCE" : "§cOFFLINE");
                            signState.setLine(2, "");
                            signState.setLine(3, "");
                        }
                        signState.update();
                    }
                }catch (Exception e){}
            }
        };
        task2.runTaskTimer(Main.getPlugin(), 5, this.period);
    }
    
    public int getPlayerCount(){return this.playerCount;}
    
    public int getMaxPlayer(){return this.maxPlayer;}
    
    public String getServerName(){return this.serverName;}
    
    public String getDisplayName(){return this.displayName;}
    
    public boolean getRunningMatch(){return this.runningMatch;}
    
    public boolean getRestartingServer(){return this.restartingServer;}
    
    public String getMapName(){return this.mapName;}
    
    public Block getSign(){return this.sign;}
    
    public String getInfo(){return this.info;}
    
    public List<String> getUUIDList() {return uuidList;}
    
    public long getMatchStartTime() {return matchStartTime;}
    
    public long getWaitingEndTime() {return waitingEndTime;}
    
    public boolean isMaintenance(){return this.maintenance;}
    
    public boolean isOnline(){return this.online;}
    
    public void setRunningMatch(boolean is){this.runningMatch = is;}
    
    public void setRestartingServer(boolean is){this.restartingServer = is;}
    
    public void setMapName(String name){this.mapName = name;}
    
    public void setMaintenance(boolean is){this.maintenance = is;}
    
    public void setMatchStartTime(long matchStartTime) {this.matchStartTime = matchStartTime;}
    
    public void setWaitingEndTime(long waitingEndTime) {this.waitingEndTime = waitingEndTime;}
    
    public void stopTask(){this.task.cancel();}
    
}
