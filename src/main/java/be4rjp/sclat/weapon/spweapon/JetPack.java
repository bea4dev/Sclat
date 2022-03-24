
package be4rjp.sclat.weapon.spweapon;

import be4rjp.blockstudio.BlockStudio;
import be4rjp.blockstudio.api.BSObject;
import be4rjp.blockstudio.api.BlockStudioAPI;
import be4rjp.blockstudio.file.ObjectData;
import be4rjp.sclat.Main;
import static be4rjp.sclat.Main.conf;

import be4rjp.sclat.Sclat;
import be4rjp.sclat.Sphere;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.manager.ArmorStandMgr;
import be4rjp.sclat.manager.PaintMgr;
import be4rjp.sclat.manager.SPWeaponMgr;
import be4rjp.sclat.manager.SuperJumpMgr;
import be4rjp.sclat.manager.WeaponClassMgr;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.v1_14_R1.EntityArmorStand;
import net.minecraft.server.v1_14_R1.EnumItemSlot;
import net.minecraft.server.v1_14_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_14_R1.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_14_R1.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_14_R1.PlayerConnection;
import net.minecraft.server.v1_14_R1.WorldServer;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_14_R1.util.CraftChatMessage;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
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
public class JetPack {
    public static void JetPackRunnable(Player player){
    
        BlockStudioAPI api = BlockStudio.getBlockStudioAPI();
        ObjectData objectData = api.getObjectData("jetpack");
        BSObject bsObject = api.createObjectFromObjectData(String.valueOf(Main.getNotDuplicateNumber()),
                            player.getLocation(), objectData, 40, false);
        bsObject.startTaskAsync(40);
        
        BukkitRunnable task = new BukkitRunnable(){
            Player p = player;
            Location ol = DataMgr.getPlayerData(player).getPlayerGroundLocation();
            int i = 0;
            int id = 0;
            Location btl = player.getLocation();
            ArmorStand as = player.getWorld().spawn(player.getLocation(), ArmorStand.class, armorStand -> {
                armorStand.setSmall(true);
                armorStand.setGravity(false);
                armorStand.setVisible(false);
                armorStand.setBasePlate(false);
                armorStand.setMarker(true);
            });
            ArmorStand leader = player.getWorld().spawn(player.getLocation(), ArmorStand.class, armorStand -> {
                armorStand.setSmall(true);
                armorStand.setVisible(false);
                armorStand.setBasePlate(false);
                //armorStand.setMarker(true);
            });
            List<ArmorStand> list = new ArrayList<ArmorStand>();
            
            Vector vehicleVector = new Vector(0, 0, 0);
            
            @Override
            public void run(){

                p.setSprinting(true);

                boolean onBlock = false;
                int yh = 1;
                for(int y = p.getLocation().getBlockY(); y >= 1 && y >= p.getLocation().getBlockY() - 7; y--){
                    Location bl = new Location(p.getLocation().getWorld(), p.getLocation().getX(), y, p.getLocation().getZ());
                    if(bl.getBlock().getType() != Material.AIR && bl.getBlock().getType() != Material.WATER){
                        onBlock = true;
                        break;
                    }
                    yh++;
                }
                
                p.setAllowFlight(true);
                p.setFlying(true);
                
                Vector vec = new Vector(0, 0, 0);
                if(i % 2 == 0)
                    vec = DataMgr.getPlayerData(p).getVehicleVector().clone().multiply(0.8);
                Vector pvec = p.getEyeLocation().getDirection();
                Vector w_WASDVector = (new Vector(pvec.getX(), 0, pvec.getZ())).multiply(vec.getX());
                Vector d_WASDVector = (new Vector(pvec.getZ(), 0, pvec.getX() * -1)).multiply(vec.getZ());
                Vector xzVector = w_WASDVector.add(d_WASDVector);
                Vector moveVector = new Vector(xzVector.getX(), onBlock ? (i >= 30 ? vec.getY() + 0.1 : 0.7) : -0.5, xzVector.getZ()).multiply(0.3);
                if((vehicleVector.clone().add(moveVector)).lengthSquared() <= 0.19){
                    vehicleVector.add(moveVector);
                    vehicleVector = vehicleVector.multiply(0.9);
                }
                leader.setVelocity(vehicleVector);
                //as.teleport(as.getLocation().add(vehicleVector));
                
                
                if(!as.getPassengers().contains(p)){
                    as.addPassenger(p);
                }

                //p.setWalkSpeed(0.1F);
                //p.setFlySpeed(0.02F);
    
                Location leaderEyeLoc = leader.getEyeLocation();
                Location leaderLoc = leader.getLocation().add(0, -0.3, 0);
                leaderEyeLoc.setYaw(p.getEyeLocation().getYaw());
                //as.teleport(leaderLoc);
                ((CraftArmorStand)as).getHandle().setPositionRotation(leaderLoc.getX(), leaderLoc.getY(), leaderLoc.getZ(), p.getLocation().getYaw(), 0);
                
                //move object
                Vector pv = p.getEyeLocation().getDirection();
                Vector direction = new Vector(pv.getX(), 0 , pv.getZ()).normalize();
                Vector locPlus = direction.clone().multiply(-0.2);
                bsObject.setBaseLocation(leaderLoc.clone().add(locPlus.getX(), 0.5, locPlus.getZ()));
                bsObject.setDirection(direction);
                bsObject.move();
                
                Vector vec1 = new Vector(pv.getX(), 0, pv.getZ()).normalize().multiply(-0.2);
                Location pl = leaderEyeLoc.clone();
                //Location loc1 = pl.add(vec1.getX() + sv.getX(), sv.getY() * 0.8, vec1.getZ() + sv.getZ());
                Location loc1 = pl.add(vec1.getX(), 0, vec1.getZ());


                Vector vec2 = new Vector(vec1.getZ() * -1, 0, vec1.getX()).normalize().multiply(0.6);
                Vector vec3 = new Vector(vec1.getZ(), 0, vec1.getX() * -1).normalize().multiply(0.6);
                Location loc2 = loc1.clone().add(vec2.getX(), 0, vec2.getZ());
                Location loc3 = loc1.clone().add(vec3.getX(), 0, vec3.getZ());
                
                PaintMgr.PaintHightestBlock(loc2, player, false, true);
                PaintMgr.PaintHightestBlock(loc3, player, false, true);
                
                if(i != 0){
                    //effect
                    //org.bukkit.block.data.BlockData bd = DataMgr.getPlayerData(player).getTeam().getTeamColor().getWool().createBlockData();
                    Location position = loc2.clone().add(0, -0.2, 0);
                    Location position2 = loc3.clone().add(0, -0.2, 0);
                    for (Player o_player : Main.getPlugin().getServer().getOnlinePlayers()) {
                        if(!DataMgr.getPlayerData(o_player).getSettings().ShowEffect_SPWeapon())
                            continue;
                        if(o_player.getWorld() == position.getWorld()){
                            if(o_player.getLocation().distanceSquared(position) < Main.PARTICLE_RENDER_DISTANCE_SQUARED){
                                for(int i = 0; i <= 10; i++) {
                                    double random = 0.015;
                                    o_player.spawnParticle(Particle.ITEM_CRACK, position, 0, Math.random() * random - random/2, -0.13, Math.random() * random - random/2, 10, new ItemStack(DataMgr.getPlayerData(player).getTeam().getTeamColor().getWool()));
                                    //o_player.spawnParticle(org.bukkit.Particle.BLOCK_DUST, position, 0, 0, -2, 0, 10, bd);
                                }
                            }
                            if(o_player.getLocation().distanceSquared(position2) < Main.PARTICLE_RENDER_DISTANCE_SQUARED){
                                for(int i = 0; i <= 10; i++) {
                                    double random = 0.015;
                                    o_player.spawnParticle(Particle.ITEM_CRACK, position, 0, Math.random() * random - random/2, -0.13, Math.random() * random - random/2, 10, new ItemStack(DataMgr.getPlayerData(player).getTeam().getTeamColor().getWool()));
                                }
                            }
                        }
                    }

                    /*
                    for (Player o_player : Main.getPlugin().getServer().getOnlinePlayers()) {
                        if(!DataMgr.getPlayerData(o_player).getSettings().ShowEffect_SPWeapon())
                            continue;
                        if(o_player.getWorld() == position.getWorld()){
                            if(o_player.getLocation().distanceSquared(position) < Main.PARTICLE_RENDER_DISTANCE_SQUARED){
                                for(int i = 0; i <= 10; i++) {
                                    double random = 0.015;
                                    o_player.spawnParticle(Particle.ITEM_CRACK, position, 0, Math.random() * random - random/2, -0.13, Math.random() * random - random/2, 10, new ItemStack(DataMgr.getPlayerData(player).getTeam().getTeamColor().getWool()));
                                }
                            }
                        }
                    }

                     */
                }

                if(i == 0){
                    DataMgr.getPlayerData(p).setIsUsingSP(true);
                    SPWeaponMgr.setSPCoolTimeAnimation(player, 175);
                    DataMgr.getPlayerData(p).setIsUsingJetPack(true);
                    
                    p.getInventory().clear();
                
                    ItemStack item = new ItemStack(Material.QUARTZ);
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName("右クリックで弾を発射");
                    item.setItemMeta(meta);
                    for (int count = 0; count < 9; count++){
                        player.getInventory().setItem(count, item);
                    }
                    player.updateInventory();
                    
                    WorldServer nmsWorld = ((CraftWorld) p.getWorld()).getHandle();
                    EntityArmorStand as = new EntityArmorStand(nmsWorld, ol.getX(), ol.getY(), ol.getZ());
                    as.setPosition(ol.getX(), ol.getY(), ol.getZ());
                    as.setInvisible(true);
                    as.setNoGravity(true);
                    as.setBasePlate(false);
                    as.setCustomName(CraftChatMessage.fromStringOrNull(DataMgr.getPlayerData(p).getTeam().getTeamColor().getColorCode() + "↓↓↓  くコ:彡  ↓↓↓"));
                    as.setCustomNameVisible(true);
                    as.setSmall(true);
                    id = as.getBukkitEntity().getEntityId();
                    for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                        if(p.getWorld() == target.getWorld()){
                            ((CraftPlayer)target).getHandle().playerConnection.sendPacket(new PacketPlayOutSpawnEntityLiving(as));
                        }
                    }
                }
                

                Location atl = p.getLocation();
                //p.sendMessage(String.valueOf(sv.getX() + ", " + sv.getY() + ", " + sv.getZ()));
                btl = p.getLocation();

                if(i == 170 || p.getGameMode().equals(GameMode.SPECTATOR) || !DataMgr.getPlayerData(p).isInMatch() || DataMgr.getPlayerData(p).getIsDead()){
                    if(as.getPassengers().contains(p))
                        as.removePassenger(p);
                    as.remove();
                    leader.remove();
                    ((CraftPlayer)p).getHandle().stopRiding();
                    
                    for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                        if(p.getWorld() == target.getWorld()){
                            ((CraftPlayer)target).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(id));
                        }
                    }
                    p.getInventory().clear();
                    if(p.getWorld() == ol.getWorld() && !p.getGameMode().equals(GameMode.SPECTATOR)){
                        if(p.getLocation().distanceSquared(ol) > 9 /* 3^2 */){
                            SuperJumpMgr.SuperJumpRunnable(p, ol);
                            p.getWorld().playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 2, 1.3F);
                        }else{
                            p.getWorld().playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 2, 1.3F);
                            Vector v = new Vector(0, 1, 0);
                            p.setVelocity(v);
                            p.getInventory().clear();
                            WeaponClassMgr.setWeaponClass(p);
                        }
                    }
                    DataMgr.getPlayerData(p).setIsUsingJetPack(false);
                    DataMgr.getPlayerData(p).setIsUsingSP(false);
                    bsObject.remove();
                    p.setFlySpeed(0.1F);
                    DataMgr.getPlayerData(player).setCanUseSubWeapon(true);
                    //WeaponClassMgr.setWeaponClass(p);
                    cancel();
                    return;
                }
                    
                i++;
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 1);
    }
    
    public static void ShootJetPack(Player player){
        BukkitRunnable task = new BukkitRunnable(){
            Player p = player;
            Vector p_vec;
            double x = 0;
            double z = 0;
            boolean block_check = false;
            int c = 0;
            Item drop;
            Snowball ball;
            @Override
            public void run(){
                try{
                    if(c == 0){
                        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.5F, 1.2F);
                        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_WITHER_SHOOT, 0.2F, 2F);
                        p_vec = p.getEyeLocation().getDirection();
                        ItemStack bom = new ItemStack(DataMgr.getPlayerData(p).getTeam().getTeamColor().getWool()).clone();
                        ItemMeta bom_m = bom.getItemMeta();
                        bom_m.setLocalizedName(String.valueOf(Main.getNotDuplicateNumber()));
                        bom.setItemMeta(bom_m);
                        Location dl = p.getEyeLocation().add(p_vec.clone().multiply(1.5));
                        drop = p.getWorld().dropItem(dl, bom);
                        drop.setVelocity(p_vec.multiply(1.5));
                        drop.setGravity(false);
                        //雪玉をスポーンさせた瞬間にプレイヤーに雪玉がデスポーンした偽のパケットを送信する
                        ball = (Snowball)player.getWorld().spawnEntity(dl, EntityType.SNOWBALL);
                        ball.setShooter(p);
                        ball.setGravity(false);
                        ball.setVelocity(new Vector(0, 0, 0));
                        ball.setCustomName("JetPack");
                        DataMgr.setSnowballIsHit(ball, false);

                        for (Player o_player : Main.getPlugin().getServer().getOnlinePlayers()) {
                            PlayerConnection connection = ((CraftPlayer) o_player).getHandle().playerConnection;
                            connection.sendPacket(new PacketPlayOutEntityDestroy(ball.getEntityId()));
                        }
                        p_vec = p.getEyeLocation().getDirection();
                    }

                    if(!drop.isOnGround() && !(drop.getVelocity().getX() == 0 && drop.getVelocity().getZ() != 0) && !(drop.getVelocity().getX() != 0 && drop.getVelocity().getZ() == 0))
                        ball.setVelocity(drop.getVelocity());  
                    
                    for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                        if(!DataMgr.getPlayerData(target).getSettings().ShowEffect_SPWeapon()){
                            continue;
                        }
                        if(target.getWorld() == ball.getWorld()){
                            if(target.getLocation().distanceSquared(ball.getLocation()) < Main.PARTICLE_RENDER_DISTANCE_SQUARED){
                                org.bukkit.block.data.BlockData bd = DataMgr.getPlayerData(p).getTeam().getTeamColor().getWool().createBlockData();
                                target.spawnParticle(org.bukkit.Particle.BLOCK_DUST, ball.getLocation(), 1, 0, 0, 0, 1, bd);
                            }
                        }

                        //ボムの視認用エフェクト
                        if(target.getWorld() == drop.getLocation().getWorld()) {
                            if (target.getLocation().distanceSquared(drop.getLocation()) < Main.PARTICLE_RENDER_DISTANCE_SQUARED) {
                                Particle.DustOptions dustOptions = new Particle.DustOptions(DataMgr.getPlayerData(p).getTeam().getTeamColor().getBukkitColor(), 1);
                                target.spawnParticle(Particle.REDSTONE, drop.getLocation(), 1, 0, 0, 0, 50, dustOptions);
                            }
                        }

                    }

                    if(DataMgr.getSnowballIsHit(ball) || drop.isOnGround()){

                        //半径
                        double maxDist = 4;

                        //爆発音
                        player.getWorld().playSound(drop.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1, 1);

                        //爆発エフェクト
                        Sclat.createInkExplosionEffect(drop.getLocation(), 3, 25, player);

                        //塗る
                        for(int i = 0; i <= maxDist; i++){
                            List<Location> p_locs = Sphere.getSphere(drop.getLocation(), i, 20);
                            for(Location loc : p_locs){
                                PaintMgr.Paint(loc, p, false);
                            }
                        }



                        //攻撃判定の処理

                        for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                            if(!DataMgr.getPlayerData(target).isInMatch() || target.getWorld() != p.getWorld())
                                continue;
                            if (target.getLocation().distanceSquared(drop.getLocation()) <= 12.25 /* 3.5^2 */) {
                                double damage = (3.5 - target.getLocation().distance(drop.getLocation())) * 10;
                                if(DataMgr.getPlayerData(player).getTeam() != DataMgr.getPlayerData(target).getTeam() && target.getGameMode().equals(GameMode.ADVENTURE)){
                                    Sclat.giveDamage(player, target, damage, "spWeapon");

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
                            if (as instanceof ArmorStand && as.getLocation().distanceSquared(drop.getLocation()) <= 12.25 /* 3.5^2 */){
                                double damage = (3.5 - as.getLocation().distance(drop.getLocation())) * 10;
                                ArmorStandMgr.giveDamageArmorStand((ArmorStand)as, damage, p);
                            }
                        }
                        drop.remove();
                        cancel();
                        return;
                    }

                    //ちょっと上の方に移動
                    /*
                    //ボムの視認用エフェクト
                    for (Player o_player : Main.getPlugin().getServer().getOnlinePlayers()) {
                        if(DataMgr.getPlayerData(o_player).getSettings().ShowEffect_SPWeapon()){
                            if(o_player.getWorld() == drop.getLocation().getWorld()) {
                                if (o_player.getLocation().distanceSquared(drop.getLocation()) < Main.PARTICLE_RENDER_DISTANCE_SQUARED) {
                                    Particle.DustOptions dustOptions = new Particle.DustOptions(DataMgr.getPlayerData(p).getTeam().getTeamColor().getBukkitColor(), 1);
                                    o_player.spawnParticle(Particle.REDSTONE, drop.getLocation(), 1, 0, 0, 0, 50, dustOptions);
                                }
                            }
                        }
                    }

                     */

                    c++;
                    x = drop.getLocation().getX();
                    z = drop.getLocation().getZ();


                    if(c > 20){
                        drop.remove();
                        ball.remove();
                        cancel();
                        return;
                    }
                }catch(Exception e){
                    drop.remove();
                    cancel();
                    Main.getPlugin().getLogger().warning(e.getMessage());
                }
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 1);
        
        BukkitRunnable cooltime = new BukkitRunnable(){
            @Override
            public void run(){
                DataMgr.getPlayerData(player).setCanUseSubWeapon(true);
            }
        };
        cooltime.runTaskLater(Main.getPlugin(), 20);
    }
}
