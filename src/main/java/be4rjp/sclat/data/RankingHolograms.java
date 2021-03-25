package be4rjp.sclat.data;

import be4rjp.sclat.Main;
import be4rjp.sclat.manager.PlayerStatusMgr;
import be4rjp.sclat.manager.RankMgr;
import net.minecraft.server.v1_14_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_14_R1.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

import static be4rjp.sclat.Main.conf;

public class RankingHolograms {
    private final EntityArmorStand title;
    private final EntityArmorStand separator;
    private final EntityArmorStand you;
    private final EntityArmorStand mode;
    
    private final EntityArmorStand clickHit1;
    private final EntityArmorStand clickHit2;
    private final EntityArmorStand clickHit3;
    private final EntityArmorStand clickHit4;
    
    private final List<EntityArmorStand> rankArmorStands;
    
    private final List<EntityArmorStand> armorStandList;
    
    private final Player player;
    
    private final Location location;
    
    private RankingType rankingType = RankingType.TOTAL;
    
    public RankingHolograms(Player player){
        this.player = player;
        
        armorStandList = new ArrayList<>();
    
        String WorldName = conf.getConfig().getString("RankingHolograms.WorldName");
        World w = Bukkit.getWorld(WorldName);
        double ix = conf.getConfig().getDouble("RankingHolograms.X");
        double iy = conf.getConfig().getDouble("RankingHolograms.Y");
        double iz = conf.getConfig().getDouble("RankingHolograms.Z");
        location = new Location(w, ix + 0.5, iy, iz + 0.5);
    
        WorldServer nmsWorld = ((CraftWorld) location.getWorld()).getHandle();
        
        title = new EntityArmorStand(nmsWorld, location.getX(), location.getY() + 2.5, location.getZ());
        title.setNoGravity(true);
        title.setPosition(location.getX(), location.getY() + 2.5, location.getZ());
        title.setBasePlate(false);
        title.setInvisible(true);
        title.setSmall(true);
        title.setCustomName(CraftChatMessage.fromStringOrNull("§a----------- §b§lTotal Ranking §r§a-----------"));
        title.setCustomNameVisible(true);
        armorStandList.add(title);
    
        separator = new EntityArmorStand(nmsWorld, location.getX(), location.getY() + 0.0, location.getZ());
        separator.setNoGravity(true);
        separator.setPosition(location.getX(), location.getY() + 0.0, location.getZ());
        separator.setBasePlate(false);
        separator.setInvisible(true);
        separator.setSmall(true);
        separator.setCustomName(CraftChatMessage.fromStringOrNull("§a-------------------------------------"));
        separator.setCustomNameVisible(true);
        armorStandList.add(separator);
    
        you = new EntityArmorStand(nmsWorld, location.getX(), location.getY() - 0.4, location.getZ());
        you.setNoGravity(true);
        you.setPosition(location.getX(), location.getY() - 0.4, location.getZ());
        you.setBasePlate(false);
        you.setInvisible(true);
        you.setSmall(true);
        you.setCustomName(CraftChatMessage.fromStringOrNull("--"));
        you.setCustomNameVisible(true);
        armorStandList.add(you);
    
        mode = new EntityArmorStand(nmsWorld, location.getX(), location.getY() - 0.8, location.getZ());
        mode.setNoGravity(true);
        mode.setPosition(location.getX(), location.getY() - 0.8, location.getZ());
        mode.setBasePlate(false);
        mode.setInvisible(true);
        mode.setSmall(true);
        mode.setCustomName(CraftChatMessage.fromStringOrNull("§a§l[Total] §7§l[Kill] [Paint]"));
        mode.setCustomNameVisible(true);
        armorStandList.add(mode);
    
        clickHit1 = new EntityArmorStand(nmsWorld, location.getX() + 0.3, location.getY() + 0.0, location.getZ() + 0.3);
        clickHit1.setNoGravity(true);
        clickHit1.setPosition(location.getX() + 0.3, location.getY() + 0.0, location.getZ() + 0.3);
        clickHit1.setBasePlate(false);
        clickHit1.setInvisible(true);
        clickHit1.setCustomName(CraftChatMessage.fromStringOrNull("clickHit1"));
        clickHit1.setCustomNameVisible(false);
        armorStandList.add(clickHit1);
    
        clickHit2 = new EntityArmorStand(nmsWorld, location.getX() + 0.3, location.getY() + 0.0, location.getZ() - 0.3);
        clickHit2.setNoGravity(true);
        clickHit2.setPosition(location.getX() + 0.3, location.getY() + 0.0, location.getZ() - 0.3);
        clickHit2.setBasePlate(false);
        clickHit2.setInvisible(true);
        clickHit2.setCustomName(CraftChatMessage.fromStringOrNull("clickHit2"));
        clickHit2.setCustomNameVisible(false);
        armorStandList.add(clickHit2);
    
        clickHit3 = new EntityArmorStand(nmsWorld, location.getX() - 0.3, location.getY() + 0.0, location.getZ() - 0.3);
        clickHit3.setNoGravity(true);
        clickHit3.setPosition(location.getX() - 0.3, location.getY() + 0.0, location.getZ() - 0.3);
        clickHit3.setBasePlate(false);
        clickHit3.setInvisible(true);
        clickHit3.setCustomName(CraftChatMessage.fromStringOrNull("clickHit3"));
        clickHit3.setCustomNameVisible(false);
        armorStandList.add(clickHit3);
    
        clickHit4 = new EntityArmorStand(nmsWorld, location.getX() - 0.3, location.getY() + 0.0, location.getZ() + 0.3);
        clickHit4.setNoGravity(true);
        clickHit4.setPosition(location.getX() - 0.3, location.getY() + 0.0, location.getZ() + 0.3);
        clickHit4.setBasePlate(false);
        clickHit4.setInvisible(true);
        clickHit4.setCustomName(CraftChatMessage.fromStringOrNull("clickHit4"));
        clickHit4.setCustomNameVisible(false);
        armorStandList.add(clickHit4);
        
        
        rankArmorStands = new ArrayList<>();
        for(int i = 0; i < 5; i++){
            EntityArmorStand armorStand = new EntityArmorStand(nmsWorld, location.getX(), location.getY() + 2.0 - (0.4 * (double)i), location.getZ());
            armorStand.setNoGravity(true);
            armorStand.setPosition(location.getX(), location.getY() + 2.0 - (0.4 * (double)i), location.getZ());
            armorStand.setBasePlate(false);
            armorStand.setInvisible(true);
            armorStand.setSmall(true);
            armorStand.setCustomName(CraftChatMessage.fromStringOrNull("--"));
            armorStand.setCustomNameVisible(true);
            rankArmorStands.add(armorStand);
            armorStandList.add(armorStand);
        }
        
        refreshRankingAsync();
    }
    
    public RankingType getRankingType(){return this.rankingType;}
    
    public EntityArmorStand getClickHit1(){return this.clickHit1;}
    
    public void setRankingType(RankingType type){this.rankingType = type;}
    
    public List<EntityArmorStand> getArmorStandList(){return this.armorStandList;}
    
    public void switchNextRankingType(){
        switch (rankingType){
            case TOTAL:
                rankingType = RankingType.KILL;
                title.setCustomName(CraftChatMessage.fromStringOrNull("§a----------- §b§lKill Ranking §r§a-----------"));
                separator.setCustomName(CraftChatMessage.fromStringOrNull("§a------------------------------------"));
                mode.setCustomName(CraftChatMessage.fromStringOrNull("§7§l[Total] §a§l[Kill] §7§l[Paint]"));
                break;
            case KILL:
                rankingType = RankingType.PAINT;
                title.setCustomName(CraftChatMessage.fromStringOrNull("§a----------- §b§lPaint Ranking §r§a-----------"));
                separator.setCustomName(CraftChatMessage.fromStringOrNull("§a-------------------------------------"));
                mode.setCustomName(CraftChatMessage.fromStringOrNull("§7§l[Total] [Kill] §a§l[Paint]"));
                break;
            case PAINT:
                rankingType = RankingType.TOTAL;
                title.setCustomName(CraftChatMessage.fromStringOrNull("§a----------- §b§lTotal Ranking §r§a-----------"));
                separator.setCustomName(CraftChatMessage.fromStringOrNull("§a-------------------------------------"));
                mode.setCustomName(CraftChatMessage.fromStringOrNull("§a§l[Total] §7§l[Kill] [Paint]"));
                break;
        }
    }
    
    public void refreshRankingAsync(){
        BukkitRunnable async = new BukkitRunnable() {
            @Override
            public void run() {
                
                List<EntityArmorStand> list = new ArrayList<>();
                list.add(clickHit1);
                list.add(clickHit2);
                list.add(clickHit3);
                list.add(clickHit4);
                
                int i = 0;
                for(EntityArmorStand armorStand : rankArmorStands){
                    try {
                        list.add(armorStand);
                        
                        if(rankingType == RankingType.TOTAL) {
                            String uuid = RankMgr.ranking.get(i);
                            String mcid = conf.getUUIDCash().getString(uuid);
    
                            int rank = PlayerStatusMgr.getRank(uuid);
    
                            if (rank != 0) {
                                armorStand.setCustomName(CraftChatMessage.fromStringOrNull
                                        ("§e" + String.valueOf(i + 1) + "位 §f" + mcid + "  §6Rank : §r" + rank + " [§b " + RankMgr.toABCRank(rank) + " §f]"));
                            } else {
                                armorStand.setCustomName(CraftChatMessage.fromStringOrNull("--"));
                            }
                        }
    
                        if(rankingType == RankingType.KILL) {
                            String uuid = RankMgr.killRanking.get(i);
                            String mcid = conf.getUUIDCash().getString(uuid);
        
                            int kill = PlayerStatusMgr.getKill(uuid);
        
                            if (kill != 0) {
                                armorStand.setCustomName(CraftChatMessage.fromStringOrNull
                                        ("§e" + String.valueOf(i + 1) + "位 §f" + mcid + "  §6Kill(s) : §r" + kill));
                            } else {
                                armorStand.setCustomName(CraftChatMessage.fromStringOrNull("--"));
                            }
                        }
    
                        if(rankingType == RankingType.PAINT) {
                            String uuid = RankMgr.paintRanking.get(i);
                            String mcid = conf.getUUIDCash().getString(uuid);
        
                            int paint = PlayerStatusMgr.getPaint(uuid);
        
                            if (paint != 0) {
                                armorStand.setCustomName(CraftChatMessage.fromStringOrNull
                                        ("§e" + String.valueOf(i + 1) + "位 §f" + mcid + "  §6Paint(s) : §r" + paint));
                            } else {
                                armorStand.setCustomName(CraftChatMessage.fromStringOrNull("--"));
                            }
                        }
                        
                    }catch (Exception e){}
                    i++;
                }
                
                try{
                    if(rankingType == RankingType.TOTAL) {
                        String mcid = player.getName();
                        int ranking = 1;
                        for (String uuid : RankMgr.ranking) {
                            if (uuid.equals(player.getUniqueId().toString()))
                                break;
                            ranking++;
                        }
    
                        int rank = PlayerStatusMgr.getRank(player.getUniqueId().toString());
    
                        you.setCustomName(CraftChatMessage.fromStringOrNull
                                ("§aYou ->> §e" + (rank == 0 ? "-" : ranking) + "位 §f" + mcid + "  §6Rank : §r" + rank + " [§b " + RankMgr.toABCRank(rank) + " §f]"));
                    }
    
                    if(rankingType == RankingType.KILL) {
                        String mcid = player.getName();
                        int ranking = 1;
                        for (String uuid : RankMgr.killRanking) {
                            if (uuid.equals(player.getUniqueId().toString()))
                                break;
                            ranking++;
                        }
        
                        int kill = PlayerStatusMgr.getKill(player.getUniqueId().toString());
        
                        you.setCustomName(CraftChatMessage.fromStringOrNull
                                ("§aYou ->> §e" + (kill == 0 ? "-" : ranking) + "位 §f" + mcid + "  §6Kill(s) : §r" + kill));
                    }
    
                    if(rankingType == RankingType.PAINT) {
                        String mcid = player.getName();
                        int ranking = 1;
                        for (String uuid : RankMgr.paintRanking) {
                            if (uuid.equals(player.getUniqueId().toString()))
                                break;
                            ranking++;
                        }
        
                        int paint = PlayerStatusMgr.getPaint(player.getUniqueId().toString());
        
                        you.setCustomName(CraftChatMessage.fromStringOrNull
                                ("§aYou ->> §e" + (paint == 0 ? "-" : ranking) + "位 §f" + mcid + "  §6Paint(s) : §r" + paint));
                    }
                    list.add(you);
                }catch (Exception e){}
                
                list.add(title);
                list.add(separator);
                list.add(mode);
                
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
