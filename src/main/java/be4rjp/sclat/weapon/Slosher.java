
package be4rjp.sclat.weapon;

import be4rjp.sclat.Main;
import static be4rjp.sclat.Main.conf;

import be4rjp.sclat.Sclat;
import be4rjp.sclat.Sphere;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import be4rjp.sclat.manager.ArmorStandMgr;
import be4rjp.sclat.manager.DamageMgr;
import be4rjp.sclat.manager.DeathMgr;
import be4rjp.sclat.manager.PaintMgr;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 *
 * @author Be4rJP
 */
public class Slosher {
    public static void ShootSlosher(Player player){
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
                //for (int i = 0; i < data.getWeaponClass().getMainWeapon().getRollerShootQuantity(); i++) 
                    Slosher.Shoot(player, null);
            }
        };
        if(data.getCanRollerShoot()){
            delay.runTaskLater(Main.getPlugin(), data.getWeaponClass().getMainWeapon().getDelay());
            data.setCanRollerShoot(false);
        }
    }
    
    public static void Shoot(Player player, Vector v){
    
        if(player.getGameMode() == GameMode.SPECTATOR) return;
        
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
        DataMgr.mws.add(name);
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
                try{
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
                                    target.spawnParticle(org.bukkit.Particle.BLOCK_DUST, inkball.getLocation(), 3, 0, 0, 0, 1, bd);
                                }
                            }
                    }
                    
                    
                    PaintMgr.PaintHightestBlock(inkball.getLocation(), p, false, true);

                    if(i >= tick && !addedFallVec){
                        inkball.setVelocity(fallvec);
                        addedFallVec = true;
                    }
                    if(i >= tick)
                        inkball.setVelocity(inkball.getVelocity().add(new Vector(0, -0.1, 0)));
                    if(inkball.isDead()){
                        //半径
                        double maxDist = data.getWeaponClass().getMainWeapon().getBlasterExHankei();

                        //爆発音
                        player.getWorld().playSound(inkball.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 0.7F, 1);

                        //爆発エフェクト
                        Sclat.createInkExplosionEffect(inkball.getLocation(), maxDist, 25, player);

                        //塗る
                        for(int i = 0; i <= maxDist; i++){
                            List<Location> p_locs = Sphere.getSphere(inkball.getLocation(), i, 20);
                            for(Location loc : p_locs){
                                PaintMgr.Paint(loc, p, false);
                                PaintMgr.PaintHightestBlock(loc, p, false, false);
                            }
                        }



                        //攻撃判定の処理

                        for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                            if(!DataMgr.getPlayerData(target).isInMatch())
                                continue;
                            if (target.getLocation().distance(inkball.getLocation()) <= maxDist) {
                                double damage = (maxDist - target.getLocation().distance(inkball.getLocation())) * data.getWeaponClass().getMainWeapon().getBlasterExDamage();
                                if(DataMgr.getPlayerData(player).getTeam() != DataMgr.getPlayerData(target).getTeam() && target.getGameMode().equals(GameMode.ADVENTURE)){
                                    if(target.getHealth() + DataMgr.getPlayerData(target).getArmor() > damage){
                                        DamageMgr.SclatGiveDamage(target, damage);
                                        PaintMgr.Paint(target.getLocation(), player, true);
                                    }else{
                                        target.setGameMode(GameMode.SPECTATOR);
                                        DeathMgr.PlayerDeathRunnable(target, player, "killed");
                                        PaintMgr.Paint(target.getLocation(), player, true);
                                    }

                                    //AntiNoDamageTime
                                    BukkitRunnable task = new BukkitRunnable(){
                                        Player p = target;
                                        @Override
                                        public void run(){
                                            target.setNoDamageTicks(0);
                                        }
                                    };
                                    task.runTaskLater(Main.getPlugin(), 1);


                                }
                            }
                        }


                        for(Entity as : player.getWorld().getEntities()){
                            if(as instanceof ArmorStand){
                                if (as.getLocation().distance(inkball.getLocation()) <= maxDist) {
                                    double damage = (maxDist - as.getLocation().distance(inkball.getLocation())) * data.getWeaponClass().getMainWeapon().getBlasterExDamage();
                                    ArmorStandMgr.giveDamageArmorStand((ArmorStand)as, damage, p);
                                }
                            }
                        }
                        cancel();
                    }

                    i++;
                }catch(Exception e){
                    cancel();
                }
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 1);
    }
}
