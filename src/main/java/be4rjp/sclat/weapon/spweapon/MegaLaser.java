
package be4rjp.sclat.weapon.spweapon;

import be4rjp.sclat.Main;
import static be4rjp.sclat.Main.conf;
import be4rjp.sclat.Sclat;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.manager.ArmorStandMgr;
import be4rjp.sclat.manager.DamageMgr;
import be4rjp.sclat.manager.DeathMgr;
import be4rjp.sclat.manager.PaintMgr;
import be4rjp.sclat.manager.SPWeaponMgr;
import be4rjp.sclat.manager.WeaponClassMgr;
import be4rjp.sclat.raytrace.BoundingBox;
import be4rjp.sclat.raytrace.RayTrace;
import be4rjp.sclat.weapon.Gear;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.v1_13_R1.EnumItemSlot;
import net.minecraft.server.v1_13_R1.PacketPlayOutEntityEquipment;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_13_R1.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

/**
 *
 * @author Be4rJP
 */
public class MegaLaser {
    public static void MegaLaserRunnable(Player player){
        BukkitRunnable task = new BukkitRunnable(){
            Player p = player;
            List<ArmorStand> list = new ArrayList<ArmorStand>();
            ArmorStand as9;
            Vector v;
            Location ol;
            int c = 0;
            @Override
            public void run(){

                if(c == 0){
                    p.getInventory().clear();
                    DataMgr.getPlayerData(p).setIsUsingSP(true);
                    SPWeaponMgr.setSPCoolTimeAnimation(player, 180);
                    as9 = (ArmorStand)p.getWorld().spawnEntity(p.getLocation().add(0, -1.6, 0), EntityType.ARMOR_STAND);
                    as9.addPassenger(p);
                    list.add(as9);
                    ol = p.getLocation();
                    v = p.getEyeLocation().getDirection();
                }
                if(!as9.getPassengers().contains(p) && c < 9)
                    as9.addPassenger(p);

                Vector vec = new Vector(v.getX(), 0 ,v.getZ()).normalize().multiply(1.5);
                Vector vec1 = new Vector(vec.getZ() * -1, 0, vec.getX()).normalize().multiply(0.605);
                Vector vec2 = new Vector(vec.getZ(), 0, vec.getX() * -1).normalize().multiply(0.605);
                Location loc = p.getLocation().add(vec);
                loc.add(0, -0.5, 0);
                Location loc1 = p.getLocation().add(vec.clone().normalize().multiply(1));
                loc1.add(0, -0.5, 0);
                Location loc2 = p.getLocation().add(vec.clone().normalize().multiply(0.9));
                loc2.add(0, -0.5, 0);
                Location loc3 = p.getLocation().add(vec.clone().normalize().multiply(1.32));
                loc3.add(0, -0.5, 0);
                Location loc4 = p.getLocation().add(vec.clone().normalize().multiply(0.9));
                loc4.add(0, -1.35, 0);
                
                
                //ガジェットのエフェクト
                Location el = ol.clone().add(vec);
                if(c < 83){
                    for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                        if(p.getWorld() != target.getWorld())
                            continue;
                        if(el.distance(target.getLocation()) < 32){
                            if(DataMgr.getPlayerData(target).getSettings().ShowEffect_ChargerLine()){
                                org.bukkit.block.data.BlockData bd = DataMgr.getPlayerData(p).getTeam().getTeamColor().getWool().createBlockData();
                                target.spawnParticle(org.bukkit.Particle.BLOCK_DUST, el, 7, 0.5, 0.5, 0.5, 1, bd);
                            }
                        }
                    }
                }
                
                if(c == 0){
                    ArmorStand as1 = (ArmorStand)p.getWorld().spawnEntity(loc.clone().add(0, 0.57, 0), EntityType.ARMOR_STAND);
                    ArmorStand as2 = (ArmorStand)p.getWorld().spawnEntity(loc.clone().add(0, -0.57, 0), EntityType.ARMOR_STAND);
                    ArmorStand as3 = (ArmorStand)p.getWorld().spawnEntity(loc.clone().add(vec1.getX(), 0, vec1.getZ()), EntityType.ARMOR_STAND);
                    ArmorStand as4 = (ArmorStand)p.getWorld().spawnEntity(loc.clone().add(vec2.getX(), 0, vec2.getZ()), EntityType.ARMOR_STAND);
                    ArmorStand as5 = (ArmorStand)p.getWorld().spawnEntity(loc1.clone().add(0, -0.25, 0), EntityType.ARMOR_STAND);
                    ArmorStand as6 = (ArmorStand)p.getWorld().spawnEntity(loc2.clone().add(0, -0.25, 0), EntityType.ARMOR_STAND);
                    ArmorStand as7 = (ArmorStand)p.getWorld().spawnEntity(loc3.clone().add(0, 0, 0), EntityType.ARMOR_STAND);
                    ArmorStand as8 = (ArmorStand)p.getWorld().spawnEntity(loc4.clone().add(0, 0, 0), EntityType.ARMOR_STAND);


                    list.add(as1);
                    list.add(as2);
                    list.add(as3);
                    list.add(as4);
                    list.add(as5);
                    list.add(as6);
                    list.add(as7);
                    list.add(as8);

                    for(ArmorStand as : list){
                        as.setVisible(false);
                        as.setBasePlate(false);
                        as.setGravity(false);
                    }

                    as1.setHeadPose(new EulerAngle(-0.9, 0, 0));
                    as2.setHeadPose(new EulerAngle(0.9, 0, 0));
                    as3.setHeadPose(new EulerAngle(-1.6, -0.6, 0));
                    as4.setHeadPose(new EulerAngle(-1.6, 0.6, 0));
                    as5.setHeadPose(new EulerAngle(0, 0, 0));
                    as6.setHeadPose(new EulerAngle(0.43, 0, 0));
                    as7.setHeadPose(new EulerAngle(-1.6, 0, 0));
                    as8.setHeadPose(new EulerAngle(0, 0, 0));

                    for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                        if(p.getWorld() != target.getWorld())
                            continue;
                        ((CraftPlayer)target).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(as1.getEntityId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(Material.IRON_TRAPDOOR))));
                        ((CraftPlayer)target).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(as2.getEntityId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(Material.IRON_TRAPDOOR))));
                        ((CraftPlayer)target).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(as3.getEntityId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(Material.IRON_TRAPDOOR))));
                        ((CraftPlayer)target).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(as4.getEntityId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(Material.IRON_TRAPDOOR))));
                        ((CraftPlayer)target).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(as5.getEntityId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(Material.IRON_BLOCK))));
                        ((CraftPlayer)target).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(as6.getEntityId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(Material.END_ROD))));
                        ((CraftPlayer)target).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(as7.getEntityId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(Material.IRON_TRAPDOOR))));
                        ((CraftPlayer)target).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(as8.getEntityId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(Material.NETHER_BRICK_FENCE))));
                    }

                }

                if(c == 15 || c == 25 || c == 35){
                    ol.getWorld().playSound(ol, Sound.ENTITY_WITHER_SHOOT, 0.3F, 0.5F);
                    RayTrace rayTrace1 = new RayTrace(ol.toVector(), v);
                    ArrayList<Vector> positions = rayTrace1.traverse(300, 2);
                    ray : for(int i = 1; i < positions.size();i++){
                        Location position = positions.get(i).toLocation(p.getLocation().getWorld());

                        int r = 1;

                        if(i == 1)
                            r = 1;
                        if(i == 2)
                            r = 3;
                        if(i >= 3)
                            r = 5;

                        RayTrace rayTrace2 = new RayTrace(position.toVector(), new Vector(v.getZ() * -1, 0, v.getX()));
                        ArrayList<Vector> positions2 = rayTrace2.traverse(10, 0.5);

                        RayTrace rayTrace3 = new RayTrace(position.toVector(), new Vector(v.getZ(), 0, v.getX() * -1));
                        ArrayList<Vector> positions3 = rayTrace3.traverse(10, 0.5);

                        for(int s = 0; s <= 360; s+=15){
                            double y = r * Math.cos(Math.toRadians(s));
                            double x = r * Math.sin(Math.toRadians(s));
                            double lx = 0;
                            double lz = 0;
                            if(x >= 0){
                                lx = positions2.get((int)(x * 2)).getX();
                                lz = positions2.get((int)(x * 2)).getZ();
                            }
                            else{
                                lx = positions3.get((int)(x * -2)).getX();
                                lz = positions3.get((int)(x * -2)).getZ();
                            }
                            Location eloc = new Location(ol.getWorld(), lx, ol.getY() + y, lz);
                            for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                                if(p.getWorld() != target.getWorld())
                                    continue;
                                if(eloc.distance(target.getLocation()) < conf.getConfig().getInt("ParticlesRenderDistance")){
                                    if(DataMgr.getPlayerData(target).getSettings().ShowEffect_ChargerLine()){
                                        Particle.DustOptions dustOptions = new Particle.DustOptions(DataMgr.getPlayerData(p).getTeam().getTeamColor().getBukkitColor(), 1);
                                        target.spawnParticle(Particle.REDSTONE, eloc, 1, 0, 0, 0, 50, dustOptions);
                                    }
                                }
                            }
                        }
                    }
                    
                    //音
                    RayTrace rayTrace5 = new RayTrace(ol.toVector(), v);
                    ArrayList<Vector> positions5 = rayTrace5.traverse(300, 20);
                    for(int i = 1; i < positions5.size();i++){
                        Location position = positions5.get(i).toLocation(p.getLocation().getWorld());
                        position.getWorld().playSound(position, Sound.ENTITY_WITHER_SHOOT, 0.3F, 0.5F);
                    }
                }
                
                
                
                if(c == 40 || c == 45 || c == 50 || c == 55 || c == 60 || c == 65 || c == 70 || c == 75 || c == 80){
                    ol.getWorld().playSound(ol, Sound.ENTITY_WITHER_SHOOT, 0.3F, 0.6F);
                    RayTrace rayTrace1 = new RayTrace(ol.toVector(), v);
                    ArrayList<Vector> positions = rayTrace1.traverse(300, 1);
                    ray : for(int i = 3; i < positions.size();i++){
                        if(((c /4) % 2 == 0) == (i % 2 == 0)){
                            Location position = positions.get(i).toLocation(p.getLocation().getWorld());

                            int r = 1;

                            if(i == 2)
                                r = 1;
                            if(i == 3)
                                r = 3;
                            if(i >= 4)
                                r = 5;

                            RayTrace rayTrace2 = new RayTrace(position.toVector(), new Vector(v.getZ() * -1, 0, v.getX()));
                            ArrayList<Vector> positions2 = rayTrace2.traverse(10, 0.5);

                            RayTrace rayTrace3 = new RayTrace(position.toVector(), new Vector(v.getZ(), 0, v.getX() * -1));
                            ArrayList<Vector> positions3 = rayTrace3.traverse(10, 0.5);

                            for(int s = 0; s <= 360; s+=15){
                                double y = r * Math.cos(Math.toRadians(s));
                                double x = r * Math.sin(Math.toRadians(s));
                                double lx = 0;
                                double lz = 0;
                                if(x >= 0){
                                    lx = positions2.get((int)(x * 2)).getX();
                                    lz = positions2.get((int)(x * 2)).getZ();
                                }
                                else{
                                    lx = positions3.get((int)(x * -2)).getX();
                                    lz = positions3.get((int)(x * -2)).getZ();
                                }
                                Location eloc = new Location(ol.getWorld(), lx, ol.getY() + y, lz);
                                for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                                    if(p.getWorld() != target.getWorld())
                                        continue;
                                    if(eloc.distance(target.getLocation()) < conf.getConfig().getInt("ParticlesRenderDistance")){
                                        if(DataMgr.getPlayerData(target).getSettings().ShowEffect_ChargerLine()){
                                            Particle.DustOptions dustOptions = new Particle.DustOptions(DataMgr.getPlayerData(p).getTeam().getTeamColor().getBukkitColor(), 1);
                                            target.spawnParticle(Particle.REDSTONE, eloc, 1, 0, 0, 0, 50, dustOptions);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    //攻撃判定
                    RayTrace rayTrace4 = new RayTrace(ol.clone().add(0, -1, 0).toVector(), v);
                    ArrayList<Vector> positions4 = rayTrace4.traverse(300, 1);
                    for(int i = 1; i < positions4.size();i++){
                        Location position = positions4.get(i).toLocation(p.getLocation().getWorld());
                        if(i > 6){//攻撃判定
                            double maxDist = 5;
                            double damage = 7.5;
                            for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                                if(!DataMgr.getPlayerData(target).isInMatch())
                                    continue;
                                if(target.getWorld() != p.getWorld())
                                    continue;
                                if (target.getLocation().distance(position) <= maxDist) {
                                    if(DataMgr.getPlayerData(p).getTeam() != DataMgr.getPlayerData(target).getTeam() && target.getGameMode().equals(GameMode.ADVENTURE)){                   
                                        if(target.getHealth() + DataMgr.getPlayerData(target).getArmor() > damage){
                                            DamageMgr.SclatGiveStrongDamage(target, damage, player);
                                            //PaintMgr.Paint(target.getLocation(), p, true);
                                        }else{
                                            target.setGameMode(GameMode.SPECTATOR);
                                            DeathMgr.PlayerDeathRunnable(target, p, "spWeapon");
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
                    
                    //音
                    RayTrace rayTrace5 = new RayTrace(ol.toVector(), v);
                    ArrayList<Vector> positions5 = rayTrace5.traverse(300, 20);
                    for(int i = 1; i < positions5.size();i++){
                        Location position = positions5.get(i).toLocation(p.getLocation().getWorld());
                        position.getWorld().playSound(position, Sound.ENTITY_WITHER_SHOOT, 0.3F, 0.6F);
                    }
                }
                
                if(c >= 15 && c % 2 == 0){
                    RayTrace rayTrace4 = new RayTrace(ol.clone().add(0, -1, 0).toVector(), v);
                    ArrayList<Vector> positions4 = rayTrace4.traverse(300, 1);
                    List<Player> list = new ArrayList<>();
                    for(int i = 7; i < positions4.size();i++){
                        Location position = positions4.get(i).toLocation(p.getLocation().getWorld());
                        double maxDist = 5;
                        for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                            if(!DataMgr.getPlayerData(target).isInMatch())
                                continue;
                            if(target.getWorld() != p.getWorld())
                                continue;
                            if(DataMgr.getPlayerData(target).getTeam() == DataMgr.getPlayerData(p).getTeam())
                                continue;
                            if (target.getLocation().distance(position) <= maxDist) {
                                //if(DataMgr.getPlayerData(target).getTeam() == DataMgr.getPlayerData(p).getTeam())
                                    //continue;
                                list.add(target);
                            }
                        }
                    }
                    
                    for (Player target : Main.getPlugin().getServer().getOnlinePlayers()){
                        if(list.contains(target))
                            Sclat.sendWorldBorderWarningPacket(target);
                        else
                            Sclat.sendWorldBorderWarningClearPacket(target);
                    }
                }
                
                if(c == 88){
                    for(ArmorStand as : list)
                        as.setGravity(true);
                }
                
                if(c == 90 || !DataMgr.getPlayerData(p).isInMatch()){
                    for(ArmorStand as : list)
                        as.remove();
                    DataMgr.getPlayerData(p).setIsUsingSP(false);
                    for (Player target : Main.getPlugin().getServer().getOnlinePlayers()){
                        Sclat.sendWorldBorderWarningClearPacket(target);
                    }
                    cancel();
                }

                if(c == 10){
                    if(as9.getPassengers().contains(p)){
                        as9.removePassenger(p);
                        ((CraftPlayer)p).getHandle().stopRiding();
                    }
                    WeaponClassMgr.setWeaponClass(p);
                }
                c++;
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 2);
    }
}
