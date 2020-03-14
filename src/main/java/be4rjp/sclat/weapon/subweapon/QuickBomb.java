
package be4rjp.sclat.weapon.subweapon;

import be4rjp.sclat.Main;
import be4rjp.sclat.Sphere;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.manager.DamageMgr;
import be4rjp.sclat.manager.DeathMgr;
import be4rjp.sclat.manager.PaintMgr;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 *
 * @author Be4rJP
 */
public class QuickBomb {
    
    public static void QuickBomRunnable(Player player){
        BukkitRunnable task = new BukkitRunnable(){
            Player p = player;
            Vector p_vec;
            double x = 0;
            double z = 0;
            boolean collision = false;
            boolean block_check = false;
            int c = 0;
            Item drop;
            @Override
            public void run(){
                if(c == 0){
                    if(!DataMgr.getPlayerData(player).getIsBombRush())
                        p.setExp(p.getExp() - 0.39F);
                    ItemStack bom = new ItemStack(DataMgr.getPlayerData(p).getTeam().getTeamColor().getWool()).clone();
                    ItemMeta bom_m = bom.getItemMeta();
                    //bom_m.setLocalizedName(String.valueOf(Main.getNotDuplicateNumber()));
                    bom.setItemMeta(bom_m);
                    drop = p.getWorld().dropItem(p.getEyeLocation(), bom);
                    drop.setVelocity(p.getEyeLocation().getDirection());
                    p_vec = p.getEyeLocation().getDirection();
                }
                
                if(c != 0){
                    if(!(p_vec.getX() == 0 && p_vec.getZ() == 0)){
                        if(p_vec.getX() == 0 && p_vec.getZ() != 0){
                            if((drop.getLocation().getZ() - z) == 0)
                                collision = true;
                        }
                        if(p_vec.getX() != 0 && p_vec.getZ() == 0){
                            if((drop.getLocation().getX() - x) == 0)
                                collision = true;
                        }
                        if(p_vec.getX() != 0 && p_vec.getZ() != 0){
                            if((drop.getLocation().getX() - x) == 0)
                                collision = true;
                            if((drop.getLocation().getZ() - z) == 0)
                                collision = true;
                        }
                    }
                }
                
                Block block = drop.getLocation().getBlock();
                Block block1 = block.getRelative(BlockFace.UP);
                Block block2 = block.getRelative(BlockFace.DOWN);
                Block block3 = block.getRelative(BlockFace.EAST);
                Block block4 = block.getRelative(BlockFace.SOUTH);
                Block block5 = block.getRelative(BlockFace.NORTH);
                Block block6 = block.getRelative(BlockFace.WEST);
                if(!block.getType().equals(Material.AIR) || !block1.getType().equals(Material.AIR) || !block2.getType().equals(Material.AIR) || !block3.getType().equals(Material.AIR) || !block4.getType().equals(Material.AIR) || !block5.getType().equals(Material.AIR) || !block6.getType().equals(Material.AIR))
                    block_check = true;
                
                
                if((drop.isOnGround() || collision) && block_check){
                    
                    //半径
                    double maxDist = 3;
                    
                    //爆発音
                    player.getWorld().playSound(drop.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1, 1);
                    
                    //爆発エフェクト
                    List<Location> s_locs = Sphere.getSphere(drop.getLocation(), maxDist, 20);
                    for (Player o_player : Main.getPlugin().getServer().getOnlinePlayers()) {
                        if(DataMgr.getPlayerData(o_player).getSettings().ShowEffect_BombEx()){
                            for(Location loc : s_locs){
                                org.bukkit.block.data.BlockData bd = DataMgr.getPlayerData(player).getTeam().getTeamColor().getWool().createBlockData();
                                o_player.spawnParticle(org.bukkit.Particle.BLOCK_DUST, loc, 1, 0, 0, 0, 1, bd);
                            }
                        }
                    }
                    
                    //塗る
                    for(int i = 0; i <= maxDist; i++){
                        List<Location> p_locs = Sphere.getSphere(drop.getLocation(), i, 20);
                        for(Location loc : p_locs){
                            PaintMgr.Paint(loc, p, false);
                        }
                    }
                    
                    
                    
                    //攻撃判定の処理
               
                    for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                        if(!DataMgr.getPlayerData(target).isInMatch())
                            continue;
                        if (target.getLocation().distance(drop.getLocation()) <= maxDist) {
                            double damage = (maxDist - target.getLocation().distance(drop.getLocation())) * 7;
                            if(DataMgr.getPlayerData(player).getTeam() != DataMgr.getPlayerData(target).getTeam() && target.getGameMode().equals(GameMode.ADVENTURE)){
                                if(target.getHealth() > damage){
                                    DamageMgr.SclatGiveDamage(target, damage);
                                    PaintMgr.Paint(target.getLocation(), player, true);
                                }else{
                                    target.setGameMode(GameMode.SPECTATOR);
                                    DeathMgr.PlayerDeathRunnable(target, player, "subWeapon");
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
                    drop.remove();
                    cancel();
                    return;
                }
                
                //ボムの視認用エフェクト
                for (Player o_player : Main.getPlugin().getServer().getOnlinePlayers()) {
                    if(DataMgr.getPlayerData(o_player).getSettings().ShowEffect_Bomb()){
                        Particle.DustOptions dustOptions = new Particle.DustOptions(DataMgr.getPlayerData(p).getTeam().getTeamColor().getBukkitColor(), 1);
                        o_player.spawnParticle(Particle.REDSTONE, drop.getLocation(), 1, 0, 0, 0, 50, dustOptions);
                    }
                }
                
                c++;
                x = drop.getLocation().getX();
                z = drop.getLocation().getZ();

                
                if(c > 1000){
                    drop.remove();
                    cancel();
                    return;
                }
                
            }
        };
        
        BukkitRunnable cooltime = new BukkitRunnable(){
            @Override
            public void run(){
                DataMgr.getPlayerData(player).setCanUseSubWeapon(true);
            }
        };
        cooltime.runTaskLater(Main.getPlugin(), 10);
                
        if(player.getExp() > 0.4 || DataMgr.getPlayerData(player).getIsBombRush())
            task.runTaskTimer(Main.getPlugin(), 0, 1);
        else
            player.sendTitle("", ChatColor.RED + "インクが足りません", 0, 5, 2);
    }
}
