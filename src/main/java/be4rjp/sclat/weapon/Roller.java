
package be4rjp.sclat.weapon;

import be4rjp.sclat.GaugeAPI;
import be4rjp.sclat.Main;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import be4rjp.sclat.manager.ArmorStandMgr;
import be4rjp.sclat.manager.DamageMgr;
import be4rjp.sclat.manager.DeathMgr;
import be4rjp.sclat.manager.PaintMgr;
import be4rjp.sclat.raytrace.RayTrace;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 *
 * @author Be4rJP
 */
public class Roller {
    public static void HoldRunnable(Player player){
        BukkitRunnable task = new BukkitRunnable(){
            Player p = player;
            @Override
            public void run(){
                PlayerData data = DataMgr.getPlayerData(p);
                
                data.setTick(data.getTick() + 1);
                
                if(!data.isInMatch() || !p.isOnline()){
                    cancel();
                    return;
                }
                
                if(data.getTick() >= 5 && data.isInMatch()){
                    data.setTick(7);
                    data.setIsHolding(false);
                    data.setCanPaint(false);
                    data.setCanShoot(true);
                }
                
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 1);
    }
    
    public static void RollPaintRunnable(Player player){
        BukkitRunnable task = new BukkitRunnable(){
            Player p = player;
            @Override
            public void run(){
                PlayerData data = DataMgr.getPlayerData(p);
                if(data.getIsHolding() && data.getCanPaint()){
                    if(player.getExp() <= data.getWeaponClass().getMainWeapon().getNeedInk()){
                        player.sendTitle("", ChatColor.RED + "インクが足りません", 0, 13, 2);
                        return;
                    }
                    p.setExp(p.getExp() - data.getWeaponClass().getMainWeapon().getRollerNeedInk());
                    Vector locvec = p.getEyeLocation().getDirection();
                    Location eloc = p.getEyeLocation();
                    Vector vec = new Vector(locvec.getX(), 0, locvec.getZ()).normalize();
                    //RayTrace rayTrace1 = new RayTrace(front.toVector(), vec1);
                    //ArrayList<Vector> positions1 = rayTrace1.traverse(data.getWeaponClass().getMainWeapon().getRollerWidth(), 0.5);
                    Location front = eloc.add(vec.getX() * 3.3, -0.9, vec.getZ() * 3.3);
                    org.bukkit.block.data.BlockData bd = DataMgr.getPlayerData(p).getTeam().getTeamColor().getWool().createBlockData();
                    p.getLocation().getWorld().spawnParticle(org.bukkit.Particle.BLOCK_DUST, front, 2, 0, 0, 0, 1, bd);
                    Vector vec1 = new Vector(vec.getZ() * -1, 0, vec.getX());
                    Vector vec2 = new Vector(vec.getZ(), 0, vec.getX() * -1);
                    
                    //筆系武器
                    if(data.getWeaponClass().getMainWeapon().getIsHude()){
                        Location position = p.getLocation();
                        PaintMgr.PaintHightestBlock(position, p, false, true);
                        p.getLocation().getWorld().spawnParticle(org.bukkit.Particle.BLOCK_DUST, position, 2, 0, 0, 0, 1, bd);
                        
                        for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                            if(DataMgr.getPlayerData(target).getSettings().ShowEffect_RollerRoll())
                                target.spawnParticle(org.bukkit.Particle.BLOCK_DUST, position, 2, 0, 0, 0, 1, bd);
                        }
                        
                        double maxDist = 2;
                        for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                            if(!DataMgr.getPlayerData(target).isInMatch())
                                continue;
                            if (target.getLocation().distance(position) <= maxDist) {
                                if(DataMgr.getPlayerData(p).getTeam() != DataMgr.getPlayerData(target).getTeam() && target.getGameMode().equals(GameMode.ADVENTURE)){
                                    
                                    double damage = DataMgr.getPlayerData(p).getWeaponClass().getMainWeapon().getRollerDamage();
                                    
                                    if(target.getHealth() + DataMgr.getPlayerData(target).getArmor() > damage){
                                        DamageMgr.SclatGiveDamage(target, damage);
                                        PaintMgr.Paint(target.getLocation(), p, true);
                                        p.setVelocity(p.getEyeLocation().getDirection().multiply(-0.5));
                                    }else{
                                        target.setGameMode(GameMode.SPECTATOR);
                                        DeathMgr.PlayerDeathRunnable(target, p, "killed");
                                        PaintMgr.Paint(target.getLocation(), p, true);
                                        
                                    }
                                }
                            }
                        }
                        
                        for(Entity as : player.getWorld().getEntities()){
                            if (as.getLocation().distance(position) <= maxDist){
                                if(as instanceof ArmorStand){
                                    double damage = DataMgr.getPlayerData(p).getWeaponClass().getMainWeapon().getRollerDamage();
                                    ArmorStandMgr.giveDamageArmorStand((ArmorStand)as, damage, player);
                                }
                            }
                        }
                        p.setWalkSpeed(data.getWeaponClass().getMainWeapon().getUsingWalkSpeed());
                        return;
                    }
                    
                    //法線ベクトルでロール部分の取得
                    
                    
                    RayTrace rayTrace1 = new RayTrace(front.toVector(), vec1);
                    ArrayList<Vector> positions1 = rayTrace1.traverse(data.getWeaponClass().getMainWeapon().getRollerWidth(), 0.5);
                    loop : for(int i = 0; i < positions1.size();i++){
                        Location position = positions1.get(i).toLocation(p.getLocation().getWorld());
                        Block block = p.getLocation().getWorld().getBlockAt(position);
                        if(!block.getType().equals(Material.AIR))
                            break loop;
                        PaintMgr.PaintHightestBlock(position, p, false, true);
                        p.getLocation().getWorld().spawnParticle(org.bukkit.Particle.BLOCK_DUST, position, 2, 0, 0, 0, 1, bd);
                        
                        for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                            if(DataMgr.getPlayerData(target).getSettings().ShowEffect_RollerRoll())
                                target.spawnParticle(org.bukkit.Particle.BLOCK_DUST, position, 2, 0, 0, 0, 1, bd);
                        }
                        
                        double maxDist = 2;
                        for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                            if(!DataMgr.getPlayerData(target).isInMatch())
                                continue;
                            if (target.getLocation().distance(position) <= maxDist) {
                                if(DataMgr.getPlayerData(p).getTeam() != DataMgr.getPlayerData(target).getTeam() && target.getGameMode().equals(GameMode.ADVENTURE)){
                                    
                                    double damage = DataMgr.getPlayerData(p).getWeaponClass().getMainWeapon().getRollerDamage();
                                    
                                    if(target.getHealth() + DataMgr.getPlayerData(target).getArmor() > damage){
                                        DamageMgr.SclatGiveDamage(target, damage);
                                        PaintMgr.Paint(target.getLocation(), p, true);
                                        p.setVelocity(p.getEyeLocation().getDirection().multiply(-0.5));
                                    }else{
                                        target.setGameMode(GameMode.SPECTATOR);
                                        DeathMgr.PlayerDeathRunnable(target, p, "killed");
                                        PaintMgr.Paint(target.getLocation(), p, true);
                                        
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
                                    double damage = DataMgr.getPlayerData(p).getWeaponClass().getMainWeapon().getRollerDamage();
                                    ArmorStandMgr.giveDamageArmorStand((ArmorStand)as, damage, player);
                                }
                            }
                        }
                    }
                    
                    
                    RayTrace rayTrace2 = new RayTrace(front.toVector(), vec2);
                    ArrayList<Vector> positions2 = rayTrace2.traverse(data.getWeaponClass().getMainWeapon().getRollerWidth(), 0.5);
                    loop : for(int i = 0; i < positions2.size();i++){
                        Location position = positions2.get(i).toLocation(p.getLocation().getWorld());
                        Block block = p.getLocation().getWorld().getBlockAt(position);
                        if(!block.getType().equals(Material.AIR))
                            break loop;
                        PaintMgr.PaintHightestBlock(position, p, false, true);
                        for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                            if(DataMgr.getPlayerData(target).getSettings().ShowEffect_RollerRoll())
                                target.spawnParticle(org.bukkit.Particle.BLOCK_DUST, position, 2, 0, 0, 0, 1, bd);
                        }
                        
                        double maxDist = 2;
                        for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                            if(!DataMgr.getPlayerData(target).isInMatch())
                                continue;
                            if (target.getLocation().distance(position) <= maxDist) {
                                if(DataMgr.getPlayerData(p).getTeam() != DataMgr.getPlayerData(target).getTeam() && target.getGameMode().equals(GameMode.ADVENTURE)){
                                    
                                    double damage = DataMgr.getPlayerData(p).getWeaponClass().getMainWeapon().getRollerDamage();
                                    
                                    if(target.getHealth() + DataMgr.getPlayerData(target).getArmor() > damage){
                                        DamageMgr.SclatGiveDamage(target, damage);
                                        PaintMgr.Paint(target.getLocation(), p, true);
                                        p.setVelocity(p.getEyeLocation().getDirection().multiply(-0.5));
                                    }else{
                                        target.setGameMode(GameMode.SPECTATOR);
                                        DeathMgr.PlayerDeathRunnable(target, p, "killed");
                                        PaintMgr.Paint(target.getLocation(), p, true);
                                        
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
                                    double damage = DataMgr.getPlayerData(p).getWeaponClass().getMainWeapon().getRollerDamage();
                                    ArmorStandMgr.giveDamageArmorStand((ArmorStand)as, damage, p);
                                }     
                            }
                        }
                    }
                    PaintMgr.PaintHightestBlock(eloc, p, false, true);
                    p.setWalkSpeed(data.getWeaponClass().getMainWeapon().getUsingWalkSpeed());
                }
                
                if(!data.isInMatch())
                    cancel();
                
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 5);
    }
    
    public static void ShootPaintRunnable(Player player){
        PlayerData pdata = DataMgr.getPlayerData(player);
        BukkitRunnable task = new BukkitRunnable(){
            Player p = player;
            PlayerData data = pdata;
            @Override
            public void run(){
                p.playSound(p.getLocation(), Sound.ITEM_BUCKET_EMPTY, 1F, 1F);
                if(!p.getGameMode().equals(GameMode.ADVENTURE) || p.getInventory().getItemInMainHand().getItemMeta().equals(Material.AIR))
                    return;
                if(data.getCanRollerShoot()){
                    data.setCanRollerShoot(false);
                    Vector vec = player.getLocation().getDirection().multiply(DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getShootSpeed());
                    final double random = data.getWeaponClass().getMainWeapon().getHudeRandom();
                    vec.add(new Vector(Math.random() * random - random/2, Math.random() * random / 4 - random/8, Math.random() * random - random/2));
                    for (int i = 0; i < data.getWeaponClass().getMainWeapon().getRollerShootQuantity(); i++) {
                        if(data.getWeaponClass().getMainWeapon().getIsHude())
                            Roller.Shoot(p, vec);
                        else
                            Roller.Shoot(p, null);
                    }
                    ShootRunnable(p);
                    data.setCanPaint(true);
                }

            }
        
        };
        task.runTaskLater(Main.getPlugin(), pdata.getWeaponClass().getMainWeapon().getShootTick());
    }
    
    public static void ShootRunnable(Player player){
        PlayerData data = DataMgr.getPlayerData(player);
        BukkitRunnable task = new BukkitRunnable(){
            @Override
            public void run(){
                data.setCanRollerShoot(true);
            }
        };
        task.runTaskLater(Main.getPlugin(), data.getWeaponClass().getMainWeapon().getShootTick());
    }
    
    public static void Shoot(Player player, Vector v){
        PlayerData data = DataMgr.getPlayerData(player);
        if(player.getExp() <= data.getWeaponClass().getMainWeapon().getNeedInk()){
            player.sendTitle("", ChatColor.RED + "インクが足りません", 0, 13, 2);
            return;
        }
        player.setExp(player.getExp() - data.getWeaponClass().getMainWeapon().getNeedInk());
        Snowball ball = player.launchProjectile(Snowball.class);
        Vector vec = player.getLocation().getDirection().multiply(DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getShootSpeed());
        if(v != null)
            vec = v;
        double random = DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getRandom();
        int distick = DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getDistanceTick();
        if(!data.getWeaponClass().getMainWeapon().getIsHude()){
            if(player.isOnGround())
                vec.add(new Vector(Math.random() * random - random/2, Math.random() * random / 4 - random/8, Math.random() * random - random/2));
            if(!player.isOnGround()){
                if(data.getWeaponClass().getMainWeapon().getCanTatehuri())
                    vec.add(new Vector(Math.random() * random / 4 - random/8, Math.random() * random, Math.random() * random / 4 - random/8));
                if(!data.getWeaponClass().getMainWeapon().getCanTatehuri())
                    vec.add(new Vector(Math.random() * random - random/2, Math.random() * random / 4 - random/8, Math.random() * random - random/2));
            }
        }else{
            vec.add(new Vector(Math.random() * random - random/2, Math.random() * random / 4 - random/8, Math.random() * random - random/2));
        }
        ball.setVelocity(vec);
        ball.setShooter(player);
        BukkitRunnable task = new BukkitRunnable(){
            int i = 0;
            int tick = distick;
            Snowball inkball = ball;
            Player p = player;
            Vector fallvec = new Vector(inkball.getVelocity().getX(), inkball.getVelocity().getY()  , inkball.getVelocity().getZ()).multiply(DataMgr.getPlayerData(p).getWeaponClass().getMainWeapon().getShootSpeed()/17);
            @Override
            public void run(){
                for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                    if(!DataMgr.getPlayerData(target).getSettings().ShowEffect_RollerShot())
                        continue;
                    org.bukkit.block.data.BlockData bd = DataMgr.getPlayerData(p).getTeam().getTeamColor().getWool().createBlockData();
                    target.spawnParticle(org.bukkit.Particle.BLOCK_DUST, inkball.getLocation(), 1, 0, 0, 0, 1, bd);
                }

                if(i == tick)
                    inkball.setVelocity(fallvec);
                if(i >= tick)
                    inkball.setVelocity(inkball.getVelocity().add(new Vector(0, -0.1, 0)));
                if(i != tick)
                    PaintMgr.PaintHightestBlock(inkball.getLocation(), p, true, true);
                if(inkball.isDead())
                    cancel();

                i++;
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 1);
        BukkitRunnable delay = new BukkitRunnable(){
            Player p = player;
            @Override
            public void run(){
                //p.getInventory().setItem(0, DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getWeaponIteamStack());
                //DataMgr.getPlayerData(p).setCanShoot(true);
            }
        };
        delay.runTaskLater(Main.getPlugin(), DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getShootTick());
    }
    
}
