
package be4rjp.sclat.weapon;

import be4rjp.sclat.Main;
import static be4rjp.sclat.Main.conf;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import be4rjp.sclat.manager.PaintMgr;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 *
 * @author Be4rJP
 */
public class Bucket {
    public static void ShootBucket(Player player){
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
                for (int i = 0; i < data.getWeaponClass().getMainWeapon().getRollerShootQuantity(); i++) 
                    Shoot(player, null);
            }
        };
        if(data.getCanRollerShoot()){
            delay.runTaskLater(Main.getPlugin(), data.getWeaponClass().getMainWeapon().getDelay());
            data.setCanRollerShoot(false);
        }
    }
    
    public static void Shoot(Player player, Vector v){
        PlayerData data = DataMgr.getPlayerData(player);
        if(player.getExp() <= (float)(data.getWeaponClass().getMainWeapon().getNeedInk() / Gear.getGearInfluence(player, Gear.Type.MAIN_INK_EFFICIENCY_UP))){
            player.sendTitle("", ChatColor.RED + "インクが足りません", 0, 13, 2);
            return;
        }
        player.setExp(player.getExp() - (float)(data.getWeaponClass().getMainWeapon().getNeedInk() / Gear.getGearInfluence(player, Gear.Type.MAIN_INK_EFFICIENCY_UP)));
        Snowball ball = player.launchProjectile(Snowball.class);
        Vector vec = player.getLocation().getDirection().multiply(DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getShootSpeed());
        if(v != null)
            vec = v;
        double random = DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getRandom();
        int distick = DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getDistanceTick();
        vec.add(new Vector(Math.random() * random - random/2, Math.random() * random/1.5 - random/3, Math.random() * random - random/2));
        ball.setVelocity(vec);
        ball.setShooter(player);
        String name = String.valueOf(Main.getNotDuplicateNumber());
        ball.setCustomName(name);
        DataMgr.getMainSnowballNameMap().put(name, ball);
        DataMgr.setSnowballHitCount(name, 0);
        BukkitRunnable task = new BukkitRunnable(){
            int i = 0;
            int tick = distick;
            Snowball inkball = ball;
            Player p = player;
            boolean addedFallVec = false;
            Vector fallvec = new Vector(inkball.getVelocity().getX(), inkball.getVelocity().getY()  , inkball.getVelocity().getZ()).multiply(DataMgr.getPlayerData(p).getWeaponClass().getMainWeapon().getShootSpeed()/17);
            @Override
            public void run(){
                inkball = DataMgr.getMainSnowballNameMap().get(name);
                        
                    if(!inkball.equals(ball)){
                        i+=DataMgr.getSnowballHitCount(name);
                        DataMgr.setSnowballHitCount(name, 0);
                    }
                    
                for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                    if(!DataMgr.getPlayerData(target).getSettings().ShowEffect_RollerShot())
                        continue;
                    if(target.getWorld() == inkball.getWorld()){
                        if(target.getLocation().distance(inkball.getLocation()) < conf.getConfig().getInt("ParticlesRenderDistance")){
                            org.bukkit.block.data.BlockData bd = DataMgr.getPlayerData(p).getTeam().getTeamColor().getWool().createBlockData();
                            target.spawnParticle(org.bukkit.Particle.BLOCK_DUST, inkball.getLocation(), 1, 0, 0, 0, 1, bd);
                        }
                    }
                }

                if(i >= tick && !addedFallVec){
                    inkball.setVelocity(fallvec);
                    addedFallVec = true;
                }
                if(i >= tick)
                    inkball.setVelocity(inkball.getVelocity().add(new Vector(0, -0.1, 0)));
                if(i != tick)
                    PaintMgr.PaintHightestBlock(inkball.getLocation(), p, true, true);
                if(inkball.isDead())
                    cancel();

                i++;
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 1);
    }
}
