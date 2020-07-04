package be4rjp.sclat.manager;

import be4rjp.sclat.Main;
import static be4rjp.sclat.Main.conf;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.Path;
import be4rjp.sclat.data.SplashShieldData;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.server.v1_13_R1.EnumItemSlot;
import net.minecraft.server.v1_13_R1.PacketPlayOutEntityEquipment;
import static org.bukkit.Bukkit.getServer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_13_R1.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 *
 * @author Be4rJP
 */
public class ArmorStandMgr {
    
    private static boolean spawnedStand = false;
    
    public static boolean getIsSpawned(){
        return spawnedStand;
    }
    
    public static void setIsSpawned(boolean is){
        spawnedStand = is;
    }
    
    public static void ArmorStandSetup(Player player){
        for(Entity e : player.getWorld().getEntities()){
            if(e instanceof ArmorStand)
                e.remove();
        }
        
        for (String name : conf.getArmorStandSettings().getConfigurationSection("ArmorStand").getKeys(false)){
            World w = getServer().getWorld(conf.getArmorStandSettings().getString("ArmorStand." + name + ".WorldName"));
            int ix = conf.getArmorStandSettings().getInt("ArmorStand." + name + ".X");
            int iy = conf.getArmorStandSettings().getInt("ArmorStand." + name + ".Y");
            int iz = conf.getArmorStandSettings().getInt("ArmorStand." + name + ".Z");
            int iyaw = conf.getArmorStandSettings().getInt("ArmorStand." + name + ".Yaw");
            Location il = new Location(w, ix + 0.5D, iy, iz + 0.5D);
            il.setYaw(iyaw);
            ArmorStand as = (ArmorStand) w.spawnEntity(il, EntityType.ARMOR_STAND);
            as.setHelmet(new ItemStack(Material.LEATHER_HELMET));
            as.setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
            as.setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
            as.setBoots(new ItemStack(Material.LEATHER_BOOTS));
            as.setCustomName("20");
            as.setCustomNameVisible(true);
            as.setVisible(true);
            DataMgr.setArmorStandPlayer(as, player);
        }
    }
    
    public static void BeaconArmorStandSetup(Player player){
        Location al;
        if(conf.getConfig().getString("WorkMode").equals("Trial"))
            al = Main.lobby;
        else
            al = DataMgr.getPlayerData(player).getMatchLocation();
        ArmorStand as = (ArmorStand) player.getWorld().spawnEntity(al, EntityType.ARMOR_STAND);
        as.setVisible(false);
        as.setSmall(true);
        as.setGravity(false);
        as.setCustomName("100");
        as.setBasePlate(false);
        as.setCustomNameVisible(false);
        DataMgr.setArmorStandPlayer(as, player);
        DataMgr.setBeaconFromPlayer(player, as);
        BukkitRunnable effect = new BukkitRunnable(){
            Player p = player;
            int c = 0;
            @Override
            public void run(){
                if(as.getCustomName().equals("21")){
                    Particle.DustOptions dustOptions = new Particle.DustOptions(DataMgr.getPlayerData(p).getTeam().getTeamColor().getBukkitColor(), 1);
                    p.getWorld().spawnParticle(Particle.REDSTONE, as.getLocation().add(0, 0.3, 0), 3, 0.3, 0.3, 0.3, 1, dustOptions);
                    if(c % 10 == 0){
                        for (Player player : Main.getPlugin().getServer().getOnlinePlayers()) {
                            if(as.getWorld() == player.getWorld()){
                                ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(as.getEntityId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(Material.IRON_TRAPDOOR))));
                            }
                        }
                    }
                    c++;
                }
                if(!DataMgr.getPlayerData(p).isInMatch() || !p.isOnline())
                    cancel();
            }
        };
        effect.runTaskTimer(Main.getPlugin(), 0, 2);
        
        BukkitRunnable task2 = new BukkitRunnable(){
            Player p = player;
            @Override
            public void run(){
                Location loc = as.getLocation();
                float yaw = as.getLocation().getYaw();
                if(yaw >= 175)
                    yaw = -180;
                yaw+=3;
                loc.setYaw(yaw);
                as.teleport(loc);
                if(!DataMgr.getPlayerData(p).isInMatch() || !p.isOnline())
                    cancel();
            }
        };
        task2.runTaskTimer(Main.getPlugin(), 0, 1);
    }
    
    public static void SprinklerArmorStandSetup(Player player){
        Location al;
        if(conf.getConfig().getString("WorkMode").equals("Trial"))
            al = Main.lobby;
        else
            al = DataMgr.getPlayerData(player).getMatchLocation();
        ArmorStand as = (ArmorStand) player.getWorld().spawnEntity(al, EntityType.ARMOR_STAND);
        as.setVisible(false);
        as.setSmall(true);
        as.setGravity(false);
        as.setCustomName("100");
        as.setBasePlate(false);
        as.setCustomNameVisible(false);
        DataMgr.setArmorStandPlayer(as, player);
        DataMgr.setSprinklerFromPlayer(player, as);
        BukkitRunnable task = new BukkitRunnable(){
            Player p = player;
            int c = 0;
            @Override
            public void run(){
                if(as.getCustomName().equals("21")){
                    Particle.DustOptions dustOptions = new Particle.DustOptions(DataMgr.getPlayerData(p).getTeam().getTeamColor().getBukkitColor(), 1);
                    p.getWorld().spawnParticle(Particle.REDSTONE, as.getLocation().add(0, 0.3, 0), 3, 0.3, 0.3, 0.3, 1, dustOptions);
                    if(c % 10 == 0){
                        for (Player player : Main.getPlugin().getServer().getOnlinePlayers()) {
                            if(as.getWorld() == player.getWorld()){
                                ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(as.getEntityId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(DataMgr.getPlayerData(p).getTeam().getTeamColor().getGlass()))));
                            }
                        }
                    }
                    c++;
                }
                if(!DataMgr.getPlayerData(p).isInMatch() || !p.isOnline())
                    cancel();
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 2);
        
        BukkitRunnable task2 = new BukkitRunnable(){
            Player p = player;
            @Override
            public void run(){
                Location loc = as.getLocation();
                float yaw = as.getLocation().getYaw();
                if(yaw >= 175)
                    yaw = -180;
                yaw+=3;
                loc.setYaw(yaw);
                as.teleport(loc);
                if(!DataMgr.getPlayerData(p).isInMatch() || !p.isOnline())
                    cancel();
            }
        };
        task2.runTaskTimer(Main.getPlugin(), 0, 1);
        
        BukkitRunnable shoot = new BukkitRunnable(){
            Player p = player;
            @Override
            public void run(){
                if(as.getCustomName().equals("21")){
                    Block b = as.getLocation().add(0, 0.5, 0).getBlock();
                    Block u = b.getRelative(BlockFace.UP);
                    Block n = b.getRelative(BlockFace.NORTH);
                    Block s = b.getRelative(BlockFace.SOUTH);
                    Block w = b.getRelative(BlockFace.WEST);
                    Block e = b.getRelative(BlockFace.EAST);
                    Block d = b.getRelative(BlockFace.DOWN);
                    
                    Vector vec = new Vector(0, 1, 0);
                    
                    if(!n.getType().equals(Material.AIR))
                        vec = new Vector(0, 0, 0.5);
                    if(!s.getType().equals(Material.AIR))
                        vec = new Vector(0, 0, -0.5);
                    if(!w.getType().equals(Material.AIR))
                        vec = new Vector(0.5, 0, 0);
                    if(!e.getType().equals(Material.AIR))
                        vec = new Vector(-0.5, 0, 0);
                    if(!u.getType().equals(Material.AIR))
                        vec = new Vector(0, -0.5, 0);
                    if(!d.getType().equals(Material.AIR))
                        vec = new Vector(0, 0.5, 0);
                    SprinklerMgr.sprinklerShoot(p, as, vec);
                    if(!DataMgr.getPlayerData(p).isInMatch() || !p.isOnline())
                        cancel();
                }
            }
        };
        
        shoot.runTaskTimer(Main.getPlugin(), 0, 4);
        
        
    }
    
    
    public static void giveDamageArmorStand(ArmorStand as, double damage, Player shooter){
        if(as.getCustomName() == null)
            return;
        if(as.getCustomName().equals("SplashShield")){
            SplashShieldData ssdata = DataMgr.getSplashShieldDataFromArmorStand(as);
            if(DataMgr.getPlayerData(ssdata.getPlayer()).getTeam() != DataMgr.getPlayerData(shooter).getTeam()){
                ssdata.setDamage(ssdata.getDamage() + DataMgr.getPlayerData(shooter).getWeaponClass().getMainWeapon().getDamage());
                as.getWorld().playSound(as.getLocation(), Sound.ENTITY_PLAYER_HURT, 0.8F, 1.2F);
            }
            return;
        }
        
        if(as.getCustomName().equals("Path")){
            for (Path path : DataMgr.getPlayerData(shooter).getMatch().getMapData().getPathList()){
                if(path.getArmorStand().equals(as))
                    path.setTeam(DataMgr.getPlayerData(shooter).getTeam());
            }
            return;
        }
        
        double health = Double.parseDouble(as.getCustomName());
        if(health <= 20){
            if(as.isVisible()){
                if(health > damage){
                    int h = (int)(health - damage);
                    as.setCustomName(String.valueOf(h));
                    as.getLocation().getWorld().playSound(as.getLocation(), Sound.ENTITY_PLAYER_HURT, 1, 1);
                }else{
                    shooter.playSound(shooter.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 10);
                    Item drop1 = as.getWorld().dropItem(as.getEyeLocation(), new ItemStack(Material.LEATHER_HELMET));
                    Item drop2 = as.getWorld().dropItem(as.getEyeLocation(), new ItemStack(Material.LEATHER_CHESTPLATE));
                    Item drop3 = as.getWorld().dropItem(as.getEyeLocation(), new ItemStack(Material.LEATHER_LEGGINGS));
                    Item drop4 = as.getWorld().dropItem(as.getEyeLocation(), new ItemStack(Material.LEATHER_BOOTS));
                    final double random = 0.4;
                    drop1.setVelocity(new Vector(Math.random() * random - random/2, random * 2/3, Math.random() * random - random/2));
                    drop2.setVelocity(new Vector(Math.random() * random - random/2, random * 2/3, Math.random() * random - random/2));
                    drop3.setVelocity(new Vector(Math.random() * random - random/2, random * 2/3, Math.random() * random - random/2));
                    drop4.setVelocity(new Vector(Math.random() * random - random/2, random * 2/3, Math.random() * random - random/2));

                    org.bukkit.block.data.BlockData bd = DataMgr.getPlayerData(shooter).getTeam().getTeamColor().getWool().createBlockData();
                    as.getWorld().spawnParticle(org.bukkit.Particle.BLOCK_DUST, as.getEyeLocation(), 15, 1, 1, 1, 1, bd);

                    as.setCustomNameVisible(false);
                    as.setVisible(false);
                    as.setHelmet(new ItemStack(Material.AIR));
                    as.setChestplate(new ItemStack(Material.AIR));
                    as.setLeggings(new ItemStack(Material.AIR));
                    as.setBoots(new ItemStack(Material.AIR));


                    BukkitRunnable delay = new BukkitRunnable(){
                        @Override
                        public void run(){
                            drop1.remove();
                            drop2.remove();
                            drop3.remove();
                            drop4.remove();
                            as.setCustomNameVisible(true);
                            as.setVisible(true);
                            as.setHelmet(new ItemStack(Material.LEATHER_HELMET));
                            as.setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
                            as.setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
                            as.setBoots(new ItemStack(Material.LEATHER_BOOTS));
                            as.setCustomName("20");
                        }
                    };
                    delay.runTaskLater(Main.getPlugin(), 60);

                }
            }
        }else if(health == 21){
            Player player = DataMgr.getArmorStandPlayer(as);
            if(DataMgr.getPlayerData(shooter).getTeam() != DataMgr.getPlayerData(player).getTeam()){
                as.setCustomName("100");
                as.setVisible(false);
                as.setHelmet(new ItemStack(Material.AIR));
                as.getLocation().getWorld().playSound(as.getLocation(), Sound.ENTITY_ARROW_HIT, 1, 2F);
                as.teleport(as.getLocation().add(0, -1, 0));
            }
        }
    }
}
