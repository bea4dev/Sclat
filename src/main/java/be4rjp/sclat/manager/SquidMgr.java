package be4rjp.sclat.manager;

import be4rjp.sclat.Main;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Be4rJP
 */
public class SquidMgr {
    public static void SquidRunnable(Player player){
        BukkitRunnable task = new BukkitRunnable(){
            Player p = player;
            boolean is = false;
            boolean is2 = false;
            //LivingEntity squid;
            @Override
            public void run(){
                PlayerData data = DataMgr.getPlayerData(p);
                if(!data.isInMatch())
                    return;
                if(p.getInventory().getItemInMainHand().getType().equals(Material.AIR)){
                    data.setIsSquid(true);
                }else{
                    data.setIsSquid(false);
                }
            
                if(data.getIsOnInk() && data.getIsSquid()){
                    
                    if(!is){
                        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_SWIM, 0.1F, 10F);  
                        is = true;
                    }
                    
                        if(p.getExp() <= 0.999F){
                            p.setExp(p.getExp() + 0.005F);
                        }
                        p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 3));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 200, 1));
                        p.setFoodLevel(20);
                        p.setSprinting(true);
                        p.setWalkSpeed(0.25F);
                        
                        
                    
                }
                    /*
                    is = false;
                    p.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 200, 3));
                    if(!is2){
                        Entity e = p.getLocation().getWorld().spawnEntity(p.getLocation(), EntityType.SQUID);
                        squid = (LivingEntity)e;
                        squid.setAI(false);
                        squid.setSwimming(true);
                        squid.setCustomName(p.getDisplayName());
                        squid.setCustomNameVisible(true);
                        is2 = true;
                        p.setFoodLevel(4);
                    }
                    squid.teleport(p);
                    */
                else{
                    is = false;
                    if(p.hasPotionEffect(PotionEffectType.REGENERATION))
                        p.removePotionEffect(PotionEffectType.REGENERATION);
                    if(p.hasPotionEffect(PotionEffectType.INVISIBILITY))
                        p.removePotionEffect(PotionEffectType.INVISIBILITY);
                    p.setFoodLevel(4);
                    player.setWalkSpeed(0.2F);
                }
            } 
        };
        task.runTaskTimer(Main.getPlugin(), 0, 1);
    }
}
