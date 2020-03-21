package be4rjp.sclat.manager;

import static be4rjp.sclat.Main.conf;
import be4rjp.sclat.data.DataMgr;
import static org.bukkit.Bukkit.getServer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
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
    public static void ArmorStandSetup(){
        for (String name : conf.getArmorStandSettings().getConfigurationSection("ArmorStand").getKeys(false)){
            World w = getServer().getWorld(conf.getArmorStandSettings().getString("ArmorStand." + name + ".WorldName"));
            int ix = conf.getArmorStandSettings().getInt("ArmorStand." + name + ".X");
            int iy = conf.getArmorStandSettings().getInt("ArmorStand." + name + ".Y");
            int iz = conf.getArmorStandSettings().getInt("ArmorStand." + name + ".Z");
            int iyaw = conf.getArmorStandSettings().getInt("ArmorStand." + name + ".Yaw");
            Location il = new Location(w, ix, iy, iz);
            il.setYaw(iyaw);
            ArmorStand as = (ArmorStand) w.spawnEntity(il, EntityType.ARMOR_STAND);
            as.setHelmet(new ItemStack(Material.LEATHER_HELMET));
            as.setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
            as.setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
            as.setBoots(new ItemStack(Material.LEATHER_BOOTS));
            as.setCustomName("20");
            as.setCustomNameVisible(true);
            as.setVisible(true);
        }
    }
    
    public static void giveDamageArmorStand(ArmorStand as, double damage, Player shooter){
        double health = Double.parseDouble(as.getCustomName());
        if(health > damage){
            int h = (int)(health - damage);
            as.setCustomName(String.valueOf(h));
        }else{
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
            as.getWorld().spawnParticle(org.bukkit.Particle.BLOCK_DUST, as.getEyeLocation(), 15, 1, 0, 1, 1, bd);
            
            as.setVisible(false);
            
            
            BukkitRunnable delay = new BukkitRunnable(){
                @Override
                public void run(){
                    drop1.remove();
                    drop2.remove();
                    drop3.remove();
                    drop4.remove();
                    as.setVisible(true);
                    as.setCustomName("20");
                }
            };
        }
    }
}
