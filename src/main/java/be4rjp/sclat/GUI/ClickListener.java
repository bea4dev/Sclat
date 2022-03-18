
package be4rjp.sclat.GUI;

import be4rjp.sclat.*;

import static be4rjp.sclat.Main.conf;

import be4rjp.sclat.data.*;
import be4rjp.sclat.manager.*;
import be4rjp.sclat.weapon.*;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Be4rJP
 */
public class ClickListener implements Listener{
    @EventHandler
    public void onGUIClick(InventoryClickEvent event){
        if(event.getCurrentItem() == null || event.getCurrentItem().getItemMeta() == null || event.getCurrentItem().getItemMeta().getDisplayName() == null || event.getView().getTitle() == null)
            return;
        
        String name = event.getCurrentItem().getItemMeta().getDisplayName();
        Player player = (Player)event.getWhoClicked();
    
        if(name.equals(".")) {
            event.setCancelled(true);
            return;
        }
        
        if(name.equals(""))
            return;
        else
            player.closeInventory();
        //player.sendMessage(name);
        
        
        switch(name){
            case"試合に参加 / JOIN THE MATCH":
                if(Main.type == ServerType.LOBBY)
                    ServerStatusManager.openServerList(player);
                else
                    MatchMgr.PlayerJoinMatch(player);
                break;
            case"装備変更 / EQUIPMENT":
                OpenGUI.equipmentGUI(player, false);
                break;
            case"§bギア変更 / GEAR":
                OpenGUI.gearGUI(player, false);
                break;
            case"§6武器変更 / WEAPON":
                OpenGUI.openWeaponSelect(player, "Main", "null", false);
                break;
            case"§bギア購入 / GEAR":
                OpenGUI.gearGUI(player, true);
                break;
            case"§6武器購入 / WEAPON":
                OpenGUI.openWeaponSelect(player, "Main", "null", true);
                break;
            case"設定 / SETTINGS":
                OpenGUI.openSettingsUI(player);
                break;
            case"ショップを開く / OPEN SHOP":
                OpenGUI.equipmentGUI(player, true);
                break;
            case"塗りをリセット / RESET INK":
                if(MatchMgr.canRollback) {
                    Sclat.sendMessage("§a§lインクがリセットされました！", MessageType.ALL_PLAYER);
                    Sclat.sendMessage("§a§l3分後に再リセットできるようになります", MessageType.ALL_PLAYER);
                    for(Player op : Main.getPlugin().getServer().getOnlinePlayers())
                        Sclat.playGameSound(op, SoundType.SUCCESS);
                }
                Match match = DataMgr.getPlayerData(player).getMatch();
                match.getBlockUpdater().stop();
                MatchMgr.RollBack();
                player.setExp(0.99F);
                BlockUpdater bur = new BlockUpdater();
                if(conf.getConfig().contains("BlockUpdateRate"))
                    bur.setMaxBlockInOneTick(conf.getConfig().getInt("BlockUpdateRate"));
                bur.start();
                match.setBlockUpdater(bur);
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
                        pdata.setMatch(match);
                        pdata.setTeam(match.getTeam0());
                        pdata.setOrigianlType(block.getType());
                        DataMgr.setPaintDataFromBlock(block, pdata);
                        block.setType(match.getTeam0().getTeamColor().getGlass());
                    }
                }
                break;
            case"ロビーへ戻る / RETURN TO LOBBY":
                if(Main.type != ServerType.LOBBY) {
                    BungeeCordMgr.PlayerSendServer(player, "sclat");
                    DataMgr.getPlayerData(player).setServerName("Sclat");
                }else{
                    BungeeCordMgr.PlayerSendServer(player, "lobby");
                    DataMgr.getPlayerData(player).setServerName("Lobby");
                }
                break;
            case"JGへ戻る / RETURN TO JG":
                BungeeCordMgr.PlayerSendServer(player, "jg");
                DataMgr.getPlayerData(player).setServerName("JG");
                break;
            case"試し打ちサーバーへ接続 / TRAINING FIELD":
                BungeeCordMgr.PlayerSendServer(player, "sclattest");
                DataMgr.getPlayerData(player).setServerName("sclattest");
                break;
            case"チームデスマッチサーバーへ接続 / CONNECT TO TDM SERVER":
                BungeeCordMgr.PlayerSendServer(player, "tdm");
                DataMgr.getPlayerData(player).setServerName("TDM");
                break;
            case"ナワバリバトル":
                Match ma = DataMgr.getMatchFromId(MatchMgr.matchcount);
                ma.addNawabari_T_Count();
                break;
            case"チームデスマッチ":
                Match m = DataMgr.getMatchFromId(MatchMgr.matchcount);
                m.addTDM_T_Count();
                break;
            case"ガチエリア":
                Match m2 = DataMgr.getMatchFromId(MatchMgr.matchcount);
                m2.addGatiArea_T_Count();
                break;
            case"戻る":
                if(!name.equals("武器選択") || !name.equals("Shop"))
                    OpenGUI.openMenu(player);
                break;
        }
        if(name.equals("リソースパックをダウンロード / DOWNLOAD RESOURCEPACK"))
            player.setResourcePack(conf.getConfig().getString("ResourcePackURL"));
        if(event.getView().getTitle().equals("Gear")){
            for(int i = 0; i <= 8;){
                if(Gear.getGearName(i).equals(name)){
                    DataMgr.getPlayerData(player).setGearNumber(i);
                    PlayerStatusMgr.setGear(player, i);
                    Sclat.sendMessage("ギア[" + ChatColor.AQUA + name + ChatColor.RESET + "]を選択しました", MessageType.PLAYER, player);
                    break;
                }
                i++;
            }
        }else if(event.getView().getTitle().equals("Gear shop")){
            for(int i = 0; i <= 8;){
                if(Gear.getGearName(i).equals(name)){
                    if(PlayerStatusMgr.getMoney(player) >= Gear.getGearPrice(i)){
                        PlayerStatusMgr.addGear(player, i);
                        PlayerStatusMgr.subMoney(player, Gear.getGearPrice(i));
                        Sclat.sendMessage(ChatColor.GREEN + "購入に成功しました", MessageType.PLAYER, player);
                        Sclat.playGameSound(player, SoundType.SUCCESS);
                        PlayerStatusMgr.sendHologramUpdate(player);
                    }else{
                        Sclat.sendMessage(ChatColor.RED + "お金が足りません", MessageType.PLAYER, player);
                        Sclat.playGameSound(player, SoundType.ERROR);
                    }
                    break;
                }
                i++;
            }
        }
        if(event.getView().getTitle().equals("Server List")){
            for (ServerStatus ss : ServerStatusManager.serverList){
                if(ss.getDisplayName().equals(name)){
                    if(ss.getRestartingServer()){
                        Sclat.sendMessage("§c§nこのサーバーは再起動中のため参加できません", MessageType.PLAYER, player);
                        Sclat.playGameSound(player, SoundType.ERROR);
                        return;
                    }
                    if(ss.isOnline()) {
                        if(ss.getPlayerCount() < ss.getMaxPlayer()) {
                            if(ss.getRunningMatch()) {
                                Sclat.sendMessage("§c§nこのサーバーは試合中のため参加できません", MessageType.PLAYER, player);
                                Sclat.playGameSound(player, SoundType.ERROR);
                                return;
                            }
                            BungeeCordMgr.PlayerSendServer(player, ss.getServerName());
                            DataMgr.getPlayerData(player).setServerName(ss.getDisplayName());
                        }else{
                            Sclat.sendMessage("§c§nこのサーバーは満員のため参加できません", MessageType.PLAYER, player);
                            Sclat.playGameSound(player, SoundType.ERROR);
                        }
                    }else{
                        if(ss.isMaintenance())
                            Sclat.sendMessage("§c§nこのサーバーは現在メンテナンス中のため参加できません", MessageType.PLAYER, player);
                        else
                            Sclat.sendMessage("§c§nこのサーバーは現在オフラインのため参加できません", MessageType.PLAYER, player);
                        Sclat.playGameSound(player, SoundType.ERROR);
                    }
                    return;
                }
            }
        }
        
        if(event.getView().getTitle().equals("武器選択")){
            if(name.equals("装備選択へ戻る") || name.equals("戻る") || name.equals("シューター") || name.equals("ローラー") || name.equals("チャージャー") || name.equals("ブラスター") || name.equals("バーストシューター") || name.equals("スロッシャー") || name.equals("シェルター") || name.equals("ブラシ") || name.equals("スピナー") || name.equals("マニューバー")){
                switch(name){
                    case"シューター":
                        OpenGUI.openWeaponSelect(player, "Weapon", "Shooter", false);
                        break;
                    case"ブラスター":
                        OpenGUI.openWeaponSelect(player, "Weapon", "Blaster", false);
                        break;
                    case"バーストシューター":
                        OpenGUI.openWeaponSelect(player, "Weapon", "Burst", false);
                        break;
                    case"ローラー":
                        OpenGUI.openWeaponSelect(player, "Weapon", "Roller", false);
                        break;
                    case"スロッシャー":
                        OpenGUI.openWeaponSelect(player, "Weapon", "Slosher", false);
                        break;
                    case"シェルター":
                        OpenGUI.openWeaponSelect(player, "Weapon", "Kasa", false);
                        break;
                    case"ブラシ":
                        OpenGUI.openWeaponSelect(player, "Weapon", "Hude", false);
                        break;
                    case"スピナー":
                        OpenGUI.openWeaponSelect(player, "Weapon", "Spinner", false);
                        break;
                    case"チャージャー":
                        OpenGUI.openWeaponSelect(player, "Weapon", "Charger", false);
                        break;
                    case"マニューバー":
                        OpenGUI.openWeaponSelect(player, "Weapon", "Maneu", false);
                        break;
                    case"戻る":
                        OpenGUI.openWeaponSelect(player, "Main", "null", false);
                        break;
                    case"装備選択へ戻る":
                        OpenGUI.equipmentGUI(player, false);
                        break;
                }
                return;
            }
            if(name.contains("§6レベル")){
                Sclat.sendMessage("§cレベルが足りないため、まだ選択できません", MessageType.PLAYER, player);
                Sclat.playGameSound(player, SoundType.ERROR);
                return;
            }
            //試しうちモード
            if(conf.getConfig().getString("WorkMode").equals("Trial")){
                
                player.getInventory().clear();
                DataMgr.getPlayerData(player).reset();
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
                        DataMgr.getPlayerData(p).setIsInMatch(true);
                        DataMgr.getPlayerData(p).setIsJoined(true);
                        DataMgr.getPlayerData(p).setMainItemGlow(false);
                        DataMgr.getPlayerData(p).setTick(10);
                        WeaponClass wc = DataMgr.getWeaponClass(name);
                        DataMgr.getPlayerData(p).setWeaponClass(wc);
                        if(DataMgr.getPlayerData(p).getWeaponClass().getSubWeaponName().equals("ビーコン"))
                            ArmorStandMgr.BeaconArmorStandSetup(p);
                        if(DataMgr.getPlayerData(p).getWeaponClass().getSubWeaponName().equals("スプリンクラー"))
                            ArmorStandMgr.SprinklerArmorStandSetup(p);
                        if(DataMgr.getPlayerData(p).getWeaponClass().getMainWeapon().getWeaponType().equals("Shooter")){
                            Shooter.ShooterRunnable(p);
                            if(DataMgr.getPlayerData(p).getWeaponClass().getMainWeapon().getIsManeuver()){
                                Shooter.ManeuverRunnable(p);
                                Shooter.ManeuverShootRunnable(p);
                            }
                        }
                        if(DataMgr.getPlayerData(p).getWeaponClass().getMainWeapon().getWeaponType().equals("Blaster")){
                            if(DataMgr.getPlayerData(p).getWeaponClass().getMainWeapon().getIsManeuver()){
                                Shooter.ManeuverRunnable(p);
                            }
                        }
                        if(DataMgr.getPlayerData(p).getWeaponClass().getMainWeapon().getWeaponType().equals("Charger"))
                            Charger.ChargerRunnable(p);
                        if(DataMgr.getPlayerData(p).getWeaponClass().getMainWeapon().getWeaponType().equals("Spinner"))
                            Spinner.SpinnerRunnable(p);
                        if(DataMgr.getPlayerData(p).getWeaponClass().getMainWeapon().getWeaponType().equals("Roller")){
                            if(DataMgr.getPlayerData(p).getWeaponClass().getMainWeapon().getIsHude()){
                                Brush.HoldRunnable(p);
                                Brush.RollPaintRunnable(p);
                            }else {
                                Roller.HoldRunnable(p);
                                Roller.RollPaintRunnable(p);
                            }
                        }

                        if(DataMgr.getPlayerData(p).getWeaponClass().getMainWeapon().getWeaponType().equals("Kasa")){
                            Kasa.KasaRunnable(p, false);
                        }

                        if(DataMgr.getPlayerData(p).getWeaponClass().getMainWeapon().getWeaponType().equals("Camping")){
                            Kasa.KasaRunnable(p, true);
                            DataMgr.getPlayerData(p).setMainItemGlow(true);
                            WeaponClassMgr.setWeaponClass(p);
                        }
                        WeaponClassMgr.setWeaponClass(p);
                        player.setExp(0.99F);
                        
                        //p.setScoreboard(DataMgr.getPlayerData(p).getMatch().getScoreboard());
                        //DataMgr.getPlayerData(p).getTeam().getTeam().addEntry(p.getName());
                        
                        SPWeaponMgr.SPWeaponRunnable(player);
                        SquidMgr.SquidShowRunnable(player);
                    }
                };
                delay.runTaskLater(Main.getPlugin(), 15);
            }else{
                DataMgr.getPlayerData(player).setWeaponClass(DataMgr.getWeaponClass(name));
            }
            Sclat.sendMessage("ブキ[" + ChatColor.GOLD + name + ChatColor.RESET + "]を選択しました", MessageType.PLAYER, player);
        }
        
        if(event.getView().getTitle().equals("Shop")){
            if(name.equals("装備選択へ戻る") || name.equals("戻る") || name.equals("シューター") || name.equals("ローラー") || name.equals("チャージャー") || name.equals("ブラスター") || name.equals("バーストシューター") || name.equals("スロッシャー") || name.equals("シェルター") || name.equals("ブラシ") || name.equals("スピナー") || name.equals("マニューバー")){
                switch(name){
                    case"シューター":
                        OpenGUI.openWeaponSelect(player, "Weapon", "Shooter", true);
                        break;
                    case"ブラスター":
                        OpenGUI.openWeaponSelect(player, "Weapon", "Blaster", true);
                        break;
                    case"バーストシューター":
                        OpenGUI.openWeaponSelect(player, "Weapon", "Burst", true);
                        break;
                    case"ローラー":
                        OpenGUI.openWeaponSelect(player, "Weapon", "Roller", true);
                        break;
                    case"スロッシャー":
                        OpenGUI.openWeaponSelect(player, "Weapon", "Slosher", true);
                        break;
                    case"シェルター":
                        OpenGUI.openWeaponSelect(player, "Weapon", "Kasa", true);
                        break;
                    case"ブラシ":
                        OpenGUI.openWeaponSelect(player, "Weapon", "Hude", true);
                        break;
                    case"スピナー":
                        OpenGUI.openWeaponSelect(player, "Weapon", "Spinner", true);
                        break;
                    case"チャージャー":
                        OpenGUI.openWeaponSelect(player, "Weapon", "Charger", true);
                        break;
                    case"マニューバー":
                        OpenGUI.openWeaponSelect(player, "Weapon", "Maneu", true);
                        break;
                    case"戻る":
                        OpenGUI.openWeaponSelect(player, "Main", "null", true);
                        break;
                    case"装備選択へ戻る":
                        OpenGUI.equipmentGUI(player, true);
                        break;
                }
                return;
            }
            if(name.contains("§6レベル")){
                Sclat.sendMessage("§cレベルが足りないため、まだ購入できません", MessageType.PLAYER, player);
                Sclat.playGameSound(player, SoundType.ERROR);
                return;
            }
            
            player.closeInventory();
            if(PlayerStatusMgr.getMoney(player) >= DataMgr.getWeaponClass(name).getMainWeapon().getMoney()){
                PlayerStatusMgr.addWeapon(player, name);
                PlayerStatusMgr.subMoney(player, DataMgr.getWeaponClass(name).getMainWeapon().getMoney());
                Sclat.sendMessage(ChatColor.GREEN + "購入に成功しました", MessageType.PLAYER, player);
                Sclat.playGameSound(player, SoundType.SUCCESS);
                PlayerStatusMgr.sendHologramUpdate(player);
            }else{
                Sclat.sendMessage(ChatColor.RED + "お金が足りません", MessageType.PLAYER, player);
                Sclat.playGameSound(player, SoundType.ERROR);
            }
        }
        
        if(event.getView().getTitle().equals("Chose Target")){
            if(name.equals("§r§6リスポーン地点へジャンプ")){
                Location loc = Main.lobby.clone();
                if(!conf.getConfig().getString("WorkMode").equals("Trial"))
                    loc = DataMgr.getPlayerData(player).getMatchLocation();
                SuperJumpMgr.SuperJumpCollTime(player, loc);
            }
            if(name.equals("§r§6ロビーへジャンプ")){
                String WorldName = conf.getConfig().getString("LobbyJump.WorldName");
                World w = Bukkit.getWorld(WorldName);
                int ix = conf.getConfig().getInt("LobbyJump.X");
                int iy = conf.getConfig().getInt("LobbyJump.Y");
                int iz = conf.getConfig().getInt("LobbyJump.Z");
                Location loc = new Location(w, ix + 0.5, iy, iz + 0.5);
                SuperJumpMgr.SuperJumpCollTime(player, loc);
            }
            for(Player p : Main.getPlugin(Main.class).getServer().getOnlinePlayers()){
                if (p.getName().equals(name)){
                    if(event.getCurrentItem().getType().equals(Material.PLAYER_HEAD)) {
                        if(p.getGameMode() == GameMode.SPECTATOR){
                            Sclat.sendMessage("§c今そのプレイヤーにはジャンプできない！", MessageType.PLAYER, player);
                            Sclat.playGameSound(player, SoundType.ERROR);
                            break;
                        }
                        SuperJumpMgr.SuperJumpCollTime(player, DataMgr.getPlayerData(p).getPlayerGroundLocation());
                    }
                    if(event.getCurrentItem().getType().equals(Material.IRON_TRAPDOOR))
                        SuperJumpMgr.SuperJumpCollTime(player, DataMgr.getBeaconFromplayer(p).getLocation());
                }
            }
        }
        
        
        if(event.getView().getTitle().equals("設定")){
            if(name.equals("戻る")){
                OpenGUI.openMenu(player);
                return;
            }
            
            switch (name){
                case "メインウエポンのインクエフェクト":
                    DataMgr.getPlayerData(player).getSettings().S_ShowEffect_MainWeaponInk();
                    break;
                case "チャージャーのレーザー":
                    DataMgr.getPlayerData(player).getSettings().S_ShowEffect_ChargerLine();
                    break;
                case "スペシャルウエポンのエフェクト":
                    DataMgr.getPlayerData(player).getSettings().S_ShowEffect_SPWeapon();
                    break;
                case "スペシャルウエポンの範囲エフェクト":
                    DataMgr.getPlayerData(player).getSettings().S_ShowEffect_SPWeaponRegion();
                    break;
                case "弾の表示":
                    DataMgr.getPlayerData(player).getSettings().S_ShowSnowBall();
                    break;
                case "BGM":
                    DataMgr.getPlayerData(player).getSettings().S_PlayBGM();
                    break;
                case "投擲武器の視認用エフェクト":
                    DataMgr.getPlayerData(player).getSettings().S_ShowEffect_Bomb();
                    break;
                case "爆発エフェクト":
                    DataMgr.getPlayerData(player).getSettings().S_ShowEffect_BombEx();
                    break;
                case "チャージキープ":
                    DataMgr.getPlayerData(player).getSettings().S_doChargeKeep();
                    break;
            }
            
            OpenGUI.openSettingsUI(player);
            
            player.playNote(player.getLocation(), Instrument.STICKS, Note.flat(1, Note.Tone.C));
            
            String B = DataMgr.getPlayerData(player).getSettings().PlayBGM() ? "1" : "0";
            String E_S = DataMgr.getPlayerData(player).getSettings().ShowEffect_MainWeaponInk() ? "1" : "0";
            String E_CL = DataMgr.getPlayerData(player).getSettings().ShowEffect_ChargerLine() ? "1" : "0";
            String E_CS = DataMgr.getPlayerData(player).getSettings().ShowEffect_SPWeapon() ? "1" : "0";
            String E_RR = DataMgr.getPlayerData(player).getSettings().ShowEffect_SPWeaponRegion() ? "1" : "0";
            String E_RS = DataMgr.getPlayerData(player).getSettings().ShowSnowBall() ? "1" : "0";
            //String E_BGM = DataMgr.getPlayerData(player).getSettings().PlayBGM() ? "1" : "0";
            String E_B = DataMgr.getPlayerData(player).getSettings().ShowEffect_Bomb() ? "1" : "0";
            String E_BEx = DataMgr.getPlayerData(player).getSettings().ShowEffect_BombEx() ? "1" : "0";
            String ck = DataMgr.getPlayerData(player).getSettings().doChargeKeep() ? "1" : "0";
            
            String s_data = B + E_S + E_CL + E_CS + E_RR + E_RS + E_B + E_BEx + ck;
            
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
        
        if(player.getInventory().getItemInMainHand() == null || player.getInventory().getItemInMainHand().getItemMeta() == null || player.getInventory().getItemInMainHand().getItemMeta().getDisplayName() == null)
            return;
        
        if(action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK) || action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK)){
            if(player.getInventory().getItemInMainHand().getType().equals(Material.CHEST))
                OpenGUI.openMenu(player);
            switch (player.getInventory().getItemInMainHand().getItemMeta().getDisplayName()){
                case "スーパージャンプ":
                    OpenGUI.SuperJumpGUI(player);
                    break;
                case "§c§n右クリックで退出":
                    BungeeCordMgr.PlayerSendServer(player, "sclat");
                    DataMgr.getPlayerData(player).setServerName("Sclat");
                    break;
                case "§a§n右クリックで参加":
                    MatchMgr.PlayerJoinMatch(player);
                    break;
            }
            if(player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("スーパージャンプ"))
                OpenGUI.SuperJumpGUI(player);
            
        }
    }
}
