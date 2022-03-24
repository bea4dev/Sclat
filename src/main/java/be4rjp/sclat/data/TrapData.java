package be4rjp.sclat.data;

import be4rjp.sclat.Main;
import be4rjp.sclat.Sclat;
import be4rjp.sclat.Sphere;
import be4rjp.sclat.manager.ArmorStandMgr;
import be4rjp.sclat.manager.PaintMgr;
import be4rjp.sclat.weapon.Gear;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

import static be4rjp.sclat.Main.conf;

public class TrapData {
    
    private final Location location;
    private final Player player;
    private final Team team;
    
    private final BukkitRunnable task;
    private final BukkitRunnable effect;
    
    private int number = 0;
    private boolean near = false;
    
    public TrapData(Location location, Player player, Team team, int number){
        this.location = location;
        this.player = player;
        this.team = team;
        this.number = number;
    
        this.effect = new BukkitRunnable(){
            @Override
            public void run() {
                List<Location> s_locs = Sphere.getXZCircle(location.clone().add(0, 1, 0), 3, 2, 40);
                for (Player o_player : Main.getPlugin().getServer().getOnlinePlayers()) {
                    if(DataMgr.getPlayerData(o_player).getSettings().ShowEffect_Bomb()){
                        for(Location loc : s_locs){
                            if(o_player.getWorld() == loc.getWorld() && DataMgr.getPlayerData(o_player).getTeam() != null){
                                if(o_player.getLocation().distanceSquared(loc) < Main.PARTICLE_RENDER_DISTANCE_SQUARED && (DataMgr.getPlayerData(o_player).getTeam() == team || near)){
                                    Particle.DustOptions dustOptions = new Particle.DustOptions(near ? DataMgr.getPlayerData(player).getTeam().getTeamColor().getBukkitColor() : Color.BLACK, 1);
                                    o_player.spawnParticle(Particle.REDSTONE, loc, 1, 0, 0, 0, 5, dustOptions);
                                }
                            }
                        }
                    }
                }
            }
        };
        effect.runTaskTimer(Main.getPlugin(), 0, 5);
        
        this.task = new BukkitRunnable(){
            @Override
            public void run() {
                Block block = location.getBlock();
                if(DataMgr.getBlockDataMap().containsKey(block)){
                    PaintData pdata = DataMgr.getPaintDataFromBlock(block);
                    if(team != pdata.getTeam())
                        Explosion();
                }
                
                if(number + 2 < DataMgr.getPlayerData(player).getTrapCount())
                    Explosion();
    
                for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                    if(!DataMgr.getPlayerData(target).isInMatch() || target.getWorld() != location.getWorld())
                        continue;
                    if(target.getGameMode() == GameMode.SPECTATOR) continue;
                    if (target.getLocation().distance(location) <= 3 && DataMgr.getPlayerData(target).getTeam() != team) {
                        Explosion();
                    }
                }
    
                for(Entity as : player.getWorld().getEntities()) {
                    if (as instanceof ArmorStand && as.getLocation().distanceSquared(location) <= 9 /*3^2*/) {
                        if(as.getCustomName() != null){
                            if(as.getCustomName() == null) continue;
                            if(!as.getCustomName().equals("Path") && !as.getCustomName().equals("21") && !as.getCustomName().equals("100")&& !as.getCustomName().equals("SplashShield") && !as.getCustomName().equals("Kasa")){
                                Explosion();
                            }
                        }
                    }
                }
                
                if(!DataMgr.getPlayerData(player).isInMatch() || !player.isOnline()){
                    task.cancel();
                    effect.cancel();
                }
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 2);
    }
    
    public void Explosion(){
        near = true;
        task.cancel();
        
        BukkitRunnable ex = new BukkitRunnable() {
            int i = 0;
            @Override
            public void run() {
                if(i >= 0 && i <= 4){
                    if(i % 2 == 0)
                        player.getWorld().playSound(location, Sound.BLOCK_NOTE_BLOCK_PLING, 1.1F, 1.8F);
                }
                
                if(i == 20) {
                    //半径
                    double maxDist = 4;
                    double maxDistSquared = 16; /* 4^2 */
    
                    //爆発音
                    player.getWorld().playSound(location, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1, 1);
    
                    //爆発エフェクト
                    Sclat.createInkExplosionEffect(location, maxDist, 15, player);
    
                    //バリアをはじく
                    Sclat.repelBarrier(location, maxDist, player);
                    
                    //センサーエフェクト
                    List<Location> s_locs = Sphere.getSphere(location, maxDist + 1, 25);
                    for (Player o_player : Main.getPlugin().getServer().getOnlinePlayers()) {
                        if(DataMgr.getPlayerData(o_player).getSettings().ShowEffect_BombEx()){
                            for(Location loc : s_locs){
                                if(o_player.getWorld() == loc.getWorld()){
                                    if(o_player.getLocation().distanceSquared(loc) < Main.PARTICLE_RENDER_DISTANCE_SQUARED){
                                        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.BLACK, 1);
                                        o_player.spawnParticle(Particle.REDSTONE, loc, 1, 0, 0, 0, 1, dustOptions);
                                    }
                                }
                            }
                        }
                    }
    
                    //塗る
                    for (int i = 0; i <= maxDist; i++) {
                        List<Location> p_locs = Sphere.getSphere(location, i, 20);
                        for (Location loc : p_locs) {
                            PaintMgr.Paint(loc, player, false);
                        }
                    }
                    
                    //発光効果
                    for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                        if(!DataMgr.getPlayerData(target).isInMatch() || target.getWorld() != player.getWorld())
                            continue;
                        if (target.getLocation().distance(location) <= maxDist + 1) {
                            if(DataMgr.getPlayerData(player).getTeam().getID() != DataMgr.getPlayerData(target).getTeam().getID()){
                                target.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 200, 1));
                            }
            
                        }
                    }
    
                    for(Entity as : player.getWorld().getEntities()){
                        if (as instanceof ArmorStand && as.getLocation().distance(location) <= maxDist + 1){
                            if(as.getCustomName() != null){
                                if(!as.getCustomName().equals("Path") && !as.getCustomName().equals("21") && !as.getCustomName().equals("100")&& !as.getCustomName().equals("SplashShield") && !as.getCustomName().equals("Kasa")){
                                    ((ArmorStand)as).addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 200, 1));
                                }
                            }
                        }
                    }
    
                    //攻撃判定の処理
                    for (Entity as : player.getWorld().getEntities()) {
                        if (as instanceof ArmorStand) {
                            if (as.getLocation().distanceSquared(location) <= maxDistSquared) {
                                if (as.getCustomName() != null) {
                                    try {
                                        if (as.getCustomName().equals("Kasa")) {
                                            KasaData kasaData = DataMgr.getKasaDataFromArmorStand((ArmorStand) as);
                                            if (DataMgr.getPlayerData(kasaData.getPlayer()).getTeam() != DataMgr.getPlayerData(player).getTeam()) {
                                                cancel();
                                            }
                                        } else if (as.getCustomName().equals("SplashShield")) {
                                            SplashShieldData splashShieldData = DataMgr.getSplashShieldDataFromArmorStand((ArmorStand) as);
                                            if (DataMgr.getPlayerData(splashShieldData.getPlayer()).getTeam() != DataMgr.getPlayerData(player).getTeam()) {
                                                cancel();
                                            }
                                        }
                                    }catch (Exception e){}
                                }
                            }
                        }
                    }
    
    
                    for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                        if (!DataMgr.getPlayerData(target).isInMatch() || target.getWorld() != player.getWorld())
                            continue;
                        if (target.getLocation().distanceSquared(location) <= maxDistSquared) {
                            double damage = (maxDist - target.getLocation().distance(location)) * 5.0 * Gear.getGearInfluence(player, Gear.Type.SUB_SPEC_UP);
                            if (DataMgr.getPlayerData(player).getTeam() != DataMgr.getPlayerData(target).getTeam() && target.getGameMode().equals(GameMode.ADVENTURE)) {
                                Sclat.giveDamage(player, target, damage, "subWeapon");
                
                                //AntiNoDamageTime
                                BukkitRunnable task = new BukkitRunnable() {
                                    Player p = target;
                    
                                    @Override
                                    public void run() {
                                        target.setNoDamageTicks(0);
                                    }
                                };
                                task.runTaskLater(Main.getPlugin(), 1);
                            }
                        }
                    }
    
                    for (Entity as : player.getWorld().getEntities()) {
                        if (as instanceof ArmorStand) {
                            if (as.getLocation().distanceSquared(location) <= maxDistSquared) {
                                double damage = (maxDist - as.getLocation().distance(location)) * 2.5 * Gear.getGearInfluence(player, Gear.Type.SUB_SPEC_UP);
                                ArmorStandMgr.giveDamageArmorStand((ArmorStand) as, damage, player);
                                if (as.getCustomName() != null) {
                                    if (as.getCustomName().equals("SplashShield") || as.getCustomName().equals("Kasa"))
                                        break;
                                }
                            }
                        }
                    }
                    
                    effect.cancel();
                    cancel();
                }
                i++;
            }
        };
        ex.runTaskTimer(Main.getPlugin(), 0, 1);
    }
    
    public int getNumber(){return this.number;}
    
    public void addNumber(){this.number++;}
}