
package be4rjp.sclat.weapon;

import be4rjp.sclat.Main;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import be4rjp.sclat.manager.PaintMgr;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import net.minecraft.server.v1_13_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_13_R1.CraftServer;
import org.bukkit.craftbukkit.v1_13_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftMinecart;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_13_R1.inventory.CraftItemStack;
import org.bukkit.util.Vector;

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
            if("スプラッシュボム".equals(player.getInventory().getItemInMainHand().getItemMeta().getDisplayName())){
                BukkitRunnable task = new BukkitRunnable(){
 
                    
                    FallingBlock fb;
                    FallingBlock fb2;
                    Player p = player;
                    int i = 0;
                    int count = 0;
                    @Override
                    public void run(){
                        if(i == 0){
                            
                            //m.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
                            org.bukkit.block.data.BlockData bd = DataMgr.getPlayerData(p).getTeam().getTeamColor().getGlass().createBlockData();
                            fb = p.getWorld().spawnFallingBlock(p.getEyeLocation(), bd);
                            
                            fb.setGravity(true);
                            fb.setDropItem(false);
                            fb.setVelocity(p.getEyeLocation().getDirection().multiply(0.4));
                        }
                        if(fb.isDead()){
                            count++;
                        }
                        if(count == 1){
                            org.bukkit.block.data.BlockData bd = DataMgr.getPlayerData(p).getTeam().getTeamColor().getGlass().createBlockData();
                            fb2 = p.getWorld().spawnFallingBlock(fb.getLocation().add(0, 0.1, 0), bd);
                            fb2.setGravity(false);
                            fb2.setDropItem(false);
                            Vector vec = fb.getVelocity();
                            fb2.setVelocity(new Vector(vec.getX(), 0, vec.getZ()));
                        }
                        
                        if(count == 30){
                            //m.remove();
                            fb.remove();
                            fb2.remove();
                            org.bukkit.block.data.BlockData bd = DataMgr.getPlayerData(player).getTeam().getTeamColor().getWool().createBlockData();
                            p.getWorld().spawnParticle(org.bukkit.Particle.BLOCK_DUST, fb2.getLocation(), 50, 3, 3, 3, 1, bd);
                            PaintMgr.Paint(fb2.getLocation().add(0, -1, 0), p);
                            cancel();
                        }
                        if(i == 1000){
                            fb.remove();
                            fb2.remove();
                            org.bukkit.block.data.BlockData bd = DataMgr.getPlayerData(player).getTeam().getTeamColor().getWool().createBlockData();
                            p.getWorld().spawnParticle(org.bukkit.Particle.BLOCK_DUST, fb2.getLocation(), 50, 3, 3, 3, 1, bd);
                            PaintMgr.Paint(fb2.getLocation().add(0, -1, 0), p);
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
