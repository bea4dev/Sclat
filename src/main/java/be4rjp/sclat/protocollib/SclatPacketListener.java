package be4rjp.sclat.protocollib;

import be4rjp.sclat.Main;
import be4rjp.sclat.SoundType;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.RankingHolograms;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import net.minecraft.server.v1_13_R2.EntityArmorStand;
import org.bukkit.Sound;
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
    }
}
