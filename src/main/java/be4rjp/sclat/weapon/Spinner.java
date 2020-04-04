
package be4rjp.sclat.weapon;

import be4rjp.sclat.GaugeAPI;
import be4rjp.sclat.Main;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import be4rjp.sclat.manager.PaintMgr;
import be4rjp.sclat.raytrace.RayTrace;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 *
 * @author Be4rJP
 */
public class Spinner {
    public static void SpinnerRunnable(Player player){
        BukkitRunnable task = new BukkitRunnable(){
            Player p = player;
            int charge = 0;
            int max = DataMgr.getPlayerData(p).getWeaponClass().getMainWeapon().getMaxCharge();
            @Override
            public void run(){
                PlayerData data = DataMgr.getPlayerData(p);
                
                data.setTick(data.getTick() + 1);
                
                if(data.getTick() <= 5 && data.isInMatch()){
                    ItemStack w = data.getWeaponClass().getMainWeapon().getWeaponIteamStack().clone();
                    ItemMeta wm = w.getItemMeta();
                    
                    //data.setTick(data.getTick() + 1);
                    if(charge < max)
                        charge++;
                    
                    wm.setDisplayName(wm.getDisplayName() + "§7[" + GaugeAPI.toGauge(charge, max, data.getTeam().getTeamColor().getColorCode(), "§7") + "]");
                    w.setItemMeta(wm);
                    p.getInventory().setItem(0, w);
                }
                if(data.getTick() == 6 && data.isInMatch()){
                    if(p.getExp() > data.getWeaponClass().getMainWeapon().getNeedInk() * charge){
                        SpinnerShootRunnable(charge, p);
                    }else{
                        p.sendTitle("", ChatColor.RED + "インクが足りません", 0, 10, 2);
                    }
                    charge = 0;
                    p.getInventory().setItem(0, data.getWeaponClass().getMainWeapon().getWeaponIteamStack());
                    data.setTick(7);
                    data.setIsHolding(false);
                }
                
                if(!data.isInMatch() || !p.isOnline())
                    cancel();
                
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 1);
    }
    
    public static void SpinnerShootRunnable(int charge, Player player){
        BukkitRunnable task = new BukkitRunnable(){
            Player p = player;
            int c = 0;
            @Override
            public void run(){
                if(c == charge || !p.isOnline() || DataMgr.getPlayerData(p).getIsSquid())
                    cancel();
                Spinner.Shoot(p, charge);
                c++;
            }
        };
        task.runTaskTimer(Main.getPlugin(), 2, DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getShootTick());
    }
    
    public static void Shoot(Player player, int charge){
        PlayerData data = DataMgr.getPlayerData(player);
        if(player.getExp() <= data.getWeaponClass().getMainWeapon().getNeedInk()){
            player.sendTitle("", ChatColor.RED + "インクが足りません", 0, 5, 2);
            return;
        }
        player.setExp(player.getExp() - data.getWeaponClass().getMainWeapon().getNeedInk());
        Snowball ball = player.launchProjectile(Snowball.class);
        player.playSound(player.getLocation(), Sound.ENTITY_PIG_STEP, 0.3F, 1F);
                Vector vec = player.getLocation().getDirection().multiply(DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getShootSpeed() * charge);
                double random = DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getRandom();
                int distick = DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getDistanceTick();
                vec.add(new Vector(Math.random() * random - random/2, 0, Math.random() * random - random/2));
                ball.setVelocity(vec);
                ball.setShooter(player);
                BukkitRunnable task = new BukkitRunnable(){
                    int i = 0;
                    int tick = distick;
                    //Vector fallvec;
                    Vector origvec = vec;
                    Snowball inkball = ball;
                    Player p = player;
                    Vector fallvec = new Vector(inkball.getVelocity().getX(), inkball.getVelocity().getY()  , inkball.getVelocity().getZ()).multiply(DataMgr.getPlayerData(p).getWeaponClass().getMainWeapon().getShootSpeed()/17);
                    @Override
                    public void run(){
                        org.bukkit.block.data.BlockData bd = DataMgr.getPlayerData(p).getTeam().getTeamColor().getWool().createBlockData();
                        for (Player o_player : Main.getPlugin().getServer().getOnlinePlayers()) {
                            if(DataMgr.getPlayerData(o_player).getSettings().ShowEffect_Shooter())
                                o_player.spawnParticle(org.bukkit.Particle.BLOCK_DUST, inkball.getLocation(), 1, 0, 0, 0, 1, bd);
                        }
                        
                        if(i == tick)
                            inkball.setVelocity(fallvec);
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
