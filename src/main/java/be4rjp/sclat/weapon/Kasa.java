
package be4rjp.sclat.weapon;

import be4rjp.sclat.Main;
import static be4rjp.sclat.Main.conf;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.KasaData;
import be4rjp.sclat.data.PlayerData;
import be4rjp.sclat.data.Team;
import be4rjp.sclat.manager.MainWeaponMgr;
import be4rjp.sclat.manager.PaintMgr;
import static be4rjp.sclat.weapon.Bucket.Shoot;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.chart.PieChart;
import net.minecraft.server.v1_13_R1.EnumItemSlot;
import net.minecraft.server.v1_13_R1.PacketPlayOutEntityEquipment;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_13_R1.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
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
            Player p = player;
            @Override
            public void run(){
                for (int i = 0; i < data.getWeaponClass().getMainWeapon().getRollerShootQuantity(); i++) 
                    Shoot(player, null);
                player.playSound(p.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 0.9F, 1.3F);
            }
        };
        if(data.getCanRollerShoot()){
            delay.runTaskLater(Main.getPlugin(), data.getWeaponClass().getMainWeapon().getDelay());
            data.setCanRollerShoot(false);
        }
    }
    
    public static void Shoot(Player player, Vector v){
        PlayerData data = DataMgr.getPlayerData(player);
        if(player.getExp() <= (float)(data.getWeaponClass().getMainWeapon().getNeedInk() / Gear.getGearInfluence(player, Gear.Type.MAIN_INK_EFFICIENCY_UP))){
            player.sendTitle("", ChatColor.RED + "インクが足りません", 0, 13, 2);
            return;
        }
        player.setExp(player.getExp() - (float)(data.getWeaponClass().getMainWeapon().getNeedInk() / Gear.getGearInfluence(player, Gear.Type.MAIN_INK_EFFICIENCY_UP)));
        Snowball ball = player.launchProjectile(Snowball.class);
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
                        i+=DataMgr.getSnowballHitCount(name);
                        DataMgr.setSnowballHitCount(name, 0);
                    }
                    
                for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                    if(!DataMgr.getPlayerData(target).getSettings().ShowEffect_RollerShot())
                        continue;
                    if(target.getWorld() == inkball.getWorld()){
                        if(target.getLocation().distance(inkball.getLocation()) < conf.getConfig().getInt("ParticlesRenderDistance")){
                            org.bukkit.block.data.BlockData bd = DataMgr.getPlayerData(p).getTeam().getTeamColor().getWool().createBlockData();
                            target.spawnParticle(org.bukkit.Particle.BLOCK_DUST, inkball.getLocation(), 1, 0, 0, 0, 1, bd);
                        }
                    }
                }

                if(i >= tick && !addedFallVec){
                    inkball.setVelocity(fallvec);
                    addedFallVec = true;
                }
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
    }
    
    public static void KasaRunnable(Player player){
        KasaData kdata = new KasaData(player);
        DataMgr.setKasaDataWithPlayer(player, kdata);
        
        BukkitRunnable task = new BukkitRunnable() {
            Player p = player;
            int i = 0;
            List<ArmorStand> list = new ArrayList<ArmorStand>();
            boolean weapon = false;
            boolean sound = false;

            ArmorStand as1;
            ArmorStand as2;
            ArmorStand as3;
            ArmorStand as4;
            ArmorStand as5;
            ArmorStand as6;
            ArmorStand as7;

            @Override
            public void run() {
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
                                as.teleport(p.getLocation().add(0, 25, 0));
                            }
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
}
