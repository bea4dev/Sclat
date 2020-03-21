
package be4rjp.sclat.manager;

import be4rjp.sclat.Main;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;

/**
 *
 * @author Be4rJP
 */
public class BungeeCordMgr {
    public static void PlayerSendServer(Player player, String servername){
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(servername);
        player.sendPluginMessage(Main.getPlugin(), "BungeeCord", out.toByteArray());
    }
}
