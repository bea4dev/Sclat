
package be4rjp.sclat.data;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

/**
 *
 * @author Be4rJP
 */
public class KasaData {
    
    private List<ArmorStand> list = new ArrayList<ArmorStand>();
    private Player player;
    double damage = 0;
    
    public KasaData(Player player){this.player = player;}
    
    public double getDamage(){return this.damage;}
    
    public List<ArmorStand> getArmorStandList(){return this.list;}
    
    public Player getPlayer(){return this.player;}
    
    
    public void setDamage(double damage){this.damage = damage;}
    
    public void setArmorStandList(List<ArmorStand> list){this.list = list;}
}
