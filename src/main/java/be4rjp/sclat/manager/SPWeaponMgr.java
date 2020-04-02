
package be4rjp.sclat.manager;

import be4rjp.sclat.GaugeAPI;
import be4rjp.sclat.Main;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import be4rjp.sclat.weapon.spweapon.AirStrike;
import be4rjp.sclat.weapon.spweapon.BombRush;
import be4rjp.sclat.weapon.spweapon.SuperArmor;
import be4rjp.sclat.weapon.spweapon.SuperSensor;
import be4rjp.sclat.weapon.subweapon.QuickBomb;
import be4rjp.sclat.weapon.subweapon.SplashBomb;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Be4rJP
 */
public class SPWeaponMgr {
    
    public static void addSPCharge(Player player){
        PlayerData data = DataMgr.getPlayerData(player);
        if(data.getSPGauge() < 100)
            data.addSPGauge();
    }
    
    public static void resetSPCharge(Player player){
        PlayerData data = DataMgr.getPlayerData(player);
        if(data.getSPGauge() > 20)
            data.setSPGauge(20);
    }
    
    public static String getSPGauge(Player player){
        PlayerData data = DataMgr.getPlayerData(player);
        if(data.getSPGauge() == 100)
            return "§b§n! READY !";
        return GaugeAPI.toGauge(data.getSPGauge() / 5, 20, "§a", "§7");
    }
    
    public static void SPWeaponRunnable(Player player){
        BukkitRunnable task = new BukkitRunnable(){
            Player p = player;
            
            @Override
            public void run(){
                PlayerData data = DataMgr.getPlayerData(p);
                if(data.getSPGauge() == 100){
                    if(!DataMgr.getPlayerData(p).getIsSP()){
                        setSPWeapon(p);
                        p.playSound(p.getLocation(), Sound.BLOCK_CHEST_OPEN, 0.8F, 2);
                        p.sendMessage("§6§l！ スペシャルウエポン使用可能 ！");
                    }
                    DataMgr.getPlayerData(p).setIsSP(true);
                }else{
                    p.getInventory().setItem(4, new ItemStack(Material.AIR));
                    DataMgr.getPlayerData(p).setIsSP(false);
                }
                if(!DataMgr.getPlayerData(p).isInMatch())
                    cancel();
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 20);
    }
    
    public static void setSPWeapon(Player p){
        PlayerData data = DataMgr.getPlayerData(p);
        switch (data.getWeaponClass().getSPWeaponName()){
            case "スーパーアーマー":
                ItemStack is = new ItemStack(Material.TOTEM_OF_UNDYING);
                ItemMeta ism = is.getItemMeta();
                ism.setDisplayName("スーパーアーマー");
                is.setItemMeta(ism);
                p.getInventory().setItem(4, is); 
                break;
            case "ボムラッシュ":
                ItemStack is1 = new ItemStack(Material.FEATHER);
                ItemMeta ism1 = is1.getItemMeta();
                ism1.setDisplayName("ボムラッシュ");
                is1.setItemMeta(ism1);
                p.getInventory().setItem(4, is1);
                break;
            case "スーパーセンサー":
                ItemStack is2 = new ItemStack(Material.NETHER_STAR);
                ItemMeta ism2 = is2.getItemMeta();
                ism2.setDisplayName("スーパーセンサー");
                is2.setItemMeta(ism2);
                p.getInventory().setItem(4, is2);
                break;
            case "インクストライク":
                ItemStack is3 = new ItemStack(Material.ARROW);
                ItemMeta ism3 = is3.getItemMeta();
                ism3.setDisplayName("インクストライク");
                is3.setItemMeta(ism3);
                p.getInventory().setItem(4, is3);
                break;
        }
    }
    
    public static void UseSPWeapon(Player player, String name){
        PlayerData data = DataMgr.getPlayerData(player);
        switch (name) {
            case "スーパーアーマー":
                SuperArmor.setArmor(player, 20, 160, true);
                player.getInventory().setItem(4, new ItemStack(Material.AIR));
                data.setSPGauge(0);
                player.setExp(0.99F);
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 2);
                break;
            case "ボムラッシュ":
                BombRush.BombRushRunnable(player);
                player.getInventory().setItem(4, new ItemStack(Material.AIR));
                data.setSPGauge(0);
                player.setExp(0.99F);
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 2);
                break;
            case "スーパーセンサー":
                SuperSensor.SuperSensorRunnable(player);
                player.getInventory().setItem(4, new ItemStack(Material.AIR));
                data.setSPGauge(0);
                player.setExp(0.99F);
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 2);
                break;
            case "インクストライク":
                MapKitMgr.setMapKit(player);
                data.setSPGauge(0);
                player.setExp(0.99F);
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 2);
                break;
             case "カーソルを合わせて右クリックで発射":
                AirStrike.AirStrikeRunnable(player);
                break;
        }
    }
}
