
package be4rjp.sclat.GUI;

import static be4rjp.sclat.Main.conf;
import be4rjp.sclat.data.DataMgr;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Be4rJP
 */
public class OpenGUI {
    public static void openMenu(Player player){
        Inventory inv = Bukkit.createInventory(null, 9, "メインメニュー");
        
        ItemStack join = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta joinmeta = join.getItemMeta();
        joinmeta.setDisplayName("試合に参加 / JOIN THE MATCH");
        join.setItemMeta(joinmeta);
        if(!conf.getConfig().getString("WorkMode").equals("Trial"))
            inv.setItem(2, join);
        
        ItemStack setting = new ItemStack(Material.COMPARATOR);
        ItemMeta setting_m = setting.getItemMeta();
        setting_m.setDisplayName("設定 / SETTINGS");
        setting.setItemMeta(setting_m);
        inv.setItem(4, setting);
        
        ItemStack w = new ItemStack(Material.WOODEN_HOE);
        ItemMeta wmeta = w.getItemMeta();
        wmeta.setDisplayName("武器選択 / CHOSE WEAPONS");
        w.setItemMeta(wmeta);
        inv.setItem(6, w);
        player.openInventory(inv);
        
        ItemStack t = new ItemStack(Material.GRASS_BLOCK);
        ItemMeta tmeta = t.getItemMeta();
        tmeta.setDisplayName("リソースパックをダウンロード / DOWNLOAD RESOURCEPACK");
        t.setItemMeta(tmeta);
        inv.setItem(8, t);
        
        ItemStack r = new ItemStack(Material.MILK_BUCKET);
        ItemMeta rmeta = r.getItemMeta();
        rmeta.setDisplayName("塗りをリセット / RESET INK");
        r.setItemMeta(rmeta);
        if(conf.getConfig().getString("WorkMode").equals("Trial"))
            inv.setItem(2, r);
        
        if(conf.getConfig().getString("WorkMode").equals("Trial")){
            ItemStack b = new ItemStack(Material.OAK_DOOR);
            ItemMeta bmeta = b.getItemMeta();
            bmeta.setDisplayName("ロビーへ戻る / RETURN TO LOBBY");
            b.setItemMeta(bmeta);
            inv.setItem(0, b);
        }else{
            ItemStack b = new ItemStack(Material.ARMOR_STAND);
            ItemMeta bmeta = b.getItemMeta();
            bmeta.setDisplayName("試し打ちサーバーへ接続 / TRAINING FIELD");
            b.setItemMeta(bmeta);
            inv.setItem(0, b);
        }
        
        player.openInventory(inv);
    }
    public static void openWeaponSelect(Player player){
        int slotnum = 0;
        Inventory inv = Bukkit.createInventory(null, 54, "武器選択");
        for (String classname : conf.getClassConfig().getConfigurationSection("WeaponClass").getKeys(false)){
            String ClassName = conf.getClassConfig().getString("WeaponClass." + classname + ".MainWeaponName");
            ItemStack item = new ItemStack(DataMgr.getWeaponClass(ClassName).getMainWeapon().getWeaponIteamStack());
            ItemMeta itemm = item.getItemMeta();
            itemm.setDisplayName(ClassName);
            List lores = new ArrayList();
            lores.add("§r§6SubWeapon : " + conf.getClassConfig().getString("WeaponClass." + classname + ".SubWeaponName"));
            lores.add("§r§6SPWeapon  : " + conf.getClassConfig().getString("WeaponClass." + classname + ".SPWeaponName"));
            itemm.setLore(lores);
            item.setItemMeta(itemm);
            if (slotnum <= 53){
                inv.setItem(slotnum, item);
            }
            slotnum++;
        }
        player.openInventory(inv);
    }
    
    public static void openSettingsUI(Player player){
        Inventory inv = Bukkit.createInventory(null, 36, "設定");
        
        ItemStack shooter = new ItemStack(Material.WOODEN_HOE);
        ItemMeta shooter_m = shooter.getItemMeta();
        shooter_m.setDisplayName("シューターのパーティクル");
        ArrayList<String> shooter_r = new ArrayList<String>();
        if(DataMgr.getPlayerData(player).getSettings().ShowEffect_Shooter())
            shooter_r.add("§a§l[Enable]");
        else
            shooter_r.add("§7§l[Disable]");
        shooter_m.setLore(shooter_r);
        shooter.setItemMeta(shooter_m);
        inv.setItem(9, shooter);
        
        ItemStack shooter_p;
        if(DataMgr.getPlayerData(player).getSettings().ShowEffect_Shooter())
            shooter_p = new ItemStack(Material.LIME_DYE);
        else
            shooter_p = new ItemStack(Material.GUNPOWDER);
        ItemMeta shooter_p_m = shooter_p.getItemMeta();
        shooter_p_m.setDisplayName("シューターのパーティクル");
        ArrayList<String> shooter_p_r = new ArrayList<String>();
        if(DataMgr.getPlayerData(player).getSettings().ShowEffect_Shooter())
            shooter_p_r.add("§a§l[Enable]");
        else
            shooter_p_r.add("§7§l[Disable]");
        shooter_p_m.setLore(shooter_p_r);
        shooter_p.setItemMeta(shooter_p_m);
        inv.setItem(18, shooter_p);
        
        
        
        
        ItemStack charger = new ItemStack(Material.WOODEN_SWORD);
        ItemMeta charger_m = charger.getItemMeta();
        charger_m.setDisplayName("チャージャーのレーザー");
        ArrayList<String> charger_r = new ArrayList<String>();
        if(DataMgr.getPlayerData(player).getSettings().ShowEffect_ChargerLine())
            charger_r.add("§a§l[Enable]");
        else
            charger_r.add("§7§l[Disable]");
        charger_m.setLore(charger_r);
        charger.setItemMeta(charger_m);
        inv.setItem(10, charger);
        
        ItemStack charger_p;
        if(DataMgr.getPlayerData(player).getSettings().ShowEffect_ChargerLine())
            charger_p = new ItemStack(Material.LIME_DYE);
        else
            charger_p = new ItemStack(Material.GUNPOWDER);
        ItemMeta charger_p_m = charger_p.getItemMeta();
        charger_p_m.setDisplayName("チャージャーのレーザー");
        ArrayList<String> charger_p_r = new ArrayList<String>();
        if(DataMgr.getPlayerData(player).getSettings().ShowEffect_ChargerLine())
            charger_p_r.add("§a§l[Enable]");
        else
            charger_p_r.add("§7§l[Disable]");
        charger_p_m.setLore(charger_p_r);
        charger_p.setItemMeta(charger_p_m);
        inv.setItem(19, charger_p);
        
        
        
        
        ItemStack chargerS = new ItemStack(Material.WOODEN_SWORD);
        ItemMeta chargerS_m = chargerS.getItemMeta();
        chargerS_m.setDisplayName("チャージャーの射撃エフェクト");
        ArrayList<String> chargerS_r = new ArrayList<String>();
        if(DataMgr.getPlayerData(player).getSettings().ShowEffect_ChargerShot())
            chargerS_r.add("§a§l[Enable]");
        else
            chargerS_r.add("§7§l[Disable]");
        chargerS_m.setLore(chargerS_r);
        chargerS.setItemMeta(chargerS_m);
        inv.setItem(11, chargerS);
        
        ItemStack chargerS_p;
        if(DataMgr.getPlayerData(player).getSettings().ShowEffect_ChargerShot())
            chargerS_p = new ItemStack(Material.LIME_DYE);
        else
            chargerS_p = new ItemStack(Material.GUNPOWDER);
        ItemMeta chargerS_p_m = chargerS_p.getItemMeta();
        chargerS_p_m.setDisplayName("チャージャーの射撃エフェクト");
        ArrayList<String> chargerS_p_r = new ArrayList<String>();
        if(DataMgr.getPlayerData(player).getSettings().ShowEffect_ChargerShot())
            chargerS_p_r.add("§a§l[Enable]");
        else
            chargerS_p_r.add("§7§l[Disable]");
        chargerS_p_m.setLore(chargerS_p_r);
        chargerS_p.setItemMeta(chargerS_p_m);
        inv.setItem(20, chargerS_p);
        
        
        
        ItemStack rollaerL = new ItemStack(Material.WOODEN_PICKAXE);
        ItemMeta rollaerL_m = rollaerL.getItemMeta();
        rollaerL_m.setDisplayName("ローラーのロール");
        ArrayList<String> rollaerL_r = new ArrayList<String>();
        if(DataMgr.getPlayerData(player).getSettings().ShowEffect_RollerRoll())
            rollaerL_r.add("§a§l[Enable]");
        else
            rollaerL_r.add("§7§l[Disable]");
        rollaerL_m.setLore(rollaerL_r);
        rollaerL.setItemMeta(rollaerL_m);
        inv.setItem(12, rollaerL);
        
        ItemStack rollaerL_p;
        if(DataMgr.getPlayerData(player).getSettings().ShowEffect_RollerRoll())
            rollaerL_p = new ItemStack(Material.LIME_DYE);
        else
            rollaerL_p = new ItemStack(Material.GUNPOWDER);
        ItemMeta rollaerL_p_m = rollaerL_p.getItemMeta();
        rollaerL_p_m.setDisplayName("ローラーのロール");
        ArrayList<String> rollaerL_p_r = new ArrayList<String>();
        if(DataMgr.getPlayerData(player).getSettings().ShowEffect_RollerRoll())
            rollaerL_p_r.add("§a§l[Enable]");
        else
            rollaerL_p_r.add("§7§l[Disable]");
        rollaerL_p_m.setLore(rollaerL_p_r);
        rollaerL_p.setItemMeta(rollaerL_p_m);
        inv.setItem(21, rollaerL_p);
        
        
        
        ItemStack rollerS = new ItemStack(Material.WOODEN_PICKAXE);
        ItemMeta rollerS_m = rollerS.getItemMeta();
        rollerS_m.setDisplayName("ローラーのしぶき");
        ArrayList<String> rollerS_r = new ArrayList<String>();
        if(DataMgr.getPlayerData(player).getSettings().ShowEffect_RollerShot())
            rollerS_r.add("§a§l[Enable]");
        else
            rollerS_r.add("§7§l[Disable]");
        rollerS_m.setLore(rollerS_r);
        rollerS.setItemMeta(rollerS_m);
        inv.setItem(13, rollerS);
        
        ItemStack rollerS_p;
        if(DataMgr.getPlayerData(player).getSettings().ShowEffect_RollerShot())
            rollerS_p = new ItemStack(Material.LIME_DYE);
        else
            rollerS_p = new ItemStack(Material.GUNPOWDER);
        ItemMeta rollerS_p_m = rollerS_p.getItemMeta();
        rollerS_p_m.setDisplayName("ローラーのしぶき");
        ArrayList<String> rollerS_p_r = new ArrayList<String>();
        if(DataMgr.getPlayerData(player).getSettings().ShowEffect_RollerShot())
            rollerS_p_r.add("§a§l[Enable]");
        else
            rollerS_p_r.add("§7§l[Disable]");
        rollerS_p_m.setLore(rollerS_p_r);
        rollerS_p.setItemMeta(rollerS_p_m);
        inv.setItem(22, rollerS_p);
        
        
        
        
        ItemStack bgm_p;
        if(DataMgr.getPlayerData(player).getSettings().PlayBGM())
            bgm_p = new ItemStack(Material.LIME_DYE);
        else
            bgm_p = new ItemStack(Material.GUNPOWDER);
        ItemMeta bgm_p_m = bgm_p.getItemMeta();
        bgm_p_m.setDisplayName("BGM");
        ArrayList<String> bgm_p_r = new ArrayList<String>();
        if(DataMgr.getPlayerData(player).getSettings().PlayBGM())
            bgm_p_r.add("§a§l[Enable]");
        else
            bgm_p_r.add("§7§l[Disable]");
        bgm_p_m.setLore(bgm_p_r);
        bgm_p.setItemMeta(bgm_p_m);
        inv.setItem(26, bgm_p);
        
        ItemStack bgm = new ItemStack(Material.MUSIC_DISC_13);
        ItemMeta bgm_m = bgm.getItemMeta();
        bgm_m.setDisplayName("BGM");
        ArrayList<String> bgm_r = new ArrayList<String>();
        if(DataMgr.getPlayerData(player).getSettings().PlayBGM())
            bgm_r.add("§a§l[Enable]");
        else
            bgm_r.add("§7§l[Disable]");
        bgm_m.setLore(bgm_r);
        bgm.setItemMeta(bgm_m);
        inv.setItem(17, bgm);
        
        player.openInventory(inv);
    }
}
