package be4rjp.sclat.manager;

import be4rjp.sclat.*;
import be4rjp.sclat.GUI.OpenGUI;

import static be4rjp.sclat.Main.conf;

import be4rjp.sclat.data.*;
import be4rjp.sclat.lobby.LobbyScoreboardRunnable;
import be4rjp.sclat.packet.PacketHandler;
import be4rjp.sclat.server.EquipmentClient;
import be4rjp.sclat.server.EquipmentServerManager;
import be4rjp.sclat.server.StatusClient;
import be4rjp.sclat.tutorial.Tutorial;
import be4rjp.sclat.weapon.*;

import java.util.*;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;

import static org.bukkit.Bukkit.getServer;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;





/**
 *
 * @author Be4rJP
 */
public class GameMgr implements Listener{
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();
        player.getInventory().clear();
        player.getInventory().setHeldItemSlot(0);
    
        ((LivingEntity)player).setCollidable(false);
        //player.setDisplayName(player.getName());
        
        if(PlayerReturnManager.isReturned(player.getUniqueId().toString()))
            e.setJoinMessage(ChatColor.GOLD + player.getName() + " returned from a match.");
        
        player.setGameMode(GameMode.ADVENTURE);
        PlayerData data = new PlayerData(player);
        
        String uuid = player.getUniqueId().toString();
        PlayerSettings settings = new PlayerSettings(player);        
        data.setSettings(settings);
        data.setWeaponClass(DataMgr.getWeaponClass(conf.getConfig().getString("DefaultClass")));
        DataMgr.setPlayerData(player, data);
        
        //((LivingEntity)player).setCollidable(false);

        PlayerStatusMgr.setupPlayerStatus(player);
        
        conf.getUUIDCash().set(player.getUniqueId().toString(), player.getName());
        if(Main.type == ServerType.LOBBY) {
            RankingHolograms rankingHolograms = new RankingHolograms(player);
            DataMgr.setRankingHolograms(player, rankingHolograms);
            PlayerStatusMgr.HologramUpdateRunnable(player);
        }

        if(Main.type != ServerType.MATCH){
            data.setGearNumber(PlayerStatusMgr.getGear(player));
            data.setWeaponClass(DataMgr.getWeaponClass(PlayerStatusMgr.getEquiptClass(player)));
        }
        //処理の分散
        BukkitRunnable task = new BukkitRunnable(){
            int i = 0;
            @Override
            public void run(){
                switch(i){
                    case 0:{//----------------------------------------------------------------------------
                        if(!conf.getConfig().getString("WorkMode").equals("Trial") && Main.type != ServerType.MATCH)
                            PlayerStatusMgr.sendHologram(player);
                    }
                    case 1:{//----------------------------------------------------------------------------
                        SettingMgr.setSettings(settings, player);
                    }
                    case 2:{//----------------------------------------------------------------------------
                        BukkitRunnable head = new BukkitRunnable() {
                            @Override
                            public void run() {
                                ItemStack item = new ItemStack(Material.PLAYER_HEAD);
                                SkullMeta meta = (SkullMeta)item.getItemMeta();
                                meta.setOwningPlayer(player);
                                meta.setDisplayName(player.getName());
                                item.setItemMeta(meta);
                                data.setPlayerHead(CraftItemStack.asNMSCopy(item));
                            }
                        };
                        head.runTaskAsynchronously(Main.getPlugin());
                        if(Main.type == ServerType.MATCH){
                            if(Main.modList.contains(player.getName()))
                                Main.modList.remove(player.getName());
                            else
                                MatchMgr.PlayerJoinMatch(player);
                        }
                    }
                    case 3:{
                        cancel();
                    }
                }
                
                i++;
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 5);
        
        
        //PacketHandler
        PacketHandler packetHandler = new PacketHandler(player);
        ChannelPipeline pipeline = ((CraftPlayer)player).getHandle().playerConnection.networkManager.channel.pipeline();
        pipeline.addBefore("packet_handler", "SclatPacketInjector:" + player.getName(), packetHandler);
        
        
        //試し撃ちモード
        if(conf.getConfig().getString("WorkMode").equals("Trial")){
            Match match = DataMgr.getMatchFromId(MatchMgr.matchcount);
            data.setMatch(match);
            data.setTeam(match.getTeam0());
            player.teleport(Main.lobby);
            ItemStack join = new ItemStack(Material.CHEST);
            ItemMeta joinmeta = join.getItemMeta();
            joinmeta.setDisplayName(ChatColor.GOLD + "右クリックでメインメニューを開く");
            join.setItemMeta(joinmeta);
            player.getInventory().clear();
            SquidMgr.SquidRunnable(player);
            SquidMgr.SquidShowRunnable(player);
            player.setExp(0.99F);
            player.getInventory().setItem(7, join);
    
            if(Main.tutorial){
                Tutorial.setInkResetTimer(player);
                Tutorial.clearList.add(player);
            }
            
            BukkitRunnable delay = new BukkitRunnable(){
                Player p = player;
                @Override
                public void run(){
                    //WeaponClassMgr.setWeaponClass(p);
                    player.getInventory().clear();
                    ItemStack join = new ItemStack(Material.CHEST);
                    ItemMeta joinmeta = join.getItemMeta();
                    joinmeta.setDisplayName(ChatColor.GOLD + "右クリックでメインメニューを開く");
                    join.setItemMeta(joinmeta);
                    if(!Main.tutorial)
                        player.getInventory().setItem(7, join);
                    player.setExp(0F);
                    SPWeaponMgr.SPWeaponRunnable(player);
                    SPWeaponMgr.ArmorRunnable(p);
                    SquidMgr.SquidShowRunnable(player);
                    if(!Main.tutorial) {
                        EquipmentServerManager.doCommands();
                        OpenGUI.openWeaponSelect(p, "Main", "null", false);
                    }else{
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
                                WeaponClass wc = DataMgr.getWeaponClass(conf.getConfig().getString("DefaultClass"));
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
                                
                                SPWeaponMgr.SPWeaponRunnable(player);
                                SquidMgr.SquidShowRunnable(player);
                            }
                        };
                        delay.runTaskLater(Main.getPlugin(), 15);
                    }
                }
            };
            delay.runTaskLater(Main.getPlugin(), 15);
            
            BukkitRunnable armor = new BukkitRunnable(){
                @Override
                public void run(){
                    ArmorStandMgr.ArmorStandSetup(player);
                }
            };
            if(ArmorStandMgr.getIsSpawned()) return;
            armor.runTaskLater(Main.getPlugin(), 50);
            ArmorStandMgr.setIsSpawned(true);
            
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
            
            //Equipment
            player.getInventory().clear();
                

            for(ArmorStand as : DataMgr.getBeaconMap().values()){
                if(DataMgr.getBeaconFromplayer(player) == as)
                    as.remove();
            }
            for(ArmorStand as : DataMgr.getSprinklerMap().values()){
                if(DataMgr.getSprinklerFromplayer(player) == as)
                    as.remove();
            }
            
            return;
        }
        
        DataMgr.setUUIDData(player.getUniqueId().toString(), data);
        player.setWalkSpeed(0.2F);
        SquidMgr.SquidRunnable(player);

        player.getInventory().clear();
        if(Main.type != ServerType.LOBBY) {
            player.teleport(Main.lobby);
        }else{
            if(PlayerStatusMgr.getTutorialState(player.getUniqueId().toString()) == 1) {
                String WorldName = conf.getConfig().getString("Tutorial.WorldName");
                World w = Bukkit.getWorld(WorldName);
                int ix = conf.getConfig().getInt("Tutorial.X");
                int iy = conf.getConfig().getInt("Tutorial.Y");
                int iz = conf.getConfig().getInt("Tutorial.Z");
                int iyaw = conf.getConfig().getInt("Tutorial.Yaw");
                Location tutorial = new Location(w, ix + 0.5, iy, iz + 0.5);
                tutorial.setYaw(iyaw);
                player.teleport(tutorial);
            }else
                player.teleport(Main.lobby);
        }
        if(Main.type != ServerType.MATCH) {
            if(PlayerStatusMgr.getTutorialState(player.getUniqueId().toString()) == 2) {
                ItemStack join = new ItemStack(Material.CHEST);
                ItemMeta joinmeta = join.getItemMeta();
                joinmeta.setDisplayName(ChatColor.GOLD + "右クリックでメインメニューを開く");
                join.setItemMeta(joinmeta);
                player.getInventory().clear();
                player.getInventory().setItem(0, join);
            }
        }else{
            ItemStack b = new ItemStack(Material.BARRIER);
            ItemMeta bmeta = b.getItemMeta();
            bmeta.setDisplayName("§c§n右クリックで退出");
            b.setItemMeta(bmeta);
            player.getInventory().clear();
            player.getInventory().setItem(8, b);
    
            ItemStack join = new ItemStack(Material.LIME_STAINED_GLASS);
            ItemMeta joinmeta = join.getItemMeta();
            joinmeta.setDisplayName("§a§n右クリックで参加");
            join.setItemMeta(joinmeta);
            player.getInventory().setItem(0, join);
        }
        
        if(Main.type == ServerType.LOBBY){
            //Scoreboard
            LobbyScoreboardRunnable runnable = new LobbyScoreboardRunnable(player);
            runnable.runTaskTimerAsynchronously(Main.getPlugin(), 0, 10);
        }
        
        Match match = DataMgr.getMatchFromId(Integer.MAX_VALUE);
        data.setMatch(match);
        data.setTeam(match.getTeam0());
        
        if(!DataMgr.getPlayerIsQuitMap().containsKey(player.getUniqueId().toString())){
            DataMgr.setPlayerIsQuit(uuid, false);
        }
        
        if(!DataMgr.pul.contains(uuid))
            DataMgr.pul.add(uuid);
        
        if(Main.type == ServerType.LOBBY){
            if(PlayerStatusMgr.getTutorialState(player.getUniqueId().toString()) == 0){
                player.sendTitle("", "チュートリアルサーバーへ転送中...", 0, 20, 0);
                Sclat.sendMessage("§bチュートリアルサーバーへ転送中...", MessageType.PLAYER, player);
                BukkitRunnable run = new BukkitRunnable() {
                    @Override
                    public void run() {
                        List<String> list = Main.tutorialServers.getConfig().getStringList("server-list");
                        BungeeCordMgr.PlayerSendServer(player, list.get(new Random().nextInt(list.size())));
                        DataMgr.getPlayerData(player).setServerName(conf.getServers().getString("Tutorial.DisplayName"));
                    }
                };
                run.runTaskLater(Main.getPlugin(), 20);
            }
        }
        
        //player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
    }
    
    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event){
        Player player = event.getPlayer();
        if(DataMgr.getPlayerData(player).isInMatch())
            OpenGUI.SuperJumpGUI(player);
        event.setCancelled(true);
    }
    
    @EventHandler
    public void onDamageByFall(EntityDamageEvent event){
        if(event.getCause() == DamageCause.FALL || event.getCause() == DamageCause.SUFFOCATION)
            event.setCancelled(true);
        if (event.getEntity() instanceof Player){
            
            Player target = (Player)event.getEntity();
            if(event.getCause() == DamageCause.POISON){
                DataMgr.getPlayerData(target).setIsPoisonCoolTime(true);
                SquidMgr.PoisonCoolTime(target);
            }
            //AntiDamageTime
            BukkitRunnable task = new BukkitRunnable(){
                Player p = target;
                @Override
                public void run(){
                    target.setNoDamageTicks(0);
                }
            };
            task.runTaskLater(Main.getPlugin(), 1);

            /*
            Timer timer = new Timer(false);
            TimerTask t = new TimerTask(){
                Player p = target;
                @Override
                public void run(){
                    try{
                        target.setNoDamageTicks(0);
                        timer.cancel();
                    }catch(Exception e){
                        timer.cancel();
                    }
                }
            };
            timer.schedule(t, 25);*/
        }
    }
    
    @EventHandler
    public void onPlaceBlockByEntity(EntityChangeBlockEvent event){
        if (!(event.getEntity() instanceof Player)){
            event.setCancelled(true);
            if(event.getBlock().getType().toString().contains("CONCRETE"))
                event.getBlock().getState().update(false, false);
        }
    
    }
    
    //@EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        //event.setCancelled(true);
        /*
        if(!Main.LunaChat){
            Player player = event.getPlayer();
            if(DataMgr.getPlayerData(player).getIsJoined())
                event.setFormat("<" + DataMgr.getPlayerData(player).getTeam().getTeamColor().getColorCode() + player.getName() + "§r> " + event.getMessage());
            else
                event.setFormat("<" + player.getName() + "> " + event.getMessage());
        }
        */
    }
    
    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent event){
        event.setCancelled(true);
    }
    
    
    @EventHandler
    public void onBlockFall(BlockPhysicsEvent event){
        if(event.getChangedType().toString().contains("CONCRETE"))
            event.setCancelled(true);
    }
    
    @EventHandler
    public void onPickItem(EntityPickupItemEvent event){
        if (event.getEntity() instanceof Player){
            if(!((Player)event.getEntity()).getGameMode().equals(GameMode.CREATIVE))
                event.setCancelled(true);
        }
    }
    

    
    
    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event){
        event.setCancelled(true);
    }
    
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        event.setCancelled(true);
        Player player = (Player)event.getPlayer();
        PlayerData data = DataMgr.getPlayerData(player);
        if(data.isInMatch() && data.getSPGauge() == 100)
            SPWeaponMgr.UseSPWeapon(player, data.getWeaponClass().getSPWeaponName());
        
        //if(data.isInMatch())
            //WeaponClassMgr.setWeaponClass(player);
    }
    
    
    
    //sign
    @EventHandler
    public void onClickSign(PlayerInteractEvent e){
        Player player = (Player) e.getPlayer();
        Action action = e.getAction();
        if(e.getClickedBlock() != null){
            if(e.getClickedBlock().getType().toString().endsWith("SIGN")){
                Sign sign = (Sign) e.getClickedBlock().getState();
                
                if(Main.type == ServerType.LOBBY){
                    for (ServerStatus ss : ServerStatusManager.serverList){
                        if(ss.getSign().equals(e.getClickedBlock())){
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
                
                String line = sign.getLine(2);
                switch(line){
                    case "[ Join ]":
                        if(Main.type == ServerType.LOBBY)
                            ServerStatusManager.openServerList(player);
                        else
                            MatchMgr.PlayerJoinMatch(player);
                        break;
                    case "[ Equipment ]":
                        OpenGUI.equipmentGUI(player, false);
                        break;
                    case "[ Equip shop ]":
                        OpenGUI.equipmentGUI(player, true);
                        break;
                    case "[ OpenMenu ]":
                        OpenGUI.openMenu(player);
                        break;
                    case "Click to Download":
                        player.setResourcePack(conf.getConfig().getString("ResourcePackURL"));
                        break;
                    case "Click to Return":
                        BungeeCordMgr.PlayerSendServer(player, "lobby");
                        DataMgr.getPlayerData(player).setServerName("Lobby");
                        break;
                    case "[ Training Mode ]":
                        BungeeCordMgr.PlayerSendServer(player, "sclattest");
                        DataMgr.getPlayerData(player).setServerName("sclattest");
                        break;
                    case "[ Return to jg ]":
                        BungeeCordMgr.PlayerSendServer(player, "jg");
                        DataMgr.getPlayerData(player).setServerName("JG");
                        break;
                    case "Return to sclat":
                        BungeeCordMgr.PlayerSendServer(player, "sclat");
                        DataMgr.getPlayerData(player).setServerName("Sclat");
                        break;
                    case "[Charge special]":
                        if(DataMgr.getPlayerData(player).isInMatch() && !DataMgr.getPlayerData(player).getIsUsingSP())
                            DataMgr.getPlayerData(player).setSPGauge(100);
                        break;
                    case "[ Sclat ]":
                        BungeeCordMgr.PlayerSendServer(player, "sclat");
                        DataMgr.getPlayerData(player).setServerName("Sclat");
                        break;
                    case "[ Tutorial ]":
                        List<String> list = Main.tutorialServers.getConfig().getStringList("server-list");
                        BungeeCordMgr.PlayerSendServer(player, list.get(new Random().nextInt(list.size())));
                        DataMgr.getPlayerData(player).setServerName(conf.getServers().getString("Tutorial.DisplayName"));
                        break;
                    case "[ Instructions ]":
                        player.performCommand("torisetu");
                        break;
                    case "[ Shooter ]":
                        OpenGUI.openWeaponSelect(player, "Weapon", "Shooter", false);
                        break;
                    case "[ Roller ]":
                        OpenGUI.openWeaponSelect(player, "Weapon", "Roller", false);
                        break;
                    case "[ Charger ]":
                        OpenGUI.openWeaponSelect(player, "Weapon", "Charger", false);
                        break;
                    case "[ PatchNote ]":
                        TextComponent component = new TextComponent();
                        component.setText("[パッチノートを見るにはここをクリック]");
                        component.setColor(net.md_5.bungee.api.ChatColor.AQUA);
                        component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://be4rjp.github.io/Sclat-PatchNote/note/v102b/note.html"));
                        player.spigot().sendMessage(component);
                        break;
                }
            }
        }
    }
    
    @EventHandler
    public void onFrameBreak(HangingBreakByEntityEvent event) {
        if(!(event.getRemover() instanceof Player))
            return;
        Player player = (Player) event.getRemover();
        if(player.getGameMode().equals(GameMode.CREATIVE)) 
            return;
        if(event.getEntity() instanceof ItemFrame) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        Player player = (Player) event.getPlayer();
        PlayerData data = DataMgr.getPlayerData(player);
        
        //PacketHandler
        Channel channel = ((CraftPlayer)player).getHandle().playerConnection.networkManager.channel;
        channel.eventLoop().submit(() -> {
            channel.pipeline().remove(player.getName());
            return null;
        });
        
        
        
        if(Main.type == ServerType.MATCH) {
            if (DataMgr.joinedList.contains(player)) {
                DataMgr.setPlayerIsQuit(player.getUniqueId().toString(), true);
                if (data.getMatch().canJoin())
                    data.getMatch().subJoinedPlayerCount();
        
                Team team = data.getTeam();
                team.subtractRateTotal(PlayerStatusMgr.getRank(player));
        
                DataMgr.joinedList.remove(player);
            }
        }
        
        String server = DataMgr.getPlayerData(player).getServername();
        if(!server.equals("")) {
            event.setQuitMessage("§6" + player.getName() + " switched to " + server);
    
            if(Main.type == ServerType.LOBBY) {
                for (String serverName : conf.getServers().getConfigurationSection("Servers").getKeys(false)) {
                    String name = conf.getServers().getString("Servers." + serverName + ".Server");
                    String displayName = conf.getServers().getString("Servers." + serverName + ".DisplayName");
                    if (displayName.equals(server)) {
                        List<String> commands = new ArrayList<>();
                        commands.add("set weapon " + data.getWeaponClass().getClassName() + " " + player.getUniqueId().toString());
                        commands.add("set gear " + data.getGearNumber() + " " + player.getUniqueId().toString());
                        commands.add("set rank " + String.valueOf(PlayerStatusMgr.getRank(player)) + " " + player.getUniqueId().toString());
                        commands.add("setting " + conf.getPlayerSettings().getString("Settings." + player.getUniqueId().toString()) + " " + player.getUniqueId().toString());
                        commands.add("stop");
                        EquipmentClient sc = new EquipmentClient(conf.getConfig().getString("EquipShare." + name + ".Host"),
                                conf.getConfig().getInt("EquipShare." + name + ".Port"), commands);
                        sc.startClient();
                    }
                }
                if(server.equals("sclattest")){
                    List<String> commands = new ArrayList<>();
                    commands.add("set rank " + String.valueOf(PlayerStatusMgr.getRank(player)) + " " + player.getUniqueId().toString());
                    commands.add("set lv " + String.valueOf(PlayerStatusMgr.getLv(player)) + " " + player.getUniqueId().toString());
                    commands.add("setting " + conf.getPlayerSettings().getString("Settings." + player.getUniqueId().toString()) + " " + player.getUniqueId().toString());
                    commands.add("stop");
                    EquipmentClient sc = new EquipmentClient(conf.getConfig().getString("EquipShare.Trial.Host"),
                            conf.getConfig().getInt("EquipShare.Trial.Port"), commands);
                    sc.startClient();
                }
            }
        }
        
        if(data.getWeaponClass().getSubWeaponName().equals("ビーコン") && data.isInMatch()){
            DataMgr.getBeaconFromplayer(player).remove();
        }
        if(data.getWeaponClass().getSubWeaponName().equals("スプリンクラー") && data.isInMatch()){
            DataMgr.getSprinklerFromplayer(player).remove();
        }
        
        if(data.getWeaponClass() != null)
            PlayerStatusMgr.setEquiptClass(player, data.getWeaponClass().getClassName());
    }
}
