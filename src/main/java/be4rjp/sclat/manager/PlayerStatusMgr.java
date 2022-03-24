
package be4rjp.sclat.manager;

import static be4rjp.sclat.Main.conf;

import be4rjp.sclat.Main;
import be4rjp.sclat.ServerType;
import be4rjp.sclat.data.Color;
import be4rjp.sclat.data.DataMgr;
import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.server.v1_14_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_14_R1.CraftServer;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_14_R1.util.CraftChatMessage;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Be4rJP
 */
public class PlayerStatusMgr {
    
    public static Map<Player, EntityArmorStand> list = new HashMap<>();
    public static Map<Player, EntityArmorStand> list1 = new HashMap<>();
    public static Map<Player, EntityArmorStand> list2 = new HashMap<>();
    
    public static void setupPlayerStatus(Player player){
        if(!conf.getPlayerStatus().contains("Status." + player.getUniqueId().toString())){
            setDefaultStatus(player);
        }else if(!conf.getPlayerStatus().contains("Status." + player.getUniqueId().toString() + ".Money")){
            setDefaultStatus(player);
        }
    }
    
    public static void setDefaultStatus(Player player){
        conf.getPlayerStatus().set("Status." + player.getUniqueId().toString() + ".Money", 10000);
        conf.getPlayerStatus().set("Status." + player.getUniqueId().toString() + ".Lv", 0);
        conf.getPlayerStatus().set("Status." + player.getUniqueId().toString() + ".Rank", 0);
        List<String> wlist = new ArrayList<String>();
        wlist.add(conf.getConfig().getString("DefaultClass"));
        conf.getPlayerStatus().set("Status." + player.getUniqueId().toString() + ".WeaponClass", wlist);
        List<Integer> glist = new ArrayList<Integer>();
        glist.add(0);
        conf.getPlayerStatus().set("Status." + player.getUniqueId().toString() + ".GearList", glist);
        conf.getPlayerStatus().set("Status." + player.getUniqueId().toString() + ".Gear", 0);
        conf.getPlayerStatus().set("Status." + player.getUniqueId().toString() + ".Kill", 0);
        conf.getPlayerStatus().set("Status." + player.getUniqueId().toString() + ".Paint", 0);
        conf.getPlayerStatus().set("Status." + player.getUniqueId().toString() + ".EquiptClass", conf.getConfig().getString("DefaultClass"));
        conf.getPlayerStatus().set("Status." + player.getUniqueId().toString() + ".Tutorial", 0);
    }
    
    public static void sendHologram(Player player){
        World w = Bukkit.getWorld(conf.getConfig().getString("Hologram.WorldName"));
        int ix = conf.getConfig().getInt("Hologram.X");
        int iy = conf.getConfig().getInt("Hologram.Y");
        int iz = conf.getConfig().getInt("Hologram.Z");
        int iyaw = conf.getConfig().getInt("Hologram.Yaw");
        Location location = new Location(w, ix + 0.5D, iy, iz + 0.5D);
        location.setYaw(iyaw);
        
        
        MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer nmsWorld = ((CraftWorld) location.getWorld()).getHandle();
        GameProfile gameProfile = new GameProfile(player.getUniqueId(), player.getName());

        EntityPlayer npc = new EntityPlayer(nmsServer, nmsWorld, gameProfile, new PlayerInteractManager(nmsWorld));
        npc.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), 0);
        npc.getDataWatcher().set(DataWatcherRegistry.a.a(15), (byte)127);
        
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc));
        connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
        connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte) ((location.getYaw() * 256.0F) / 360.0F)));
        connection.sendPacket(new PacketPlayOutAnimation(npc, 0));
        connection.sendPacket(new PacketPlayOutEntityMetadata(npc.getId(), npc.getDataWatcher(), true));
        
        EntityArmorStand as = new EntityArmorStand(nmsWorld, location.getX(), location.getY() + 0.8D, location.getZ());
        as.setLocation(location.getX(), location.getY() + 0.8D, location.getZ(), location.getYaw(), 0);
        as.setInvisible(true);
        as.setCustomNameVisible(true);
        as.setNoGravity(true);
        as.setCustomName(CraftChatMessage.fromStringOrNull("§aMoney : §r" + String.valueOf(getMoney(player)) + "  §aLv : §r" + String.valueOf(getLv(player))));
        
        list.put(player, as);
        
        EntityArmorStand as1 = new EntityArmorStand(nmsWorld, location.getX(), location.getY() + 1.2D, location.getZ());
        as1.setLocation(location.getX(), location.getY() + 1.2D, location.getZ(), location.getYaw(), 0);
        as1.setInvisible(true);
        as1.setCustomNameVisible(true);
        as1.setNoGravity(true);
        as1.setCustomName(CraftChatMessage.fromStringOrNull("§6Rank : §r" + String.valueOf(getRank(player)) + "  [ §b" + RankMgr.toABCRank(getRank(player)) + " §r]"));
        
        list1.put(player, as1);
    
        EntityArmorStand as2 = new EntityArmorStand(nmsWorld, location.getX(), location.getY() + 0.4D, location.getZ());
        as2.setLocation(location.getX(), location.getY() + 0.4D, location.getZ(), location.getYaw(), 0);
        as2.setInvisible(true);
        as2.setCustomNameVisible(true);
        as2.setNoGravity(true);
        as2.setCustomName(CraftChatMessage.fromStringOrNull("§aPaints : §r" + String.valueOf(getPaint(player)) + "  §aKills : §r" + String.valueOf(getKill(player))));
        
        list2.put(player, as2);
        
        connection.sendPacket(new PacketPlayOutSpawnEntityLiving(as));
        connection.sendPacket(new PacketPlayOutSpawnEntityLiving(as1));
        connection.sendPacket(new PacketPlayOutSpawnEntityLiving(as2));
    }
    
    public static void HologramUpdateRunnable(Player player){
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                if(!player.isOnline())
                    cancel();
                try {
                    EntityArmorStand as = list.get(player);
                    PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
                    connection.sendPacket(new PacketPlayOutEntityDestroy(as.getBukkitEntity().getEntityId()));
                    as.setCustomName(CraftChatMessage.fromStringOrNull("§aMoney : §r" + String.valueOf(getMoney(player)) + "  §aLv : §r" + String.valueOf(getLv(player))));
                    connection.sendPacket(new PacketPlayOutSpawnEntityLiving(as));
    
                    EntityArmorStand as1 = list1.get(player);
                    connection.sendPacket(new PacketPlayOutEntityDestroy(as1.getBukkitEntity().getEntityId()));
                    as1.setCustomName(CraftChatMessage.fromStringOrNull("§6Rank : §r" + String.valueOf(getRank(player)) + "  [ §b" + RankMgr.toABCRank(getRank(player)) + " §r]"));
                    connection.sendPacket(new PacketPlayOutSpawnEntityLiving(as1));
    
                    EntityArmorStand as2 = list2.get(player);
                    connection.sendPacket(new PacketPlayOutEntityDestroy(as2.getBukkitEntity().getEntityId()));
                    as2.setCustomName(CraftChatMessage.fromStringOrNull("§aPaints : §r" + String.valueOf(getPaint(player)) + "  §aKills : §r" + String.valueOf(getKill(player))));
                    connection.sendPacket(new PacketPlayOutSpawnEntityLiving(as2));
                }catch (Exception e){}
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, conf.getConfig().getInt("HologramUpdatePeriod"));
    }
    
    public static void sendHologramUpdate(Player player){
        EntityArmorStand as = list.get(player);
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutEntityDestroy(as.getBukkitEntity().getEntityId()));
        as.setCustomName(CraftChatMessage.fromStringOrNull("§aMoney : §r" + String.valueOf(getMoney(player)) + "  §aLv : §r" + String.valueOf(getLv(player))));
        connection.sendPacket(new PacketPlayOutSpawnEntityLiving(as));
    }
    
    public static boolean haveWeapon(Player player, String wname){
        List<String> wlist = conf.getPlayerStatus().getStringList("Status." + player.getUniqueId().toString() + ".WeaponClass");
        return wlist.contains(wname);
    }
    
    public static boolean haveGear(Player player, int g){
        List<Integer> glist = conf.getPlayerStatus().getIntegerList("Status." + player.getUniqueId().toString() + ".GearList");
        return glist.contains(g);
    }
    
    public static void setRank(Player player, int rank){
        String uuid = player.getUniqueId().toString();
        conf.getPlayerStatus().set("Status." + uuid + ".Rank", rank);
    }
    
    public static void setRank(String uuid, int rank){
        conf.getPlayerStatus().set("Status." + uuid + ".Rank", rank);
    }
    
    public static void setLv(String uuid, int lv){
        conf.getPlayerStatus().set("Status." + uuid + ".Lv", lv);
    }
    
    public static void setGear(Player player, int g){
        String uuid = player.getUniqueId().toString();
        conf.getPlayerStatus().set("Status." + uuid + ".Gear", g);
    }
    
    public static void setEquiptClass(Player player, String name){
        String uuid = player.getUniqueId().toString();
        conf.getPlayerStatus().set("Status." + uuid + ".EquiptClass", name);
    }
    
    public static void setTutorialState(String uuid, int g){
        conf.getPlayerStatus().set("Status." + uuid + ".Tutorial", g);
    }
    
    public static void addWeapon(Player player, String wname){
        List<String> wlist = conf.getPlayerStatus().getStringList("Status." + player.getUniqueId().toString() + ".WeaponClass");
        wlist.add(wname);
        conf.getPlayerStatus().set("Status." + player.getUniqueId().toString() + ".WeaponClass", wlist);
    }
    
    public static void addGear(Player player, int g){
        List<Integer> glist = conf.getPlayerStatus().getIntegerList("Status." + player.getUniqueId().toString() + ".GearList");
        glist.add(g);
        conf.getPlayerStatus().set("Status." + player.getUniqueId().toString() + ".GearList", glist);
    }
    
    public static void addMoney(Player player, int m){
        String uuid = player.getUniqueId().toString();
        conf.getPlayerStatus().set("Status." + uuid + ".Money", conf.getPlayerStatus().getInt("Status." + uuid + ".Money") + m);
    }

    public static void addMoney(String uuid, int m){
        conf.getPlayerStatus().set("Status." + uuid + ".Money", conf.getPlayerStatus().getInt("Status." + uuid + ".Money") + m);
    }
    
    public static void subMoney(Player player, int m){
        String uuid = player.getUniqueId().toString();
        conf.getPlayerStatus().set("Status." + uuid + ".Money", conf.getPlayerStatus().getInt("Status." + uuid + ".Money") - m);
    }
    
    public static void addLv(Player player, int m){
        String uuid = player.getUniqueId().toString();
        conf.getPlayerStatus().set("Status." + uuid + ".Lv", conf.getPlayerStatus().getInt("Status." + uuid + ".Lv") + m);
    }
    
    public static void addRank(Player player, int m){
        String uuid = player.getUniqueId().toString();
        if(PlayerStatusMgr.getRank(player) + m > 0) {
            conf.getPlayerStatus().set("Status." + uuid + ".Rank", conf.getPlayerStatus().getInt("Status." + uuid + ".Rank") + m);
        }else {
            conf.getPlayerStatus().set("Status." + uuid + ".Rank", 0);
        }
    }
    
    public static void addKill(Player player, int m){
        String uuid = player.getUniqueId().toString();
        conf.getPlayerStatus().set("Status." + uuid + ".Kill", conf.getPlayerStatus().getInt("Status." + uuid + ".Kill") + m);
    }
    
    public static void addPaint(Player player, int m){
        String uuid = player.getUniqueId().toString();
        conf.getPlayerStatus().set("Status." + uuid + ".Paint", conf.getPlayerStatus().getInt("Status." + uuid + ".Paint") + m);
    }

    public static void addLv(String uuid, int m){
        conf.getPlayerStatus().set("Status." + uuid + ".Lv", conf.getPlayerStatus().getInt("Status." + uuid + ".Lv") + m);
    }

    public static void addRank(String uuid, int m){
        if(PlayerStatusMgr.getRank(uuid) + m > 0) {
            conf.getPlayerStatus().set("Status." + uuid + ".Rank", conf.getPlayerStatus().getInt("Status." + uuid + ".Rank") + m);
        }else {
            conf.getPlayerStatus().set("Status." + uuid + ".Rank", 0);
        }
    }

    public static void addKill(String uuid, int m){
        conf.getPlayerStatus().set("Status." + uuid + ".Kill", conf.getPlayerStatus().getInt("Status." + uuid + ".Kill") + m);
    }

    public static void addPaint(String uuid, int m){
        conf.getPlayerStatus().set("Status." + uuid + ".Paint", conf.getPlayerStatus().getInt("Status." + uuid + ".Paint") + m);
    }
    
    public static int getMoney(Player player){
        String uuid = player.getUniqueId().toString();
        return conf.getPlayerStatus().getInt("Status." + uuid + ".Money");
    }
    
    public static int getLv(Player player){
        String uuid = player.getUniqueId().toString();
        return conf.getPlayerStatus().getInt("Status." + uuid + ".Lv");
    }
    
    public static int getLv(String uuid){
        return conf.getPlayerStatus().getInt("Status." + uuid + ".Lv");
    }
    
    public static int getRank(Player player){
        String uuid = player.getUniqueId().toString();
        return conf.getPlayerStatus().getInt("Status." + uuid + ".Rank");
    }
    
    public static int getRank(String uuid){
        return conf.getPlayerStatus().getInt("Status." + uuid + ".Rank");
    }
    
    public static int getGear(Player player){
        String uuid = player.getUniqueId().toString();
        return conf.getPlayerStatus().getInt("Status." + uuid + ".Gear");
    }
    
    public static int getKill(Player player){
        String uuid = player.getUniqueId().toString();
        return conf.getPlayerStatus().getInt("Status." + uuid + ".Kill");
    }
    
    public static int getKill(String uuid){
        return conf.getPlayerStatus().getInt("Status." + uuid + ".Kill");
    }
    
    public static int getPaint(Player player){
        String uuid = player.getUniqueId().toString();
        return conf.getPlayerStatus().getInt("Status." + uuid + ".Paint");
    }
    
    public static int getPaint(String uuid){
        return conf.getPlayerStatus().getInt("Status." + uuid + ".Paint");
    }
    
    public static String getEquiptClass(Player player){
        String uuid = player.getUniqueId().toString();
        return conf.getPlayerStatus().getString("Status." + uuid + ".EquiptClass");
    }
    
    public static int getTutorialState(String uuid){
        return conf.getPlayerStatus().getInt("Status." + uuid + ".Tutorial");
    }
}
