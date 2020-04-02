package be4rjp.sclat.manager;

import be4rjp.sclat.GUI.OpenGUI;
import be4rjp.sclat.Main;
import static be4rjp.sclat.Main.conf;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.Match;
import be4rjp.sclat.data.PaintData;
import be4rjp.sclat.data.PlayerData;
import be4rjp.sclat.data.PlayerSettings;
import be4rjp.sclat.weapon.Blaster;
import be4rjp.sclat.weapon.Roller;
import be4rjp.sclat.weapon.Shooter;
import java.util.ArrayList;
import java.util.List;
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
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;





/**
 *
 * @author Be4rJP
 */
public class GameMgr implements Listener{
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();
        
        for(Player p : Main.getPlugin(Main.class).getServer().getOnlinePlayers()){
            player.showPlayer(Main.getPlugin(), p);
        }
        
        for(Player p : Main.getPlugin(Main.class).getServer().getOnlinePlayers()){
            p.showPlayer(Main.getPlugin(), player);
        }
        
        player.setGameMode(GameMode.ADVENTURE);
        PlayerData data = new PlayerData(player);
        
        String uuid = player.getUniqueId().toString();
        PlayerSettings settings = new PlayerSettings(player);
        
        if(conf.getPlayerSettings().contains("Settings." + uuid)){
            if(conf.getPlayerSettings().getString("Settings." + uuid).substring(1,2).equals("0"))
                settings.S_ShowEffect_Shooter();             
            if(conf.getPlayerSettings().getString("Settings." + uuid).substring(2,3).equals("0"))
                settings.S_ShowEffect_ChargerLine();
            if(conf.getPlayerSettings().getString("Settings." + uuid).substring(3,4).equals("0"))
                settings.S_ShowEffect_ChargerShot();
            if(conf.getPlayerSettings().getString("Settings." + uuid).substring(4,5).equals("0"))
                settings.S_ShowEffect_RollerRoll();
            if(conf.getPlayerSettings().getString("Settings." + uuid).substring(5,6).equals("0"))
                settings.S_ShowEffect_RollerShot();
            if(conf.getPlayerSettings().getString("Settings." + uuid).substring(0,1).equals("0"))
                settings.S_PlayBGM();
            if(conf.getPlayerSettings().getString("Settings." + uuid).substring(6,7).equals("0"))
                settings.S_ShowEffect_Bomb();
            if(conf.getPlayerSettings().getString("Settings." + uuid).substring(7,8).equals("0"))
                settings.S_ShowEffect_BombEx();
        }else{
            conf.getPlayerSettings().set("Settings." + uuid, "1111111");
        }
            
        data.setSettings(settings);
        data.setWeaponClass(DataMgr.getWeaponClass("わかばシューター"));
        DataMgr.setPlayerData(player, data);
        
        //試し撃ちモード
        if(conf.getConfig().getString("WorkMode").equals("Trial")){
            data.setTick(10);
            data.setIsJoined(true);
            data.setIsInMatch(true);
            Match match = DataMgr.getMatchFromId(MatchMgr.matchcount);
            data.setMatch(match);
            data.setTeam(match.getTeam0());
            player.teleport(Main.lobby);
            WeaponClassMgr.setWeaponClass(player);
            ItemStack join = new ItemStack(Material.CHEST);
            ItemMeta joinmeta = join.getItemMeta();
            joinmeta.setDisplayName("メインメニュー");
            join.setItemMeta(joinmeta);
            player.getInventory().clear();
            Shooter.ShooterRunnable(player);
            SPWeaponMgr.SPWeaponRunnable(player);
            WeaponClassMgr.setWeaponClass(player);
            SquidMgr.SquidRunnable(player);
            player.setExp(0.99F);
            player.getInventory().setItem(7, join);
            
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
            
            return;
        }
        
        DataMgr.setUUIDData(player.getUniqueId().toString(), data);
        //MatchMgr.PlayerJoinMatch(player);
        player.setWalkSpeed(0.2F);
        SquidMgr.SquidRunnable(player);
        
        //Main.getPlugin().getLogger().info(Main.lobby.getWorld().getName());
        
        player.teleport(Main.lobby);
        ItemStack join = new ItemStack(Material.CHEST);
        ItemMeta joinmeta = join.getItemMeta();
        joinmeta.setDisplayName("メインメニュー");
        join.setItemMeta(joinmeta);
        player.getInventory().clear();
        player.getInventory().setItem(0, join);
        //Shooter.ShooterRunnable(player);
        
        //SquidMgr.SquidRunnable(player);
        
        Match match = DataMgr.getMatchFromId(Integer.MAX_VALUE);
        data.setMatch(match);
        data.setTeam(match.getTeam0());
        
        if(!DataMgr.getPlayerIsQuitMap().containsKey(player.getUniqueId().toString()))
            DataMgr.setPlayerIsQuit(player.getUniqueId().toString(), false);  
        
        
        
        //player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
    }
    
    @EventHandler
    public void onDamageByFall(EntityDamageEvent event){
        if (event.getEntity() instanceof Player){
            if(event.getCause() == DamageCause.FALL)
                event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlaceBlockByEntity(EntityChangeBlockEvent event){
        if (!(event.getEntity() instanceof Player)){
            event.setCancelled(true);
        }
    
    }
    
    @EventHandler
    public void onPickItem(EntityPickupItemEvent event){
        if (event.getEntity() instanceof Player){
            if(!((Player)event.getEntity()).getGameMode().equals(GameMode.CREATIVE))
                event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onbWeatherChange(WeatherChangeEvent event){
        event.setCancelled(true);
    }
    
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        event.setCancelled(true);
        Player player = (Player)event.getPlayer();
        PlayerData data = DataMgr.getPlayerData(player);
        if(data.isInMatch() && data.getSPGauge() == 100)
            SPWeaponMgr.UseSPWeapon(player, data.getWeaponClass().getSPWeaponName());
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
                        MatchMgr.PlayerJoinMatch(player);
                        break;
                    case "[ Weapon Select ]":
                        OpenGUI.openWeaponSelect(player);
                        break;
                    case "[ OpenMenu ]":
                        OpenGUI.openMenu(player);
                        break;
                    case "Click to Download":
                        player.setResourcePack(conf.getConfig().getString("ResourcePackURL"));
                        break;
                    case "Click to Return":
                        BungeeCordMgr.PlayerSendServer(player, "lobby");
                        break;
                    case "[ Training Mode ]":
                        BungeeCordMgr.PlayerSendServer(player, "trial");
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
        if(data.getIsJoined()){
            DataMgr.setPlayerIsQuit(player.getUniqueId().toString(), true);
        }
    }
}
