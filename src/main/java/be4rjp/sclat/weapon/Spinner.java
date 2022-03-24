
package be4rjp.sclat.weapon;

import be4rjp.sclat.GaugeAPI;
import be4rjp.sclat.Main;
import static be4rjp.sclat.Main.conf;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import be4rjp.sclat.manager.PaintMgr;
import be4rjp.sclat.raytrace.RayTrace;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftSnowball;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
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
            int keeping = 0;
            int max = DataMgr.getPlayerData(p).getWeaponClass().getMainWeapon().getMaxCharge();
            @Override
            public void run(){
                PlayerData data = DataMgr.getPlayerData(p);
                
                data.setTick(data.getTick() + 1);
                
                if(keeping == data.getWeaponClass().getMainWeapon().getChargeKeepingTime() && data.getWeaponClass().getMainWeapon().getCanChargeKeep() && data.getSettings().doChargeKeep())
                    charge = 0;
    
                if(data.getIsUsingMM() || data.getIsUsingJetPack() || data.getIsUsingTyakuti() || data.getIsUsingSS()){
                    charge = 0;
                    data.setTick(8);
                    return;
                }
                
                if(data.getTick() <= 6 && data.isInMatch()){
                    ItemStack w = data.getWeaponClass().getMainWeapon().getWeaponIteamStack().clone();
                    ItemMeta wm = w.getItemMeta();
                    
                    //data.setTick(data.getTick() + 1);
                    if(charge < max)
                        charge++;
                    
                    wm.setDisplayName(wm.getDisplayName() + "§7[" + GaugeAPI.toGauge(charge, max, data.getTeam().getTeamColor().getColorCode(), "§7") + "]");
                    w.setItemMeta(wm);
                    p.getInventory().setItem(0, w);
                }
                
                if(charge == max || data.getWeaponClass().getMainWeapon().getHanbunCharge())
                    if(p.getInventory().getItemInMainHand().getType().equals(Material.AIR))
                        if(data.getWeaponClass().getMainWeapon().getCanChargeKeep())
                            if(data.getSettings().doChargeKeep())
                                data.setTick(11);
                
                if(p.getGameMode().equals(GameMode.SPECTATOR))
                    charge = 0;
                
                if(data.getTick() >= 11 && (charge == max || data.getWeaponClass().getMainWeapon().getHanbunCharge()))
                    keeping++;
                else
                    keeping = 0;
                
                if(data.getTick() == 7 && data.isInMatch()){
                    if(p.getExp() > data.getWeaponClass().getMainWeapon().getNeedInk() * charge){
                        SpinnerShootRunnable((int)(charge * data.getWeaponClass().getMainWeapon().getChargeRatio()), p);
                    }else{
                        int reach = (int)(p.getExp() / data.getWeaponClass().getMainWeapon().getNeedInk());
                        if(reach >= 2){
                            SpinnerShootRunnable((int)(reach * data.getWeaponClass().getMainWeapon().getChargeRatio()), p);
                        }else{
                            p.sendTitle("", ChatColor.RED + "インクが足りません", 0, 10, 2);
                            p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1F, 1.63F);
                        }

                    }
                    charge = 0;
                    p.getInventory().setItem(0, data.getWeaponClass().getMainWeapon().getWeaponIteamStack());
                    data.setTick(8);
                    data.setIsHolding(false);
                }
                
                if(!data.isInMatch() || !p.isOnline())
                    cancel();
                
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 1);
    }
    
    public static void SpinnerShootRunnable(int charge, Player player){
        DataMgr.getPlayerData(player).setCanCharge(false);
        BukkitRunnable task = new BukkitRunnable(){
            Player p = player;
            int c = 0;
            @Override
            public void run(){
                if(c == charge || !p.isOnline() || DataMgr.getPlayerData(p).getIsSquid()){
                    DataMgr.getPlayerData(p).setCanCharge(true);
                    cancel();
                }
                PlayerData data = DataMgr.getPlayerData(p);
                if(data.getIsUsingMM() || data.getIsUsingJetPack() || data.getIsUsingTyakuti() || data.getIsUsingSS()){
                    cancel();
                }
                Spinner.Shoot(p, (int)(charge / DataMgr.getPlayerData(p).getWeaponClass().getMainWeapon().getChargeRatio()));
                c++;
            }
        };
        task.runTaskTimer(Main.getPlugin(), 2, DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getShootTick());
    }
    
    public static void Shoot(Player player, int charge){
    
        if(player.getGameMode() == GameMode.SPECTATOR) return;
        
        PlayerData data = DataMgr.getPlayerData(player);
        if(player.getExp() <= (float)(data.getWeaponClass().getMainWeapon().getNeedInk() / Gear.getGearInfluence(player, Gear.Type.MAIN_INK_EFFICIENCY_UP))){
            player.sendTitle("", ChatColor.RED + "インクが足りません", 0, 5, 2);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1F, 1.63F);
            return;
        }
        player.setExp(player.getExp() - (float)(data.getWeaponClass().getMainWeapon().getNeedInk() / Gear.getGearInfluence(player, Gear.Type.MAIN_INK_EFFICIENCY_UP)));
        Snowball ball = player.launchProjectile(Snowball.class);
        ((CraftSnowball)ball).getHandle().setItem(CraftItemStack.asNMSCopy(new ItemStack(DataMgr.getPlayerData(player).getTeam().getTeamColor().getWool())));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PIG_STEP, 0.3F, 1.1F);
                Vector vec = player.getLocation().getDirection().multiply(DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getShootSpeed() * charge);
                double random = DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getRandom();
                int distick = DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getDistanceTick();
                vec.add(new Vector(Math.random() * random - random/2, Math.random() * random - random/2, Math.random() * random - random/2));
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
                    //Vector fallvec;
                    Vector origvec = vec;
                    Snowball inkball = ball;
                    boolean addedFallVec = false;
                    Player p = player;
                    Vector fallvec = new Vector(inkball.getVelocity().getX(), inkball.getVelocity().getY()  , inkball.getVelocity().getZ()).multiply(DataMgr.getPlayerData(p).getWeaponClass().getMainWeapon().getShootSpeed()/17);
                    @Override
                    public void run(){
                        inkball = DataMgr.getMainSnowballNameMap().get(name);
                        
                        if(!inkball.equals(ball)){
                            i+=DataMgr.getSnowballHitCount(name) - 1;
                            DataMgr.setSnowballHitCount(name, 0);
                        }
    
                        if(i != 0) {
                            org.bukkit.block.data.BlockData bd = DataMgr.getPlayerData(p).getTeam().getTeamColor().getWool().createBlockData();
                            for (Player o_player : Main.getPlugin().getServer().getOnlinePlayers()) {
                                if (DataMgr.getPlayerData(o_player).getSettings().ShowEffect_MainWeaponInk())
                                    if (o_player.getWorld() == inkball.getWorld())
                                        if (o_player.getLocation().distanceSquared(inkball.getLocation()) < Main.PARTICLE_RENDER_DISTANCE_SQUARED)
                                            o_player.spawnParticle(org.bukkit.Particle.BLOCK_DUST, inkball.getLocation(), 0, 0, -1, 0, 1, bd);
                            }
                        }
                        
                        if(i >= tick && !addedFallVec){
                            inkball.setVelocity(fallvec);
                            addedFallVec = true;
                        }
                        if(i >= tick && i <= tick + 15)
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
