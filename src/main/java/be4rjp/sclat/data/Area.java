
package be4rjp.sclat.data;

import be4rjp.sclat.GlowingAPI;
import be4rjp.sclat.Main;
import be4rjp.sclat.MessageType;
import be4rjp.sclat.Sclat;
import be4rjp.sclat.manager.PaintMgr;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.v1_14_R1.EntityShulker;
import net.minecraft.server.v1_14_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_14_R1.PacketPlayOutSpawnEntityLiving;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftShulker;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Shulker;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Be4rJP
 */
public class Area {
    private final Location from;
    private final Location to;
    private Match match;
    private Team team = null;
    private org.bukkit.scoreboard.Team colorTeam0;
    private org.bukkit.scoreboard.Team colorTeam1;
    private BukkitRunnable task;
    private List<Shulker> slist = new ArrayList<>();
    private List<Block> blist = new ArrayList<>();
    
    
    public Area(Location from, Location to){this.from = from; this.to = to;}
    
    public void setupAreaTeam(){
        colorTeam0 = match.getTeam0().getTeam().getScoreboard().registerNewTeam("ColorTeam0" + Main.getNotDuplicateNumber());
        colorTeam0.setCanSeeFriendlyInvisibles(false);
        colorTeam0.setColor(match.getTeam0().getTeamColor().getChatColor());
    
        colorTeam1 = match.getTeam0().getTeam().getScoreboard().registerNewTeam("ColorTeam1" + Main.getNotDuplicateNumber());
        colorTeam1.setCanSeeFriendlyInvisibles(false);
        colorTeam1.setColor(match.getTeam1().getTeamColor().getChatColor());
    }
    
    public void setup(Match match){
        this.match = match;
        this.team = null;
        this.blist = new ArrayList<>();
        this.slist = new ArrayList<>();
        
        for(int x = this.from.getBlockX(); x <= this.to.getBlockX(); x++){
            for(int z = this.from.getBlockZ(); z <= this.to.getBlockZ(); z++){
                Location loc = new Location(this.from.getWorld(), x, this.from.getBlockY(), z);
                if(!loc.getBlock().getType().equals(Material.AIR))
                    this.blist.add(loc.getBlock());
                if(x == this.from.getBlockX() || x == this.to.getBlockX() || z == this.from.getBlockZ() || z == this.to.getBlockZ()){
                    Shulker sl = (Shulker)this.from.getWorld().spawnEntity(loc.clone().add(0, 0, 0), EntityType.SHULKER);
                    sl.setAI(false);
                    sl.setGravity(false);
                    sl.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 4000, 1, false, false));
                    sl.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 4000, 1, false, false));
                    //EntityShulker esl = ((CraftShulker)sl).getHandle();
                    //esl.setFlag(5, true);
                    this.slist.add(sl);
                    /*
                    BukkitRunnable task = new BukkitRunnable() {
                        @Override
                        public void run() {
                            for(Player oplayer : Main.getPlugin(Main.class).getServer().getOnlinePlayers()){
                                if(!DataMgr.getPlayerData(oplayer).getSettings().ShowAreaRegion()) {
                                    ((CraftPlayer) oplayer).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(sl.getEntityId()));
                                }
                            }
                        }
                    };
                    task.runTaskLater(Main.getPlugin(), 40);
                    
                     */
                }
                /*
                for(Shulker sl : this.slist){
                    Block b = sl.getLocation().getBlock().getRelative(BlockFace.UP);
                    if(b.getType().equals(Material.AIR) || b.getType().toString().contains("CARPET")){
                        match.getBlockUpdater().setBlock(b, Material.WHITE_CARPET);
                        DataMgr.rblist.add(b);
                    }
                    //sl.remove();
                }
                 */
            }
        }
        
        task = new BukkitRunnable(){
            @Override
            public void run(){
                //エリアの発光表示
                /*
                for(Shulker sl : slist) {
                    for (Player oplayer : Main.getPlugin(Main.class).getServer().getOnlinePlayers()) {
                        if (DataMgr.getPlayerData(oplayer).getSettings().ShowAreaRegion()) {
                            GlowingAPI.setGlowing(sl, oplayer, true);
                        }
                    }
                }*/
                
                //エリア処理
                int t0c = 0;
                int t1c = 0;
                for(Block block : blist){
                    if(DataMgr.getBlockDataMap().containsKey(block)){
                        if(match.getTeam0() == DataMgr.getBlockDataMap().get(block).getTeam())
                            t0c++;
                        else
                            t1c++;
                    }
                }
                if(team != null){
                    if(team == match.getTeam0()){
                        if((blist.size() * 0.5) < (double)t1c){
                            Sclat.sendMessage("§3§lカウントストップ!", MessageType.ALL_PLAYER);
                            for(Player oplayer : Main.getPlugin(Main.class).getServer().getOnlinePlayers()){
                                if(DataMgr.getPlayerData(oplayer).isInMatch()){
                                    oplayer.playSound(oplayer.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1F, 2F);
                                }
                            }
                            removeColor();
                            team = null;
                        }
                    }
                    if(team == match.getTeam1()){
                        if((blist.size() * 0.5) < (double)t0c){
                            Sclat.sendMessage("§3§lカウントストップ!", MessageType.ALL_PLAYER);
                            for(Player oplayer : Main.getPlugin(Main.class).getServer().getOnlinePlayers()){
                                if(DataMgr.getPlayerData(oplayer).isInMatch()){
                                    oplayer.playSound(oplayer.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1F, 2F);
                                }
                            }
                            removeColor();
                            team = null;
                        }
                    }
                }else{
                    if((blist.size() * 0.6) < (double)t0c){
                        team = match.getTeam0();
                        for(Player oplayer : Main.getPlugin(Main.class).getServer().getOnlinePlayers()){
                            if(DataMgr.getPlayerData(oplayer).isInMatch()){
                                if(team == DataMgr.getPlayerData(oplayer).getTeam()){
                                    Sclat.sendMessage("§fエリアを確保した!", MessageType.PLAYER, oplayer);
                                    oplayer.sendTitle("", "§fエリアを確保した!", 10, 20, 10);
                                    oplayer.playSound(oplayer.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1F, 3F);
                                }else{
                                    Sclat.sendMessage("§4エリアが確保された!", MessageType.PLAYER, oplayer);
                                    oplayer.sendTitle("", "§4エリアが確保された!", 10, 20, 10);
                                    oplayer.playSound(oplayer.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1F, 3F);
                                }
                            }
                        }
                        updateBlocks();
                    }else if((blist.size() * 0.6) < (double)t1c){
                        team = match.getTeam1();
                        for(Player oplayer : Main.getPlugin(Main.class).getServer().getOnlinePlayers()){
                            if(DataMgr.getPlayerData(oplayer).isInMatch()){
                                if(team == DataMgr.getPlayerData(oplayer).getTeam()){
                                    Sclat.sendMessage("§fエリアを確保した!", MessageType.PLAYER, oplayer);
                                    oplayer.sendTitle("", "§fエリアを確保した!", 10, 20, 10);
                                    oplayer.playSound(oplayer.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1F, 3F);
                                }else{
                                    Sclat.sendMessage("§4エリアが確保された!", MessageType.PLAYER, oplayer);
                                    oplayer.sendTitle("", "§4エリアが確保された!", 10, 20, 10);
                                    oplayer.playSound(oplayer.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1F, 3F);
                                }
                            }
                        }
                        updateBlocks();
                    }
                }
            }
        };
    }
    
    public void start(){this.task.runTaskTimer(Main.getPlugin(), 0, 20);}
    
    public void stop(){
        this.task.cancel();
        this.slist.forEach(sl -> sl.remove());
        this.slist.clear();
    }
    
    public List<Shulker> getShulkerBoxes(){return this.slist;}
    
    public Team getTeam(){return this.team;}
    
    private void updateBlocks(){
        for(Block block : this.blist){
            //Block b = block.getLocation().getWorld().getHighestBlockAt(block.getLocation());
            PaintMgr.PaintByTeam(block, team, match);
        }
        
        for(Shulker sl : this.slist){
            if(match.getTeam0() == team){
                colorTeam0.addEntry(sl.getUniqueId().toString());
            }else{
                colorTeam1.addEntry(sl.getUniqueId().toString());
            }
            //this.team.getTeam().addEntry(sl.getUniqueId().toString());
        }
    }
    
    public void removeColor(){
        for(Shulker sl : this.slist){
            if(match.getTeam0() == team){
                colorTeam0.removeEntry(sl.getUniqueId().toString());
            }else{
                colorTeam1.removeEntry(sl.getUniqueId().toString());
            }
            //this.team.getTeam().removeEntry(sl.getUniqueId().toString());
        }
    }
}
