
package be4rjp.sclat.weapon.spweapon;

import be4rjp.sclat.Main;
import be4rjp.sclat.Sphere;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import be4rjp.sclat.manager.ArmorStandMgr;
import be4rjp.sclat.manager.DamageMgr;
import be4rjp.sclat.manager.DeathMgr;
import be4rjp.sclat.manager.PaintMgr;
import be4rjp.sclat.manager.SPWeaponMgr;
import be4rjp.sclat.manager.WeaponClassMgr;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.server.v1_13_R1.EntitySquid;
import net.minecraft.server.v1_13_R1.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_13_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_13_R1.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_13_R1.PacketPlayOutSpawnPosition;
import net.minecraft.server.v1_13_R1.WorldServer;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_13_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 *
 * @author Be4rJP
 */
public class MultiMissile {
    public static void MMLockRunnable(Player player){
        BukkitRunnable task = new BukkitRunnable(){
            Map<Player, EntitySquid> ps = new HashMap<>();
            Player p = player;
            int c = 0;
            @Override
            public void run(){
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
                            EntitySquid es = new EntitySquid(nmsWorld);
                            es.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
                            es.setInvisible(true);
                            es.setNoGravity(true);
                            es.setNoAI(true);
                            //es.setFlag(6, true);
                            ps.put(op, es);
                            ((CraftPlayer)p).getHandle().playerConnection.sendPacket(new PacketPlayOutSpawnEntityLiving(es));
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
                                es.setFlag(6, true);
                            else
                                es.setFlag(6, false);
                            //((CraftPlayer)p).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityTeleport(es));
                            ((CraftPlayer)p).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(es.getBukkitEntity().getEntityId()));
                            ((CraftPlayer)p).getHandle().playerConnection.sendPacket(new PacketPlayOutSpawnEntityLiving(es));
                        }
                    }
                }
                if(!DataMgr.getPlayerData(p).getIsUsingMM() || c == 200){
                    for(Player op : Main.getPlugin(Main.class).getServer().getOnlinePlayers()){
                        if(DataMgr.getPlayerData(op).isInMatch() && op.getWorld() == p.getWorld() && !op.getName().equals(p.getName()) && DataMgr.getPlayerData(p).getTeam() != DataMgr.getPlayerData(op).getTeam()){
                            EntitySquid es = ps.get(op);
                            ((CraftPlayer)p).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(es.getBukkitEntity().getEntityId()));
                            if(MMCheckCanLock(p, op)){
                                op.sendTitle("", ChatColor.RED + "敵に狙われている！", 0, 20, 4);
                                op.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 80, 1));
                                MMShootRunnable(p, op);
                            }
                        }
                    }
                    if(p.hasPotionEffect(PotionEffectType.SLOW))
                        p.removePotionEffect(PotionEffectType.SLOW);
                    p.getInventory().clear();
                    WeaponClassMgr.setWeaponClass(p);
                    DataMgr.getPlayerData(p).setIsUsingSP(true);
                    Firework f = (Firework) p.getWorld().spawn(player.getLocation(), Firework.class);
                    SPWeaponMgr.setSPCoolTimeAnimation(p, 100);
                    cancel();
                }
                if(!DataMgr.getPlayerData(p).isInMatch() || !p.isOnline())
                    cancel();
                c++;
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 1);
    }
    
    public static void MMShootRunnable(Player shooter, Player target){
        BukkitRunnable task = new BukkitRunnable(){
            Player s = shooter;
            Player t = target;
            int c = 0;
            @Override
            public void run(){
                MMRunnable(s, t);
                if(c == 4 || t.getGameMode().equals(GameMode.SPECTATOR) || !DataMgr.getPlayerData(s).isInMatch())
                    cancel();
                c++;
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 10);
    }
    
    public static void MMRunnable(Player shooter, Player target){
        BukkitRunnable task = new BukkitRunnable(){
            Player s = shooter;
            Player t = target;
            Location tl = target.getLocation();
            Item drop;
            int c = 0;
            boolean finded = false;
            @Override
            public void run(){
                if(c == 0){
                    drop = shooter.getWorld().dropItem(t.getLocation().add(0, 40, 0), new ItemStack(DataMgr.getPlayerData(s).getTeam().getTeamColor().getWool()));
                    drop.setGravity(false);
                }
                Location dl = drop.getLocation();
                if(dl.distance(tl) < 10){
                    finded = true;
                }else{
                    tl = t.getLocation();
                }
                
                if(!finded)
                    drop.setVelocity((new Vector(tl.getX() - dl.getX(), tl.getY() - dl.getY(), tl.getZ() - dl.getZ())).normalize().multiply(0.8));
                if(t.getGameMode().equals(GameMode.SPECTATOR))
                    drop.setVelocity(drop.getVelocity().add(new Vector(0, -0.1, 0)));
                
                org.bukkit.block.data.BlockData bd = DataMgr.getPlayerData(s).getTeam().getTeamColor().getWool().createBlockData();
                for (Player o_player : Main.getPlugin().getServer().getOnlinePlayers()) {
                    if(DataMgr.getPlayerData(o_player).getSettings().ShowEffect_Shooter())
                        o_player.spawnParticle(org.bukkit.Particle.BLOCK_DUST, drop.getLocation(), 1, 0, 0, 0, 1, bd);
                }
                
                if(drop.isOnGround()){
                    //半径
                    double maxDist = 3;
                    
                    //爆発音
                    s.getWorld().playSound(drop.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1, 1);
                    
                    //爆発エフェクト
                    List<Location> s_locs = Sphere.getSphere(drop.getLocation(), maxDist, 25);
                    for (Player o_player : Main.getPlugin().getServer().getOnlinePlayers()) {
                        if(DataMgr.getPlayerData(o_player).getSettings().ShowEffect_BombEx()){
                            for(Location loc : s_locs){
                                o_player.spawnParticle(org.bukkit.Particle.BLOCK_DUST, loc, 1, 0, 0, 0, 1, bd);
                            }
                        }
                    }
                    
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
                        if (target.getLocation().distance(drop.getLocation()) <= maxDist) {
                            double damage = (maxDist - target.getLocation().distance(drop.getLocation())) * 7;
                            if(DataMgr.getPlayerData(s).getTeam() != DataMgr.getPlayerData(target).getTeam() && target.getGameMode().equals(GameMode.ADVENTURE)){
                                if(target.getHealth() + DataMgr.getPlayerData(target).getArmor() > damage){
                                    DamageMgr.SclatGiveDamage(target, damage);
                                    PaintMgr.Paint(target.getLocation(), s, true);
                                }else{
                                    target.setGameMode(GameMode.SPECTATOR);
                                    DeathMgr.PlayerDeathRunnable(target, s, "spWeapon");
                                    PaintMgr.Paint(target.getLocation(), s, true);
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
                    
                    for(Entity as : s.getWorld().getEntities()){
                        if (as.getLocation().distance(drop.getLocation()) <= maxDist){
                            if(as instanceof ArmorStand){
                                double damage = (maxDist - as.getLocation().distance(drop.getLocation())) * 2;
                                ArmorStandMgr.giveDamageArmorStand((ArmorStand)as, damage, s);
                            }
                        }
                    }
                    
                    drop.remove();
                    cancel();
                }
                
                if(!DataMgr.getPlayerData(s).isInMatch() || !s.isOnline() || drop.isDead()){
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
        EntitySquid es = new EntitySquid(nmsWorld);
        es.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        es.setInvisible(true);
        es.setNoGravity(true);
        es.setNoAI(true);
        es.setFlag(6, true);
        ((CraftPlayer)shooter).getHandle().playerConnection.sendPacket(new PacketPlayOutSpawnEntityLiving(es));
        
        BukkitRunnable task = new BukkitRunnable(){
            Player s = shooter;
            Player t = target;
            @Override
            public void run(){
                
            }
        };
    }
    
    public static boolean MMCheckCanLock(Player sp, Player target){
        Vector sv = sp.getEyeLocation().getDirection().normalize();
        Location tl = target.getLocation();
        Location sl = sp.getLocation();
        Vector tpv = (new Vector(tl.getX() - sl.getX(), tl.getY() - sl.getY(), tl.getZ() - sl.getZ())).normalize();
        float angle = sv.angle(tpv);
        if(angle < 0.4F)
            return true;
        return false;
    }
}
