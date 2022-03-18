package be4rjp.sclat.packet;

import be4rjp.sclat.Main;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import be4rjp.sclat.data.PlayerSettings;
import io.netty.channel.*;
import net.minecraft.server.v1_14_R1.EntityTypes;
import net.minecraft.server.v1_14_R1.PacketPlayOutAbilities;
import net.minecraft.server.v1_14_R1.PacketPlayOutSpawnEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;

public class PacketHandler extends ChannelDuplexHandler{
    
    private final Player player;
    private final PlayerData playerData;
    private final PlayerSettings playerSettings;
    
    public PacketHandler(Player player){
        this.player = player;
        this.playerData = DataMgr.getPlayerData(player);
        this.playerSettings = playerData.getSettings();
    }
    
    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object packet) throws Exception {
        
        super.channelRead(channelHandlerContext, packet);
    }
    
    @Override
    public void write(ChannelHandlerContext channelHandlerContext, Object packet, ChannelPromise channelPromise) throws Exception {
        
        if(packet instanceof PacketPlayOutSpawnEntity){
            Field k = packet.getClass().getDeclaredField("k");
            k.setAccessible(true);
            EntityTypes entityTypes = (EntityTypes) k.get(packet);
            
            if(entityTypes == EntityTypes.SNOWBALL){
                if(!playerSettings.ShowSnowBall())
                    return;
            }
        }
        
        if(packet instanceof PacketPlayOutAbilities){
            if(playerData.getIsCharging()){
                ((PacketPlayOutAbilities) packet).b(playerData.getFov());
            }
        }
        
        
        super.write(channelHandlerContext, packet, channelPromise);
    }
}
