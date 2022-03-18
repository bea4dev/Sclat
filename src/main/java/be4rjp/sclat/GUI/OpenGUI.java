
package be4rjp.sclat.GUI;

import be4rjp.sclat.Main;
import static be4rjp.sclat.Main.conf;

import be4rjp.sclat.ServerType;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.Match;
import be4rjp.sclat.data.PlayerData;
import be4rjp.sclat.manager.MatchMgr;
import be4rjp.sclat.manager.PlayerStatusMgr;
import be4rjp.sclat.manager.RankMgr;
import be4rjp.sclat.tutorial.Tutorial;
import be4rjp.sclat.weapon.Gear;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
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
        Inventory inv = Bukkit.createInventory(null, 45, "メインメニュー");
    
        for (int i = 0; i <= 44; ) {
            ItemStack is = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            ItemMeta ism = is.getItemMeta();
            ism.setDisplayName(".");
            is.setItemMeta(ism);
            inv.setItem(i, is);
            i++;
        }
        
        ItemStack join = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta joinmeta = join.getItemMeta();
        joinmeta.setDisplayName("試合に参加 / JOIN THE MATCH");
        join.setItemMeta(joinmeta);
        if(!conf.getConfig().getString("WorkMode").equals("Trial"))
            inv.setItem(10, join);
        
        ItemStack setting = new ItemStack(Material.COMPARATOR);
        ItemMeta setting_m = setting.getItemMeta();
        setting_m.setDisplayName("設定 / SETTINGS");
        setting.setItemMeta(setting_m);
        inv.setItem(14, setting);
        
        ItemStack w = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemMeta wmeta = w.getItemMeta();
        wmeta.setDisplayName("装備変更 / EQUIPMENT");
        w.setItemMeta(wmeta);
        inv.setItem(12, w);
        player.openInventory(inv);
        
        ItemStack t = new ItemStack(Material.GRASS_BLOCK);
        ItemMeta tmeta = t.getItemMeta();
        tmeta.setDisplayName("リソースパックをダウンロード / DOWNLOAD RESOURCEPACK");
        t.setItemMeta(tmeta);
        inv.setItem(28, t);
        
        ItemStack r = new ItemStack(Material.MILK_BUCKET);
        ItemMeta rmeta = r.getItemMeta();
        rmeta.setDisplayName("塗りをリセット / RESET INK");
        r.setItemMeta(rmeta);
        if(conf.getConfig().getString("WorkMode").equals("Trial"))
            inv.setItem(10, r);
        
        if(conf.getConfig().getString("WorkMode").equals("Trial")){
            ItemStack b = new ItemStack(Material.OAK_DOOR);
            ItemMeta bmeta = b.getItemMeta();
            bmeta.setDisplayName("ロビーへ戻る / RETURN TO LOBBY");
            b.setItemMeta(bmeta);
            inv.setItem(16, b);
        }else{
            
            ItemStack ta = new ItemStack(Material.ARMOR_STAND);
            ItemMeta tameta = ta.getItemMeta();
            tameta.setDisplayName("試し打ちサーバーへ接続 / TRAINING FIELD");
            ta.setItemMeta(tameta);
            inv.setItem(30, ta);
            
            ItemStack b = new ItemStack(Material.CHEST);
            ItemMeta bmeta = b.getItemMeta();
            bmeta.setDisplayName("ショップを開く / OPEN SHOP");
            b.setItemMeta(bmeta);
            inv.setItem(16, b);
            
            PlayerData data = DataMgr.getPlayerData(player);
            ItemStack status = new ItemStack(Material.PLAYER_HEAD);
            if(data.getPlayerHead() != null)
                status = CraftItemStack.asBukkitCopy(data.getPlayerHead()).clone();
            ItemMeta statusMeta = status.getItemMeta();
            statusMeta.setDisplayName("§r§e" + player.getName() + " のステータス");
            List lores = new ArrayList();
            lores.add("§r§6Rank : §r" + PlayerStatusMgr.getRank(player) + " [ §b" + RankMgr.toABCRank(PlayerStatusMgr.getRank(player)) + " §r]");
            lores.add("§r§6Lv : §r" + PlayerStatusMgr.getLv(player));
            lores.add("§r§bKill(s) : §r" + PlayerStatusMgr.getKill(player));
            lores.add("§r§bPaint(s) : §r" + PlayerStatusMgr.getPaint(player));
            lores.add("§r§aMoney : §r" + PlayerStatusMgr.getMoney(player));
            statusMeta.setLore(lores);
            status.setItemMeta(statusMeta);
            inv.setItem(32, status);
        }
        
        if(Main.type == ServerType.LOBBY){
            ItemStack b = new ItemStack(Material.OAK_DOOR);
            ItemMeta bmeta = b.getItemMeta();
            bmeta.setDisplayName("JGへ戻る / RETURN TO JG");
            b.setItemMeta(bmeta);
            inv.setItem(34, b);
        }
    
        ItemStack b = new ItemStack(Material.BARRIER);
        ItemMeta bmeta = b.getItemMeta();
        bmeta.setDisplayName("閉じる");
        b.setItemMeta(bmeta);
        inv.setItem(44, b);
        
        
        player.openInventory(inv);
    }
    
    public static void gearGUI(Player player, boolean shop){
        Inventory inv = Bukkit.createInventory(null, 9, shop ? "Gear shop" : "Gear");
        
        if(shop) {
            for (int i = 0; i <= 8; ) {
        
                if (PlayerStatusMgr.haveGear(player, i)) {
                    ItemStack n = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
                    ItemMeta nmeta = n.getItemMeta();
                    nmeta.setDisplayName(".");
                    n.setItemMeta(nmeta);
                    inv.setItem(i, n);
                    i++;
                    continue;
                }
        
                ItemStack n = new ItemStack(Gear.getGearMaterial(i));
                ItemMeta nmeta = n.getItemMeta();
                nmeta.setDisplayName(Gear.getGearName(i));
                List<String> list = new ArrayList<>();
                list.add("");
                list.add("§r§bMoney : " + String.valueOf(Gear.getGearPrice(i)));
                nmeta.setLore(list);
                n.setItemMeta(nmeta);
                inv.setItem(i, n);
                i++;
            }
        }else {
            for (int i = 0; i <= 8; ) {
    
                if (!(PlayerStatusMgr.haveGear(player, i) || conf.getConfig().getString("WorkMode").equals("Trial") ||
                        !Main.shop)) {
                    ItemStack n = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
                    ItemMeta nmeta = n.getItemMeta();
                    nmeta.setDisplayName(".");
                    n.setItemMeta(nmeta);
                    inv.setItem(i, n);
                    i++;
                    continue;
                }
        
                ItemStack n = new ItemStack(Gear.getGearMaterial(i));
                ItemMeta nmeta = n.getItemMeta();
                nmeta.setDisplayName(Gear.getGearName(i));
                n.setItemMeta(nmeta);
                inv.setItem(i, n);
                i++;
            }
        }
        
        player.openInventory(inv);
    }
    
    public static void equipmentGUI(Player player, boolean shop){
        Inventory inv = Bukkit.createInventory(null, 27, shop ? "Equipment shop" : "Equipment");
    
        for (int i = 0; i <= 26; ) {
            ItemStack is = new ItemStack(shop ? Material.WHITE_STAINED_GLASS_PANE : Material.BLACK_STAINED_GLASS_PANE);
            ItemMeta ism = is.getItemMeta();
            ism.setDisplayName(".");
            is.setItemMeta(ism);
            inv.setItem(i, is);
            i++;
        }
        
        ItemStack n = new ItemStack(Gear.getGearMaterial(DataMgr.getPlayerData(player).getGearNumber()));
        ItemMeta nmeta = n.getItemMeta();
        nmeta.setDisplayName(shop ? "§bギア購入 / GEAR" : "§bギア変更 / GEAR");
        n.setItemMeta(nmeta);
        inv.setItem(15, n);
        
        ItemStack t = DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getWeaponIteamStack().clone();
        ItemMeta tmeta = t.getItemMeta();
        tmeta.setDisplayName(shop ? "§6武器購入 / WEAPON" : "§6武器変更 / WEAPON");
        t.setItemMeta(tmeta);
        inv.setItem(11, t);
    
        ItemStack is = new ItemStack(Material.OAK_DOOR);
        ItemMeta ism = is.getItemMeta();
        ism.setDisplayName("戻る");
        is.setItemMeta(ism);
        inv.setItem(26, is);
        
        
        player.openInventory(inv);
    }
    
    public static void MatchTohyoGUI(Player player){
        Inventory inv = Bukkit.createInventory(null, 18, "Chose a Gamemode");
    
        for (int i = 0; i <= 17; ) {
            ItemStack is = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            ItemMeta ism = is.getItemMeta();
            ism.setDisplayName(".");
            is.setItemMeta(ism);
            inv.setItem(i, is);
            i++;
        }
        
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
        ism.setDisplayName(Main.tutorial ? "§r§6ロビーへジャンプ" : "§r§6リスポーン地点へジャンプ");
        is.setItemMeta(ism);
        Location loc = Main.lobby.clone();
        if(!conf.getConfig().getString("WorkMode").equals("Trial"))
            loc = DataMgr.getPlayerData(player).getMatchLocation().clone();
        if(loc.getWorld() == player.getWorld()){
            if(player.getLocation().distance(loc) > 10 && !Tutorial.clearList.contains(player))
                if(!Main.tutorial)
                    inv.setItem(0, is);
        }
        
        int slotnum = 1;
        
        for(Player p : Main.getPlugin(Main.class).getServer().getOnlinePlayers()){
            if(p.getGameMode() == GameMode.SPECTATOR) continue;
            if(DataMgr.getPlayerData(p).getTeam().getID() == DataMgr.getPlayerData(player).getTeam().getID() && p.getWorld() == player.getWorld() && p != player){
                if(DataMgr.getPlayerData(p).getPlayerGroundLocation().distance(player.getLocation()) > 10 && DataMgr.getPlayerData(p).getPlayerHead() != null){
                    if (slotnum <= 17){
                        ItemStack head = CraftItemStack.asBukkitCopy(DataMgr.getPlayerData(p).getPlayerHead()).clone();
                        ItemMeta headM = head.getItemMeta();
                        List lores = new ArrayList();
                        lores.add("§r§aプレイヤーへジャンプ");
                        headM.setLore(lores);
                        head.setItemMeta(headM);
                        inv.setItem(slotnum, head);
                    }
                    slotnum++;
                }
            }
        }
        for(ArmorStand as : DataMgr.getBeaconMap().values()){
            if(as.getCustomName().equals("21")){
                Player p = DataMgr.getArmorStandPlayer(as);
                if(DataMgr.getPlayerData(player).getTeam() == DataMgr.getPlayerData(p).getTeam()){
                    if(as.getWorld() == player.getWorld()){
                        if(as.getLocation().distance(player.getLocation()) > 10){
                            ItemStack item = new ItemStack(Material.IRON_TRAPDOOR);
                            ItemMeta im = item.getItemMeta();
                            im.setDisplayName(p.getName());
                            List lores = new ArrayList();
                            lores.add("§r§6プレイヤーのビーコンへジャンプ");
                            im.setLore(lores);
                            item.setItemMeta(im);
                            if (slotnum <= 17){
                                inv.setItem(slotnum, item);
                            }
                            slotnum++;
                        }
                    }
                }
            }
        }
        player.openInventory(inv);
    }
    
    public static void openShop(Player player){
        int slotnum = 0;
        Inventory shooter = Bukkit.createInventory(null, 54, "武器選択");
        for (String ClassName : conf.getClassConfig().getConfigurationSection("WeaponClass").getKeys(false)){
            ItemStack item = new ItemStack(DataMgr.getWeaponClass(ClassName).getMainWeapon().getWeaponIteamStack());
            ItemMeta itemm = item.getItemMeta();
            itemm.setDisplayName(ClassName);
            List lores = new ArrayList();
            lores.add("§r§6SubWeapon : " + conf.getClassConfig().getString("WeaponClass." + ClassName + ".SubWeaponName"));
            lores.add("§r§6SPWeapon  : " + conf.getClassConfig().getString("WeaponClass." + ClassName + ".SPWeaponName"));
            itemm.setLore(lores);
            item.setItemMeta(itemm);
            if (slotnum <= 44 && (DataMgr.getWeaponClass(ClassName).getMainWeapon().getWeaponType().equals("Shooter") || DataMgr.getWeaponClass(ClassName).getMainWeapon().getWeaponType().equals("Burst") || DataMgr.getWeaponClass(ClassName).getMainWeapon().getWeaponType().equals("Blaster") || DataMgr.getWeaponClass(ClassName).getMainWeapon().getWeaponType().equals("Spinner"))){
                if(DataMgr.getWeaponClass(ClassName).getMainWeapon().getMoney() == 0){
                    shooter.setItem(slotnum, item);
                    slotnum++;
                }else if(PlayerStatusMgr.haveWeapon(player, ClassName)){
                    shooter.setItem(slotnum, item);
                    slotnum++;
                }
            }
        }
    }
    
    public static void openWeaponSelect(Player player, String type, String weaponType, boolean shop){
        switch(type){
            case"Weapon":
                int slotnum = 0;
                Inventory shooter = Bukkit.createInventory(null, 54, "武器選択");
                if(shop)
                    shooter = Bukkit.createInventory(null, 54, "Shop");
                for (String ClassName : conf.getClassConfig().getConfigurationSection("WeaponClass").getKeys(false)){
                    ItemStack item = new ItemStack(DataMgr.getWeaponClass(ClassName).getMainWeapon().getWeaponIteamStack());
                    ItemMeta itemm = item.getItemMeta();
                    itemm.setDisplayName(ClassName);
                    List lores = new ArrayList();
                    lores.add("§r§6SubWeapon : " + conf.getClassConfig().getString("WeaponClass." + ClassName + ".SubWeaponName"));
                    lores.add("§r§6SPWeapon  : " + conf.getClassConfig().getString("WeaponClass." + ClassName + ".SPWeaponName"));
                    if(shop){
                        lores.add("");
                        lores.add("§r§bMoney : " + String.valueOf(DataMgr.getWeaponClass(ClassName).getMainWeapon().getMoney()));
                    }
                    itemm.setLore(lores);
                    item.setItemMeta(itemm);
                    
                    List<String> list = new ArrayList<>();
                    list.add(weaponType);
                    
                    
                    switch(weaponType){
                        case"Slosher":
                            list.add("Bucket");
                            break;
                        case"Kasa":
                            list.add("Camping");
                    }
                    boolean equals = false;
                    for(String wtype : list){
                        if(wtype.equals(DataMgr.getWeaponClass(ClassName).getMainWeapon().getWeaponType()))
                            equals = true;
                    }
                    if(weaponType.equals("Hude") && DataMgr.getWeaponClass(ClassName).getMainWeapon().getWeaponType().equals("Roller")){
                        if(DataMgr.getWeaponClass(ClassName).getMainWeapon().getIsHude())
                            equals = true;
                    }
                    if(weaponType.equals("Roller") && DataMgr.getWeaponClass(ClassName).getMainWeapon().getWeaponType().equals("Roller")){
                        if(DataMgr.getWeaponClass(ClassName).getMainWeapon().getIsHude())
                            equals = false;
                    }
    
                    if(weaponType.equals("Maneu") && DataMgr.getWeaponClass(ClassName).getMainWeapon().getWeaponType().equals("Shooter")){
                        if(DataMgr.getWeaponClass(ClassName).getMainWeapon().getIsManeuver())
                            equals = true;
                    }
                    if(weaponType.equals("Shooter") && DataMgr.getWeaponClass(ClassName).getMainWeapon().getWeaponType().equals("Shooter")){
                        if(DataMgr.getWeaponClass(ClassName).getMainWeapon().getIsManeuver())
                            equals = false;
                    }
    
    
                    if (slotnum <= 52 && equals){
                        if(shop){
                            if(DataMgr.getWeaponClass(ClassName).getMainWeapon().getMoney() != 0 && !PlayerStatusMgr.haveWeapon(player, ClassName)){
                                if(DataMgr.getWeaponClass(ClassName).getMainWeapon().getLevel() > PlayerStatusMgr.getLv(player)){
                                    ItemStack glass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
                                    ItemMeta gmeta = glass.getItemMeta();
                                    gmeta.setDisplayName("§6レベル§c" + DataMgr.getWeaponClass(ClassName).getMainWeapon().getLevel() + "§6で解禁");
                                    glass.setItemMeta(gmeta);
                                    shooter.setItem(slotnum, glass);
                                }else{
                                    shooter.setItem(slotnum, item);
                                }
                                slotnum++;
                            }
                        }else{
                            if(DataMgr.getWeaponClass(ClassName).getMainWeapon().getMoney() == 0 || conf.getConfig().getString("WorkMode").equals("Trial")){
                                if(DataMgr.getWeaponClass(ClassName).getMainWeapon().getLevel() > PlayerStatusMgr.getLv(player)){
                                    ItemStack glass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
                                    ItemMeta gmeta = glass.getItemMeta();
                                    gmeta.setDisplayName("§6レベル§c" + DataMgr.getWeaponClass(ClassName).getMainWeapon().getLevel() + "§6で解禁");
                                    glass.setItemMeta(gmeta);
                                    shooter.setItem(slotnum, glass);
                                }else{
                                    shooter.setItem(slotnum, item);
                                }
                                slotnum++;
                            }else if(PlayerStatusMgr.haveWeapon(player, ClassName) || !Main.shop){
                                shooter.setItem(slotnum, item);
                                slotnum++;
                            }
                        }
                    }
                }
                if(!Main.tutorial) {
                    ItemStack is = new ItemStack(Material.OAK_DOOR);
                    ItemMeta ism = is.getItemMeta();
                    ism.setDisplayName("戻る");
                    is.setItemMeta(ism);
                    shooter.setItem(53, is);
                }
                
                player.openInventory(shooter);
                break;
            case"Main":
                Inventory wm = Bukkit.createInventory(null, 18, "武器選択");
                if(shop)
                    wm = Bukkit.createInventory(null, 18, "Shop");
                
                ItemStack s = new ItemStack(Material.WOODEN_HOE);
                ItemMeta sm = s.getItemMeta();
                sm.setDisplayName("シューター");
                s.setItemMeta(sm);
                
                ItemStack b = new ItemStack(Material.DIAMOND_SHOVEL);
                ItemMeta bm = b.getItemMeta();
                bm.setDisplayName("ブラスター");
                b.setItemMeta(bm);
                
                ItemStack ba = new ItemStack(Material.DIAMOND_AXE);
                ItemMeta bam = ba.getItemMeta();
                bam.setDisplayName("バーストシューター");
                ba.setItemMeta(bam);

                ItemStack r = new ItemStack(Material.STONE_PICKAXE);
                ItemMeta rm = r.getItemMeta();
                rm.setDisplayName("ローラー");
                r.setItemMeta(rm);
                
                ItemStack f = new ItemStack(Material.CARROT_ON_A_STICK);
                ItemMeta fm = f.getItemMeta();
                fm.setDisplayName("ブラシ");
                f.setItemMeta(fm);
                
                ItemStack sy = new ItemStack(Material.KELP);
                ItemMeta sym = sy.getItemMeta();
                sym.setDisplayName("シェルター");
                sy.setItemMeta(sym);
                
                ItemStack sr = new ItemStack(Material.NETHER_BRICK);
                ItemMeta srm = sr.getItemMeta();
                srm.setDisplayName("スロッシャー");
                sr.setItemMeta(srm);

                ItemStack c = new ItemStack(Material.WOODEN_SWORD);
                ItemMeta cm = c.getItemMeta();
                cm.setDisplayName("チャージャー");
                c.setItemMeta(cm);
                
                ItemStack sp = new ItemStack(Material.IRON_INGOT);
                ItemMeta spm = sp.getItemMeta();
                spm.setDisplayName("スピナー");
                sp.setItemMeta(spm);
    
                ItemStack m = new ItemStack(Material.GOLDEN_HOE);
                ItemMeta mm = m.getItemMeta();
                mm.setDisplayName("マニューバー");
                m.setItemMeta(mm);

                wm.setItem(0, s);
                wm.setItem(1, b);
                wm.setItem(2, ba);
                wm.setItem(3, r);
                wm.setItem(4, f);
                wm.setItem(5, sy);
                wm.setItem(6, sr);
                wm.setItem(7, c);
                wm.setItem(8, sp);
                wm.setItem(9, m);
                player.openInventory(wm);
    
                ItemStack is = new ItemStack(Material.OAK_DOOR);
                ItemMeta ism = is.getItemMeta();
                ism.setDisplayName("装備選択へ戻る");
                is.setItemMeta(ism);
                wm.setItem(17, is);
                
                break;
        }
    }
    
    public static void openSettingsUI(Player player){
        Inventory inv = Bukkit.createInventory(null, 36, "設定");
        
        for (int i = 0; i <= 35; ) {
            ItemStack is = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            ItemMeta ism = is.getItemMeta();
            ism.setDisplayName(".");
            is.setItemMeta(ism);
            inv.setItem(i, is);
            i++;
        }
    
        ItemStack is = new ItemStack(Material.OAK_DOOR);
        ItemMeta ism = is.getItemMeta();
        ism.setDisplayName("戻る");
        is.setItemMeta(ism);
        inv.setItem(35, is);
        
        ItemStack shooter = new ItemStack(Material.WOODEN_HOE);
        ItemMeta shooter_m = shooter.getItemMeta();
        shooter_m.setDisplayName("メインウエポンのインクエフェクト");
        ArrayList<String> shooter_r = new ArrayList<String>();
        if(DataMgr.getPlayerData(player).getSettings().ShowEffect_MainWeaponInk())
            shooter_r.add("§a§l[Enable]");
        else
            shooter_r.add("§7§l[Disable]");
        shooter_r.add("");
        shooter_r.add("§b[INFO]§r");
        shooter_r.add("メインウエポンの弾の軌跡にインクのエフェクトを描画します。");
        shooter_r.add("無効化するとクライアントのパーティクル描画負担と");
        shooter_r.add("通信量を削減することができます。");
        shooter_m.setLore(shooter_r);
        shooter.setItemMeta(shooter_m);
        inv.setItem(9, shooter);
        
        ItemStack shooter_p;
        if(DataMgr.getPlayerData(player).getSettings().ShowEffect_MainWeaponInk())
            shooter_p = new ItemStack(Material.LIME_DYE);
        else
            shooter_p = new ItemStack(Material.GUNPOWDER);
        ItemMeta shooter_p_m = shooter_p.getItemMeta();
        shooter_p_m.setDisplayName("メインウエポンのインクエフェクト");
        ArrayList<String> shooter_p_r = new ArrayList<String>();
        if(DataMgr.getPlayerData(player).getSettings().ShowEffect_MainWeaponInk())
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
        charger_r.add("");
        charger_r.add("§b[INFO]§r");
        charger_r.add("他のプレイヤーがチャージして狙っているときに");
        charger_r.add("方向と射撃距離を表すレーザーを描画します。");
        charger_r.add("無効化するとクライアントのパーティクル描画負担と");
        charger_r.add("通信量を削減することができます。");
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
        
        
        
        
        ItemStack chargerS = new ItemStack(Material.END_CRYSTAL);
        ItemMeta chargerS_m = chargerS.getItemMeta();
        chargerS_m.setDisplayName("スペシャルウエポンのエフェクト");
        ArrayList<String> chargerS_r = new ArrayList<String>();
        if(DataMgr.getPlayerData(player).getSettings().ShowEffect_SPWeapon())
            chargerS_r.add("§a§l[Enable]");
        else
            chargerS_r.add("§7§l[Disable]");
        chargerS_r.add("");
        chargerS_r.add("§b[INFO]§r");
        chargerS_r.add("プレイヤーがスペシャルウエポンを使用しているときに");
        chargerS_r.add("演出用のエフェクトを描画します。");
        chargerS_r.add("無効化するとクライアントのパーティクル描画負担と");
        chargerS_r.add("通信量を削減することができます。");
        chargerS_m.setLore(chargerS_r);
        chargerS.setItemMeta(chargerS_m);
        inv.setItem(11, chargerS);
        
        ItemStack chargerS_p;
        if(DataMgr.getPlayerData(player).getSettings().ShowEffect_SPWeapon())
            chargerS_p = new ItemStack(Material.LIME_DYE);
        else
            chargerS_p = new ItemStack(Material.GUNPOWDER);
        ItemMeta chargerS_p_m = chargerS_p.getItemMeta();
        chargerS_p_m.setDisplayName("スペシャルウエポンのエフェクト");
        ArrayList<String> chargerS_p_r = new ArrayList<String>();
        if(DataMgr.getPlayerData(player).getSettings().ShowEffect_SPWeapon())
            chargerS_p_r.add("§a§l[Enable]");
        else
            chargerS_p_r.add("§7§l[Disable]");
        chargerS_p_m.setLore(chargerS_p_r);
        chargerS_p.setItemMeta(chargerS_p_m);
        inv.setItem(20, chargerS_p);
        
        
        
        ItemStack rollaerL = new ItemStack(Material.SHULKER_SHELL);
        ItemMeta rollaerL_m = rollaerL.getItemMeta();
        rollaerL_m.setDisplayName("スペシャルウエポンの範囲エフェクト");
        ArrayList<String> rollaerL_r = new ArrayList<String>();
        if(DataMgr.getPlayerData(player).getSettings().ShowEffect_SPWeaponRegion())
            rollaerL_r.add("§a§l[Enable]");
        else
            rollaerL_r.add("§7§l[Disable]");
        rollaerL_r.add("");
        rollaerL_r.add("§b[INFO]§r");
        rollaerL_r.add("プレイヤーがスペシャルウエポンを使用しているときに");
        rollaerL_r.add("スペシャルウエポンの効果範囲を表すエフェクトを描画します。");
        rollaerL_r.add("無効化するとクライアントのパーティクル描画負担と");
        rollaerL_r.add("通信量を削減することができますが");
        rollaerL_r.add("スペシャルウエポンの効果範囲を把握しづらくなります。");
        rollaerL_m.setLore(rollaerL_r);
        rollaerL.setItemMeta(rollaerL_m);
        inv.setItem(12, rollaerL);
        
        ItemStack rollaerL_p;
        if(DataMgr.getPlayerData(player).getSettings().ShowEffect_SPWeaponRegion())
            rollaerL_p = new ItemStack(Material.LIME_DYE);
        else
            rollaerL_p = new ItemStack(Material.GUNPOWDER);
        ItemMeta rollaerL_p_m = rollaerL_p.getItemMeta();
        rollaerL_p_m.setDisplayName("スペシャルウエポンの範囲エフェクト");
        ArrayList<String> rollaerL_p_r = new ArrayList<String>();
        if(DataMgr.getPlayerData(player).getSettings().ShowEffect_SPWeaponRegion())
            rollaerL_p_r.add("§a§l[Enable]");
        else
            rollaerL_p_r.add("§7§l[Disable]");
        rollaerL_p_m.setLore(rollaerL_p_r);
        rollaerL_p.setItemMeta(rollaerL_p_m);
        inv.setItem(21, rollaerL_p);
        
        
        
        ItemStack rollerS = new ItemStack(Material.SNOWBALL);
        ItemMeta rollerS_m = rollerS.getItemMeta();
        rollerS_m.setDisplayName("弾の表示");
        ArrayList<String> rollerS_r = new ArrayList<String>();
        if(DataMgr.getPlayerData(player).getSettings().ShowSnowBall())
            rollerS_r.add("§a§l[Enable]");
        else
            rollerS_r.add("§7§l[Disable]");
        rollerS_r.add("");
        rollerS_r.add("§b[INFO]§r");
        rollerS_r.add("メインウエポンから発射された弾を描画します。");
        rollerS_r.add("無効化するとクライアントのエンティティ描画負担と");
        rollerS_r.add("通信量を削減することができます。");
        rollerS_m.setLore(rollerS_r);
        rollerS.setItemMeta(rollerS_m);
        inv.setItem(13, rollerS);
        
        ItemStack rollerS_p;
        if(DataMgr.getPlayerData(player).getSettings().ShowSnowBall())
            rollerS_p = new ItemStack(Material.LIME_DYE);
        else
            rollerS_p = new ItemStack(Material.GUNPOWDER);
        ItemMeta rollerS_p_m = rollerS_p.getItemMeta();
        rollerS_p_m.setDisplayName("弾の表示");
        ArrayList<String> rollerS_p_r = new ArrayList<String>();
        if(DataMgr.getPlayerData(player).getSettings().ShowSnowBall())
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
        //if(Main.NoteBlockAPI)
            //inv.setItem(26, bgm_p);
        
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
        //if(Main.NoteBlockAPI)
            //inv.setItem(17, bgm);
        
        
        
        ItemStack bomb = new ItemStack(Material.WHITE_STAINED_GLASS);
        ItemMeta bomb_m = bomb.getItemMeta();
        bomb_m.setDisplayName("投擲武器の視認用エフェクト");
        ArrayList<String> bomb_r = new ArrayList<String>();
        if(DataMgr.getPlayerData(player).getSettings().ShowEffect_Bomb())
            bomb_r.add("§a§l[Enable]");
        else
            bomb_r.add("§7§l[Disable]");
        bomb_r.add("");
        bomb_r.add("§b[INFO]§r");
        bomb_r.add("サブウエポン等の投擲武器の軌跡にエフェクトを描画します。");
        bomb_r.add("無効化するとクライアントのパーティクル描画負担と");
        bomb_r.add("通信量を削減することができます。");
        bomb_m.setLore(bomb_r);
        bomb.setItemMeta(bomb_m);
        inv.setItem(14, bomb);
        
        ItemStack bomb_p;
        if(DataMgr.getPlayerData(player).getSettings().ShowEffect_Bomb())
            bomb_p = new ItemStack(Material.LIME_DYE);
        else
            bomb_p = new ItemStack(Material.GUNPOWDER);
        ItemMeta bomb_p_m = bomb_p.getItemMeta();
        bomb_p_m.setDisplayName("投擲武器の視認用エフェクト");
        ArrayList<String> bomb_p_r = new ArrayList<String>();
        if(DataMgr.getPlayerData(player).getSettings().ShowEffect_Bomb())
            bomb_p_r.add("§a§l[Enable]");
        else
            bomb_p_r.add("§7§l[Disable]");
        bomb_p_m.setLore(bomb_p_r);
        bomb_p.setItemMeta(bomb_p_m);
        inv.setItem(23, bomb_p);
        
        
        ItemStack bombEx = new ItemStack(Material.TNT);
        ItemMeta bombEx_m = bombEx.getItemMeta();
        bombEx_m.setDisplayName("爆発エフェクト");
        ArrayList<String> bombEx_r = new ArrayList<String>();
        if(DataMgr.getPlayerData(player).getSettings().ShowEffect_BombEx())
            bombEx_r.add("§a§l[Enable]");
        else
            bombEx_r.add("§7§l[Disable]");
        bombEx_r.add("");
        bombEx_r.add("§b[INFO]§r");
        bombEx_r.add("ボム等の爆発エフェクトを描画します。");
        bombEx_r.add("無効化するとクライアントのパーティクル描画負担と");
        bombEx_r.add("通信量を削減することができます。");
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
        
        
        ItemStack ck = new ItemStack(Material.GOLDEN_SWORD);
        ItemMeta ck_m = ck.getItemMeta();
        ck_m.setDisplayName("チャージキープ");
        ArrayList<String> ck_r = new ArrayList<String>();
        if(DataMgr.getPlayerData(player).getSettings().doChargeKeep())
            ck_r.add("§a§l[Enable]");
        else
            ck_r.add("§7§l[Disable]");
        ck_r.add("");
        ck_r.add("§b[INFO]§r");
        ck_r.add("チャージャー等のチャージキープ機能を発動できるようになります。");
        ck_r.add("(チャージキープは十分チャージした後にイカ状態に切り替えると発動します。)");
        ck_m.setLore(ck_r);
        ck.setItemMeta(ck_m);
        inv.setItem(16, ck);
        
        ItemStack ck_p;
        if(DataMgr.getPlayerData(player).getSettings().doChargeKeep())
            ck_p = new ItemStack(Material.LIME_DYE);
        else
            ck_p = new ItemStack(Material.GUNPOWDER);
        ItemMeta ck_p_m = ck_p.getItemMeta();
        ck_p_m.setDisplayName("チャージキープ");
        ArrayList<String> ck_p_r = new ArrayList<String>();
        if(DataMgr.getPlayerData(player).getSettings().doChargeKeep())
            ck_p_r.add("§a§l[Enable]");
        else
            ck_p_r.add("§7§l[Disable]");
        ck_p_m.setLore(ck_p_r);
        ck_p.setItemMeta(ck_p_m);
        inv.setItem(25, ck_p);
        
        player.openInventory(inv);
    }
}
