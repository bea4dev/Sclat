
package be4rjp.sclat.weapon.subweapon;

import be4rjp.sclat.Main;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import be4rjp.sclat.weapon.Gear;
import net.minecraft.server.v1_14_R1.EnumItemSlot;
import net.minecraft.server.v1_14_R1.PacketPlayOutEntityEquipment;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Be4rJP
 */
public class Beacon {
    public static void setBeacon(Player player){
        if(player.isOnGround() && DataMgr.getPlayerData(player).isInMatch() && player.getExp() >= 0.4 / Gear.getGearInfluence(player, Gear.Type.SUB_SPEC_UP)){
            ArmorStand as = DataMgr.getBeaconFromplayer(player);
            as.setVisible(false);
            for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                if(as.getWorld() != target.getWorld())
                    continue;
                ((CraftPlayer)target).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(as.getEntityId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(Material.AIR))));
            }
            as.teleport(player.getLocation().add(0, -0.45, 0));
            as.setCustomName("21");
            player.setExp(player.getExp() - (float)(0.39 / Gear.getGearInfluence(player, Gear.Type.SUB_SPEC_UP)));
            BukkitRunnable delay = new BukkitRunnable(){
                @Override
                public void run(){
                    as.setVisible(true);
                    for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                        if(as.getWorld() != target.getWorld())
                            continue;
                        ((CraftPlayer)target).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(as.getEntityId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(Material.IRON_TRAPDOOR))));
                    }
                    as.getWorld().playSound(as.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 1F, 1F);
                    //as.setHelmet(new ItemStack(Material.IRON_TRAPDOOR));
                }
            };
            delay.runTaskLater(Main.getPlugin(), 10);
        }else if(player.getExp() < (float)(0.4 / Gear.getGearInfluence(player, Gear.Type.SUB_SPEC_UP))){
            player.sendTitle("", ChatColor.RED + "インクが足りません", 0, 5, 2);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1F, 1.63F);
        }
        
        BukkitRunnable delay = new BukkitRunnable(){
            Player p = player;
            @Override
            public void run(){
                PlayerData data = DataMgr.getPlayerData(player);
                data.setCanUseSubWeapon(true);
            }
        };
        delay.runTaskLater(Main.getPlugin(), 20);
    }
}
