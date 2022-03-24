package be4rjp.sclat.weapon.subweapon;

import be4rjp.sclat.Main;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import be4rjp.sclat.data.TrapData;
import be4rjp.sclat.manager.PaintMgr;
import be4rjp.sclat.weapon.Gear;
import net.minecraft.server.v1_14_R1.EnumItemSlot;
import net.minecraft.server.v1_14_R1.PacketPlayOutEntityEquipment;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class Trap{
    public static void useTrap(Player player){
        PlayerData data = DataMgr.getPlayerData(player);
        Block block = player.getLocation().add(0, -1, 0).getBlock();
        if(data.getTeam() != null && PaintMgr.canPaint(block)) {
            PaintMgr.Paint(block.getLocation(), player, true);
            if (player.isOnGround() && DataMgr.getPlayerData(player).isInMatch() && player.getExp() >= 0.4 / Gear.getGearInfluence(player, Gear.Type.SUB_SPEC_UP)) {
                TrapData trapData = new TrapData(player.getLocation().add(0, -1, 0), player, data.getTeam(), data.getTrapCount());
                data.addTrapCount();
                player.setExp(player.getExp() - (float)(0.39 / Gear.getGearInfluence(player, Gear.Type.SUB_SPEC_UP)));
                player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_ON, 1F, 1.2F);
            }else if (player.getExp() < (float) (0.4 / Gear.getGearInfluence(player, Gear.Type.SUB_SPEC_UP))){
                player.sendTitle("", ChatColor.RED + "インクが足りません", 0, 5, 2);
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1F, 1.63F);
            }
            else if(!player.isOnGround()){
                player.sendTitle("", ChatColor.RED + "空中では使用できません", 0, 5, 2);
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1F, 1.63F);
            }
        }else if (!PaintMgr.canPaint(block)){
            player.sendTitle("", ChatColor.RED + "ここでは使用できません", 0, 5, 2);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1F, 1.63F);
        }
    
        BukkitRunnable delay = new BukkitRunnable(){
            @Override
            public void run(){
                PlayerData data = DataMgr.getPlayerData(player);
                data.setCanUseSubWeapon(true);
            }
        };
        delay.runTaskLater(Main.getPlugin(), 20);
    }
}