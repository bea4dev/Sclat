package be4rjp.sclat.data;

public class TrapData(){
    
    private Location location;
    private Player player;
    private BukkitRunnable task;
    
    public TrapData(Location location, Player player){
        this.location = location;
        this.player = player;
        
        this.task = new BukkitRunnable(){
        
        };
    }
}