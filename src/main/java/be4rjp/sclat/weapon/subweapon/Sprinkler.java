
package be4rjp.sclat.weapon.subweapon;

import be4rjp.sclat.Main;
import be4rjp.sclat.Sphere;
import be4rjp.sclat.data.DataMgr;
import java.util.List;
import net.minecraft.server.v1_13_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_13_R1.PlayerConnection;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
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
public class Sprinkler {
    public static void SprinklerRunnable(Player player){
        BukkitRunnable task = new BukkitRunnable(){
            Player p = player;
            Vector p_vec;
            double x = 0;
            double z = 0;
            boolean collision = false;
            boolean block_check = false;
            boolean cb = false;
            Location l = p.getLocation();
            int cc = 0;
            int c = 0;
            Item drop;
            @Override
            public void run(){
                try{
                    if(c == 0){
                        if(!DataMgr.getPlayerData(player).getIsBombRush())
                            p.setExp(p.getExp() - 0.59F);
                        ItemStack bom = new ItemStack(Material.BIRCH_FENCE_GATE).clone();
                        ItemMeta bom_m = bom.getItemMeta();
                        bom_m.setLocalizedName(String.valueOf(Main.getNotDuplicateNumber()));
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
                        ArmorStand as = DataMgr.getSprinklerFromplayer(player);
                        as.setVisible(false);
                        as.setHelmet(new ItemStack(Material.AIR));
                        as.teleport(drop.getLocation().add(0, -0.4, 0));
                        as.setCustomName("21");
                        SprinklerRunnable2(as);

                        drop.remove();
                        cancel();
                        return;
                    }

                    //視認用エフェクト
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
                }catch(Exception e){
                    cancel();
                    drop.remove();
                    Main.getPlugin().getLogger().warning(e.getMessage());
                }
            }
        };
        
        BukkitRunnable cooltime = new BukkitRunnable(){
            @Override
            public void run(){
                DataMgr.getPlayerData(player).setCanUseSubWeapon(true);
            }
        };
        cooltime.runTaskLater(Main.getPlugin(), 8);
                
        if(player.getExp() > 0.4 || DataMgr.getPlayerData(player).getIsBombRush())
            task.runTaskTimer(Main.getPlugin(), 0, 1);
        else
            player.sendTitle("", ChatColor.RED + "インクが足りません", 0, 5, 2);
    }
    
    public static void SprinklerRunnable2(ArmorStand as){
        BukkitRunnable delay = new BukkitRunnable(){
            @Override
            public void run(){
                as.setHelmet(new ItemStack(Material.BIRCH_FENCE_GATE));
            }
        };
        delay.runTaskLater(Main.getPlugin(), 10);
    }
}
