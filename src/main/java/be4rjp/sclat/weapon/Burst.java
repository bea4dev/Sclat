
package be4rjp.sclat.weapon;

import be4rjp.sclat.Main;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import static be4rjp.sclat.weapon.Blaster.Shoot;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Be4rJP
 */
public class Burst {
    public static void BurstCooltime(Player player){
        PlayerData data = DataMgr.getPlayerData(player);
        BukkitRunnable delay1 = new BukkitRunnable(){
            Player p = player;
            @Override
            public void run(){
                PlayerData data = DataMgr.getPlayerData(player);
                data.setCanRollerShoot(true);
            }
        };
        if(data.getCanRollerShoot())
            delay1.runTaskLater(Main.getPlugin(), data.getWeaponClass().getMainWeapon().getCoolTime());
        
        BukkitRunnable delay = new BukkitRunnable(){
            Player p = player;
            @Override
            public void run(){
                Burstshoot(player);
            }
        };
        if(data.getCanRollerShoot()){
            delay.runTaskLater(Main.getPlugin(), data.getWeaponClass().getMainWeapon().getDelay());
            data.setCanRollerShoot(false);
        }
    }
    
    public static void Burstshoot(Player player){
        BukkitRunnable task = new BukkitRunnable(){
            Player p = player;
            int c = 0;
            @Override
            public void run(){
                Shooter.Shoot(p, false);
                c++;
                if(c == DataMgr.getPlayerData(p).getWeaponClass().getMainWeapon().getRollerShootQuantity())
                    cancel();
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getShootTick());
    }
}
