
package be4rjp.sclat.weapon;

import be4rjp.sclat.Main;
import static be4rjp.sclat.Main.conf;

import be4rjp.sclat.Sclat;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.KasaData;
import be4rjp.sclat.data.PlayerData;
import be4rjp.sclat.data.Team;
import be4rjp.sclat.manager.MainWeaponMgr;
import be4rjp.sclat.manager.PaintMgr;
import be4rjp.sclat.manager.WeaponClassMgr;
import be4rjp.sclat.raytrace.BoundingBox;
import be4rjp.sclat.raytrace.RayTrace;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.v1_14_R1.EnumItemSlot;
import net.minecraft.server.v1_14_R1.PacketPlayOutEntityEquipment;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftSnowball;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

/**
 *
 * @author Be4rJP
 */
public class Kasa {
    public static void ShootKasa(Player player){
        
        if(player.isSneaking())
            return;
        
        PlayerData data = DataMgr.getPlayerData(player);
        BukkitRunnable delay1 = new BukkitRunnable(){
            Player p = player;
            @Override
            public void run(){
                PlayerData data = DataMgr.getPlayerData(player);
                data.setCanRollerShoot(true);
            }
        };
        if(data.getCanRollerShoot())
            delay1.runTaskLater(Main.getPlugin(), data.getWeaponClass().getMainWeapon().getCoolTime());
        
        BukkitRunnable delay = new BukkitRunnable(){
            final Player p = player;
            @Override
            public void run(){
                boolean sound = false;
                for (int i = 0; i < data.getWeaponClass().getMainWeapon().getRollerShootQuantity(); i++) {
                    boolean is = Shoot(player, null);
                    if(is) sound = true;
                }
                player.getWorld().playSound(p.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 0.9F, 1.3F);
                if(sound){
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1F, 1.63F);
                }
            }
        };
        if(data.getCanRollerShoot()){
            delay.runTaskLater(Main.getPlugin(), data.getWeaponClass().getMainWeapon().getDelay());
            data.setCanRollerShoot(false);
        }
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
        vec.add(new Vector(Math.random() * random - random/2, Math.random() * random/1.5 - random/3, Math.random() * random - random/2));
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
                        if (!DataMgr.getPlayerData(target).getSettings().ShowEffect_MainWeaponInk())
                            continue;
                        if (target.getWorld() == inkball.getWorld()) {
                            if (target.getLocation().distanceSquared(inkball.getLocation()) < Main.PARTICLE_RENDER_DISTANCE_SQUARED) {
                                org.bukkit.block.data.BlockData bd = DataMgr.getPlayerData(p).getTeam().getTeamColor().getWool().createBlockData();
                                target.spawnParticle(org.bukkit.Particle.BLOCK_DUST, inkball.getLocation(), 1, 0, 0, 0, 1, bd);
                            }
                        }
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
    
    public static void KasaRunnable(Player player, boolean big){
        KasaData kdata = new KasaData(player);
        DataMgr.setKasaDataWithPlayer(player, kdata);
        
        BukkitRunnable task = new BukkitRunnable() {
            Player p = player;
            int i = 0;
            List<ArmorStand> list = new ArrayList<ArmorStand>();
            boolean weapon = false;
            boolean sound = true;

            ArmorStand as1;
            ArmorStand as2;
            ArmorStand as3;
            ArmorStand as4;
            ArmorStand as5;
            ArmorStand as6;
            ArmorStand as7;

            @Override
            public void run() {
                try{
                    PlayerData data = DataMgr.getPlayerData(p);

                    try {
                        if(MainWeaponMgr.equalWeapon(p))
                            weapon = true;
                        else
                            weapon = false;
                    } catch (Exception e) {
                        weapon = false;
                    }

                    if(data.getIsSneaking() && kdata.getDamage() <= 200){
                        if(!sound){
                            sound = true;
                            p.getWorld().playSound(p.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 1F, 1F);
                        }
                    }else{
                        sound = false;
                    }

                    Location ploc = player.getLocation();
                    Vector pvec = player.getEyeLocation().getDirection().normalize();
                    Vector vec = new Vector(pvec.getX(), 0, pvec.getZ()).normalize().multiply(1.3);
                    Vector mvec = vec.clone().normalize().multiply(-1.2);
                    Location aml = ploc.clone().add(vec.getX(), -1.15, vec.getZ());
                    Location aml2 = ploc.clone().add(vec.getX() * 0.8, -1.15, vec.getZ() * 0.8);
                    Vector v1 = new Vector(vec.getZ() * -1, 0 , vec.getX()).normalize().multiply(0.31);
                    Vector v2 = new Vector(vec.getZ(), 0, vec.getX() * -1).normalize().multiply(0.31);
                    Location rl = aml.clone().add(v1);
                    Location rl2 = aml2.clone().add(v1);
                    Location ll = aml.clone().add(v2);
                    Location ll2 = aml2.clone().add(v2);

                    if(i == 0){
                        as1 = (ArmorStand)player.getWorld().spawnEntity(rl.clone().add(0, 0.2, 0), EntityType.ARMOR_STAND);
                        as2 = (ArmorStand)player.getWorld().spawnEntity(rl2.clone().add(0, -0.52, 0), EntityType.ARMOR_STAND);
                        as3 = (ArmorStand)player.getWorld().spawnEntity(ll.clone().add(0, 0.2, 0), EntityType.ARMOR_STAND);
                        as4 = (ArmorStand)player.getWorld().spawnEntity(ll2.clone().add(0, -0.52, 0), EntityType.ARMOR_STAND);
                        as5 = (ArmorStand)player.getWorld().spawnEntity(aml.clone().add(mvec.getX(), 0.35, mvec.getZ()), EntityType.ARMOR_STAND);
                        as6 = (ArmorStand)player.getWorld().spawnEntity(rl.clone().add(0, 0.8, 0), EntityType.ARMOR_STAND);
                        as7 = (ArmorStand)player.getWorld().spawnEntity(ll.clone().add(0, 0.8, 0), EntityType.ARMOR_STAND);
                        //as5.setSmall(true);
                        list.add(as1);
                        list.add(as2);
                        list.add(as4);
                        list.add(as3);
                        list.add(as5);
                        list.add(as6);
                        list.add(as7);

                        kdata.setArmorStandList(list);

                        int c = 1;
                        for(ArmorStand as : list){
                            //as.setHeadPose(new EulerAngle(Math.toRadians(90), 0, 0));
                            as.setBasePlate(false);
                            as.setVisible(false);
                            as.setGravity(false);
                            as.setCustomName("Kasa");
                            DataMgr.setKasaDataWithARmorStand(as, kdata);
                            /*
                            if(c <= 4){
                                for (Player o_player : Main.getPlugin().getServer().getOnlinePlayers()) {
                                    ((CraftPlayer)o_player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(as.getEntityId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(Material.WHITE_STAINED_GLASS_PANE))));
                                }
                            }
                            if(c == 5){
                                for (Player o_player : Main.getPlugin().getServer().getOnlinePlayers()) {
                                    ((CraftPlayer)o_player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(as.getEntityId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(Material.END_ROD))));
                                }
                            }*/
                            c++;
                        }

                        as1.setHeadPose(new EulerAngle(Math.toRadians(350), 0, 0));
                        as2.setHeadPose(new EulerAngle(Math.toRadians(10), 0, 0));
                        as3.setHeadPose(new EulerAngle(Math.toRadians(350), 0, 0));
                        as4.setHeadPose(new EulerAngle(Math.toRadians(10), 0, 0));
                        as5.setHeadPose(new EulerAngle(Math.toRadians(30), 0, 0));
                    }

                    if(i != 0){
                        if(p.isSneaking() && kdata.getDamage() <= 200 && weapon && p.getGameMode().equals(GameMode.ADVENTURE)){
                            as1.teleport(rl.clone().add(0, 0.2, 0));
                            as2.teleport(rl2.clone().add(0, -0.52, 0));
                            as3.teleport(ll.clone().add(0, 0.2, 0));
                            as4.teleport(ll2.clone().add(0, -0.52, 0));
                            as5.teleport(aml.clone().add(mvec.getX(), 0.35, mvec.getZ()));
                            as6.teleport(rl.clone().add(0, 1, 0));
                            as7.teleport(ll.clone().add(0, 1, 0));

                            if(i % 10 == 0){
                                ArmorStandItemDelay(list, p, kdata);
                            }
                        }else{
                            if(i % 5 == 0){
                                for(ArmorStand as : list){
                                    for (Player o_player : Main.getPlugin().getServer().getOnlinePlayers()) {
                                        ((CraftPlayer)o_player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(as.getEntityId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(Material.AIR))));
                                    }
                                }
                                ArmorStandTeleportDelay(list, p, kdata);
                            }
                        }
                    }

                    if(i % 100 == 0){
                        if(kdata.getDamage() - 50 > 0){
                            kdata.setDamage(kdata.getDamage() - 50);
                        }else{
                            kdata.setDamage(0);
                        }
                    }

                    if(p.getGameMode().equals(GameMode.SPECTATOR))
                        kdata.setDamage(0);


                    if(!data.isInMatch() || !p.isOnline()){
                        for(ArmorStand as : list)
                            as.remove();
                        cancel();
                    }

                    i++;
                }catch(Exception e){
                    cancel();
                }
            }
        };
        
        
        BukkitRunnable bigktask = new BukkitRunnable() {
            Player p = player;
            int i = 0;
            int c = 0;
            boolean is = true;

            @Override
            public void run() {
                if(p.isSneaking() && is && p.getGameMode() != GameMode.SPECTATOR && !DataMgr.getPlayerData(p).getIsUsingTyakuti()){
                    is = false;
                    Camping(p);
                    DataMgr.getPlayerData(p).setMainItemGlow(false);
                    if(!DataMgr.getPlayerData(p).getIsUsingSP())
                        WeaponClassMgr.setWeaponClass(p);
                }
                if(!is){
                    c++;
                    if(c == 180){
                        is = true;
                        c = 0;
                        DataMgr.getPlayerData(p).setMainItemGlow(true);
                        if(!DataMgr.getPlayerData(p).getIsUsingSP())
                            WeaponClassMgr.setWeaponClass(p);
                    }
                }
                i++;
                if(!p.isOnline() || !DataMgr.getPlayerData(p).isInMatch()){
                    cancel();
                }
            }
        };
        
        if(big)
            bigktask.runTaskTimer(Main.getPlugin(), 0, 1);
        else
            task.runTaskTimer(Main.getPlugin(), 0, 1);
    }
    
    public static void Camping(Player player){
        KasaData kdata = new KasaData(player);
        DataMgr.setKasaDataWithPlayer(player, kdata);
        player.getWorld().playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 1F, 1F);
        
        BukkitRunnable task = new BukkitRunnable() {
            Player p = player;
            int i = 0;
            boolean bp = false;
            boolean squid = true;

            Vector dir = new Vector(1, 0, 0);

            List<ArmorStand> list = new ArrayList<ArmorStand>();
            List<ArmorStand> ul = new ArrayList<ArmorStand>();
            List<ArmorStand> dl = new ArrayList<ArmorStand>();

            ArmorStand as1;
            ArmorStand as2;
            ArmorStand as3;
            ArmorStand as4;
            ArmorStand as5;
            ArmorStand as6;
            ArmorStand as7;
            ArmorStand as8;
            ArmorStand as9;
            ArmorStand as10;
            ArmorStand as11;
            ArmorStand as12;
            ArmorStand as13;
            ArmorStand as14;
            ArmorStand as15;
            ArmorStand as16;
            ArmorStand as17;
            ArmorStand as18;
            ArmorStand as19;
            ArmorStand as20;
            //ArmorStand as21;
            //ArmorStand as22;
            ArmorStand las;

            @Override
            public void run() {
                try{
                    Location loc = p.getLocation().add(0, -1.7, 0);

                    if(bp){
                        if(las.isOnGround())
                            las.setVelocity(dir.clone().multiply(0.18));
                        loc = las.getLocation().add(0, -1.7, 0);
                    }
                    Vector pv = p.getEyeLocation().getDirection().normalize();
                    Vector vec = new Vector(pv.getX(), 0, pv.getZ()).normalize();
                    if(bp)
                        vec = dir;
                    Vector mvec = vec.clone().multiply(-1);
                    Location floc = loc.add(vec.clone().multiply(1.5));
                    Vector vec1 = new Vector(vec.getZ() * -1, 0, vec.getX());
                    Vector vec2 = new Vector(vec.getZ(), 0, vec.getX() * -1);

                    Location l1 = floc.clone().add(vec1.clone().multiply(0.6));
                    Location l2 = floc.clone().add(vec1.clone().multiply(1.2));

                    Location r1 = floc.clone().add(vec2.clone().multiply(0.6));
                    Location r2 = floc.clone().add(vec2.clone().multiply(1.2));

                    if(i == 0){
                        as1 = (ArmorStand)p.getWorld().spawnEntity(floc.clone().add(0, -0.05, 0).add(mvec.clone().multiply(0.38)), EntityType.ARMOR_STAND);
                        as2 = (ArmorStand)p.getWorld().spawnEntity(floc.clone().add(0, 0.6, 0), EntityType.ARMOR_STAND);
                        as3 = (ArmorStand)p.getWorld().spawnEntity(floc.clone().add(0, 1.2, 0), EntityType.ARMOR_STAND);
                        as4 = (ArmorStand)p.getWorld().spawnEntity(floc.clone().add(0, 3.15, 0).add(mvec.clone().multiply(0.9)), EntityType.ARMOR_STAND);

                        as5 = (ArmorStand)p.getWorld().spawnEntity(l1.clone().add(0, -0.05, 0).add(mvec.clone().multiply(0.38)), EntityType.ARMOR_STAND);
                        as6 = (ArmorStand)p.getWorld().spawnEntity(l1.clone().add(0, 0.6, 0), EntityType.ARMOR_STAND);
                        as7 = (ArmorStand)p.getWorld().spawnEntity(l1.clone().add(0, 1.2, 0), EntityType.ARMOR_STAND);
                        as8 = (ArmorStand)p.getWorld().spawnEntity(l1.clone().add(0, 3.15, 0).add(mvec.clone().multiply(0.9)), EntityType.ARMOR_STAND);

                        as9 = (ArmorStand)p.getWorld().spawnEntity(l2.clone().add(0, -0.05, 0).add(mvec.clone().multiply(0.38)), EntityType.ARMOR_STAND);
                        as10 = (ArmorStand)p.getWorld().spawnEntity(l2.clone().add(0, 0.6, 0), EntityType.ARMOR_STAND);
                        as11 = (ArmorStand)p.getWorld().spawnEntity(l2.clone().add(0, 1.2, 0), EntityType.ARMOR_STAND);
                        as12 = (ArmorStand)p.getWorld().spawnEntity(l2.clone().add(0, 3.15, 0).add(mvec.clone().multiply(0.9)), EntityType.ARMOR_STAND);

                        as13 = (ArmorStand)p.getWorld().spawnEntity(r1.clone().add(0, -0.05, 0).add(mvec.clone().multiply(0.38)), EntityType.ARMOR_STAND);
                        as14 = (ArmorStand)p.getWorld().spawnEntity(r1.clone().add(0, 0.6, 0), EntityType.ARMOR_STAND);
                        as15 = (ArmorStand)p.getWorld().spawnEntity(r1.clone().add(0, 1.2, 0), EntityType.ARMOR_STAND);
                        as16 = (ArmorStand)p.getWorld().spawnEntity(r1.clone().add(0, 3.15, 0).add(mvec.clone().multiply(0.9)), EntityType.ARMOR_STAND);

                        as17 = (ArmorStand)p.getWorld().spawnEntity(r2.clone().add(0, -0.05, 0).add(mvec.clone().multiply(0.38)), EntityType.ARMOR_STAND);
                        as18 = (ArmorStand)p.getWorld().spawnEntity(r2.clone().add(0, 0.6, 0), EntityType.ARMOR_STAND);
                        as19 = (ArmorStand)p.getWorld().spawnEntity(r2.clone().add(0, 1.2, 0), EntityType.ARMOR_STAND);
                        as20 = (ArmorStand)p.getWorld().spawnEntity(r2.clone().add(0, 3.15, 0).add(mvec.clone().multiply(0.9)), EntityType.ARMOR_STAND);

                        list.add(as16);
                        list.add(as4);
                        list.add(as2);
                        list.add(as11);
                        list.add(as20);
                        list.add(as3);
                        list.add(as6);
                        list.add(as14);
                        list.add(as7);
                        list.add(as9);
                        list.add(as10);
                        list.add(as12);
                        list.add(as8);
                        list.add(as13);
                        list.add(as15);
                        list.add(as1);
                        list.add(as17);
                        list.add(as5);
                        list.add(as19);
                        list.add(as18);

                        dl.add(as1);
                        ul.add(as4);
                        dl.add(as5);
                        ul.add(as8);
                        dl.add(as9);
                        ul.add(as12);
                        dl.add(as13);
                        ul.add(as16);
                        dl.add(as17);
                        ul.add(as20);

                        List<ArmorStand> aslist = new ArrayList<ArmorStand>();
                        aslist.addAll(list);
                        kdata.setArmorStandList(aslist);

                        for(ArmorStand as : list){
                            //as.setHeadPose(new EulerAngle(Math.toRadians(90), 0, 0));
                            as.setBasePlate(false);
                            as.setVisible(false);
                            as.setGravity(false);
                            as.setCustomName("Kasa");
                            DataMgr.setKasaDataWithARmorStand(as, kdata);
                        }

                        for(ArmorStand as : ul){
                            as.setHeadPose(new EulerAngle(Math.toRadians(160), 0, 0));
                        }

                        for(ArmorStand as : dl){
                            as.setHeadPose(new EulerAngle(Math.toRadians(20), 0, 0));
                        }
                    }

                    if(i >= 0){
                        as1.teleport(floc.clone().add(0, -0.05, 0).add(mvec.clone().multiply(0.38)));
                        as2.teleport(floc.clone().add(0, 0.6, 0));
                        as3.teleport(floc.clone().add(0, 1.2, 0));
                        as4.teleport(floc.clone().add(0, 3.15, 0).add(mvec.clone().multiply(0.9)));

                        as5.teleport(l1.clone().add(0, -0.05, 0).add(mvec.clone().multiply(0.38)));
                        as6.teleport(l1.clone().add(0, 0.6, 0));
                        as7.teleport(l1.clone().add(0, 1.2, 0));
                        as8.teleport(l1.clone().add(0, 3.15, 0).add(mvec.clone().multiply(0.9)));

                        as9.teleport(l2.clone().add(0, -0.05, 0).add(mvec.clone().multiply(0.38)));
                        as10.teleport(l2.clone().add(0, 0.6, 0));
                        as11.teleport(l2.clone().add(0, 1.2, 0));
                        as12.teleport(l2.clone().add(0, 3.15, 0).add(mvec.clone().multiply(0.9)));

                        as13.teleport(r1.clone().add(0, -0.05, 0).add(mvec.clone().multiply(0.38)));
                        as14.teleport(r1.clone().add(0, 0.6, 0));
                        as15.teleport(r1.clone().add(0, 1.2, 0));
                        as16.teleport(r1.clone().add(0, 3.15, 0).add(mvec.clone().multiply(0.9)));

                        as17.teleport(r2.clone().add(0, -0.05, 0).add(mvec.clone().multiply(0.38)));
                        as18.teleport(r2.clone().add(0, 0.6, 0));
                        as19.teleport(r2.clone().add(0, 1.2, 0));
                        as20.teleport(r2.clone().add(0, 3.15, 0).add(mvec.clone().multiply(0.9)));

                        if(i % 2 == 0){
                            Location asl = as4.getLocation();
                            org.bukkit.block.data.BlockData bd = DataMgr.getPlayerData(p).getTeam().getTeamColor().getWool().createBlockData();
                            for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                                if(DataMgr.getPlayerData(target).getSettings().ShowEffect_MainWeaponInk())
                                    if(target.getWorld() == p.getWorld())
                                        if(target.getLocation().distanceSquared(asl) < Main.PARTICLE_RENDER_DISTANCE_SQUARED)
                                            target.spawnParticle(org.bukkit.Particle.BLOCK_DUST, asl, 1, 0, 0, 0, 1, bd);
                            }

                            for(ArmorStand as : ul){
                                PaintMgr.PaintHightestBlock(as.getLocation(), p, false, false);
                            }
                        }

                        if(i % 3 == 0){
                            for(ArmorStand as : dl){
                                RayTrace rayTrace = new RayTrace(as.getLocation().toVector(), new Vector(0, 1, 0));
                                double damage = 3;
                                for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                                    if(!DataMgr.getPlayerData(target).isInMatch())
                                        continue;
                                    if(DataMgr.getPlayerData(player).getTeam() != DataMgr.getPlayerData(target).getTeam() && target.getGameMode().equals(GameMode.ADVENTURE)){
                                        if(rayTrace.intersects(new BoundingBox((Entity)target), 5, 0.05)){
                                            Sclat.giveDamage(player, target, damage, "killed");

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
                            }
                        }


                        int c = 1;
                        for(ArmorStand as : list){
                            PlayerData data = DataMgr.getPlayerData(player);
                            Team team = data.getMatch().getTeam0();
                            if(team == data.getTeam())
                                team = data.getMatch().getTeam1();
                            for (Player o_player : Main.getPlugin().getServer().getOnlinePlayers()) {
                                if(kdata.getDamage() == 0){
                                    ((CraftPlayer)o_player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(as.getEntityId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(Material.WHITE_STAINED_GLASS_PANE))));
                                }
                                if(kdata.getDamage() > 0 && kdata.getDamage() <= 50){
                                    if(c == 1)
                                        ((CraftPlayer)o_player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(as.getEntityId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(Material.getMaterial(team.getTeamColor().getGlass().toString() + "_PANE")))));
                                    else
                                        ((CraftPlayer)o_player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(as.getEntityId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(Material.WHITE_STAINED_GLASS_PANE))));
                                }
                                if(kdata.getDamage() > 50 && kdata.getDamage() <= 100){
                                    if(c <= 2)
                                        ((CraftPlayer)o_player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(as.getEntityId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(Material.getMaterial(team.getTeamColor().getGlass().toString() + "_PANE")))));
                                    else
                                        ((CraftPlayer)o_player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(as.getEntityId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(Material.WHITE_STAINED_GLASS_PANE))));
                                }
                                if(kdata.getDamage() > 100 && kdata.getDamage() <= 150){
                                    if(c <= 3)
                                        ((CraftPlayer)o_player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(as.getEntityId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(Material.getMaterial(team.getTeamColor().getGlass().toString() + "_PANE")))));
                                    else
                                        ((CraftPlayer)o_player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(as.getEntityId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(Material.WHITE_STAINED_GLASS_PANE))));
                                }
                                if(kdata.getDamage() > 150){
                                    if(c <= 4)
                                        ((CraftPlayer)o_player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(as.getEntityId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(Material.getMaterial(team.getTeamColor().getGlass().toString() + "_PANE")))));
                                    else
                                        ((CraftPlayer)o_player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(as.getEntityId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(Material.WHITE_STAINED_GLASS_PANE))));
                                }
                            }
                            c++;
                        }
                    }

                    if((p.getInventory().getItemInMainHand().getType().equals(Material.AIR) || !p.isSneaking() || p.getGameMode() == GameMode.SPECTATOR) && squid && i < 39){
                        squid = false;
                        i = 39;
                    }

                    if(i == 40){
                        bp = true;
                        dir = vec.clone().multiply(1);
                        las = (ArmorStand)p.getWorld().spawnEntity(p.getLocation(), EntityType.ARMOR_STAND);
                        las.setVisible(false);
                        las.setGravity(true);
                        las.setCustomName("Kasa");
                        List<ArmorStand> l = kdata.getArmorStandList();
                        l.add(las);
                        kdata.setArmorStandList(l);
                        DataMgr.setKasaDataWithARmorStand(las, kdata);
                        p.playNote(p.getLocation(), Instrument.STICKS, Note.flat(1, Note.Tone.C));
                        p.playSound(p.getLocation(), Sound.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_ON, 1F, 1.2F);
                    }

                    if(i == 200 || kdata.getDamage() > 200 || !p.isOnline() || !DataMgr.getPlayerData(p).isInMatch()){
                        if(kdata.getDamage() <= 200 && DataMgr.getPlayerData(p).isInMatch())
                            as1.getWorld().playSound(as1.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.8F, 0.8F);

                        for(ArmorStand as : list){
                            as.remove();
                        }
                        las.remove();
                        cancel();
                    }

                    i++;
                }catch(Exception e){
                    cancel();
                }
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 1);
    }
    
    public static void ArmorStandItemDelay(List<ArmorStand> list, Player player, KasaData kdata){
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                PlayerData data = DataMgr.getPlayerData(player);
                Team team = data.getMatch().getTeam0();
                if(team == data.getTeam())
                    team = data.getMatch().getTeam1();

                int c = 1;
                for(ArmorStand as : list){
                    if(c <= 4){
                        for (Player o_player : Main.getPlugin().getServer().getOnlinePlayers()) {
                            if(kdata.getDamage() == 0){
                                ((CraftPlayer)o_player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(as.getEntityId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(Material.WHITE_STAINED_GLASS_PANE))));
                            }
                            if(kdata.getDamage() > 0 && kdata.getDamage() <= 50){
                                if(c == 1)
                                    ((CraftPlayer)o_player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(as.getEntityId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(Material.getMaterial(team.getTeamColor().getGlass().toString() + "_PANE")))));
                                else
                                    ((CraftPlayer)o_player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(as.getEntityId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(Material.WHITE_STAINED_GLASS_PANE))));
                            }
                            if(kdata.getDamage() > 50 && kdata.getDamage() <= 100){
                                if(c <= 2)
                                    ((CraftPlayer)o_player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(as.getEntityId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(Material.getMaterial(team.getTeamColor().getGlass().toString() + "_PANE")))));
                                else
                                    ((CraftPlayer)o_player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(as.getEntityId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(Material.WHITE_STAINED_GLASS_PANE))));
                            }
                            if(kdata.getDamage() > 100 && kdata.getDamage() <= 150){
                                if(c <= 3)
                                    ((CraftPlayer)o_player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(as.getEntityId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(Material.getMaterial(team.getTeamColor().getGlass().toString() + "_PANE")))));
                                else
                                    ((CraftPlayer)o_player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(as.getEntityId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(Material.WHITE_STAINED_GLASS_PANE))));
                            }
                            if(kdata.getDamage() > 150){
                                if(c <= 4)
                                    ((CraftPlayer)o_player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(as.getEntityId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(Material.getMaterial(team.getTeamColor().getGlass().toString() + "_PANE")))));
                                else
                                    ((CraftPlayer)o_player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(as.getEntityId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(Material.WHITE_STAINED_GLASS_PANE))));
                            }
                        }
                    }

                    if(c == 5){
                        for (Player o_player : Main.getPlugin().getServer().getOnlinePlayers()) {
                            ((CraftPlayer)o_player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(as.getEntityId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(Material.END_ROD))));
                        }
                    }

                    c++;
                }
            }
        };
        task.runTaskLater(Main.getPlugin(), 10);
    }
    
    public static void ArmorStandTeleportDelay(List<ArmorStand> list, Player player, KasaData kdata){
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                for(ArmorStand as : list)
                    as.teleport(player.getLocation().add(0, 50, 0));
            }
        };
        task.runTaskLater(Main.getPlugin(), 3);
    }
}
