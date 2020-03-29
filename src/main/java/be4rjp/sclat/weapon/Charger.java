
package be4rjp.sclat.weapon;

import be4rjp.sclat.GaugeAPI;
import be4rjp.sclat.Main;
import static be4rjp.sclat.Main.conf;
import be4rjp.sclat.Sphere;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import be4rjp.sclat.manager.ArmorStandMgr;
import be4rjp.sclat.manager.DamageMgr;
import be4rjp.sclat.manager.DeathMgr;
import be4rjp.sclat.manager.PaintMgr;
import be4rjp.sclat.raytrace.BoundingBox;
import be4rjp.sclat.raytrace.RayTrace;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
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
public class Charger {
    public static void ChargerRunnable(Player player){
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
                    
                    if(data.getWeaponClass().getMainWeapon().getScope()){
                        if(charge != max)
                            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1, (int)charge / 3));
                        else
                            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40000, (int)charge / 3));
                    }
                    
                    wm.setDisplayName(wm.getDisplayName() + "§7[" + GaugeAPI.toGauge(charge, max, data.getTeam().getTeamColor().getColorCode(), "§7") + "]");
                    w.setItemMeta(wm);
                    p.getInventory().setItem(0, w);
                    RayTrace rayTrace = new RayTrace(p.getEyeLocation().toVector(),p.getEyeLocation().getDirection());
                    ArrayList<Vector> positions = rayTrace.traverse(charge / 2 * data.getWeaponClass().getMainWeapon().getDistanceTick(),0.7);
                    check : for(int i = 0; i < positions.size();i++){
                        Location position = positions.get(i).toLocation(p.getLocation().getWorld());
                        if(!position.getBlock().getType().equals(Material.AIR))
                            break check;
                        if(i % 2 == 0){
                            for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                                if(target.equals(p) || !DataMgr.getPlayerData(target).getSettings().ShowEffect_ChargerLine())
                                    continue;
                                Particle.DustOptions dustOptions = new Particle.DustOptions(data.getTeam().getTeamColor().getBukkitColor(), 1);
                                target.spawnParticle(Particle.REDSTONE, position, 1, 0, 0, 0, 50, dustOptions);
                            }
                        }
                    }
                }
                if(data.getTick() == 6 && data.isInMatch()){
                    if(player.hasPotionEffect(PotionEffectType.SLOW))
                        player.removePotionEffect(PotionEffectType.SLOW);
                    if(p.getExp() > data.getWeaponClass().getMainWeapon().getNeedInk() * charge){
                        p.setExp(p.getExp() - data.getWeaponClass().getMainWeapon().getNeedInk() * charge);
                        Charger.Shoot(p, charge / 2 * data.getWeaponClass().getMainWeapon().getDistanceTick(), data.getWeaponClass().getMainWeapon().getDamage() * charge);
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
    
    public static void Shoot(Player player, int reach, double damage){
        
        
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1, 5);
        RayTrace rayTrace = new RayTrace(player.getEyeLocation().toVector(),player.getEyeLocation().getDirection());
        ArrayList<Vector> positions = rayTrace.traverse(reach,0.2);

        
        loop : for(int i = 0; i < positions.size();i++){

            Location position = positions.get(i).toLocation(player.getLocation().getWorld());
            Block block = player.getLocation().getWorld().getBlockAt(position);
            
            if(!block.getType().equals(Material.AIR)){
                PaintMgr.Paint(position, player, true);
                break loop;
            }
            PaintMgr.PaintHightestBlock(position, player, false, true);
            
            for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                if(!DataMgr.getPlayerData(target).getSettings().ShowEffect_ChargerShot())
                    continue;
                org.bukkit.block.data.BlockData bd = DataMgr.getPlayerData(player).getTeam().getTeamColor().getWool().createBlockData();
                target.spawnParticle(org.bukkit.Particle.BLOCK_DUST, position, 2, 0, 0, 0, 1, bd);
            }
            
            
            
            double maxDist = 2;
            for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                if(!DataMgr.getPlayerData(target).isInMatch())
                    continue;
                if (target.getLocation().distance(position) <= maxDist) {
                    if(DataMgr.getPlayerData(player).getTeam() != DataMgr.getPlayerData(target).getTeam() && target.getGameMode().equals(GameMode.ADVENTURE)){
                        if(target.getHealth() + DataMgr.getPlayerData(target).getArmor() > damage){
                            DamageMgr.SclatGiveStrongDamage(target, damage, player);
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
                        break loop;
                    }
                }
            }
            
            for(Entity as : player.getWorld().getEntities()){
                if (as.getLocation().distance(position) <= maxDist){
                    if(as instanceof ArmorStand){
                        ArmorStandMgr.giveDamageArmorStand((ArmorStand)as, damage, player);
                    }          
                }
            }
            
           
        }
       
        
    }
            
}
