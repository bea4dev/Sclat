
package be4rjp.sclat.manager;

import static be4rjp.sclat.Main.conf;
import be4rjp.sclat.data.Color;
import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.server.v1_13_R2.EntityArmorStand;
import net.minecraft.server.v1_13_R2.EntityPlayer;
import net.minecraft.server.v1_13_R2.MinecraftServer;
import net.minecraft.server.v1_13_R2.PacketPlayOutAnimation;
import net.minecraft.server.v1_13_R2.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_13_R2.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_13_R2.PlayerConnection;
import net.minecraft.server.v1_13_R2.PlayerInteractManager;
import net.minecraft.server.v1_13_R2.WorldServer;
import net.minecraft.server.v1_13_R2.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_13_R2.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_13_R2.PacketPlayOutEntityHeadRotation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_13_R2.CraftServer;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_13_R2.util.CraftChatMessage;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

/**
 *
 * @author Be4rJP
 */
public class PlayerStatusMgr {
    
    public static Map<Player, EntityArmorStand> list = new HashMap<>();
    
    public static void setupPlayerStatus(Player player){
        if(!conf.getPlayerStatus().contains("Status." + player.getUniqueId().toString())){
            conf.getPlayerStatus().set("Status." + player.getUniqueId().toString() + ".Money", 0);
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
        }
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
        
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc));
        connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
        connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte) ((location.getYaw() * 256.0F) / 360.0F)));
        connection.sendPacket(new PacketPlayOutAnimation(npc, 0));
        
        EntityArmorStand as = new EntityArmorStand(nmsWorld);
        as.setLocation(location.getX(), location.getY() + 0.8D, location.getZ(), location.getYaw(), 0);
        as.setInvisible(true);
        as.setCustomNameVisible(true);
        as.setNoGravity(true);
        as.setCustomName(CraftChatMessage.fromStringOrNull("§aMoney : §r" + String.valueOf(getMoney(player)) + "  §aLv : §r" + String.valueOf(getLv(player))));
        
        list.put(player, as);
        
        EntityArmorStand as1 = new EntityArmorStand(nmsWorld);
        as1.setLocation(location.getX(), location.getY() + 1.2D, location.getZ(), location.getYaw(), 0);
        as1.setInvisible(true);
        as1.setCustomNameVisible(true);
        as1.setNoGravity(true);
        as1.setCustomName(CraftChatMessage.fromStringOrNull("§6Rank : §r" + String.valueOf(getRank(player)) + "  [ §b" + RankMgr.toABCRank(getRank(player)) + " §r]"));
    
        EntityArmorStand as2 = new EntityArmorStand(nmsWorld);
        as2.setLocation(location.getX(), location.getY() + 0.4D, location.getZ(), location.getYaw(), 0);
        as2.setInvisible(true);
        as2.setCustomNameVisible(true);
        as2.setNoGravity(true);
        as2.setCustomName(CraftChatMessage.fromStringOrNull("§aPaints : §r" + String.valueOf(getPaint(player)) + "  §aKills : §r" + String.valueOf(getKill(player))));
        
        connection.sendPacket(new PacketPlayOutSpawnEntityLiving(as));
        connection.sendPacket(new PacketPlayOutSpawnEntityLiving(as1));
        connection.sendPacket(new PacketPlayOutSpawnEntityLiving(as2));
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
    
    public static void setGear(Player player, int g){
        String uuid = player.getUniqueId().toString();
        conf.getPlayerStatus().set("Status." + uuid + ".Gear", g);
    }
    
    public static void setEquiptClass(Player player, String name){
        String uuid = player.getUniqueId().toString();
        conf.getPlayerStatus().set("Status." + uuid + ".EquiptClass", name);
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
    
    public static int getPaint(Player player){
        String uuid = player.getUniqueId().toString();
        return conf.getPlayerStatus().getInt("Status." + uuid + ".Paint");
    }
    
    public static String getEquiptClass(Player player){
        String uuid = player.getUniqueId().toString();
        return conf.getPlayerStatus().getString("Status." + uuid + ".EquiptClass");
    }
}
