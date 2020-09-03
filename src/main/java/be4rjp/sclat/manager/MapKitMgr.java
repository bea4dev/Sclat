
package be4rjp.sclat.manager;

import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import static org.bukkit.Bukkit.getServer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapCursor;
import org.bukkit.map.MapCursorCollection;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MinecraftFont;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 *
 * @author Be4rJP
 */
public class MapKitMgr {
    public static void setMapKit(Player player){
        MapView view = getServer().createMap(player.getWorld());
        view.setCenterX(player.getLocation().getBlockX());
        view.setCenterZ(player.getLocation().getBlockZ());
        view.setScale(MapView.Scale.CLOSEST);
        
        //renderer
        view.addRenderer(new MyRenderer());
        
        ItemStack item = new ItemStack(Material.FILLED_MAP);
        MapMeta meta = (MapMeta) item.getItemMeta();
        meta.setMapId(view.getId());
        meta.setDisplayName("カーソルを合わせて右クリックで発射");
        item.setItemMeta(meta);
        
        for (int count = 0; count < 9; count++){
            player.getInventory().setItem(count, item);
        }

        player.updateInventory();
        
        Location loc = player.getLocation();
        loc.setPitch(65);
        player.teleport(loc);
        
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100000, 10));
        
        DataMgr.getPlayerData(player).setPlayerMapLoc(loc);
    }
    
    public static Vector getMapLocationVector(Player player){
            Location loc = DataMgr.getPlayerData(player).getPlayerMapLoc().clone();
            Location ploc = player.getLocation().clone();
            int x = (int)(((ploc.getYaw() + 180) - (loc.getYaw() + 180))*3/2);
            int y = (int)((ploc.getPitch() - loc.getPitch())*3/2);
            if(x > 128)
                x = 128;
            if(x < -128)
                x = -128;
            if(y > 128)
                y = 128;
            if(y < -128)
                y = -128;      
            return new Vector(x, 0, y);
    }
    

    static class MyRenderer extends MapRenderer{
        @Override
        public void render(MapView view, MapCanvas canvas, Player player) {
            MapCursorCollection cursors = new MapCursorCollection();
            Location loc = DataMgr.getPlayerData(player).getPlayerMapLoc().clone();
            Location ploc = player.getLocation().clone();
            int x = (int)(((ploc.getYaw() + 180) - (loc.getYaw() + 180))*3);
            int y = (int)((ploc.getPitch() - loc.getPitch())*3);
            if(x > 128)
                x = 128;
            if(x < -128)
                x = -128;
            if(y > 128)
                y = 128;
            if(y < -128)
                y = -128;
            if(x == 128 || x == -128){
                ploc.setYaw(loc.getYaw());
                player.teleport(ploc);
            }
                
            cursors.addCursor(new MapCursor((byte)x, (byte)y, (byte)6, MapCursor.Type.WHITE_CROSS, true));
            canvas.setCursors(cursors);
        }
    }
}
