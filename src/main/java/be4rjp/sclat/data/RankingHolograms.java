package be4rjp.sclat.data;

import be4rjp.sclat.Main;
import be4rjp.sclat.manager.PlayerStatusMgr;
import be4rjp.sclat.manager.RankMgr;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_13_R2.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

import static be4rjp.sclat.Main.conf;

public class RankingHolograms {
    private final EntityArmorStand title;
    private final EntityArmorStand separator;
    private final EntityArmorStand you;
    
    private final List<EntityArmorStand> rankArmorStands;
    
    private final Player player;
    
    private final Location location;
    
    public RankingHolograms(Player player){
        this.player = player;
    
        String WorldName = conf.getConfig().getString("RankingHolograms.WorldName");
        World w = Bukkit.getWorld(WorldName);
        double ix = conf.getConfig().getDouble("RankingHolograms.X");
        double iy = conf.getConfig().getDouble("RankingHolograms.Y");
        double iz = conf.getConfig().getDouble("RankingHolograms.Z");
        location = new Location(w, ix + 0.5, iy, iz + 0.5);
    
        WorldServer nmsWorld = ((CraftWorld) location.getWorld()).getHandle();
        
        title = new EntityArmorStand(nmsWorld);
        title.setNoGravity(true);
        title.setPosition(location.getX(), location.getY() + 2.5, location.getZ());
        title.setBasePlate(false);
        title.setInvisible(true);
        title.setSmall(true);
        title.setCustomName(CraftChatMessage.fromStringOrNull("§a----------- §b§lTotal Ranking §r§a-----------"));
        title.setCustomNameVisible(true);
    
        separator = new EntityArmorStand(nmsWorld);
        separator.setNoGravity(true);
        separator.setPosition(location.getX(), location.getY() + 0.0, location.getZ());
        separator.setBasePlate(false);
        separator.setInvisible(true);
        separator.setSmall(true);
        separator.setCustomName(CraftChatMessage.fromStringOrNull("§a-------------------------------------"));
        separator.setCustomNameVisible(true);
    
        you = new EntityArmorStand(nmsWorld);
        you.setNoGravity(true);
        you.setPosition(location.getX(), location.getY() - 0.5, location.getZ());
        you.setBasePlate(false);
        you.setInvisible(true);
        you.setSmall(true);
        you.setCustomName(CraftChatMessage.fromStringOrNull("--"));
        you.setCustomNameVisible(true);
        
        rankArmorStands = new ArrayList<>();
        for(int i = 0; i < 5; i++){
            EntityArmorStand armorStand = new EntityArmorStand(nmsWorld);
            armorStand.setNoGravity(true);
            armorStand.setPosition(location.getX(), location.getY() + 2.0 - (0.4 * (double)i), location.getZ());
            armorStand.setBasePlate(false);
            armorStand.setInvisible(true);
            armorStand.setSmall(true);
            armorStand.setCustomName(CraftChatMessage.fromStringOrNull("--"));
            armorStand.setCustomNameVisible(true);
            rankArmorStands.add(armorStand);
        }
        
        refreshRankingAsync();
    }
    
    public void refreshRankingAsync(){
        BukkitRunnable async = new BukkitRunnable() {
            @Override
            public void run() {
                
                List<EntityArmorStand> list = new ArrayList<>();
                
                int i = 0;
                for(EntityArmorStand armorStand : rankArmorStands){
                    try {
                        list.add(armorStand);
                        
                        String uuid = RankMgr.ranking.get(i);
                        String mcid = conf.getUUIDCash().getString(uuid);
    
                        int rank = PlayerStatusMgr.getRank(uuid);
                        
                        if(rank != 0) {
                            armorStand.setCustomName(CraftChatMessage.fromStringOrNull
                                    ("§e" + String.valueOf(i + 1) + "位 §f" + mcid + "  §6Rank : §r" + rank + " [§b " + RankMgr.toABCRank(rank) + " §f]"));
                        }else{
                            armorStand.setCustomName(CraftChatMessage.fromStringOrNull("--"));
                        }
                    }catch (Exception e){}
                    i++;
                }
                
                try{
                    String mcid = player.getName();
                    int ranking = 1;
                    for(String uuid : RankMgr.ranking){
                        if(uuid.equals(player.getUniqueId().toString()))
                            break;
                        ranking++;
                    }
    
                    int rank = PlayerStatusMgr.getRank(player.getUniqueId().toString());
                    
                    you.setCustomName(CraftChatMessage.fromStringOrNull
                            ("§aYou ->> §e" + (rank == 0 ? "-" : ranking) +"位 §f" + mcid + "  §6Rank : §r" + rank + " [§b " + RankMgr.toABCRank(rank) + " §f]"));
    
                    list.add(you);
                }catch (Exception e){}
                
                list.add(title);
                list.add(separator);
                
                if(player.isOnline() && player.getWorld() == location.getWorld()) {
                    try {
                        for (EntityArmorStand armorStand : list) {
                            PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(armorStand.getBukkitEntity().getEntityId());
                            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(destroyPacket);
                        }
                    } catch (Exception e) {
                    }
                    try {
                        for (EntityArmorStand armorStand : list) {
                            PacketPlayOutSpawnEntityLiving spawnPacket = new PacketPlayOutSpawnEntityLiving(armorStand);
                            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(spawnPacket);
                        }
                    } catch (Exception e) {
                    }
                }
            }
        };
        async.runTaskAsynchronously(Main.getPlugin());
    }
}
