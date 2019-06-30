
package be4rjp.sclat.weapon;

import be4rjp.sclat.Main;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import be4rjp.sclat.manager.PaintMgr;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Be4rJP
 */
public class SubWeapon implements Listener{
    //サブウエポンのリスナー部分
    @EventHandler
    public void onClickSubWeapon(PlayerInteractEvent event){
        Player player = event.getPlayer();
        Action action = event.getAction();
        PlayerData data = DataMgr.getPlayerData(player);
        if(action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)){
            if("スプラッシュボム".equals(data.getWeaponClass().getSubWeaponName())){
                BukkitRunnable task = new BukkitRunnable(){
                    Zombie zonbi;
                    FallingBlock fb;
                    Player p = player;
                    int i = 0;
                    int count = 0;
                    @Override
                    public void run(){
                        if(i == 0){
                            zonbi = (Zombie)p.getLocation().getWorld().spawnEntity(p.getLocation(), EntityType.ZOMBIE);
                            zonbi.setBaby(true);
                            zonbi.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
                            zonbi.setAI(false);
                            org.bukkit.block.data.BlockData bd = DataMgr.getPlayerData(p).getTeam().getTeamColor().getGlass().createBlockData();
                            fb = p.getWorld().spawnFallingBlock(zonbi.getLocation(), bd);
                            fb.setGravity(false);
                            //zonbi.setPassenger(fb);
                            zonbi.addPassenger(fb);
                            zonbi.setVelocity(p.getLocation().getDirection().multiply(0.3));
                        }
                        if(zonbi.isOnGround()){
                            count++;
                        }
                        if(count == 30){
                            zonbi.remove();
                            fb.remove();
                            org.bukkit.block.data.BlockData bd = DataMgr.getPlayerData(player).getTeam().getTeamColor().getWool().createBlockData();
                            p.getWorld().spawnParticle(org.bukkit.Particle.BLOCK_DUST, zonbi.getLocation(), 20, 3, 3, 3, 1, bd);
                            PaintMgr.Paint(zonbi.getLocation(), p);
                            cancel();
                        }
                        i++;
                    }
                };
                task.runTaskTimer(Main.getPlugin(), 0, 1);
            }
        }
    }
}
