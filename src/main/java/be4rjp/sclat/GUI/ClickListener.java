
package be4rjp.sclat.GUI;

import be4rjp.sclat.Main;
import static be4rjp.sclat.Main.conf;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.Match;
import be4rjp.sclat.data.PaintData;
import be4rjp.sclat.data.PlayerData;
import be4rjp.sclat.data.WeaponClass;
import be4rjp.sclat.manager.ArmorStandMgr;
import be4rjp.sclat.manager.BungeeCordMgr;
import be4rjp.sclat.manager.MatchMgr;
import be4rjp.sclat.manager.PlayerStatusMgr;
import be4rjp.sclat.manager.SPWeaponMgr;
import be4rjp.sclat.manager.SuperJumpMgr;
import be4rjp.sclat.manager.WeaponClassMgr;
import be4rjp.sclat.weapon.Charger;
import be4rjp.sclat.weapon.Gear;
import be4rjp.sclat.weapon.Roller;
import be4rjp.sclat.weapon.Shooter;
import be4rjp.sclat.weapon.Spinner;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Be4rJP
 */
public class ClickListener implements Listener{
    @EventHandler
    public void onGUIClick(InventoryClickEvent event){
        String name = event.getCurrentItem().getItemMeta().getDisplayName();
        Player player = (Player)event.getWhoClicked();
        player.closeInventory();
        //player.sendMessage(name);
        
        switch(name){
            case"試合に参加 / JOIN THE MATCH":
                MatchMgr.PlayerJoinMatch(player);
                break;
            case"装備変更 / EQUIPMENT":
                OpenGUI.equipmentGUI(player);
                break;
            case"ギア変更 / GEAR":
                OpenGUI.gearGUI(player);
                break;
            case"武器変更 / WEAPON":
                OpenGUI.openWeaponSelect(player, "Main", false);
                break;
            case"設定 / SETTINGS":
                OpenGUI.openSettingsUI(player);
                break;
            case"ショップを開く / OPEN SHOP":
                OpenGUI.openWeaponSelect(player, "Main", true);
                break;
            case"塗りをリセット / RESET INK":
                MatchMgr.RollBack();
                player.setExp(0.99F);
                player.sendMessage("3分後に再リセットできるようになります");
                List<Block> blocks = new ArrayList<Block>();
                Block b0 = Main.lobby.getBlock().getRelative(BlockFace.DOWN);
                blocks.add(b0);
                blocks.add(b0.getRelative(BlockFace.EAST));
                blocks.add(b0.getRelative(BlockFace.NORTH));
                blocks.add(b0.getRelative(BlockFace.SOUTH));
                blocks.add(b0.getRelative(BlockFace.WEST));
                blocks.add(b0.getRelative(BlockFace.NORTH_EAST));
                blocks.add(b0.getRelative(BlockFace.NORTH_WEST));
                blocks.add(b0.getRelative(BlockFace.SOUTH_EAST));
                blocks.add(b0.getRelative(BlockFace.SOUTH_WEST));
                for(Block block : blocks) {
                    if(block.getType().equals(Material.WHITE_STAINED_GLASS)){
                        PaintData pdata = new PaintData(block);
                        Match match = DataMgr.getPlayerData(player).getMatch();
                        pdata.setMatch(match);
                        pdata.setTeam(match.getTeam0());
                        pdata.setOrigianlType(block.getType());
                        DataMgr.setPaintDataFromBlock(block, pdata);
                        block.setType(match.getTeam0().getTeamColor().getGlass());
                    }
                }
                break;
            case"ロビーへ戻る / RETURN TO LOBBY":
                BungeeCordMgr.PlayerSendServer(player, "lobby");
                DataMgr.getPlayerData(player).setServerName("Lobby");
                break;
            case"試し打ちサーバーへ接続 / TRAINING FIELD":
                BungeeCordMgr.PlayerSendServer(player, "trial");
                DataMgr.getPlayerData(player).setServerName("Trial");
                break;
            case"チームデスマッチサーバーへ接続 / CONNECT TO TDM SERVER":
                BungeeCordMgr.PlayerSendServer(player, "tdm");
                DataMgr.getPlayerData(player).setServerName("TDM");
                break;
            case"ナワバリバトル":
                Match match = DataMgr.getMatchFromId(MatchMgr.matchcount);
                match.addNawabari_T_Count();
                break;
            case"チームデスマッチ":
                Match m = DataMgr.getMatchFromId(MatchMgr.matchcount);
                m.addTDM_T_Count();
                break;
            case"ガチエリア":
                Match m2 = DataMgr.getMatchFromId(MatchMgr.matchcount);
                m2.addGatiArea_T_Count();
                break;
        }
        if(name.equals("リソースパックをダウンロード / DOWNLOAD RESOURCEPACK"))
            player.setResourcePack(conf.getConfig().getString("ResourcePackURL"));
        if(event.getClickedInventory().getTitle().equals("Gear")){
            for(int i = 0; i <= 6;){
                if(Gear.getGearName(i).equals(name)){
                    DataMgr.getPlayerData(player).setGearNumber(i);
                    PlayerStatusMgr.setGear(player, i);
                    break;
                }
                i++;
            }
        }
        if(event.getClickedInventory().getTitle().equals("武器選択")){
            if(name.equals("戻る") || name.equals("シューター") || name.equals("ローラー") || name.equals("チャージャー")){
                switch(name){
                    case"シューター":
                        OpenGUI.openWeaponSelect(player, "Shooter", false);
                        break;
                    case"ローラー":
                        OpenGUI.openWeaponSelect(player, "Roller", false);
                        break;
                    case"チャージャー":
                        OpenGUI.openWeaponSelect(player, "Charger", false);
                        break;
                    case"戻る":
                        OpenGUI.openWeaponSelect(player, "Main", false);
                        break;
                }
                return;
            }
            //試しうちモード
            if(conf.getConfig().getString("WorkMode").equals("Trial")){
                
                player.getInventory().clear();
                DataMgr.getPlayerData(player).setIsInMatch(false);
                DataMgr.getPlayerData(player).setIsJoined(false);
                
                for(ArmorStand as : DataMgr.getBeaconMap().values()){
                    if(DataMgr.getBeaconFromplayer(player) == as)
                        as.remove();
                }
                for(ArmorStand as : DataMgr.getSprinklerMap().values()){
                    if(DataMgr.getSprinklerFromplayer(player) == as)
                        as.remove();
                }

                BukkitRunnable delay = new BukkitRunnable(){
                    Player p = player;
                    @Override
                    public void run(){
                        DataMgr.getPlayerData(player).setIsInMatch(true);
                        DataMgr.getPlayerData(player).setIsJoined(true);
                        WeaponClass wc = DataMgr.getWeaponClass(name);
                        DataMgr.getPlayerData(player).setWeaponClass(wc);
                        if(DataMgr.getPlayerData(p).getWeaponClass().getSubWeaponName().equals("ビーコン"))
                            ArmorStandMgr.BeaconArmorStandSetup(p);
                        if(DataMgr.getPlayerData(p).getWeaponClass().getSubWeaponName().equals("スプリンクラー"))
                            ArmorStandMgr.SprinklerArmorStandSetup(p);
                        if(wc.getMainWeapon().getWeaponType().equals("Shooter"))
                            Shooter.ShooterRunnable(p);
                        if(wc.getMainWeapon().getWeaponType().equals("Charger"))
                            Charger.ChargerRunnable(p);
                        if(wc.getMainWeapon().getWeaponType().equals("Spinner"))
                            Spinner.SpinnerRunnable(p);
                        if(wc.getMainWeapon().getWeaponType().equals("Roller")){
                            Roller.HoldRunnable(p);
                            Roller.RollPaintRunnable(p);
                        }
                        WeaponClassMgr.setWeaponClass(p);
                        ItemStack join = new ItemStack(Material.CHEST);
                        ItemMeta joinmeta = join.getItemMeta();
                        joinmeta.setDisplayName("メインメニュー");
                        join.setItemMeta(joinmeta);
                        player.getInventory().setItem(7, join);
                        player.setExp(0.99F);
                        SPWeaponMgr.SPWeaponRunnable(player);
                    }
                };
                delay.runTaskLater(Main.getPlugin(), 15);
            }else{
                DataMgr.getPlayerData(player).setWeaponClass(DataMgr.getWeaponClass(name));
            }
            player.sendMessage(name + "を選択しました");
        }
        
        if(event.getClickedInventory().getTitle().equals("Shop")){
            if(name.equals("戻る") || name.equals("シューター") || name.equals("ローラー") || name.equals("チャージャー")){
                switch(name){
                    case"シューター":
                        OpenGUI.openWeaponSelect(player, "Shooter", true);
                        break;
                    case"ローラー":
                        OpenGUI.openWeaponSelect(player, "Roller", true);
                        break;
                    case"チャージャー":
                        OpenGUI.openWeaponSelect(player, "Charger", true);
                        break;
                    case"戻る":
                        OpenGUI.openWeaponSelect(player, "Main", true);
                        break;
                }
                return;
            }
            player.closeInventory();
            if(PlayerStatusMgr.getMoney(player) >= DataMgr.getWeaponClass(name).getMainWeapon().getMoney()){
                PlayerStatusMgr.addWeapon(player, name);
                PlayerStatusMgr.subMoney(player, DataMgr.getWeaponClass(name).getMainWeapon().getMoney());
                player.sendMessage(ChatColor.GREEN + "購入に成功しました");
                player.playNote(player.getLocation(), Instrument.PIANO, Note.natural(1, Note.Tone.G));
                PlayerStatusMgr.sendHologramUpdate(player);
            }else{
                player.sendMessage(ChatColor.RED + "お金が足りません");
                player.playNote(player.getLocation(), Instrument.BASS_GUITAR, Note.flat(0, Note.Tone.G));
            }
        }
        
        if(event.getClickedInventory().getTitle().equals("Chose Target")){
            if(name.equals("リスポーン地点"))
                SuperJumpMgr.SuperJumpCollTime(player, DataMgr.getPlayerData(player).getMatchLocation());
            for(Player p : Main.getPlugin(Main.class).getServer().getOnlinePlayers()){
                if (p.getName().equals(name)){
                    if(event.getCurrentItem().getType().equals(Material.PLAYER_HEAD))
                        SuperJumpMgr.SuperJumpCollTime(player, p.getLocation());
                    if(event.getCurrentItem().getType().equals(Material.IRON_TRAPDOOR))
                        SuperJumpMgr.SuperJumpCollTime(player, DataMgr.getBeaconFromplayer(p).getLocation());
                }
            }
        }
        
        
        if(event.getClickedInventory().getTitle().equals("設定")){
            switch (name){
                case "シューターのパーティクル":
                    DataMgr.getPlayerData(player).getSettings().S_ShowEffect_Shooter();
                    break;
                case "チャージャーのレーザー":
                    DataMgr.getPlayerData(player).getSettings().S_ShowEffect_ChargerLine();
                    break;
                case "チャージャーの射撃エフェクト":
                    DataMgr.getPlayerData(player).getSettings().S_ShowEffect_ChargerShot();
                    break;
                case "ローラーのロール":
                    DataMgr.getPlayerData(player).getSettings().S_ShowEffect_RollerRoll();
                    break;
                case "ローラーのしぶき":
                    DataMgr.getPlayerData(player).getSettings().S_ShowEffect_RollerShot();
                    break;
                case "BGM":
                    DataMgr.getPlayerData(player).getSettings().S_PlayBGM();
                    break;
                case "ボムの視認用エフェクト":
                    DataMgr.getPlayerData(player).getSettings().S_ShowEffect_Bomb();
                    break;
                case "爆発エフェクト":
                    DataMgr.getPlayerData(player).getSettings().S_ShowEffect_BombEx();
                    break;
            }
            
            OpenGUI.openSettingsUI(player);
            
            player.playNote(player.getLocation(), Instrument.STICKS, Note.flat(1, Note.Tone.C));
            
            String B = DataMgr.getPlayerData(player).getSettings().PlayBGM() ? "1" : "0";
            String E_S = DataMgr.getPlayerData(player).getSettings().ShowEffect_Shooter() ? "1" : "0";
            String E_CL = DataMgr.getPlayerData(player).getSettings().ShowEffect_ChargerLine() ? "1" : "0";
            String E_CS = DataMgr.getPlayerData(player).getSettings().ShowEffect_ChargerShot() ? "1" : "0";
            String E_RR = DataMgr.getPlayerData(player).getSettings().ShowEffect_RollerRoll() ? "1" : "0";
            String E_RS = DataMgr.getPlayerData(player).getSettings().ShowEffect_RollerShot() ? "1" : "0";
            //String E_BGM = DataMgr.getPlayerData(player).getSettings().PlayBGM() ? "1" : "0";
            String E_B = DataMgr.getPlayerData(player).getSettings().ShowEffect_Bomb() ? "1" : "0";
            String E_BEx = DataMgr.getPlayerData(player).getSettings().ShowEffect_BombEx() ? "1" : "0";
            
            String s_data = B + E_S + E_CL + E_CS + E_RR + E_RS + E_B + E_BEx;
            
            String uuid = player.getUniqueId().toString();
            conf.getPlayerSettings().set("Settings." + uuid, s_data);
        }
        
        if(!player.getGameMode().equals(GameMode.CREATIVE))
            event.setCancelled(true);
    }
    
    @EventHandler
    public void onOpenMainMenu(PlayerInteractEvent event){
        Player player = (Player)event.getPlayer();
        Action action = event.getAction();
        if(action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)){
            if(player.getInventory().getItemInMainHand().getType().equals(Material.CHEST))
                OpenGUI.openMenu(player);
            if(player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("スーパージャンプ"))
                OpenGUI.SuperJumpGUI(player);
        }
    }
}
