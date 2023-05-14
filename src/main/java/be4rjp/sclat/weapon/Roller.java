package be4rjp.sclat.weapon;

import be4rjp.sclat.GaugeAPI;
import be4rjp.sclat.Main;
import static be4rjp.sclat.Main.conf;

import be4rjp.sclat.Sclat;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.KasaData;
import be4rjp.sclat.data.PlayerData;
import be4rjp.sclat.data.SplashShieldData;
import be4rjp.sclat.manager.ArmorStandMgr;
import be4rjp.sclat.manager.DeathMgr;
import be4rjp.sclat.manager.PaintMgr;
import be4rjp.sclat.raytrace.BoundingBox;
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
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftSnowball;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
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
                
                if(data.getTick() >= 6 && data.isInMatch()){
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
                try{
                    PlayerData data = DataMgr.getPlayerData(p);
                    if(!data.isInMatch() || !p.isOnline())
                        cancel();
                    
                    if(data.getIsHolding() && data.getCanPaint() && data.isInMatch()){
                        if(player.getExp() <= (float)(data.getWeaponClass().getMainWeapon().getRollerNeedInk() / Gear.getGearInfluence(player, Gear.Type.MAIN_INK_EFFICIENCY_UP))){
                            player.sendTitle("", ChatColor.RED + "インクが足りません", 0, 13, 2);
                            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1F, 1.63F);
                            return;
                        }
                        p.setExp(p.getExp() - (float)(data.getWeaponClass().getMainWeapon().getRollerNeedInk() / Gear.getGearInfluence(player, Gear.Type.MAIN_INK_EFFICIENCY_UP)));
                        Vector locvec = p.getEyeLocation().getDirection();
                        Location eloc = p.getEyeLocation();
                        Vector vec = new Vector(locvec.getX(), 0, locvec.getZ()).normalize();
                        //RayTrace rayTrace1 = new RayTrace(front.toVector(), vec1);
                        //ArrayList<Vector> positions1 = rayTrace1.traverse(data.getWeaponClass().getMainWeapon().getRollerWidth(), 0.5);
                        Location front = eloc.add(vec.getX() * 2.5, -0.9, vec.getZ() * 2.5);
                        if(data.getWeaponClass().getMainWeapon().getIsHude())
                            front = eloc.add(vec.getX() * 1.5, -0.9, vec.getZ() * 1.5);
                        org.bukkit.block.data.BlockData bd = DataMgr.getPlayerData(p).getTeam().getTeamColor().getWool().createBlockData();
                        for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                            if(DataMgr.getPlayerData(target).getSettings().ShowEffect_MainWeaponInk())
                                if(target.getWorld() == p.getWorld())
                                    if(target.getLocation().distanceSquared(front) < Main.PARTICLE_RENDER_DISTANCE_SQUARED)
                                        target.spawnParticle(org.bukkit.Particle.BLOCK_DUST, front, 2, 0, 0, 0, 1, bd);
                        }
                        Vector vec1 = new Vector(vec.getZ() * -1, 0, vec.getX());
                        Vector vec2 = new Vector(vec.getZ(), 0, vec.getX() * -1);
                        
                        //筆系武器
                        if(data.getWeaponClass().getMainWeapon().getIsHude()){
                            Location position = p.getLocation();
                            PaintMgr.PaintHightestBlock(front, p, false, true);
                            p.getLocation().getWorld().spawnParticle(org.bukkit.Particle.BLOCK_DUST, position, 2, 0, 0, 0, 1, bd);
                            
                            for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                                if(DataMgr.getPlayerData(target).getSettings().ShowEffect_MainWeaponInk())
                                    if(target.getWorld() == p.getWorld())
                                        if(target.getLocation().distanceSquared(position) < Main.PARTICLE_RENDER_DISTANCE_SQUARED)
                                            target.spawnParticle(org.bukkit.Particle.BLOCK_DUST, position, 2, 0, 0, 0, 1, bd);
                            }
    
                            double maxDistSquad = 4/* 2*2 */;
                            for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                                if(!DataMgr.getPlayerData(target).isInMatch())
                                    continue;
                                if (target.getLocation().distanceSquared(position) <= maxDistSquad) {
                                    if(DataMgr.getPlayerData(p).getTeam() != DataMgr.getPlayerData(target).getTeam() && target.getGameMode().equals(GameMode.ADVENTURE)){
                
                                        double damage = DataMgr.getPlayerData(p).getWeaponClass().getMainWeapon().getRollerDamage();
                
                                        Sclat.giveDamage(p, target, damage, "killed");
                                    }
                                }
                            }
                            
                            for(Entity as : player.getWorld().getEntities()){
                                if (as instanceof ArmorStand){
                                    if(as.getCustomName() != null){
                                        if(as.getLocation().distanceSquared(position) <= maxDistSquad) {
                                            double damage = DataMgr.getPlayerData(p).getWeaponClass().getMainWeapon().getRollerDamage();
                                            ArmorStandMgr.giveDamageArmorStand((ArmorStand) as, damage, player);
                                        }
                                    }
                                }
                            }
                            p.setWalkSpeed((float)(data.getWeaponClass().getMainWeapon().getUsingWalkSpeed() * Gear.getGearInfluence(p, Gear.Type.MAIN_SPEC_UP)));
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
                                if(DataMgr.getPlayerData(target).getSettings().ShowEffect_MainWeaponInk())
                                    if(target.getWorld() == p.getWorld())
                                        if(target.getLocation().distanceSquared(position) < Main.PARTICLE_RENDER_DISTANCE_SQUARED)
                                            target.spawnParticle(org.bukkit.Particle.BLOCK_DUST, position, 2, 0, 0, 0, 1, bd);
                            }
                            
                            double maxDistSquad = 4 /* 2*2 */;
                            
                            for (Entity as : p.getWorld().getEntities()) {
                                if (as instanceof ArmorStand) {
                                    if (as.getCustomName() != null) {
                                        if (as.getLocation().distanceSquared(position) <= maxDistSquad) {
                                            try {
                                                if (as.getCustomName().equals("Kasa")) {
                                                    KasaData kasaData = DataMgr.getKasaDataFromArmorStand((ArmorStand) as);
                                                    if (DataMgr.getPlayerData(kasaData.getPlayer()).getTeam() != DataMgr.getPlayerData(p).getTeam()) {
                                                        break loop;
                                                    }
                                                } else if (as.getCustomName().equals("SplashShield")) {
                                                    SplashShieldData splashShieldData = DataMgr.getSplashShieldDataFromArmorStand((ArmorStand) as);
                                                    if (DataMgr.getPlayerData(splashShieldData.getPlayer()).getTeam() != DataMgr.getPlayerData(p).getTeam()) {
                                                        break loop;
                                                    }
                                                }
                                            }catch (Exception e){}
                                        }
                                    }
                                }
                            }
    
                            for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                                if(!DataMgr.getPlayerData(target).isInMatch())
                                    continue;
                                if (DataMgr.getPlayerData(p).getTeam() != DataMgr.getPlayerData(target).getTeam() && target.getGameMode().equals(GameMode.ADVENTURE)) {
                                    if(target.getLocation().distanceSquared(position) <= maxDistSquad){
                                        if(rayTrace1.intersects(new BoundingBox((Entity)target), data.getWeaponClass().getMainWeapon().getRollerWidth(), 0.05)){
                    
                                            double damage = DataMgr.getPlayerData(p).getWeaponClass().getMainWeapon().getRollerDamage();
                    
                                            Sclat.giveDamage(p, target, damage, "killed");
                    
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
                            }
                            
                            for(Entity as : player.getWorld().getEntities()){
                                if (as instanceof ArmorStand){
                                    if(as.getCustomName() != null){
                                        if(as.getLocation().distanceSquared(position) <= maxDistSquad) {
                                            double damage = DataMgr.getPlayerData(p).getWeaponClass().getMainWeapon().getRollerDamage();
                                            ArmorStandMgr.giveDamageArmorStand((ArmorStand) as, damage, player);
                                        }
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
                                if(DataMgr.getPlayerData(target).getSettings().ShowEffect_MainWeaponInk())
                                    if(target.getWorld() == p.getWorld())
                                        if(target.getLocation().distanceSquared(position) < Main.PARTICLE_RENDER_DISTANCE_SQUARED)
                                            target.spawnParticle(org.bukkit.Particle.BLOCK_DUST, position, 2, 0, 0, 0, 1, bd);
                            }
                            
                            double maxDistSquad = 4 /* 2*2 */;
                            
                            for (Entity as : p.getWorld().getEntities()) {
                                if (as instanceof ArmorStand) {
                                    if (as.getCustomName() != null) {
                                        if (as.getLocation().distanceSquared(position) <= maxDistSquad) {
                                            try {
                                                if (as.getCustomName().equals("Kasa")) {
                                                    KasaData kasaData = DataMgr.getKasaDataFromArmorStand((ArmorStand) as);
                                                    if (DataMgr.getPlayerData(kasaData.getPlayer()).getTeam() != DataMgr.getPlayerData(p).getTeam()) {
                                                        break loop;
                                                    }
                                                } else if (as.getCustomName().equals("SplashShield")) {
                                                    SplashShieldData splashShieldData = DataMgr.getSplashShieldDataFromArmorStand((ArmorStand) as);
                                                    if (DataMgr.getPlayerData(splashShieldData.getPlayer()).getTeam() != DataMgr.getPlayerData(p).getTeam()) {
                                                        break loop;
                                                    }
                                                }
                                            }catch (Exception e){}
                                        }
                                    }
                                }
                            }
    
                            for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                                if (!DataMgr.getPlayerData(target).isInMatch())
                                    continue;
                                if (DataMgr.getPlayerData(p).getTeam() != DataMgr.getPlayerData(target).getTeam() && target.getGameMode().equals(GameMode.ADVENTURE)) {
                                    if (rayTrace1.intersects(new BoundingBox((Entity)target), data.getWeaponClass().getMainWeapon().getRollerWidth(), 0.05)) {
                                        if (target.getLocation().distanceSquared(position) <= maxDistSquad) {
    
                                            double damage = DataMgr.getPlayerData(p).getWeaponClass().getMainWeapon().getRollerDamage();
    
                                            Sclat.giveDamage(p, target, damage, "killed");
    
                                            //AntiNoDamageTime
                                            BukkitRunnable task = new BukkitRunnable() {
                                                Player p = target;
    
                                                @Override
                                                public void run() {
                                                    target.setNoDamageTicks(0);
                                                }
                                            };
                                            task.runTaskLater(Main.getPlugin(), 1);
                                            break loop;
                                        }
                                    }
                                }
                            }
                            
                            for(Entity as : player.getWorld().getEntities()){
                                if (as instanceof ArmorStand){
                                    if(as.getLocation().distance(position) <= maxDistSquad){
                                        double damage = DataMgr.getPlayerData(p).getWeaponClass().getMainWeapon().getRollerDamage();
                                        ArmorStandMgr.giveDamageArmorStand((ArmorStand)as, damage, p);
                                    }
                                }
                            }
                        }
                        PaintMgr.PaintHightestBlock(eloc, p, false, true);
                        p.setWalkSpeed((float)(data.getWeaponClass().getMainWeapon().getUsingWalkSpeed() * Gear.getGearInfluence(p, Gear.Type.MAIN_SPEC_UP)));
                    }
                    
                }catch(Exception e){cancel();}
            }
        };
        if(DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getIsHude())
            task.runTaskTimer(Main.getPlugin(), 0, 1);
        else
            task.runTaskTimer(Main.getPlugin(), 0, 5);
    }
    
    public static void ShootPaintRunnable(Player player){
        PlayerData pdata = DataMgr.getPlayerData(player);
        BukkitRunnable task = new BukkitRunnable(){
            Player p = player;
            PlayerData data = pdata;
            @Override
            public void run(){
                if(!DataMgr.getPlayerData(p).isInMatch() || !p.isOnline()){
                    cancel();
                    return;
                }
                data.setCanRollerShoot(true);
                if(!p.getGameMode().equals(GameMode.ADVENTURE) || p.getInventory().getItemInMainHand().getItemMeta().equals(Material.AIR))
                    return;
                if(p.getExp() >= data.getWeaponClass().getMainWeapon().getNeedInk())
                    p.getWorld().playSound(p.getLocation(), Sound.ITEM_BUCKET_EMPTY, 1F, 1F);
                else
                    return;
                Vector vec = p.getLocation().getDirection().multiply(DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getShootSpeed());
                final double random = data.getWeaponClass().getMainWeapon().getHudeRandom();
                vec.add(new Vector(Math.random() * random - random/2, Math.random() * random / 4 - random/8, Math.random() * random - random/2));
                
                boolean sound = false;
                for (int i = 0; i < data.getWeaponClass().getMainWeapon().getRollerShootQuantity(); i++) {
                    boolean is;
                    if(data.getWeaponClass().getMainWeapon().getIsHude()) {
                        is = Roller.Shoot(p, vec);
                    } else {
                        is = Roller.Shoot(p, null);
                    }
                    if(is) sound = true;
                }
                if(sound){
                    p.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1F, 1.63F);
                }
                //ShootRunnable(p);
                data.setCanPaint(true);
                
            }
            
        };
        if(pdata.getCanRollerShoot()) {
            task.runTaskLater(Main.getPlugin(), pdata.getWeaponClass().getMainWeapon().getShootTick());
            pdata.setCanRollerShoot(false);
        }
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
    
    public static boolean Shoot(Player player, Vector v){
        
        if(player.getGameMode() == GameMode.SPECTATOR) return false;
        
        PlayerData data = DataMgr.getPlayerData(player);
        if(player.getExp() <= (float)(data.getWeaponClass().getMainWeapon().getNeedInk() / Gear.getGearInfluence(player, Gear.Type.MAIN_INK_EFFICIENCY_UP))){
            player.sendTitle("", ChatColor.RED + "インクが足りません", 0, 13, 2);
            return true;
        }
        player.setExp(player.getExp() - (float)(data.getWeaponClass().getMainWeapon().getNeedInk() / Gear.getGearInfluence(player, Gear.Type.MAIN_INK_EFFICIENCY_UP)));
        Snowball ball = player.launchProjectile(Snowball.class);
        ((CraftSnowball)ball).getHandle().setItem(CraftItemStack.asNMSCopy(new ItemStack(DataMgr.getPlayerData(player).getTeam().getTeamColor().getWool())));
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
                    vec.add(new Vector(Math.random() * random / 4 - random/8, Math.random() * random * 1.8 - random * 0.8, Math.random() * random / 4 - random/8));
                if(!data.getWeaponClass().getMainWeapon().getCanTatehuri())
                    vec.add(new Vector(Math.random() * random - random/2, Math.random() * random / 4 - random/8, Math.random() * random - random/2));
                //player.sendMessage(String.valueOf(player.isOnGround()));
            }
        }else{
            vec.add(new Vector(Math.random() * random - random/2, Math.random() * random / 4 - random/8, Math.random() * random - random/2));
        }
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
            Snowball inkball = ball;
            Player p = player;
            boolean addedFallVec = false;
            Vector fallvec = new Vector(inkball.getVelocity().getX(), inkball.getVelocity().getY()  , inkball.getVelocity().getZ()).multiply(DataMgr.getPlayerData(p).getWeaponClass().getMainWeapon().getShootSpeed()/17);
            @Override
            public void run(){
                inkball = DataMgr.getMainSnowballNameMap().get(name);
                
                if(!inkball.equals(ball)){
                    i+=DataMgr.getSnowballHitCount(name) - 1;
                    DataMgr.setSnowballHitCount(name, 0);
                }
                if(i != 0) {
                    for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                        if (target.getWorld() != p.getWorld()) continue;
                        if (!DataMgr.getPlayerData(target).getSettings().ShowEffect_MainWeaponInk())
                            continue;
                        org.bukkit.block.data.BlockData bd = DataMgr.getPlayerData(p).getTeam().getTeamColor().getWool().createBlockData();
                        target.spawnParticle(org.bukkit.Particle.BLOCK_DUST, inkball.getLocation(), 1, 0, 0, 0, 1, bd);
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
        
        return false;
    }
    
}