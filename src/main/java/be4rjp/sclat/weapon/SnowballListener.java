
package be4rjp.sclat.weapon;

import be4rjp.sclat.Main;
import be4rjp.sclat.Sclat;
import be4rjp.sclat.ServerType;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.KasaData;
import be4rjp.sclat.data.SplashShieldData;
import be4rjp.sclat.manager.*;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.server.v1_14_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_14_R1.PlayerConnection;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftSnowball;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 *
 * @author Be4rJP
 */
public class SnowballListener implements Listener {
    @EventHandler
    public void onBlockHit(ProjectileHitEvent event){
    
        if(Main.type == ServerType.LOBBY) return;
        
        //EntityDamage
        if(event.getHitEntity() != null){
            if(DataMgr.getSnowballIsHitMap().containsKey((Snowball)event.getEntity())){
                DataMgr.setSnowballIsHit((Snowball)event.getEntity(), true);
                if(event.getEntity().getCustomName() != null) {
                    if(event.getEntity().getCustomName().equals("JetPack") || event.getEntity().getCustomName().equals("SuperShot")) {
                        Projectile projectile = (Projectile) event.getEntity();
                        Player shooter = (Player) projectile.getShooter();
                        if (event.getHitEntity() instanceof Player) {
                            Player target = (Player) event.getHitEntity();
                            if (DataMgr.getPlayerData(shooter).getTeam() != DataMgr.getPlayerData(target).getTeam() && target.getGameMode().equals(GameMode.ADVENTURE)) {
                                if (!DataMgr.getPlayerData(shooter).getIsUsingSP())
                                    SPWeaponMgr.addSPCharge(shooter);
                        
                                Sclat.giveDamage(shooter, target, 30, "spWeapon");
                            }
                        } else if (event.getHitEntity() instanceof ArmorStand) {
                            ArmorStand as = (ArmorStand) event.getHitEntity();
                            ArmorStandMgr.giveDamageArmorStand(as, 20, shooter);
                        }
                    }
                }
            }else{
                Projectile projectile = (Projectile)event.getEntity();
                Player shooter = (Player)projectile.getShooter();
                if(event.getHitEntity() instanceof Player){
                    Player target = (Player)event.getHitEntity();
                    if(DataMgr.getPlayerData(shooter).getTeam() != DataMgr.getPlayerData(target).getTeam() && target.getGameMode().equals(GameMode.ADVENTURE)){
                        if(!DataMgr.getPlayerData(shooter).getIsUsingSP())
                            SPWeaponMgr.addSPCharge(shooter);
                        if(DataMgr.getPlayerData(target).getArmor() > 0){
                            target.getWorld().playSound(target.getLocation(), Sound.ENTITY_SPLASH_POTION_BREAK, 1F, 1.5F);
                            if(DataMgr.getPlayerData(target).getArmor() > 10000) {
                                Vector vec = projectile.getVelocity();
                                Vector v = new Vector(vec.getX(), 0, vec.getZ()).normalize();
                                target.setVelocity(new Vector(v.getX(), 0.2, v.getZ()).multiply(0.33));
                            }
                        }
                        if(projectile.getCustomName() != null){
                            if(projectile.getCustomName().equals("Sprinkler") || projectile.getCustomName().equals("Amehurasi")){
                                if(projectile.getCustomName().equals("Sprinkler"))
                                    Sclat.giveDamage(shooter, target, 4, "subWeapon");
                                else if(projectile.getCustomName().equals("Amehurasi"))
                                    Sclat.giveDamage(shooter, target, 4, "spWeapon");
                                PaintMgr.Paint(target.getLocation(), shooter, true);
                            }
                    
                            if(projectile.getCustomName().equals("SuperShot")){
                                shooter.playSound(shooter.getLocation(), Sound.ENTITY_PLAYER_HURT, 0.5F, 1F);
                                Sclat.giveDamage(shooter, target, 20, "spWeapon");
                            }
                    
                            if(DataMgr.mws.contains(projectile.getCustomName())){
                                if(DataMgr.tsl.contains(projectile.getCustomName())) {
                                    if(!projectile.getCustomName().contains(":")) {
                                        shooter.playSound(shooter.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1.2F, 1.3F);
                                        shooter.spawnParticle(Particle.FLASH, projectile.getLocation(), 1, 0.1, 0.1, 0.1, 0.1);
                                    }else{
                                        String args[] = projectile.getCustomName().split(":");
                                        switch(args[1]){
                                            case "Burst": {
                                                if(DataMgr.oto.containsKey(args[2])){
                                                    DataMgr.oto.put(args[2], DataMgr.oto.get(args[2]) + 1);
                                                }else{
                                                    DataMgr.oto.put(args[2], 1);
                                                }
                                                break;
                                            }
                                        }
                                
                                        if(DataMgr.oto.get(args[2]) == DataMgr.getPlayerData(shooter).getWeaponClass().getMainWeapon().getRollerShootQuantity()){
                                            shooter.playSound(shooter.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1.2F, 1.3F);
                                            shooter.spawnParticle(Particle.FLASH, projectile.getLocation(), 1, 0.1, 0.1, 0.1, 0.1);
                                        }
                                    }
                                }
                                shooter.playSound(shooter.getLocation(), Sound.ENTITY_PLAYER_HURT, 0.5F, 1F);
                                
                                double damage = DataMgr.getPlayerData(shooter).getWeaponClass().getMainWeapon().getDamage();
                                String type = DataMgr.getPlayerData(shooter).getWeaponClass().getMainWeapon().getWeaponType();
    
                                if(!type.equals("Blaster")) {
                                    double ticksLived = (double) projectile.getTicksLived() * 1.2;
                                    if(ticksLived > 20.0)
                                        ticksLived = 20.0;
                                    damage -= damage * (ticksLived / 100);
                                }
                                
                                Sclat.giveDamage(shooter, target, damage * Gear.getGearInfluence(shooter, Gear.Type.MAIN_SPEC_UP), "killed");
                            }
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
                
                        Timer timer = new Timer(false);
                        TimerTask t = new TimerTask(){
                            Player p = target;
                            @Override
                            public void run(){
                                try{
                                    target.setNoDamageTicks(0);
                                    timer.cancel();
                                }catch(Exception e){
                                    timer.cancel();
                                }
                            }
                        };
                        timer.schedule(t, 25);
                
                    }
                }else if(event.getHitEntity() instanceof ArmorStand){
                    ArmorStand as = (ArmorStand) event.getHitEntity();
                    if(projectile.getCustomName() != null){
                        if(DataMgr.mws.contains(projectile.getCustomName())) {
                            if (DataMgr.tsl.contains(projectile.getCustomName())) {
                                if (Sclat.isNumber(as.getCustomName())) {
                                    if (!as.getCustomName().equals("21") && !as.getCustomName().equals("100")) {
                                        if (as.isVisible()) {
                                            if(!projectile.getCustomName().contains(":")) {
                                                shooter.playSound(shooter.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1.2F, 1.3F);
                                                shooter.spawnParticle(Particle.FLASH, projectile.getLocation(), 1, 0.1, 0.1, 0.1, 0.1);
                                            }else{
                                                String args[] = projectile.getCustomName().split(":");
                                                switch(args[1]){
                                                    case "Burst": {
                                                        if(DataMgr.oto.containsKey(args[2])){
                                                            DataMgr.oto.put(args[2], DataMgr.oto.get(args[2]) + 1);
                                                        }else{
                                                            DataMgr.oto.put(args[2], 1);
                                                        }
                                                        break;
                                                    }
                                                }
                                        
                                                if(DataMgr.oto.get(args[2]) == DataMgr.getPlayerData(shooter).getWeaponClass().getMainWeapon().getRollerShootQuantity()){
                                                    shooter.playSound(shooter.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1.2F, 1.3F);
                                                    shooter.spawnParticle(Particle.FLASH, projectile.getLocation(), 1, 0.1, 0.1, 0.1, 0.1);
                                                }
                                            }
                                            //shooter.playSound(shooter.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1.2F, 1.3F);
                                        }
                                    }
                                }
                            }
                        }
                
                        if(projectile.getCustomName().equals("SuperShot")){
                            ArmorStandMgr.giveDamageArmorStand(as, 20, shooter);
                            return;
                        }
                        if(projectile.getCustomName().equals("JetPack")){
                            ArmorStandMgr.giveDamageArmorStand(as, 20, shooter);
                            return;
                        }
                    }
                    if(as.getCustomName() != null) {
                        if (!as.getCustomName().equals("Path") && !as.getCustomName().equals("21") && !as.getCustomName().equals("100") && !as.getCustomName().equals("SplashShield") && !as.getCustomName().equals("Kasa")) {
                            shooter.playSound(shooter.getLocation(), Sound.ENTITY_PLAYER_HURT, 0.5F, 1F);
                        }
                    }
                    
                    double damage = DataMgr.getPlayerData(shooter).getWeaponClass().getMainWeapon().getDamage();
                    String type = DataMgr.getPlayerData(shooter).getWeaponClass().getMainWeapon().getWeaponType();
    
                    if(!type.equals("Blaster")) {
                        double ticksLived = (double) projectile.getTicksLived() * 1.2;
                        if(ticksLived > 20.0)
                            ticksLived = 20.0;
                        damage -= damage * (ticksLived / 100);
                    }
                    ArmorStandMgr.giveDamageArmorStand(as, damage * Gear.getGearInfluence(shooter, Gear.Type.MAIN_SPEC_UP), shooter);
                }
            }
        }
        
        
        //Other
        if(DataMgr.getSnowballIsHitMap().containsKey((Snowball)event.getEntity())){
            if(event.getEntity().getCustomName() == null){
                DataMgr.setSnowballIsHit((Snowball)event.getEntity(), true);
            }else{
                if(event.getHitBlock() != null){
                    DataMgr.setSnowballIsHit((Snowball)event.getEntity(), true);
                }
                if(event.getHitEntity() != null){
                    if(event.getEntity().getCustomName() == null){
                        DataMgr.setSnowballIsHit((Snowball)event.getEntity(), true);
                    }else{
                        if(event.getEntity().getCustomName().equals("JetPack")){
                            DataMgr.setSnowballIsHit((Snowball)event.getEntity(), true);
                            return;
                        }
                        if(event.getEntity().getCustomName().equals("SuperShot"))
                            return;
                        Snowball ball = (Snowball)event.getEntity();
                        Vector vec = ball.getVelocity();
                        Location loc = ball.getLocation();
                        Snowball ball2 = (Snowball)ball.getWorld().spawnEntity(new Location(loc.getWorld(), loc.getX() + vec.getX(), loc.getY() + vec.getY(), loc.getZ() + vec.getZ()), EntityType.SNOWBALL);
                        ball2.setVelocity(vec);
                        ball2.setCustomName(ball.getCustomName());
                        DataMgr.getSnowballNameMap().put(ball.getCustomName(), ball2);
                        DataMgr.setSnowballIsHit(ball2, false);
                        for (Player o_player : Main.getPlugin().getServer().getOnlinePlayers()) {
                            PlayerConnection connection = ((CraftPlayer) o_player).getHandle().playerConnection;
                            connection.sendPacket(new PacketPlayOutEntityDestroy(ball2.getEntityId()));
                        }
                    }
                }
            }
            return;
        }
        
        if(event.getEntity() instanceof Snowball){
            if(event.getHitEntity() != null){
                if(event.getEntity().getCustomName() != null){
                    if(DataMgr.getMainSnowballNameMap().containsKey(event.getEntity().getCustomName())){
                        if(event.getHitEntity() instanceof ArmorStand){
                            if(event.getHitEntity().getCustomName() != null){
                                if(event.getHitEntity().getCustomName().equals("SplashShield")){
                                    SplashShieldData ssdata = DataMgr.getSplashShieldDataFromArmorStand((ArmorStand)event.getHitEntity());
                                    Snowball ball = (Snowball)event.getEntity();
                                    Player shooter = (Player)ball.getShooter();
                                    //if(DataMgr.getPlayerData(ssdata.getPlayer()).getTeam() != DataMgr.getPlayerData(shooter).getTeam())
                                        //ssdata.setDamage(ssdata.getDamage() + DataMgr.getPlayerData(shooter).getWeaponClass().getMainWeapon().getDamage());
                                    if(DataMgr.getPlayerData(ssdata.getPlayer()).getTeam() != DataMgr.getPlayerData(shooter).getTeam())
                                        return;
                                    Vector vec = ball.getVelocity();
                                    Location loc = ball.getLocation();
                                    Snowball ball2 = (Snowball)ball.getWorld().spawnEntity(new Location(loc.getWorld(), loc.getX() + vec.getX(), loc.getY() + vec.getY(), loc.getZ() + vec.getZ()), EntityType.SNOWBALL);
                                    ((CraftSnowball)ball2).getHandle().setItem(CraftItemStack.asNMSCopy(new ItemStack(DataMgr.getPlayerData(shooter).getTeam().getTeamColor().getWool())));
                                    ball2.setShooter(shooter);
                                    ball2.setVelocity(vec);
                                    ball2.setCustomName(ball.getCustomName());
                                    //if(!DataMgr.getPlayerData(shooter).getWeaponClass().getMainWeapon().getWeaponType().equals("Blaster"))
                                        DataMgr.addSnowballHitCount(ball.getCustomName());
                                    DataMgr.getMainSnowballNameMap().put(ball.getCustomName(), ball2);
                                }
                                if(event.getHitEntity().getCustomName().equals("Kasa")){
                                    KasaData ssdata = DataMgr.getKasaDataFromArmorStand((ArmorStand)event.getHitEntity());
                                    Snowball ball = (Snowball)event.getEntity();
                                    Player shooter = (Player)ball.getShooter();
                                    //if(DataMgr.getPlayerData(ssdata.getPlayer()).getTeam() != DataMgr.getPlayerData(shooter).getTeam())
                                        //ssdata.setDamage(ssdata.getDamage() + DataMgr.getPlayerData(shooter).getWeaponClass().getMainWeapon().getDamage());
                                    if(DataMgr.getPlayerData(ssdata.getPlayer()).getTeam() != DataMgr.getPlayerData(shooter).getTeam())
                                        return;
                                    Vector vec = ball.getVelocity();
                                    Location loc = ball.getLocation();
                                    Snowball ball2 = (Snowball)ball.getWorld().spawnEntity(new Location(loc.getWorld(), loc.getX() + vec.getX(), loc.getY() + vec.getY(), loc.getZ() + vec.getZ()), EntityType.SNOWBALL);
                                    ((CraftSnowball)ball2).getHandle().setItem(CraftItemStack.asNMSCopy(new ItemStack(DataMgr.getPlayerData(shooter).getTeam().getTeamColor().getWool())));
                                    ball2.setShooter(shooter);
                                    ball2.setVelocity(vec);
                                    ball2.setCustomName(ball.getCustomName());
                                    //if(!DataMgr.getPlayerData(shooter).getWeaponClass().getMainWeapon().getWeaponType().equals("Blaster"))
                                        DataMgr.addSnowballHitCount(ball.getCustomName());
                                    DataMgr.getMainSnowballNameMap().put(ball.getCustomName(), ball2);
                                }
                            }
                        }
                    }
                }
            }
        }

        if(event.getEntity() instanceof Snowball){
            if(event.getHitBlock() != null){
                Player shooter = (Player)event.getEntity().getShooter();
                PaintMgr.Paint(event.getHitBlock().getLocation(), shooter, true);
                shooter.getWorld().playSound(event.getHitBlock().getLocation(), Sound.ENTITY_SLIME_ATTACK, 0.3F, 2.0F);
            }
            if(event.getHitEntity() != null){
                if(event.getHitEntity() instanceof Player){
                    //AntiNoDamageTime
                    Player target = (Player)event.getHitEntity();
                    BukkitRunnable task = new BukkitRunnable(){
                        Player p = target;
                        @Override
                        public void run(){
                            target.setNoDamageTicks(0);
                        }
                    };
                    task.runTaskLater(Main.getPlugin(), 1);

                    Timer timer = new Timer(false);
                    TimerTask t = new TimerTask(){
                        Player p = target;
                        @Override
                        public void run(){
                            try{
                                target.setNoDamageTicks(0);
                                timer.cancel();
                            }catch(Exception e){
                                timer.cancel();
                            }
                        }
                    };
                    timer.schedule(t, 25);
                }
            }
        }
        
    }
    
    @EventHandler
    public void onEntityHit(EntityDamageByEntityEvent event) {
    
        if(Main.type == ServerType.LOBBY){
            if(event.getEntity() instanceof Player) event.setCancelled(true);
            /*
            if(event.getEntity() instanceof ArmorStand){
                if(event.getDamager() instanceof Player){
                    if(!((Player)event.getDamager()).hasPermission("sclat.admin")) event.setCancelled(true);
                }
            }*/
            return;
        }
        
        event.setCancelled(true);
        
        if(!(event.getDamager() instanceof Projectile))
            return;

        if(DataMgr.getSnowballIsHitMap().containsKey((Snowball)event.getDamager())){
            DataMgr.setSnowballIsHit((Snowball)event.getDamager(), true);
            if(event.getDamager().getCustomName() != null) {
                if(event.getDamager().getCustomName().equals("JetPack") || event.getDamager().getCustomName().equals("SuperShot")) {
                    Projectile projectile = (Projectile) event.getDamager();
                    Player shooter = (Player) projectile.getShooter();
                    if (event.getEntity() instanceof Player) {
                        Player target = (Player) event.getEntity();
                        if (DataMgr.getPlayerData(shooter).getTeam() != DataMgr.getPlayerData(target).getTeam() && target.getGameMode().equals(GameMode.ADVENTURE)) {
                            if (!DataMgr.getPlayerData(shooter).getIsUsingSP())
                                SPWeaponMgr.addSPCharge(shooter);
            
                            Sclat.giveDamage(shooter, target, 30, "spWeapon");
                        }
                    } else if (event.getEntity() instanceof ArmorStand) {
                        ArmorStand as = (ArmorStand) event.getEntity();
                        ArmorStandMgr.giveDamageArmorStand(as, 20, shooter);
                    }
                }
            }
        }else{
            Projectile projectile = (Projectile)event.getDamager();
            Player shooter = (Player)projectile.getShooter();
            if(event.getEntity() instanceof Player){/*
                Player target = (Player)event.getEntity();
                if(DataMgr.getPlayerData(shooter).getTeam() != DataMgr.getPlayerData(target).getTeam() && target.getGameMode().equals(GameMode.ADVENTURE)){
                    if(!DataMgr.getPlayerData(shooter).getIsUsingSP())
                        SPWeaponMgr.addSPCharge(shooter);
                    if(DataMgr.getPlayerData(target).getArmor() > 0){
                        target.getWorld().playSound(target.getLocation(), Sound.ENTITY_SPLASH_POTION_BREAK, 1F, 1.5F);
                        if(DataMgr.getPlayerData(target).getArmor() > 10000) {
                            Vector vec = projectile.getVelocity();
                            Vector v = new Vector(vec.getX(), 0, vec.getZ()).normalize();
                            target.setVelocity(new Vector(v.getX(), 0.2, v.getZ()).multiply(0.33));
                        }
                    }
                    if(projectile.getCustomName() != null){
                        if(projectile.getCustomName().equals("Sprinkler") || projectile.getCustomName().equals("Amehurasi")){
                            if(projectile.getCustomName().equals("Sprinkler"))
                                Sclat.giveDamage(shooter, target, 4, "subWeapon");
                            else if(projectile.getCustomName().equals("Amehurasi"))
                                Sclat.giveDamage(shooter, target, 4, "spWeapon");
                            PaintMgr.Paint(target.getLocation(), shooter, true);
                        }
                        
                        if(projectile.getCustomName().equals("SuperShot")){
                            shooter.playSound(shooter.getLocation(), Sound.ENTITY_PLAYER_HURT, 0.5F, 1F);
                            Sclat.giveDamage(shooter, target, 20, "spWeapon");
                        }
                        
                        if(DataMgr.mws.contains(projectile.getCustomName())){
                            if(DataMgr.tsl.contains(projectile.getCustomName())) {
                                if(!projectile.getCustomName().contains(":")) {
                                    shooter.playSound(shooter.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1.2F, 1.3F);
                                    shooter.spawnParticle(Particle.FLASH, projectile.getLocation(), 1, 0.1, 0.1, 0.1, 0.1);
                                }else{
                                    String args[] = projectile.getCustomName().split(":");
                                    switch(args[1]){
                                        case "Burst": {
                                            if(DataMgr.oto.containsKey(args[2])){
                                                DataMgr.oto.put(args[2], DataMgr.oto.get(args[2]) + 1);
                                            }else{
                                                DataMgr.oto.put(args[2], 1);
                                            }
                                            break;
                                        }
                                    }
                                    
                                    if(DataMgr.oto.get(args[2]) == DataMgr.getPlayerData(shooter).getWeaponClass().getMainWeapon().getRollerShootQuantity()){
                                        shooter.playSound(shooter.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1.2F, 1.3F);
                                        shooter.spawnParticle(Particle.FLASH, projectile.getLocation(), 1, 0.1, 0.1, 0.1, 0.1);
                                    }
                                }
                            }
                            shooter.playSound(shooter.getLocation(), Sound.ENTITY_PLAYER_HURT, 0.5F, 1F);
                            Sclat.giveDamage(shooter, target, DataMgr.getPlayerData(shooter).getWeaponClass().getMainWeapon().getDamage(), "killed");
                        }
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
                    
                    Timer timer = new Timer(false);
                    TimerTask t = new TimerTask(){
                        Player p = target;
                        @Override
                        public void run(){
                            try{
                                target.setNoDamageTicks(0);
                                timer.cancel();
                            }catch(Exception e){
                                timer.cancel();
                            }
                        }
                    };
                    timer.schedule(t, 25);
                    
                }*/
            }else if(event.getEntity() instanceof ArmorStand){
                ArmorStand as = (ArmorStand) event.getEntity();
                if(projectile.getCustomName() != null){
                    if(DataMgr.mws.contains(projectile.getCustomName())) {
                        if (DataMgr.tsl.contains(projectile.getCustomName())) {
                            if (Sclat.isNumber(as.getCustomName())) {
                                if (!as.getCustomName().equals("21") && !as.getCustomName().equals("100")) {
                                    if (as.isVisible()) {
                                        if(!projectile.getCustomName().contains(":")) {
                                            shooter.playSound(shooter.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1.2F, 1.3F);
                                            shooter.spawnParticle(Particle.FLASH, projectile.getLocation(), 1, 0.1, 0.1, 0.1, 0.1);
                                        }else{
                                            String args[] = projectile.getCustomName().split(":");
                                            switch(args[1]){
                                                case "Burst": {
                                                    if(DataMgr.oto.containsKey(args[2])){
                                                        DataMgr.oto.put(args[2], DataMgr.oto.get(args[2]) + 1);
                                                    }else{
                                                        DataMgr.oto.put(args[2], 1);
                                                    }
                                                    break;
                                                }
                                            }
        
                                            if(DataMgr.oto.get(args[2]) == DataMgr.getPlayerData(shooter).getWeaponClass().getMainWeapon().getRollerShootQuantity()){
                                                shooter.playSound(shooter.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1.2F, 1.3F);
                                                shooter.spawnParticle(Particle.FLASH, projectile.getLocation(), 1, 0.1, 0.1, 0.1, 0.1);
                                            }
                                        }
                                        //shooter.playSound(shooter.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1.2F, 1.3F);
                                    }
                                }
                            }
                        }
                    }
                    
                    if(projectile.getCustomName().equals("SuperShot")){
                        ArmorStandMgr.giveDamageArmorStand(as, 20, shooter);
                        return;
                    }
                    if(projectile.getCustomName().equals("JetPack")){
                        ArmorStandMgr.giveDamageArmorStand(as, 20, shooter);
                        return;
                    }
                }
                if(as.getCustomName() != null) {
                    if (!as.getCustomName().equals("Path") && !as.getCustomName().equals("21") && !as.getCustomName().equals("100") && !as.getCustomName().equals("SplashShield") && !as.getCustomName().equals("Kasa")) {
                        shooter.playSound(shooter.getLocation(), Sound.ENTITY_PLAYER_HURT, 0.5F, 1F);
                    }
                }
    
                double damage = DataMgr.getPlayerData(shooter).getWeaponClass().getMainWeapon().getDamage();
                String type = DataMgr.getPlayerData(shooter).getWeaponClass().getMainWeapon().getWeaponType();
    
                if(!type.equals("Burst") && !type.equals("Blaster")) {
                    double ticksLived = (double) projectile.getTicksLived() * 1.2;
                    if(ticksLived > 20.0)
                        ticksLived = 20.0;
                    damage -= damage * (ticksLived / 100);
                }
                ArmorStandMgr.giveDamageArmorStand(as, damage * Gear.getGearInfluence(shooter, Gear.Type.MAIN_SPEC_UP), shooter);
            }
        }
        
    }
}
