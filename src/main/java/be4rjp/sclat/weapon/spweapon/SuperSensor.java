
package be4rjp.sclat.weapon.spweapon;

import be4rjp.sclat.Main;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.manager.ArmorStandMgr;
import be4rjp.sclat.manager.SPWeaponMgr;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Be4rJP
 */
public class SuperSensor {
    public static void SuperSensorRunnable(Player player){
        DataMgr.getPlayerData(player).setIsUsingSP(true);
        SPWeaponMgr.setSPCoolTimeAnimation(player, 200);
        for(Player o_player : Main.getPlugin(Main.class).getServer().getOnlinePlayers()){
            if(DataMgr.getPlayerData(player).getTeam() != DataMgr.getPlayerData(o_player).getTeam() && DataMgr.getPlayerData(o_player).isInMatch())
                o_player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 200, 1));
        }
        for(Entity as : player.getWorld().getEntities()){
            if(as.getCustomName() != null){
                if(as instanceof ArmorStand && !as.getCustomName().equals("Path") && !as.getCustomName().equals("21") && !as.getCustomName().equals("100") && !as.getCustomName().equals("SplashShield") && !as.getCustomName().equals("Kasa")){
                    ((ArmorStand)as).addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 200, 1));
                }
            }
        }
        BukkitRunnable sound = new BukkitRunnable(){
            @Override
            public void run(){
                //player.playSound(player.getLocation(), Sound.BLOCK_CHEST_CLOSE, 1, 2);
                DataMgr.getPlayerData(player).setIsUsingSP(false);
            }
        };
        sound.runTaskLater(Main.getPlugin(), 200);
    }
}
