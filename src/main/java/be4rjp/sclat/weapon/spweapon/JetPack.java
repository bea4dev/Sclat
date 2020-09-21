
package be4rjp.sclat.weapon.spweapon;

import be4rjp.sclat.Main;
import static be4rjp.sclat.Main.conf;
import be4rjp.sclat.Sphere;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.manager.ArmorStandMgr;
import be4rjp.sclat.manager.DamageMgr;
import be4rjp.sclat.manager.DeathMgr;
import be4rjp.sclat.manager.PaintMgr;
import be4rjp.sclat.manager.SPWeaponMgr;
import be4rjp.sclat.manager.SuperJumpMgr;
import be4rjp.sclat.manager.WeaponClassMgr;
import be4rjp.sclat.raytrace.RayTrace;
import be4rjp.sclat.weapon.Gear;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.v1_13_R1.EntityArmorStand;
import net.minecraft.server.v1_13_R1.EnumItemSlot;
import net.minecraft.server.v1_13_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_13_R1.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_13_R1.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_13_R1.PlayerConnection;
import net.minecraft.server.v1_13_R1.WorldServer;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_13_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_13_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_13_R1.util.CraftChatMessage;
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
        BukkitRunnable task = new BukkitRunnable(){
            Player p = player;
            Location ol = player.getLocation();
            int i = 0;
            int id = 0;
            Vector sv = player.getVelocity();
            Location btl = player.getLocation();
            ArmorStand ru;
            ArmorStand rd;
            ArmorStand lu;
            ArmorStand ld;
            ArmorStand mu;
            ArmorStand md;
            List<ArmorStand> list = new ArrayList<ArmorStand>();
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
                Vector ev = p.getEyeLocation().getDirection().multiply(0.15);

                if(onBlock){
                    p.setAllowFlight(true);
                    p.setFlying(true);
                    if(i <= 30)
                        p.setVelocity(new Vector(ev.getX(), 0.2, ev.getZ()));
                }else{
                    //p.setAllowFlight(false);
                    //p.setFlying(false);
                    double y = p.getVelocity().getY();
                    p.setVelocity(new Vector(ev.getX(), y <= -0.3 ? -0.3 : y - 0.1, ev.getZ()));
                }

                //p.setWalkSpeed(0.1F);
                p.setFlySpeed(0.035F);

                Vector pv = p.getEyeLocation().getDirection();
                Vector vec1 = new Vector(pv.getX(), 0, pv.getZ()).normalize().multiply(-0.2);
                Location pl = p.getEyeLocation().add(0, -1.3, 0);
                Location loc1 = pl.add(vec1.getX() + sv.getX(), sv.getY() * 0.8, vec1.getZ() + sv.getZ());
                Location mul = loc1.clone().add(0, -0.5, 0);
                Location mdl = loc1.clone().add(0, -0.9, 0);


                Vector vec2 = new Vector(vec1.getZ() * -1, 0, vec1.getX()).normalize().multiply(0.4);
                Vector vec3 = new Vector(vec1.getZ(), 0, vec1.getX() * -1).normalize().multiply(0.4);
                Location loc2 = loc1.clone().add(vec2.getX(), 0, vec2.getZ());
                Location loc3 = loc1.clone().add(vec3.getX(), 0, vec3.getZ());
                Location rul = loc2.clone().add(0, -0.1, 0);
                Location rdl = loc2.clone().add(0, -0.5, 0);
                Location lul = loc3.clone().add(0, -0.1, 0);
                Location ldl = loc3.clone().add(0, -0.5, 0);
                
                PaintMgr.PaintHightestBlock(loc2, player, false, true);
                PaintMgr.PaintHightestBlock(loc3, player, false, true);

                //effect
                RayTrace rayTrace1 = new RayTrace(loc2.clone().add(0, -0.5, 0).toVector(), new Vector(0, -1, 0));
                ArrayList<Vector> positions = rayTrace1.traverse(yh, 0.5);
                org.bukkit.block.data.BlockData bd = DataMgr.getPlayerData(player).getTeam().getTeamColor().getWool().createBlockData();
                ray : for(int i = 0; i < positions.size();i++){
                    Location position = positions.get(i).toLocation(p.getLocation().getWorld());
                    for (Player o_player : Main.getPlugin().getServer().getOnlinePlayers()) {
                        if(!DataMgr.getPlayerData(o_player).getSettings().ShowEffect_Shooter())
                            continue;
                            if(o_player.getWorld() == position.getWorld()){
                                if(o_player.getLocation().distance(position) < conf.getConfig().getInt("ParticlesRenderDistance")){
                                    o_player.spawnParticle(org.bukkit.Particle.BLOCK_DUST, position, 1, 0, 0.5, 0, 100, bd);
                                }
                            }
                    }
                }

                RayTrace rayTrace2 = new RayTrace(loc3.clone().add(0, -0.5, 0).toVector(), new Vector(0, -1, 0));
                ArrayList<Vector> positions2 = rayTrace2.traverse(yh, 0.5);
                ray : for(int i = 0; i < positions2.size();i++){
                    Location position = positions2.get(i).toLocation(p.getLocation().getWorld());
                    for (Player o_player : Main.getPlugin().getServer().getOnlinePlayers()) {
                        if(!DataMgr.getPlayerData(o_player).getSettings().ShowEffect_Shooter())
                            continue;
                            if(o_player.getWorld() == position.getWorld()){
                                if(o_player.getLocation().distance(position) < conf.getConfig().getInt("ParticlesRenderDistance")){
                                    o_player.spawnParticle(org.bukkit.Particle.BLOCK_DUST, position, 1, 0, 0.5, 0, 100, bd);
                                }
                            }
                    }
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
                    
                    ru = (ArmorStand)p.getWorld().spawnEntity(rul, EntityType.ARMOR_STAND);
                    list.add(ru);
                    rd = (ArmorStand)p.getWorld().spawnEntity(rdl, EntityType.ARMOR_STAND);
                    list.add(rd);
                    lu = (ArmorStand)p.getWorld().spawnEntity(lul, EntityType.ARMOR_STAND);
                    list.add(lu);
                    ld = (ArmorStand)p.getWorld().spawnEntity(ldl, EntityType.ARMOR_STAND);
                    list.add(ld);
                    mu = (ArmorStand)p.getWorld().spawnEntity(mul, EntityType.ARMOR_STAND);
                    list.add(mu);
                    md = (ArmorStand)p.getWorld().spawnEntity(mdl, EntityType.ARMOR_STAND);
                    list.add(md);


                    for(ArmorStand as : list){
                        as.setVisible(false);
                        as.setBasePlate(false);
                        as.setGravity(false);
                        as.setSmall(true);
                        as.setMarker(true);
                    }

                    for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                        ((CraftPlayer)target).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(ru.getEntityId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(Material.IRON_BLOCK))));
                        ((CraftPlayer)target).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(rd.getEntityId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(Material.IRON_BLOCK))));
                        ((CraftPlayer)target).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(lu.getEntityId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(Material.IRON_BLOCK))));
                        ((CraftPlayer)target).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(ld.getEntityId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(Material.IRON_BLOCK))));
                        ((CraftPlayer)target).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(mu.getEntityId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(Material.IRON_BARS))));
                        ((CraftPlayer)target).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(md.getEntityId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(Material.IRON_BARS))));
                    }
                    
                    WorldServer nmsWorld = ((CraftWorld) p.getWorld()).getHandle();
                    EntityArmorStand as = new EntityArmorStand(nmsWorld);
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

                mu.teleport(mul);
                md.teleport(mdl);
                ru.teleport(rul);
                rd.teleport(rdl);
                lu.teleport(lul);
                ld.teleport(ldl);

                for(ArmorStand as : list){
                    //as.setVelocity(p.getVelocity().multiply(3));
                }

                Location atl = p.getLocation();
                sv = new Vector(atl.getX() - btl.getX(), atl.getY() - btl.getY(), atl.getZ() - btl.getZ()).multiply(2.8);
                //p.sendMessage(String.valueOf(sv.getX() + ", " + sv.getY() + ", " + sv.getZ()));
                btl = p.getLocation();

                if(i == 170 || p.getGameMode().equals(GameMode.SPECTATOR) || !DataMgr.getPlayerData(p).isInMatch()){
                    for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                        if(p.getWorld() == target.getWorld()){
                            ((CraftPlayer)target).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(id));
                        }
                    }
                    p.getInventory().clear();
                    if(p.getWorld() == ol.getWorld() && !p.getGameMode().equals(GameMode.SPECTATOR)){
                        if(p.getLocation().distance(ol) > 3){
                            SuperJumpMgr.SuperJumpRunnable(p, ol);
                            p.getWorld().playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 2, 1.3F);
                        }else{
                            p.getWorld().playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 2, 1.3F);
                            Vector vec = new Vector(0, 1, 0);
                            p.setVelocity(vec);
                            p.getInventory().clear();
                            WeaponClassMgr.setWeaponClass(p);
                        }
                    }
                    DataMgr.getPlayerData(p).setIsUsingJetPack(false);
                    DataMgr.getPlayerData(p).setIsUsingSP(false);
                    for(ArmorStand as : list)
                        as.remove();
                    p.setFlySpeed(0.1F);
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
                        if(!DataMgr.getPlayerData(target).getSettings().ShowEffect_Shooter())
                            continue;
                            if(target.getWorld() == ball.getWorld()){
                                if(target.getLocation().distance(ball.getLocation()) < conf.getConfig().getInt("ParticlesRenderDistance")){
                                    org.bukkit.block.data.BlockData bd = DataMgr.getPlayerData(p).getTeam().getTeamColor().getWool().createBlockData();
                                    target.spawnParticle(org.bukkit.Particle.BLOCK_DUST, ball.getLocation(), 1, 0, 0, 0, 1, bd);
                                }
                            }
                    }

                    if(DataMgr.getSnowballIsHit(ball) || drop.isOnGround()){

                        //半径
                        double maxDist = 3;

                        //爆発音
                        player.getWorld().playSound(drop.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1, 1);

                        //爆発エフェクト
                        List<Location> s_locs = Sphere.getSphere(drop.getLocation(), maxDist, 25);
                        for (Player o_player : Main.getPlugin().getServer().getOnlinePlayers()) {
                            if(DataMgr.getPlayerData(o_player).getSettings().ShowEffect_BombEx()){
                                for(Location loc : s_locs){
                                    if(o_player.getWorld() == loc.getWorld()){
                                        if(o_player.getLocation().distance(loc) < conf.getConfig().getInt("ParticlesRenderDistance")){
                                            org.bukkit.block.data.BlockData bd = DataMgr.getPlayerData(player).getTeam().getTeamColor().getWool().createBlockData();
                                            o_player.spawnParticle(org.bukkit.Particle.BLOCK_DUST, loc, 1, 0, 0, 0, 1, bd);
                                        }
                                    }
                                }
                            }
                        }

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
                            if (target.getLocation().distance(drop.getLocation()) <= maxDist) {
                                double damage = (maxDist - target.getLocation().distance(drop.getLocation())) * 10;
                                if(DataMgr.getPlayerData(player).getTeam() != DataMgr.getPlayerData(target).getTeam() && target.getGameMode().equals(GameMode.ADVENTURE)){
                                    if(target.getHealth() + DataMgr.getPlayerData(target).getArmor() > damage){
                                        DamageMgr.SclatGiveDamage(target, damage);
                                        PaintMgr.Paint(target.getLocation(), player, true);
                                    }else{
                                        target.setGameMode(GameMode.SPECTATOR);
                                        DeathMgr.PlayerDeathRunnable(target, player, "spWeapon");
                                        PaintMgr.Paint(target.getLocation(), player, true);
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

                        for(Entity as : player.getWorld().getEntities()){
                            if (as.getLocation().distance(drop.getLocation()) <= maxDist){
                                if(as instanceof ArmorStand){
                                    double damage = (maxDist - as.getLocation().distance(drop.getLocation())) * 10;
                                    ArmorStandMgr.giveDamageArmorStand((ArmorStand)as, damage, p);
                                }
                            }
                        }
                        drop.remove();
                        cancel();
                        return;
                    }

                    //ボムの視認用エフェクト
                    for (Player o_player : Main.getPlugin().getServer().getOnlinePlayers()) {
                        if(DataMgr.getPlayerData(o_player).getSettings().ShowEffect_Bomb()){
                            Particle.DustOptions dustOptions = new Particle.DustOptions(DataMgr.getPlayerData(p).getTeam().getTeamColor().getBukkitColor(), 1);
                            o_player.spawnParticle(Particle.REDSTONE, drop.getLocation(), 1, 0, 0, 0, 50, dustOptions);
                        }
                    }

                    c++;
                    x = drop.getLocation().getX();
                    z = drop.getLocation().getZ();


                    if(c > 50){
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
