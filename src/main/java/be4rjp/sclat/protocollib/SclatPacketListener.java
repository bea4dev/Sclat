package be4rjp.sclat.protocollib;

import be4rjp.sclat.Main;
import be4rjp.sclat.ServerType;
import be4rjp.sclat.SoundType;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.RankingHolograms;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import net.minecraft.server.v1_14_R1.EntityArmorStand;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class SclatPacketListener {
    public SclatPacketListener(){
        Main.protocolManager.addPacketListener(
            new PacketAdapter(Main.getPlugin(), ListenerPriority.NORMAL, PacketType.Play.Client.STEER_VEHICLE) {
                @Override
                public void onPacketReceiving(PacketEvent event) {//プレイヤーがエンティティに乗っているときのパケットを監視
                    final Player player = event.getPlayer();
                    if (event.getPacketType() == PacketType.Play.Client.STEER_VEHICLE
                            && player.getVehicle() != null) {
                        final PacketContainer packet = event.getPacket();
                
                        final float z = event.getPacket().getFloat().readSafely(0);
                        final float x = event.getPacket().getFloat().readSafely(1);
                        
                        float y = 0F;
                        
                        try {
                            if (event.getPacket().getBooleans().readSafely(1)) {
                                y = -1F;
                                if(DataMgr.getPlayerData(player).isInMatch())
                                    event.setCancelled(true);
                            }
                            if (event.getPacket().getBooleans().readSafely(0)) {
                                y = 1F;
                            }
                        } catch (Error | Exception e45) {
                        }
    
                        Vector vec = new Vector(x, y, z);
                        DataMgr.getPlayerData(player).setVehicleVector(vec);
                    }
                }
        });
    
    
        Main.protocolManager.addPacketListener(
            new PacketAdapter(Main.getPlugin(), PacketType.Play.Client.USE_ENTITY){
                @Override
                public void onPacketReceiving(PacketEvent event) {//プレイヤーがエンティティをクリックしたときのパケットの監視
                    final Player player = event.getPlayer();
                    if (event.getPacketType() == PacketType.Play.Client.USE_ENTITY) {
                        final PacketContainer packet = event.getPacket();
        
                        final int EntityID = packet.getIntegers().readSafely(0);
                        
                        try{
                            RankingHolograms rankingHolograms = DataMgr.getRankingHolograms(event.getPlayer());
                            for(EntityArmorStand armorStand : rankingHolograms.getArmorStandList()) {
                                if(armorStand.getBukkitEntity().getEntityId() == EntityID) {
                                    player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_ON, 1F, 1.2F);
                                    rankingHolograms.switchNextRankingType();
                                    rankingHolograms.refreshRankingAsync();
                                    break;
                                }
                            }
                        }catch (Exception e){}
                    }
                }
        });
    
    /*
        Main.protocolManager.addPacketListener(
            new PacketAdapter(Main.getPlugin(), PacketType.Play.Server.SPAWN_ENTITY){
                @Override
                public void onPacketReceiving(PacketEvent event) {//雪玉のスポーンパケットを遮断
                    final Player player = event.getPlayer();
                    if (event.getPacketType() == PacketType.Play.Server.SPAWN_ENTITY) {
                        final PacketContainer packet = event.getPacket();
                        
                        packet.g
                    
                        if(packet.getEntityTypeModifier().readSafely(0) == EntityType.SNOWBALL){
                            if(!DataMgr.getPlayerData(player).getSettings().ShowSnowBall())
                                event.setCancelled(true);
                        }
                    }
                }
        });
        
     */
    }
}
