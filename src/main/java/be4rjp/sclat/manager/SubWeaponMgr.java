
package be4rjp.sclat.manager;

import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import be4rjp.sclat.weapon.spweapon.JetPack;
import be4rjp.sclat.weapon.subweapon.Beacon;
import be4rjp.sclat.weapon.subweapon.KBomb;
import be4rjp.sclat.weapon.subweapon.Poison;
import be4rjp.sclat.weapon.subweapon.QuickBomb;
import be4rjp.sclat.weapon.subweapon.Sensor;
import be4rjp.sclat.weapon.subweapon.SplashBomb;
import be4rjp.sclat.weapon.subweapon.Sprinkler;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Be4rJP
 */
public class SubWeaponMgr {
    public static ItemStack getSubWeapon(Player player){
        PlayerData data = DataMgr.getPlayerData(player);
        //player.sendMessage(data.getWeaponClass().getSubWeaponName());
        ItemStack is = null;
        ItemMeta ism = null;
        
        switch (data.getWeaponClass().getSubWeaponName()) {
            case "スプラッシュボム":
                is = new ItemStack(data.getTeam().getTeamColor().getGlass());
                ism = is.getItemMeta();
                ism.setDisplayName("スプラッシュボム");
                break;
            case "クイックボム":
                is = new ItemStack(data.getTeam().getTeamColor().getWool());
                ism = is.getItemMeta();
                ism.setDisplayName("クイックボム");
                break;
            case "センサー":
                is = new ItemStack(Material.DISPENSER);
                ism = is.getItemMeta();
                ism.setDisplayName("センサー");
                break;
            case "ポイズン":
                is = new ItemStack(Material.PRISMARINE);
                ism = is.getItemMeta();
                ism.setDisplayName("ポイズン");
                break;
            case "キューバンボム":
                is = new ItemStack(data.getTeam().getTeamColor().getConcrete());
                ism = is.getItemMeta();
                ism.setDisplayName("キューバンボム");
                break;
            case "ビーコン":
                is = new ItemStack(Material.IRON_TRAPDOOR);
                ism = is.getItemMeta();
                ism.setDisplayName("ビーコン");
                break;
            case "スプリンクラー":
                is = new ItemStack(Material.BIRCH_FENCE_GATE);
                ism = is.getItemMeta();
                ism.setDisplayName("スプリンクラー");
                break;
        }
        is.setItemMeta(ism);
        //player.getInventory().setItem(2, is);  
        return is;
    }
    
    public static void UseSubWeapon(Player player, String name){
        PlayerData data = DataMgr.getPlayerData(player);
        if(!data.getCanUseSubWeapon()) return;
        if(player.getGameMode().equals(GameMode.SPECTATOR)) return;
        
        switch (name) {
            case "右クリックで弾を発射":
                JetPack.ShootJetPack(player);
                data.setCanUseSubWeapon(false);
                break;
        }
        
        if(data.getIsUsingJetPack()) return;
        
        switch (name) {
            case "スプラッシュボム":
                SplashBomb.SplashBomRunnable(player);
                data.setCanUseSubWeapon(false);
                break;
            case "クイックボム":
                QuickBomb.QuickBomRunnable(player);
                data.setCanUseSubWeapon(false);
                break;
            case "センサー":
                Sensor.SensorRunnable(player);
                data.setCanUseSubWeapon(false);
                break;
            case "ポイズン":
                Poison.PoisonRunnable(player);
                data.setCanUseSubWeapon(false);
                break;
            case "キューバンボム":
                KBomb.KBomRunnable(player);
                data.setCanUseSubWeapon(false);
                break;
            case "ビーコン":
                Beacon.setBeacon(player);
                data.setCanUseSubWeapon(false);
                break;
            case "スプリンクラー":
                Sprinkler.SprinklerRunnable(player);
                data.setCanUseSubWeapon(false);
                break;
        }
    }
}
