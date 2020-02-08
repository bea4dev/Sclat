
package be4rjp.sclat.weapon.subweapon;

import be4rjp.sclat.Main;
import be4rjp.sclat.Sphere;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.manager.DeathMgr;
import be4rjp.sclat.manager.PaintMgr;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
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
            int x = 0;
            int z = 0;
            boolean collision = false;
            int c = 0;
            Item drop;
            @Override
            public void run(){
                if(c == 0){
                    p.setExp(p.getExp() - 0.39F);
                    ItemStack bom = new ItemStack(DataMgr.getPlayerData(p).getTeam().getTeamColor().getWool()).clone();
                    ItemMeta bom_m = bom.getItemMeta();
                    bom_m.setLocalizedName(String.valueOf(Main.getNotDuplicateNumber()));
                    bom.setItemMeta(bom_m);
                    drop = p.getWorld().dropItem(p.getEyeLocation(), bom);
                    drop.setVelocity(p.getEyeLocation().getDirection());
                    p_vec = p.getEyeLocation().getDirection();
                }
                
                if(c != 0){
                    Location loc = drop.getLocation();
                    Location loc1 = new Location(loc.getWorld(), loc.getBlockX() + 1, loc.getBlockY(), loc.getBlockZ() + 1);
                    Location loc2 = new Location(loc.getWorld(), loc.getBlockX() - 0.1, loc.getBlockY(), loc.getBlockZ() + 1);
                    Location loc3 = new Location(loc.getWorld(), loc.getBlockX() + 1, loc.getBlockY(), loc.getBlockZ() - 0.1);
                    Location loc4 = new Location(loc.getWorld(), loc.getBlockX() - 0.1, loc.getBlockY(), loc.getBlockZ() - 0.1);
                    if(!loc1.getBlock().getType().equals(Material.AIR) || !loc2.getBlock().getType().equals(Material.AIR) || !loc3.getBlock().getType().equals(Material.AIR) || !loc4.getBlock().getType().equals(Material.AIR)){
                        collision = true;
                        if((loc1.getBlock().getType().equals(Material.BARRIER) || loc2.getBlock().getType().equals(Material.BARRIER) || loc3.getBlock().getType().equals(Material.BARRIER) || loc4.getBlock().getType().equals(Material.BARRIER)) && !DataMgr.getPlayerData(p).getMatch().getMapData().canPaintBBlock())
                            collision = false;
                    }
                }
                
                
                
                if(drop.isOnGround() || collision){
                    
                    //半径
                    double maxDist = 3;
                    
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
                                    target.damage(damage);
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

                
                if(c > 500){
                    drop.remove();
                    cancel();
                    return;
                }
                
            }
        };
        
        if(player.getExp() > 0.4)
            task.runTaskTimer(Main.getPlugin(), 0, 1);
        else
            player.sendTitle("", ChatColor.RED + "インクが足りません", 0, 5, 2);
    }
}
