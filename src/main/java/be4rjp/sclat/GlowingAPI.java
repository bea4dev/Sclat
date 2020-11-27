package be4rjp.sclat;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class GlowingAPI {
    public static void setGlowing(Entity entity, Player player, boolean flag) {
        PacketContainer packet = Main.protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
        packet.getIntegers().write(0, entity.getEntityId());
        WrappedDataWatcher watcher = new WrappedDataWatcher();
        WrappedDataWatcher.Serializer serializer = WrappedDataWatcher.Registry.get(Byte.class);
        watcher.setEntity(entity);
        watcher.setObject(0, serializer, (byte) (flag ? 0x40 : 0));
        packet.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());
        try {
            Main.protocolManager.sendServerPacket(player, packet);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}