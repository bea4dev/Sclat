
package be4rjp.sclat.weapon.spweapon;

import be4rjp.sclat.GlowingAPI;
import be4rjp.sclat.Main;
import be4rjp.sclat.Sclat;
import be4rjp.sclat.Sphere;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.manager.ArmorStandMgr;
import be4rjp.sclat.manager.PaintMgr;
import be4rjp.sclat.manager.SPWeaponMgr;
import be4rjp.sclat.manager.WeaponClassMgr;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.server.v1_14_R1.*;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import static be4rjp.sclat.Main.conf;

/**
 *
 * @author Be4rJP
 */
public class MultiMissile {
    public static void MMLockRunnable(Player player){
        BukkitRunnable task = new BukkitRunnable(){
            Map<Player, EntitySquid> ps = new HashMap<>();
            Map<Entity, EntityArmorStand> asl = new HashMap<>();
            Player p = player;
            int c = 0;
            @Override
            public void run(){
                try{
                    if(c == 0){
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100000, 10));
                        p.getInventory().clear();
                        ItemStack item = new ItemStack(Material.PRISMARINE_SHARD);
                        ItemMeta meta = item.getItemMeta();
                        meta.setDisplayName("プレイヤーを狙って右クリックで発射");
                        item.setItemMeta(meta);
                        for (int count = 0; count < 9; count++){
                            player.getInventory().setItem(count, item);
                        }
                        player.updateInventory();

                        DataMgr.getPlayerData(p).setIsUsingMM(true);
                        WorldServer nmsWorld = ((CraftWorld) p.getWorld()).getHandle();
                        for(Player op : Main.getPlugin(Main.class).getServer().getOnlinePlayers()){
                            if(DataMgr.getPlayerData(op).isInMatch() && op.getWorld() == p.getWorld() && !op.getName().equals(p.getName()) && DataMgr.getPlayerData(p).getTeam() != DataMgr.getPlayerData(op).getTeam()){
                                Location loc = op.getLocation();
                                EntitySquid es = new EntitySquid(EntityTypes.SQUID, nmsWorld);
                                es.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
                                es.setInvisible(true);
                                es.setNoGravity(true);
                                es.setNoAI(true);
                                ps.put(op, es);
                                ((CraftPlayer)p).getHandle().playerConnection.sendPacket(new PacketPlayOutSpawnEntityLiving(es));
                            }
                        }
                        for(Entity e : p.getWorld().getEntities()){
                            if(e instanceof ArmorStand){
                                ArmorStand as = (ArmorStand)e;
                                if(as.getCustomName() == null) continue;
                                if(!as.getCustomName().equals("Path") && !as.getCustomName().equals("21") && !as.getCustomName().equals("100") && !as.getCustomName().equals("SplashShield") && !as.getCustomName().equals("Kasa")){
                                    Location loc = as.getLocation();
                                    EntityArmorStand eas = new EntityArmorStand(nmsWorld, loc.getX(), loc.getY(), loc.getZ());
                                    eas.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
                                    eas.setInvisible(true);
                                    eas.setSmall(as.isSmall());
                                    eas.setBasePlate(as.hasBasePlate());
                                    eas.setNoGravity(true);
                                    asl.put(as, eas);
                                    ((CraftPlayer)p).getHandle().playerConnection.sendPacket(new PacketPlayOutSpawnEntityLiving(eas));
                                }
                            }
                        }
                    }
                    if(c != 0){
                        for(Player op : Main.getPlugin(Main.class).getServer().getOnlinePlayers()){
                            if(DataMgr.getPlayerData(op).isInMatch() && op.getWorld() == p.getWorld() && !op.getName().equals(p.getName()) && DataMgr.getPlayerData(p).getTeam() != DataMgr.getPlayerData(op).getTeam()){
                                EntitySquid es = ps.get(op);
                                Location loc = op.getLocation();
                                es.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
                                if(MMCheckCanLock(p, op))
                                    GlowingAPI.setGlowing(es.getBukkitEntity(), p, true);
                                else
                                    GlowingAPI.setGlowing(es.getBukkitEntity(), p, false);
                                ((CraftPlayer)p).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityTeleport(es));
                            }
                        }
                        for(Entity e : p.getWorld().getEntities()){
                            if(e instanceof ArmorStand){
                                ArmorStand as = (ArmorStand)e;
                                if(as.getCustomName() == null) continue;
                                if(!as.getCustomName().equals("Path") && !as.getCustomName().equals("21") && !as.getCustomName().equals("100") && !as.getCustomName().equals("SplashShield") && !as.getCustomName().equals("Kasa")){
                                    EntityArmorStand eas = asl.get(as);
                                    Location loc = as.getLocation();
                                    eas.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
                                    if(MMCheckCanLock(p, as))
                                        GlowingAPI.setGlowing(eas.getBukkitEntity(), p, true);
                                    else
                                        GlowingAPI.setGlowing(eas.getBukkitEntity(), p, false);
                                    ((CraftPlayer)p).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityTeleport(eas));
                                }
                            }
                        }
                    }
                    if(!DataMgr.getPlayerData(p).getIsUsingMM() || c == 200){
                        List<Entity> targetList = new ArrayList<>();
                        int count = 0;
                        for(Player op : Main.getPlugin(Main.class).getServer().getOnlinePlayers()){
                            if(DataMgr.getPlayerData(op).isInMatch() && op.getWorld() == p.getWorld() && !op.getName().equals(p.getName()) && DataMgr.getPlayerData(p).getTeam() != DataMgr.getPlayerData(op).getTeam()){
                                EntitySquid es = ps.get(op);
                                ((CraftPlayer)p).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(es.getBukkitEntity().getEntityId()));
                                if(MMCheckCanLock(p, op)){
                                    op.sendTitle("", ChatColor.RED + "ミサイル接近中！", 0, 40, 4);
                                    op.playSound(op.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1F, 1F);
                                    op.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 30, 1));
                                    targetList.add(op);
                                    count++;
                                }
                            }
                        }
                        for(Entity e : p.getWorld().getEntities()){
                            if(e instanceof ArmorStand){
                                ArmorStand as = (ArmorStand)e;
                                if(as.getCustomName() == null) continue;
                                if(!as.getCustomName().equals("Path") && !as.getCustomName().equals("21") && !as.getCustomName().equals("100") && !as.getCustomName().equals("SplashShield") && !as.getCustomName().equals("Kasa")){
                                    EntityArmorStand eas = asl.get(as);
                                    ((CraftPlayer)p).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(eas.getBukkitEntity().getEntityId()));
                                    Location loc = as.getLocation();
                                    eas.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
                                    if(MMCheckCanLock(p, as)){
                                        targetList.add(as);
                                        as.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 30, 1));
                                        count++;
                                    }
                                }
                            }
                        }

                        for(Entity e : targetList)
                            MMShootRunnable(p, e, count >= 4 ? 2 : 4);

                        if(p.hasPotionEffect(PotionEffectType.SLOW))
                            p.removePotionEffect(PotionEffectType.SLOW);
                        p.getInventory().clear();
                        WeaponClassMgr.setWeaponClass(p);
                        DataMgr.getPlayerData(p).setIsUsingSP(true);
                        DataMgr.getPlayerData(p).setIsUsingMM(false);
                        FireworksRunnable(p);
                        SPWeaponMgr.setSPCoolTimeAnimation(p, 100);
                        cancel();
                    }
                    if(!DataMgr.getPlayerData(p).isInMatch() || !p.isOnline() || p.getGameMode() == GameMode.SPECTATOR){
                        DataMgr.getPlayerData(p).setIsUsingSP(false);
                        cancel();
                    }
                    c++;
                }catch(Exception e){
                    cancel();
                }
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 1);
    }

    public static void FireworksRunnable(Player player){
        BukkitRunnable task = new BukkitRunnable() {
            Player p = player;
            int i = 0;
            @Override
            public void run() {
                try{
                    Firework f = (Firework) p.getWorld().spawn(p.getLocation(), Firework.class);
                    i++;
                    if(i == 5)
                        cancel();
                }catch(Exception e){
                    cancel();
                }
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 2);
    }

    public static void MMShootRunnable(Player shooter, Entity target, int i){
        BukkitRunnable task = new BukkitRunnable(){
            Player s = shooter;
            int c = 0;
            @Override
            public void run(){
                if(target instanceof Player){
                    Player t = (Player)target;
                    if(c == i || t.getGameMode().equals(GameMode.SPECTATOR) || !DataMgr.getPlayerData(s).isInMatch())
                        cancel();
                }else{
                    if(c == i || !DataMgr.getPlayerData(s).isInMatch())
                        cancel();
                }
                MMRunnable(s, target);
                c++;
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 10);
    }

    public static void MMRunnable(Player shooter, Entity target){
        BukkitRunnable task = new BukkitRunnable(){
            Player s = shooter;
            Entity t = target;
            Location tl = target.getLocation();
            Item drop;
            Snowball ball;
            int c = 0;
            boolean reached = false;
            @Override
            public void run(){
                if(c == 0){
                    drop = shooter.getWorld().dropItem(t.getLocation().add(0, 40, 0), new ItemStack(DataMgr.getPlayerData(s).getTeam().getTeamColor().getWool()));
                    drop.setGravity(false);
                    ball = (Snowball)s.getWorld().spawnEntity(drop.getLocation(), EntityType.SNOWBALL);
                    ball.setGravity(false);
                    ball.setShooter(s);
                    ball.setVelocity(new Vector(0, 0, 0));
                    for (Player o_player : Main.getPlugin().getServer().getOnlinePlayers()) {
                        PlayerConnection connection = ((CraftPlayer) o_player).getHandle().playerConnection;
                        connection.sendPacket(new PacketPlayOutEntityDestroy(ball.getEntityId()));
                    }
                    if(t instanceof Player){
                        if(DataMgr.getPlayerData((Player)t).isInMatch())
                            tl = DataMgr.getPlayerData((Player)t).getPlayerGroundLocation();
                    }
                    DataMgr.setSnowballIsHit(ball, false);
                }

                if(!DataMgr.getPlayerData(s).isInMatch() || !s.isOnline() || drop.isDead()){
                    drop.remove();
                    ball.remove();
                    cancel();
                }

                Location dl = drop.getLocation();

                if(!drop.isOnGround()){
                    ball.teleport(drop.getLocation());
                    ball.setVelocity(drop.getVelocity());
                }

                if(dl.distanceSquared(tl) < 100 /* 10^2 */){
                    reached = true;
                }

                if(t instanceof Player)
                    if(((Player)t).getGameMode().equals(GameMode.SPECTATOR) || !((Player)t).isOnline())
                        reached = true;

                //if(!reached)
                //tl = t.getLocation();

                if(!reached)
                    drop.setVelocity((new Vector(tl.getX() - dl.getX(), tl.getY() - dl.getY(), tl.getZ() - dl.getZ())).normalize().multiply(0.8));
                else
                    drop.setVelocity(drop.getVelocity().add(new Vector(0, -0.1, 0)));

                org.bukkit.block.data.BlockData bd = DataMgr.getPlayerData(s).getTeam().getTeamColor().getWool().createBlockData();
                for (Player o_player : Main.getPlugin().getServer().getOnlinePlayers()) {
                    if(o_player.getWorld() == drop.getLocation().getWorld()) {
                        if (o_player.getLocation().distanceSquared(drop.getLocation()) < Main.PARTICLE_RENDER_DISTANCE_SQUARED) {
                            if (DataMgr.getPlayerData(o_player).getSettings().ShowEffect_SPWeapon())
                                o_player.spawnParticle(org.bukkit.Particle.BLOCK_DUST, drop.getLocation(), 1, 0, 0, 0, 1, bd);
                        }
                    }
                }

                if(DataMgr.getSnowballIsHit(ball)){
                    //半径
                    double maxDist = 3;
                    double maxDistSquared = 9;/* 3^2 */

                    //爆発音
                    s.getWorld().playSound(drop.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1, 1);

                    //爆発エフェクト
                    Sclat.createInkExplosionEffect(drop.getLocation(), maxDist, 25, s);

                    //塗る
                    for(int i = 0; i <= maxDist; i++){
                        List<Location> p_locs = Sphere.getSphere(drop.getLocation(), i, 20);
                        for(Location loc : p_locs){
                            PaintMgr.Paint(loc, s, false);
                        }
                    }

                    //攻撃判定の処理
                    for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                        if(!DataMgr.getPlayerData(target).isInMatch() || target.getWorld() != s.getWorld())
                            continue;
                        if (target.getLocation().distanceSquared(drop.getLocation()) <= maxDistSquared) {
                            double damage = (maxDist - target.getLocation().distance(drop.getLocation())) * 14;
                            if(DataMgr.getPlayerData(s).getTeam() != DataMgr.getPlayerData(target).getTeam() && target.getGameMode().equals(GameMode.ADVENTURE)){
                                Sclat.giveDamage(s, target, damage, "spWeapon");

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

                    for(Entity as : s.getWorld().getEntities()){
                        if (as.getLocation().distanceSquared(drop.getLocation()) <= maxDistSquared){
                            if(as instanceof ArmorStand){
                                double damage = (maxDist - as.getLocation().distance(drop.getLocation())) * 2;
                                ArmorStandMgr.giveDamageArmorStand((ArmorStand)as, damage, s);
                            }
                        }
                    }

                    drop.remove();
                    cancel();
                }

                c++;
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 1);
    }

    public static void MMSquidRunnable(Player shooter, Player target){
        WorldServer nmsWorld = ((CraftWorld) target.getWorld()).getHandle();
        Location loc = target.getLocation();
        EntitySquid es = new EntitySquid(EntityTypes.SQUID, nmsWorld);
        es.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        es.setInvisible(true);
        es.setNoGravity(true);
        es.setNoAI(true);
        es.setFlag(6, true);
        ((CraftPlayer)shooter).getHandle().playerConnection.sendPacket(new PacketPlayOutSpawnEntityLiving(es));
    }

    public static boolean MMCheckCanLock(Player sp, Entity target){
        Vector sv = sp.getEyeLocation().getDirection().normalize();
        Location tl = target.getLocation();
        Location sl = sp.getLocation();
        Vector tpv = (new Vector(tl.getX() - sl.getX(), tl.getY() - sl.getY(), tl.getZ() - sl.getZ())).normalize();
        float angle = sv.angle(tpv);
        return angle < 0.4F;
    }
}