
package be4rjp.sclat.weapon.subweapon;

import be4rjp.sclat.Main;
import static be4rjp.sclat.Main.conf;
import be4rjp.sclat.Sphere;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.weapon.Gear;
import java.util.List;
import net.minecraft.server.v1_14_R1.EnumItemSlot;
import net.minecraft.server.v1_14_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_14_R1.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_14_R1.PlayerConnection;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftSnowball;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
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
            Snowball ball;
            int ndn;
            @Override
            public void run(){
                try{
                    if(c == 0){
                        if(!DataMgr.getPlayerData(player).getIsBombRush())
                            p.setExp(p.getExp() - (float)(0.59 / Gear.getGearInfluence(player, Gear.Type.SUB_SPEC_UP)));
                        ItemStack bom = new ItemStack(Material.BIRCH_FENCE_GATE).clone();
                        ItemMeta bom_m = bom.getItemMeta();
                        ndn = Main.getNotDuplicateNumber();
                        bom_m.setLocalizedName(String.valueOf(ndn));
                        bom.setItemMeta(bom_m);
                        drop = p.getWorld().dropItem(p.getEyeLocation(), bom);
                        drop.setVelocity(p.getEyeLocation().getDirection());
                        //雪玉をスポーンさせた瞬間にプレイヤーに雪玉がデスポーンした偽のパケットを送信する
                        ball = player.launchProjectile(Snowball.class);
                        ball.setVelocity(new Vector(0, 0, 0));
                        DataMgr.setSnowballIsHit(ball, false);
                        ball.setCustomName(String.valueOf(ndn));
                        DataMgr.getSnowballNameMap().put(String.valueOf(ndn), ball);
                        DataMgr.setSnowballIsHit(ball, false);

                        for (Player o_player : Main.getPlugin().getServer().getOnlinePlayers()) {
                            PlayerConnection connection = ((CraftPlayer) o_player).getHandle().playerConnection;
                            connection.sendPacket(new PacketPlayOutEntityDestroy(ball.getEntityId()));
                        }
                        p_vec = p.getEyeLocation().getDirection();
                    }
                    
                    ball = DataMgr.getSnowballNameMap().get(String.valueOf(ndn));

                    if(!drop.isOnGround() && !(drop.getVelocity().getX() == 0 && drop.getVelocity().getZ() != 0) && !(drop.getVelocity().getX() != 0 && drop.getVelocity().getZ() == 0))
                        ball.setVelocity(drop.getVelocity());

                    if(DataMgr.getSnowballIsHit(ball) || drop.isOnGround()){
                        ArmorStand as = DataMgr.getSprinklerFromplayer(player);
                        as.setVisible(false);
                        as.setHelmet(new ItemStack(Material.AIR));
                        as.teleport(drop.getLocation().add(0, -0.4, 0));
                        as.setCustomName("21");
                        SprinklerRunnable2(as, p);
                        drop.remove();
                        cancel();
                        return;
                    }

                    //視認用エフェクト
                    for (Player o_player : Main.getPlugin().getServer().getOnlinePlayers()) {
                        if(DataMgr.getPlayerData(o_player).getSettings().ShowEffect_Bomb()){
                            if(o_player.getWorld() == drop.getLocation().getWorld()){
                                if(o_player.getLocation().distanceSquared(drop.getLocation()) < Main.PARTICLE_RENDER_DISTANCE_SQUARED){
                                    Particle.DustOptions dustOptions = new Particle.DustOptions(DataMgr.getPlayerData(p).getTeam().getTeamColor().getBukkitColor(), 1);
                                    o_player.spawnParticle(Particle.REDSTONE, drop.getLocation(), 1, 0, 0, 0, 50, dustOptions);
                                }
                            }
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
                
        if(player.getExp() > (float)(0.6 / Gear.getGearInfluence(player, Gear.Type.SUB_SPEC_UP)))
            task.runTaskTimer(Main.getPlugin(), 0, 1);
        else{
            player.sendTitle("", ChatColor.RED + "インクが足りません", 0, 5, 2);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1F, 1.63F);
        }
    }
    
    public static void SprinklerRunnable2(ArmorStand as, Player player){
        BukkitRunnable delay = new BukkitRunnable(){
            @Override
            public void run(){
                for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                    if(as.getWorld() != target.getWorld())
                        continue;
                    ((CraftPlayer)target).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(as.getEntityId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(DataMgr.getPlayerData(player).getTeam().getTeamColor().getGlass()))));
                }
                as.getWorld().playSound(as.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 1F, 1F);
                //as.setHelmet(new ItemStack(DataMgr.getPlayerData(player).getTeam().getTeamColor().getGlass()));
            }
        };
        delay.runTaskLater(Main.getPlugin(), 10);
    }
}
