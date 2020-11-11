package be4rjp.sclat.protocollib;

import be4rjp.sclat.Main;
import be4rjp.sclat.data.DataMgr;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
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
    }
}
