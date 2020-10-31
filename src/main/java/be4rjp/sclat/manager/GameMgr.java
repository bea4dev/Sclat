package be4rjp.sclat.manager;

import be4rjp.sclat.GUI.OpenGUI;
import be4rjp.sclat.Main;
import static be4rjp.sclat.Main.conf;

import be4rjp.sclat.ServerType;
import be4rjp.sclat.data.*;
import be4rjp.sclat.server.EquipmentClient;
import be4rjp.sclat.server.StatusClient;
import be4rjp.sclat.weapon.Blaster;
import be4rjp.sclat.weapon.Charger;
import be4rjp.sclat.weapon.Kasa;
import be4rjp.sclat.weapon.Roller;
import be4rjp.sclat.weapon.Shooter;
import be4rjp.sclat.weapon.Spinner;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.bukkit.Bukkit;
import static org.bukkit.Bukkit.getServer;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
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
        player.setDisplayName(player.getName());
        
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
                        if(!conf.getConfig().getString("WorkMode").equals("Trial"))
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
        
        
        //試し撃ちモード
        if(conf.getConfig().getString("WorkMode").equals("Trial")){
            Match match = DataMgr.getMatchFromId(MatchMgr.matchcount);
            data.setMatch(match);
            data.setTeam(match.getTeam0());
            //player.setScoreboard(match.getScoreboard());
            //match.getTeam0().getTeam().addEntry(player.getName());
            //match.getBlockUpdater().start();
            player.teleport(Main.lobby);
            //WeaponClassMgr.setWeaponClass(player);
            ItemStack join = new ItemStack(Material.CHEST);
            ItemMeta joinmeta = join.getItemMeta();
            joinmeta.setDisplayName(ChatColor.GOLD + "右クリックでメインメニューを開く");
            join.setItemMeta(joinmeta);
            player.getInventory().clear();
            SquidMgr.SquidRunnable(player);
            SquidMgr.SquidShowRunnable(player);
            player.setExp(0.99F);
            player.getInventory().setItem(7, join);
            
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
                    player.getInventory().setItem(7, join);
                    player.setExp(0F);
                    SPWeaponMgr.SPWeaponRunnable(player);
                    SPWeaponMgr.ArmorRunnable(p);
                    SquidMgr.SquidShowRunnable(player);
                    OpenGUI.openWeaponSelect(p, "Main", "null", false);
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
        player.teleport(Main.lobby);
        if(Main.type != ServerType.MATCH) {
            ItemStack join = new ItemStack(Material.CHEST);
            ItemMeta joinmeta = join.getItemMeta();
            joinmeta.setDisplayName(ChatColor.GOLD + "右クリックでメインメニューを開く");
            join.setItemMeta(joinmeta);
            player.getInventory().clear();
            player.getInventory().setItem(0, join);
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
        
        Match match = DataMgr.getMatchFromId(Integer.MAX_VALUE);
        data.setMatch(match);
        data.setTeam(match.getTeam0());
        
        if(!DataMgr.getPlayerIsQuitMap().containsKey(player.getUniqueId().toString())){
            DataMgr.setPlayerIsQuit(uuid, false);
        }
        
        if(!DataMgr.pul.contains(uuid))
            DataMgr.pul.add(uuid);
        
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
            timer.schedule(t, 25);
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
            if(e.getClickedBlock().getType() == Material.WALL_SIGN || e.getClickedBlock().getType() == Material.SIGN){
                Sign sign = (Sign) e.getClickedBlock().getState();
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
                    case "Return to lobby":
                        BungeeCordMgr.PlayerSendServer(player, "lobby");
                        DataMgr.getPlayerData(player).setServerName("Lobby");
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
                    case "[ Instructions ]":
                        player.performCommand("torisetu");
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
        if(DataMgr.joinedList.contains(player)){
            DataMgr.setPlayerIsQuit(player.getUniqueId().toString(), true);
            if(data.getMatch().canJoin())
                data.getMatch().subJoinedPlayerCount();
            
            Team team = data.getTeam();
            team.subtractRateTotal(PlayerStatusMgr.getRank(player));
            
            DataMgr.joinedList.remove(player);
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
