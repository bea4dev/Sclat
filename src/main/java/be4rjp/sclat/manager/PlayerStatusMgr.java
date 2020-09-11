
package be4rjp.sclat.manager;

import static be4rjp.sclat.Main.conf;
import be4rjp.sclat.data.Color;
import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.server.v1_13_R1.EntityArmorStand;
import net.minecraft.server.v1_13_R1.EntityPlayer;
import net.minecraft.server.v1_13_R1.MinecraftServer;
import net.minecraft.server.v1_13_R1.PacketPlayOutAnimation;
import net.minecraft.server.v1_13_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_13_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_13_R1.PlayerConnection;
import net.minecraft.server.v1_13_R1.PlayerInteractManager;
import net.minecraft.server.v1_13_R1.WorldServer;
import net.minecraft.server.v1_13_R1.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_13_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_13_R1.PacketPlayOutEntityHeadRotation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_13_R1.CraftServer;
import org.bukkit.craftbukkit.v1_13_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_13_R1.util.CraftChatMessage;
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
            conf.getPlayerStatus().set("Status." + player.getUniqueId().toString() + ".Money", 10000);
            conf.getPlayerStatus().set("Status." + player.getUniqueId().toString() + ".Lv", 0);
            conf.getPlayerStatus().set("Status." + player.getUniqueId().toString() + ".Rank", 0);
            List<String> wlist = new ArrayList<String>();
            wlist.add(conf.getConfig().getString("DefaultClass"));
            conf.getPlayerStatus().set("Status." + player.getUniqueId().toString() + ".WeaponClass", wlist);
            conf.getPlayerStatus().set("Status." + player.getUniqueId().toString() + ".Gear", 0);
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
        as.setLocation(location.getX(), location.getY() + 0.4D, location.getZ(), location.getYaw(), 0);
        as.setInvisible(true);
        as.setCustomNameVisible(true);
        as.setNoGravity(true);
        as.setCustomName(CraftChatMessage.fromStringOrNull("§aMoney : §r" + String.valueOf(getMoney(player)) + "  §aLv : §r" + String.valueOf(getLv(player))));
        
        list.put(player, as);
        
        EntityArmorStand as1 = new EntityArmorStand(nmsWorld);
        as1.setLocation(location.getX(), location.getY() + 0.8D, location.getZ(), location.getYaw(), 0);
        as1.setInvisible(true);
        as1.setCustomNameVisible(true);
        as1.setNoGravity(true);
        as1.setCustomName(CraftChatMessage.fromStringOrNull("§6Rank : §r" + String.valueOf(getRank(player)) + "  [ §b" + RankMgr.toABCRank(getRank(player)) + " §r]"));
        
        connection.sendPacket(new PacketPlayOutSpawnEntityLiving(as));
        connection.sendPacket(new PacketPlayOutSpawnEntityLiving(as1));
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
    
    public static void addMoney(Player player, int m){
        String uuid = player.getUniqueId().toString();
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
        conf.getPlayerStatus().set("Status." + uuid + ".Rank", conf.getPlayerStatus().getInt("Status." + uuid + ".Rank") + m);
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
    
    public static int getGear(Player player){
        String uuid = player.getUniqueId().toString();
        return conf.getPlayerStatus().getInt("Status." + uuid + ".Gear");
    }
    
    public static String getEquiptClass(Player player){
        String uuid = player.getUniqueId().toString();
        return conf.getPlayerStatus().getString("Status." + uuid + ".EquiptClass");
    }
}
