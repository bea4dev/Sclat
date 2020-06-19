
package be4rjp.sclat.GUI;

import be4rjp.sclat.Main;
import static be4rjp.sclat.Main.conf;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.Match;
import be4rjp.sclat.manager.MatchMgr;
import be4rjp.sclat.manager.PlayerStatusMgr;
import be4rjp.sclat.weapon.Gear;
import java.util.ArrayList;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_13_R1.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

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
        inv.setItem(6, setting);
        
        ItemStack w = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemMeta wmeta = w.getItemMeta();
        wmeta.setDisplayName("装備変更 / EQUIPMENT");
        w.setItemMeta(wmeta);
        inv.setItem(4, w);
        player.openInventory(inv);
        
        ItemStack t = new ItemStack(Material.GRASS_BLOCK);
        ItemMeta tmeta = t.getItemMeta();
        tmeta.setDisplayName("リソースパックをダウンロード / DOWNLOAD RESOURCEPACK");
        t.setItemMeta(tmeta);
        inv.setItem(0, t);
        
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
            inv.setItem(8, b);
        }else{
            ItemStack b = new ItemStack(Material.ARMOR_STAND);
            ItemMeta bmeta = b.getItemMeta();
            bmeta.setDisplayName("試し打ちサーバーへ接続 / TRAINING FIELD");
            b.setItemMeta(bmeta);
            inv.setItem(8, b);
        }
        
        ItemStack b = new ItemStack(Material.PAPER);
        ItemMeta bmeta = b.getItemMeta();
        bmeta.setDisplayName("ショップを開く / OPEN SHOP");
        b.setItemMeta(bmeta);
        inv.setItem(8, b);
        
        player.openInventory(inv);
    }
    
    public static void gearGUI(Player player){
        Inventory inv = Bukkit.createInventory(null, 9, "Gear");
        
        for(int i = 0; i <= 7;){
            ItemStack n = new ItemStack(Gear.getGearMaterial(i));
            ItemMeta nmeta = n.getItemMeta();
            nmeta.setDisplayName(Gear.getGearName(i));
            n.setItemMeta(nmeta);
            inv.setItem(i, n);
            i++;
        }
        
        player.openInventory(inv);
    }
    
    public static void equipmentGUI(Player player){
        Inventory inv = Bukkit.createInventory(null, 27, " ");
        
        ItemStack n = new ItemStack(Gear.getGearMaterial(DataMgr.getPlayerData(player).getGearNumber()));
        ItemMeta nmeta = n.getItemMeta();
        nmeta.setDisplayName("ギア変更 / GEAR");
        n.setItemMeta(nmeta);
        inv.setItem(11, n);
        
        ItemStack t = DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getWeaponIteamStack().clone();
        ItemMeta tmeta = t.getItemMeta();
        tmeta.setDisplayName("武器変更 / WEAPON");
        t.setItemMeta(tmeta);
        inv.setItem(15, t);
        
        
        player.openInventory(inv);
    }
    
    public static void MatchTohyoGUI(Player player){
        Inventory inv = Bukkit.createInventory(null, 18, "Chose a Gamemode");
        
        ItemStack n = new ItemStack(Material.SNOWBALL);
        ItemMeta nmeta = n.getItemMeta();
        nmeta.setDisplayName("ナワバリバトル");
        n.setItemMeta(nmeta);
        inv.setItem(2, n);
        
        ItemStack t = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta tmeta = t.getItemMeta();
        tmeta.setDisplayName("チームデスマッチ");
        t.setItemMeta(tmeta);
        inv.setItem(4, t);
        
        ItemStack nu = new ItemStack(Material.GLASS);
        ItemMeta numeta = nu.getItemMeta();
        numeta.setDisplayName("ガチエリア");
        nu.setItemMeta(numeta);
        Match match = DataMgr.getMatchFromId(MatchMgr.matchcount);
        if(match.getMapData().getCanAreaBattle())
            inv.setItem(6, nu);
        
        player.openInventory(inv);
    }
    
    public static void SuperJumpGUI(Player player){
        Inventory inv = Bukkit.createInventory(null, 18, "Chose Target");
        
        ItemStack is = new ItemStack(DataMgr.getPlayerData(player).getTeam().getTeamColor().getGlass());
        ItemMeta ism = is.getItemMeta();
        ism.setDisplayName("リスポーン地点");
        is.setItemMeta(ism);
        inv.setItem(0, is);
        
        int slotnum = 1;
        
        for(Player p : Main.getPlugin(Main.class).getServer().getOnlinePlayers()){
            if(DataMgr.getPlayerData(p).getTeam().getID() == DataMgr.getPlayerData(player).getTeam().getID() && p.getWorld() == player.getWorld() && p != player){
                if (slotnum <= 17){
                    inv.setItem(slotnum, CraftItemStack.asBukkitCopy(DataMgr.getPlayerData(p).getPlayerHead()));
                }
                slotnum++;
            }
        }
        for(ArmorStand as : DataMgr.getBeaconMap().values()){
            if(as.getCustomName().equals("21")){
                Player p = DataMgr.getArmorStandPlayer(as);
                if(DataMgr.getPlayerData(player).getTeam() == DataMgr.getPlayerData(p).getTeam()){
                    ItemStack item = new ItemStack(Material.IRON_TRAPDOOR);
                    ItemMeta im = item.getItemMeta();
                    im.setDisplayName(p.getName());
                    item.setItemMeta(im);
                    if (slotnum <= 17){
                        inv.setItem(slotnum, item);
                    }
                    slotnum++;
                }
            }
        }
        player.openInventory(inv);
    }
    
    public static void WeaponSelectSetup(){
        int slotnum = 0;
        Inventory shooter = Bukkit.createInventory(null, 54, "武器選択");
        Inventory roller = Bukkit.createInventory(null, 54, "武器選択");
        Inventory charger = Bukkit.createInventory(null, 54, "武器選択");
        Inventory wm = Bukkit.createInventory(null, 9, "武器選択");
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
            if (slotnum <= 44 && (DataMgr.getWeaponClass(ClassName).getMainWeapon().getWeaponType().equals("Shooter") || DataMgr.getWeaponClass(ClassName).getMainWeapon().getWeaponType().equals("Burst") || DataMgr.getWeaponClass(ClassName).getMainWeapon().getWeaponType().equals("Blaster") || DataMgr.getWeaponClass(ClassName).getMainWeapon().getWeaponType().equals("Spinner"))){
                shooter.setItem(slotnum, item);
                slotnum++;
            }
        }
        
        slotnum = 0;
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
            if (slotnum <= 44 && (DataMgr.getWeaponClass(ClassName).getMainWeapon().getWeaponType().equals("Roller") || DataMgr.getWeaponClass(ClassName).getMainWeapon().getWeaponType().equals("Bucket") || DataMgr.getWeaponClass(ClassName).getMainWeapon().getWeaponType().equals("Slosher"))){
                roller.setItem(slotnum, item);
                slotnum++;
            }   
        }
        
        slotnum = 0;
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
            if (slotnum <= 44 && DataMgr.getWeaponClass(ClassName).getMainWeapon().getWeaponType().equals("Charger")){
                charger.setItem(slotnum, item);
                slotnum++;
            }
        }
        
        ItemStack is = new ItemStack(Material.OAK_DOOR);
        ItemMeta ism = is.getItemMeta();
        ism.setDisplayName("戻る");
        is.setItemMeta(ism);
        shooter.setItem(53, is);
        roller.setItem(53, is);
        charger.setItem(53, is);
        
        
        ItemStack s = new ItemStack(Material.WOODEN_HOE);
        ItemMeta sm = s.getItemMeta();
        sm.setDisplayName("シューター");
        s.setItemMeta(sm);
        
        ItemStack r = new ItemStack(Material.STONE_PICKAXE);
        ItemMeta rm = r.getItemMeta();
        rm.setDisplayName("ローラー");
        r.setItemMeta(rm);
        
        ItemStack c = new ItemStack(Material.WOODEN_SWORD);
        ItemMeta cm = c.getItemMeta();
        cm.setDisplayName("チャージャー");
        c.setItemMeta(cm);
        
        wm = Bukkit.createInventory(null, 9, "武器選択");
        wm.setItem(2, s);
        wm.setItem(4, r);
        wm.setItem(6, c);
    }
    
    public static void openShop(Player player){
        int slotnum = 0;
        Inventory shooter = Bukkit.createInventory(null, 54, "武器選択");
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
            if (slotnum <= 44 && (DataMgr.getWeaponClass(ClassName).getMainWeapon().getWeaponType().equals("Shooter") || DataMgr.getWeaponClass(ClassName).getMainWeapon().getWeaponType().equals("Burst") || DataMgr.getWeaponClass(ClassName).getMainWeapon().getWeaponType().equals("Blaster") || DataMgr.getWeaponClass(ClassName).getMainWeapon().getWeaponType().equals("Spinner"))){
                if(DataMgr.getWeaponClass(ClassName).getMainWeapon().getMoney() == 0){
                    shooter.setItem(slotnum, item);
                    slotnum++;
                }else if(PlayerStatusMgr.haveWeapon(player, classname)){
                    shooter.setItem(slotnum, item);
                    slotnum++;
                }
            }
        }
    }
    
    public static void openWeaponSelect(Player player, String name, boolean shop){
        switch(name){
            case"Shooter":
                int slotnum = 0;
                Inventory shooter = Bukkit.createInventory(null, 54, "武器選択");
                if(shop)
                    shooter = Bukkit.createInventory(null, 54, "Shop");
                for (String classname : conf.getClassConfig().getConfigurationSection("WeaponClass").getKeys(false)){
                    String ClassName = conf.getClassConfig().getString("WeaponClass." + classname + ".MainWeaponName");
                    ItemStack item = new ItemStack(DataMgr.getWeaponClass(ClassName).getMainWeapon().getWeaponIteamStack());
                    ItemMeta itemm = item.getItemMeta();
                    itemm.setDisplayName(ClassName);
                    List lores = new ArrayList();
                    lores.add("§r§6SubWeapon : " + conf.getClassConfig().getString("WeaponClass." + classname + ".SubWeaponName"));
                    lores.add("§r§6SPWeapon  : " + conf.getClassConfig().getString("WeaponClass." + classname + ".SPWeaponName"));
                    if(shop){
                        lores.add("");
                        lores.add("§r§bMoney  : " + String.valueOf(DataMgr.getWeaponClass(ClassName).getMainWeapon().getMoney()));
                    }
                    itemm.setLore(lores);
                    item.setItemMeta(itemm);
                    if (slotnum <= 44 && (DataMgr.getWeaponClass(ClassName).getMainWeapon().getWeaponType().equals("Shooter") || DataMgr.getWeaponClass(ClassName).getMainWeapon().getWeaponType().equals("Burst") || DataMgr.getWeaponClass(ClassName).getMainWeapon().getWeaponType().equals("Blaster") || DataMgr.getWeaponClass(ClassName).getMainWeapon().getWeaponType().equals("Spinner"))){
                        if(shop){
                            if(DataMgr.getWeaponClass(ClassName).getMainWeapon().getMoney() != 0 && !PlayerStatusMgr.haveWeapon(player, classname)){
                                shooter.setItem(slotnum, item);
                                slotnum++;
                            }
                        }else{
                            if(DataMgr.getWeaponClass(ClassName).getMainWeapon().getMoney() == 0){
                                shooter.setItem(slotnum, item);
                                slotnum++;
                            }else if(PlayerStatusMgr.haveWeapon(player, classname)){
                                shooter.setItem(slotnum, item);
                                slotnum++;
                            }
                        }
                    }
                }
                ItemStack is = new ItemStack(Material.OAK_DOOR);
                ItemMeta ism = is.getItemMeta();
                ism.setDisplayName("戻る");
                is.setItemMeta(ism);
                shooter.setItem(53, is);
                
                player.openInventory(shooter);
                break;
            case"Roller":
                slotnum = 0;
                Inventory roller = Bukkit.createInventory(null, 54, "武器選択");
                if(shop)
                    roller = Bukkit.createInventory(null, 54, "Shop");
                for (String classname : conf.getClassConfig().getConfigurationSection("WeaponClass").getKeys(false)){
                    String ClassName = conf.getClassConfig().getString("WeaponClass." + classname + ".MainWeaponName");
                    ItemStack item = new ItemStack(DataMgr.getWeaponClass(ClassName).getMainWeapon().getWeaponIteamStack());
                    ItemMeta itemm = item.getItemMeta();
                    itemm.setDisplayName(ClassName);
                    List lores = new ArrayList();
                    lores.add("§r§6SubWeapon : " + conf.getClassConfig().getString("WeaponClass." + classname + ".SubWeaponName"));
                    lores.add("§r§6SPWeapon  : " + conf.getClassConfig().getString("WeaponClass." + classname + ".SPWeaponName"));
                    if(shop){
                        lores.add("");
                        lores.add("§r§bMoney  : " + String.valueOf(DataMgr.getWeaponClass(ClassName).getMainWeapon().getMoney()));
                    }
                    itemm.setLore(lores);
                    item.setItemMeta(itemm);
                    if (slotnum <= 44 && (DataMgr.getWeaponClass(ClassName).getMainWeapon().getWeaponType().equals("Roller") || DataMgr.getWeaponClass(ClassName).getMainWeapon().getWeaponType().equals("Bucket") || DataMgr.getWeaponClass(ClassName).getMainWeapon().getWeaponType().equals("Slosher"))){
                        if(shop){
                            if(DataMgr.getWeaponClass(ClassName).getMainWeapon().getMoney() != 0 && !PlayerStatusMgr.haveWeapon(player, classname)){
                                roller.setItem(slotnum, item);
                                slotnum++;
                            }
                        }else{
                            if(DataMgr.getWeaponClass(ClassName).getMainWeapon().getMoney() == 0){
                                roller.setItem(slotnum, item);
                                slotnum++;
                            }else if(PlayerStatusMgr.haveWeapon(player, classname)){
                                roller.setItem(slotnum, item);
                                slotnum++;
                            }
                        }
                    }   
                }
                is = new ItemStack(Material.OAK_DOOR);
                ism = is.getItemMeta();
                ism.setDisplayName("戻る");
                is.setItemMeta(ism);
                roller.setItem(53, is);
                
                player.openInventory(roller);
                break;
            case"Charger":
                Inventory charger = Bukkit.createInventory(null, 54, "武器選択");
                if(shop)
                    charger = Bukkit.createInventory(null, 54, "Shop");
                slotnum = 0;
                for (String classname : conf.getClassConfig().getConfigurationSection("WeaponClass").getKeys(false)){
                    String ClassName = conf.getClassConfig().getString("WeaponClass." + classname + ".MainWeaponName");
                    ItemStack item = new ItemStack(DataMgr.getWeaponClass(ClassName).getMainWeapon().getWeaponIteamStack());
                    ItemMeta itemm = item.getItemMeta();
                    itemm.setDisplayName(ClassName);
                    List lores = new ArrayList();
                    lores.add("§r§6SubWeapon : " + conf.getClassConfig().getString("WeaponClass." + classname + ".SubWeaponName"));
                    lores.add("§r§6SPWeapon  : " + conf.getClassConfig().getString("WeaponClass." + classname + ".SPWeaponName"));
                    if(shop){
                        lores.add("");
                        lores.add("§r§bMoney  : " + String.valueOf(DataMgr.getWeaponClass(ClassName).getMainWeapon().getMoney()));
                    }
                    itemm.setLore(lores);
                    item.setItemMeta(itemm);
                    if (slotnum <= 44 && DataMgr.getWeaponClass(ClassName).getMainWeapon().getWeaponType().equals("Charger")){
                        if(shop){
                            if(DataMgr.getWeaponClass(ClassName).getMainWeapon().getMoney() != 0 && !PlayerStatusMgr.haveWeapon(player, classname)){
                                charger.setItem(slotnum, item);
                                slotnum++;
                            }
                        }else{
                            if(DataMgr.getWeaponClass(ClassName).getMainWeapon().getMoney() == 0){
                                charger.setItem(slotnum, item);
                                slotnum++;
                            }else if(PlayerStatusMgr.haveWeapon(player, classname)){
                                charger.setItem(slotnum, item);
                                slotnum++;
                            }
                        }
                    }
                }
                is = new ItemStack(Material.OAK_DOOR);
                ism = is.getItemMeta();
                ism.setDisplayName("戻る");
                is.setItemMeta(ism);
                charger.setItem(53, is);
                
                player.openInventory(charger);
                break;
            case"Main":
                Inventory wm = Bukkit.createInventory(null, 9, "武器選択");
                if(shop)
                    wm = Bukkit.createInventory(null, 9, "Shop");
                
                ItemStack s = new ItemStack(Material.WOODEN_HOE);
                ItemMeta sm = s.getItemMeta();
                sm.setDisplayName("シューター");
                s.setItemMeta(sm);

                ItemStack r = new ItemStack(Material.STONE_PICKAXE);
                ItemMeta rm = r.getItemMeta();
                rm.setDisplayName("ローラー");
                r.setItemMeta(rm);

                ItemStack c = new ItemStack(Material.WOODEN_SWORD);
                ItemMeta cm = c.getItemMeta();
                cm.setDisplayName("チャージャー");
                c.setItemMeta(cm);

                wm.setItem(2, s);
                wm.setItem(4, r);
                wm.setItem(6, c);
                player.openInventory(wm);
                break;
        }
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
        
        
        
        ItemStack bomb = new ItemStack(Material.WHITE_STAINED_GLASS);
        ItemMeta bomb_m = bomb.getItemMeta();
        bomb_m.setDisplayName("ボムの視認用エフェクト");
        ArrayList<String> bomb_r = new ArrayList<String>();
        if(DataMgr.getPlayerData(player).getSettings().ShowEffect_Bomb())
            bomb_r.add("§a§l[Enable]");
        else
            bomb_r.add("§7§l[Disable]");
        bomb_m.setLore(bomb_r);
        bomb.setItemMeta(bomb_m);
        inv.setItem(14, bomb);
        
        ItemStack bomb_p;
        if(DataMgr.getPlayerData(player).getSettings().ShowEffect_Bomb())
            bomb_p = new ItemStack(Material.LIME_DYE);
        else
            bomb_p = new ItemStack(Material.GUNPOWDER);
        ItemMeta bomb_p_m = bomb_p.getItemMeta();
        bomb_p_m.setDisplayName("ボムの視認用エフェクト");
        ArrayList<String> bomb_p_r = new ArrayList<String>();
        if(DataMgr.getPlayerData(player).getSettings().ShowEffect_Bomb())
            bomb_p_r.add("§a§l[Enable]");
        else
            bomb_p_r.add("§7§l[Disable]");
        bomb_p_m.setLore(bomb_p_r);
        bomb_p.setItemMeta(bomb_p_m);
        inv.setItem(23, bomb_p);
        
        
        ItemStack bombEx = new ItemStack(Material.WHITE_CONCRETE);
        ItemMeta bombEx_m = bombEx.getItemMeta();
        bombEx_m.setDisplayName("爆発エフェクト");
        ArrayList<String> bombEx_r = new ArrayList<String>();
        if(DataMgr.getPlayerData(player).getSettings().ShowEffect_BombEx())
            bombEx_r.add("§a§l[Enable]");
        else
            bombEx_r.add("§7§l[Disable]");
        bombEx_m.setLore(bombEx_r);
        bombEx.setItemMeta(bombEx_m);
        inv.setItem(15, bombEx);
        
        ItemStack bombEx_p;
        if(DataMgr.getPlayerData(player).getSettings().ShowEffect_BombEx())
            bombEx_p = new ItemStack(Material.LIME_DYE);
        else
            bombEx_p = new ItemStack(Material.GUNPOWDER);
        ItemMeta bombEx_p_m = bombEx_p.getItemMeta();
        bombEx_p_m.setDisplayName("爆発エフェクト");
        ArrayList<String> bombEx_p_r = new ArrayList<String>();
        if(DataMgr.getPlayerData(player).getSettings().ShowEffect_BombEx())
            bombEx_p_r.add("§a§l[Enable]");
        else
            bombEx_p_r.add("§7§l[Disable]");
        bombEx_p_m.setLore(bombEx_p_r);
        bombEx_p.setItemMeta(bombEx_p_m);
        inv.setItem(24, bombEx_p);
        
        player.openInventory(inv);
    }
}
