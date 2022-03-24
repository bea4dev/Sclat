package be4rjp.sclat.manager;

import be4rjp.sclat.Main;
import be4rjp.sclat.data.DataMgr;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_14_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_14_R1.CraftServer;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

import java.util.UUID;
import org.bukkit.scheduler.BukkitRunnable;

public class NPCMgr {


    public static void createNPC(Player player1, String npcName1, Location location1) {
        BukkitRunnable task = new BukkitRunnable(){
            EntityPlayer npc;
            
            int s = 0;
            
            Player player = player1;
            String npcName = npcName1;
            Location location = location1;
            
            @Override
            public void run(){
                if(s == 0){
                    location.setYaw(location1.getYaw());
                    
                    MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
                    WorldServer nmsWorld = ((CraftWorld) location.getWorld()).getHandle();
                    GameProfile gameProfile = new GameProfile(player.getUniqueId(), npcName);

                    npc = new EntityPlayer(nmsServer, nmsWorld, gameProfile, new PlayerInteractManager(nmsWorld));
        
                    //見えないところにスポーンさせて、クライアントにスキンを先に読み込ませる
                    npc.setLocation(location.getX(), location.getY() - 20, location.getZ(), location.getYaw(), 0);
                    npc.getDataWatcher().set(DataWatcherRegistry.a.a(15), (byte)127);
        
                    for(Player p : Main.getPlugin(Main.class).getServer().getOnlinePlayers()){
                        PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
                        connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc));
                        connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
                        connection.sendPacket(new PacketPlayOutEntityMetadata(npc.getId(), npc.getDataWatcher(), true));
                    }
                }
                if(s == 1){
                    npc.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), 0);
                    for(Player p : Main.getPlugin(Main.class).getServer().getOnlinePlayers()){
                        PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
                        connection.sendPacket(new PacketPlayOutEntityTeleport(npc));
                        connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte) ((location.getYaw() * 256.0F) / 360.0F)));
                        connection.sendPacket(new PacketPlayOutEntityEquipment(npc.getBukkitEntity().getEntityId(), EnumItemSlot.MAINHAND, CraftItemStack.asNMSCopy(DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getWeaponIteamStack())));
                        if(DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getIsManeuver())
                            connection.sendPacket(new PacketPlayOutEntityEquipment(npc.getBukkitEntity().getEntityId(), EnumItemSlot.OFFHAND, CraftItemStack.asNMSCopy(DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getWeaponIteamStack())));
                        connection.sendPacket(new PacketPlayOutAnimation(npc, 0));
                    }
                    
                }
                if(s == 3){
                    for(Player p : Main.getPlugin(Main.class).getServer().getOnlinePlayers()){
                        PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
                        connection.sendPacket(new PacketPlayOutEntityDestroy(npc.getBukkitEntity().getEntityId()));
                    }
                    cancel();
                }
                s++;
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 20);

    }
}
