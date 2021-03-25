
package be4rjp.sclat.weapon.spweapon;

import be4rjp.blockstudio.BlockStudio;
import be4rjp.blockstudio.api.BSObject;
import be4rjp.blockstudio.api.BlockStudioAPI;
import be4rjp.blockstudio.file.ObjectData;
import be4rjp.sclat.Main;
import be4rjp.sclat.Sclat;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.manager.ArmorStandMgr;
import be4rjp.sclat.manager.SPWeaponMgr;
import be4rjp.sclat.manager.WeaponClassMgr;
import be4rjp.sclat.raytrace.RayTrace;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
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
public class MegaLaser {
    public static void MegaLaserRunnable(Player player){
    
        BlockStudioAPI api = BlockStudio.getBlockStudioAPI();
        ObjectData objectData = api.getObjectData("mega");
        BSObject bsObject = api.createObjectFromObjectData("mega", player.getLocation(), objectData, false);
        bsObject.startTaskAsync(40);
        bsObject.move();
        
        BukkitRunnable task = new BukkitRunnable(){
            Player p = player;
            int c = 0;
            @Override
            public void run(){
                if(c == 0){
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100000, 2));
                    p.getInventory().clear();
                    p.getInventory().clear();
                    player.updateInventory();
                    DataMgr.getPlayerData(p).setIsUsingSP(true);
                    ItemStack item = new ItemStack(Material.SHULKER_SHELL);
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName("狙って右クリックで発射");
                    item.setItemMeta(meta);
                    for (int count = 0; count < 9; count++){
                        player.getInventory().setItem(count, item);
                    }
                    player.updateInventory();
    
                    DataMgr.getPlayerData(p).setIsUsingMM(true);
                }
                
                
                Vector direction = player.getEyeLocation().getDirection();
                Vector xz = new Vector(direction.getX(), 0, direction.getZ());
                if(xz.lengthSquared() == 0.0) xz = new Vector(1, 0, 1);
                Vector normXZ = xz.normalize();
                
                Location objectLoc = player.getLocation().add(normXZ.getX(), 0.6, normXZ.getZ());
                bsObject.setBaseLocation(objectLoc);
                bsObject.setDirection(player.getEyeLocation().getDirection());
                bsObject.move();
    
                if(!DataMgr.getPlayerData(p).getIsUsingMM() || c == 400){
                    DataMgr.getPlayerData(p).setIsUsingMM(false);
                    MegaLaserShootRunnable(player, bsObject);
                    WeaponClassMgr.setWeaponClass(p);
                    if(p.hasPotionEffect(PotionEffectType.SLOW))
                        p.removePotionEffect(PotionEffectType.SLOW);
                    cancel();
                }
                
                
                if(!p.isOnline() || !DataMgr.getPlayerData(p).isInMatch()){
                    if(p.hasPotionEffect(PotionEffectType.SLOW))
                        p.removePotionEffect(PotionEffectType.SLOW);
                    bsObject.remove();
                    cancel();
                }
                
                c++;
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 1);
    }
    
    
    public static void MegaLaserShootRunnable(Player player, BSObject bsObject){
    
        Vector direction = player.getEyeLocation().getDirection().normalize();
        Vector xz = new Vector(direction.getX(), 0, direction.getZ());
        if(xz.lengthSquared() == 0.0) xz = new Vector(1, 0, 1);
        Vector normXZ = xz.normalize();
        Location objectLoc = player.getLocation().add(normXZ.getX(), 0.6, normXZ.getZ());
        
        RayTrace rayTrace = new RayTrace(objectLoc.toVector(), direction);
        ArrayList<Vector> positions = rayTrace.traverse(300, 1);
    
        SPWeaponMgr.setSPCoolTimeAnimation(player, 130);
        
        BukkitRunnable task = new BukkitRunnable() {
            Player p = player;
            int c = 0;
    
            @Override
            public void run() {
    
                //終了処理
                if(c == 13 || !DataMgr.getPlayerData(p).isInMatch() || !p.isOnline()) {
                    DataMgr.getPlayerData(p).setIsUsingSP(false);
                    for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                        Sclat.sendWorldBorderWarningClearPacket(target);
                    }
                    bsObject.remove();
                    cancel();
                }
                
                
                //音
                if(c <= 3)
                    objectLoc.getWorld().playSound(objectLoc, Sound.ENTITY_WITHER_SHOOT, 0.3F, 0.5F);
                else
                    objectLoc.getWorld().playSound(objectLoc, Sound.ENTITY_WITHER_SHOOT, 0.3F, 0.6F);
                
                Vector axis = direction.clone().rotateAroundAxis(new Vector(0, 1, 0), 90);
                Vector plusVector = direction.clone().rotateAroundAxis(axis, -90);

                List<Vector> plusList = new ArrayList<>();
                for(int angle = 0; angle <= 360; angle+=15){
                    plusList.add(plusVector.clone().rotateAroundAxis(direction, angle));
                }
                
                
                //動作処理
                for(int i = 1; i < positions.size();i++){
                    
                    if(c % 2 == 0)
                        if(i % 2 != 0) continue;
                    if(c % 2 != 0)
                        if(i % 2 == 0) continue;

                    int r = 1;
                    
                    if(i == 2)
                        r = 1;
                    if(i == 3)
                        r = 3;
                    if(i >= 4)
                        r = 5;
                    
                    Location position = positions.get(i).toLocation(objectLoc.getWorld());

                    for(Vector plus : plusList) {
                        Location eloc = position.clone().add(plus.clone().multiply(r));
                        for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                            if (p.getWorld() != target.getWorld())
                                continue;
                            if (eloc.distance(target.getLocation()) < Main.PARTICLE_RENDER_DISTANCE) {
                                if (DataMgr.getPlayerData(target).getSettings().ShowEffect_SPWeaponRegion()) {
                                    Particle.DustOptions dustOptions = new Particle.DustOptions(DataMgr.getPlayerData(p).getTeam().getTeamColor().getBukkitColor(), c <= 3 ? 1 : 2);
                                    target.spawnParticle(Particle.REDSTONE, eloc, 1, 0, 0, 0, 10, dustOptions);
                                }
                            }
                        }
                    }
                    
                    //音
                    if(i > 5 && i % 5 == 0){
                        if(c <= 3)
                            position.getWorld().playSound(position, Sound.ENTITY_WITHER_SHOOT, 0.3F, 0.5F);
                        else
                            position.getWorld().playSound(position, Sound.ENTITY_WITHER_SHOOT, 0.3F, 0.6F);
                    }
                    
                    //画面エフェクト
                    double maxDist = 5;
                    List<Player> list = new ArrayList<>();
                    for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                        if(!DataMgr.getPlayerData(target).isInMatch())
                            continue;
                        if(target.getWorld() != p.getWorld())
                            continue;
                        if(DataMgr.getPlayerData(target).getTeam() == DataMgr.getPlayerData(p).getTeam())
                            continue;
                        if (target.getLocation().distance(position) <= maxDist) {
                            list.add(target);
                        }
                    }
                    for (Player target : Main.getPlugin().getServer().getOnlinePlayers()){
                        if(list.contains(target))
                            Sclat.sendWorldBorderWarningPacket(target);
                        else
                            Sclat.sendWorldBorderWarningClearPacket(target);
                    }
                    
                    //攻撃判定
                    if(i > 5 && c > 3){
                        double damage = 7.5;
                        
                        for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                            if(!DataMgr.getPlayerData(target).isInMatch())
                                continue;
                            if(target.getWorld() != p.getWorld())
                                continue;
                            if (target.getLocation().distance(position) <= maxDist) {
                                if(DataMgr.getPlayerData(p).getTeam() != DataMgr.getPlayerData(target).getTeam() && target.getGameMode().equals(GameMode.ADVENTURE)){
                                    Sclat.giveDamage(p, target, damage, "spWeapon");
                                    
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
    
                        for(Entity as : player.getWorld().getEntities()){
                            if (as.getLocation().distance(position) <= maxDist){
                                if(as instanceof ArmorStand){
                                    ArmorStandMgr.giveDamageArmorStand((ArmorStand)as, damage, player);
                                }
                            }
                        }
                    }
                }
                
                c++;
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 10);
    }
}
