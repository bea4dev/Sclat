package be4rjp.sclat.tutorial;

import be4rjp.sclat.Main;
import be4rjp.sclat.MessageType;
import be4rjp.sclat.Sclat;
import be4rjp.sclat.SoundType;
import be4rjp.sclat.data.*;
import be4rjp.sclat.manager.BungeeCordMgr;
import be4rjp.sclat.manager.MatchMgr;
import be4rjp.sclat.manager.PathMgr;
import be4rjp.sclat.manager.PlayerStatusMgr;
import be4rjp.sclat.server.StatusClient;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import javax.print.attribute.standard.ReferenceUriSchemesSupported;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static be4rjp.sclat.Main.conf;

public class Tutorial {
    
    public static BossBar bar;
    public static List<Player> clearList = new ArrayList<>();
    public static int clearPlayerCount = 0;
    
    public static void setupTutorial(Match match){
        final int time = Main.conf.getConfig().getInt("InkResetPeriod");
        bar = Main.getPlugin().getServer().createBossBar("§a§lインクリセットまで残り §c§l" + time + " §a§l秒", BarColor.WHITE, BarStyle.SOLID, BarFlag.CREATE_FOG);
        PathMgr.setupPath(match);
        inkResetRunnable(time, match);
    }
    
    public static void trainLightRunnable(){
        BukkitRunnable task = new BukkitRunnable() {
            int i = 0;
            @Override
            public void run() {
                if(clearPlayerCount >= 1)
                    trainLightRunRunnable();
                if(i % 20 == 0){
                    for(Entity as : Main.lobby.getWorld().getEntities()){
                        if(as instanceof ArmorStand) {
                            if (as.getCustomName() == null)
                                as.remove();
                            else if (as.getCustomName().equals(""))
                                as.remove();
                        }
                    }
                }
                i++;
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 60);
    }
    
    public static void trainLightRunRunnable(){
        BukkitRunnable task = new BukkitRunnable() {
            ArmorStand as;
            Location from;
            Location to;
            Vector vec;
            int i = 0;
            
            @Override
            public void run() {
                if(i == 0){
                    String WorldName = conf.getConfig().getString("Train.LFrom.WorldName");
                    World w = Bukkit.getWorld(WorldName);
                    int ix = conf.getConfig().getInt("Train.LFrom.X");
                    int iy = conf.getConfig().getInt("Train.LFrom.Y");
                    int iz = conf.getConfig().getInt("Train.LFrom.Z");
                    from = new Location(w, ix + 0.5, iy, iz + 0.5);
                    
                    String WorldName1 = conf.getConfig().getString("Train.LTo.WorldName");
                    World w1 = Bukkit.getWorld(WorldName);
                    int ix1 = conf.getConfig().getInt("Train.LTo.X");
                    int iy1 = conf.getConfig().getInt("Train.LTo.Y");
                    int iz1 = conf.getConfig().getInt("Train.LTo.Z");
                    to = new Location(w1, ix1 + 0.5, iy1, iz1 + 0.5);
                    
                    vec = new Vector(ix1 - ix, iy1 - iy, iz1 - iz).normalize();
                    
                    as = w.spawn(from, ArmorStand.class, armorStand -> {
                        armorStand.setVisible(false);
                        armorStand.setBasePlate(false);
                        armorStand.setHelmet(new ItemStack(Material.SEA_LANTERN));
                    });
                }
                
                as.setVelocity(vec);
                
                if(as.isDead() || as.isOnGround() || i == 100 || clearPlayerCount ==0) {
                    as.remove();
                    cancel();
                }
                
                if(as.getWorld() == to.getWorld()){
                    if(as.getLocation().distance(to) <= 3){
                        as.remove();
                        cancel();
                    }
                }
                i++;
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 1);
    
        BukkitRunnable task1 = new BukkitRunnable() {
            ArmorStand as;
            Location from;
            Location to;
            Vector vec;
            int i = 0;
        
            @Override
            public void run() {
                if(i == 0){
                    String WorldName = conf.getConfig().getString("Train.RFrom.WorldName");
                    World w = Bukkit.getWorld(WorldName);
                    int ix = conf.getConfig().getInt("Train.RFrom.X");
                    int iy = conf.getConfig().getInt("Train.RFrom.Y");
                    int iz = conf.getConfig().getInt("Train.RFrom.Z");
                    from = new Location(w, ix + 0.5, iy, iz + 0.5);
                
                    String WorldName1 = conf.getConfig().getString("Train.RTo.WorldName");
                    World w1 = Bukkit.getWorld(WorldName);
                    int ix1 = conf.getConfig().getInt("Train.RTo.X");
                    int iy1 = conf.getConfig().getInt("Train.RTo.Y");
                    int iz1 = conf.getConfig().getInt("Train.RTo.Z");
                    to = new Location(w1, ix1 + 0.5, iy1, iz1 + 0.5);
                
                    vec = new Vector(ix1 - ix, iy1 - iy, iz1 - iz).normalize();
                
                    as = w.spawn(from, ArmorStand.class, armorStand -> {
                        armorStand.setVisible(false);
                        armorStand.setBasePlate(false);
                        armorStand.setHelmet(new ItemStack(Material.SEA_LANTERN));
                    });
                }
            
                as.setVelocity(vec);
            
                if(as.isDead() || as.isOnGround() || i == 100 || clearPlayerCount ==0) {
                    as.remove();
                    cancel();
                }
            
                if(as.getWorld() == to.getWorld()){
                    if(as.getLocation().distance(to) <= 3){
                        as.remove();
                        cancel();
                    }
                }
                i++;
            }
        };
        task1.runTaskTimer(Main.getPlugin(), 0, 1);
    }
    
    public static void weaponRemoveRunnable(){
        String WorldName = conf.getConfig().getString("WeaponRemove.WorldName");
        World w = Bukkit.getWorld(WorldName);
        int ix = conf.getConfig().getInt("WeaponRemove.X");
        int iy = conf.getConfig().getInt("WeaponRemove.Y");
        int iz = conf.getConfig().getInt("WeaponRemove.Z");
        Location loc = new Location(w, ix + 0.5, iy, iz + 0.5);
        
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                for(Player player : Main.getPlugin().getServer().getOnlinePlayers()){
                    if(player.getWorld() != w) continue;
                    if(player.getLocation().distance(loc) < 8){
                        player.getInventory().clear();
                        DataMgr.getPlayerData(player).setIsInMatch(false);
                        DataMgr.getPlayerData(player).setIsJoined(false);
                    }
                }
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 5);
    }
    
    public static void clearRegionRunnable(){
        String WorldName = conf.getConfig().getString("TutorialClear.WorldName");
        World w = Bukkit.getWorld(WorldName);
        int ix = conf.getConfig().getInt("TutorialClear.X");
        int iy = conf.getConfig().getInt("TutorialClear.Y");
        int iz = conf.getConfig().getInt("TutorialClear.Z");
        Location loc = new Location(w, ix + 0.5, iy, iz + 0.5);
        
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                for(Player player : Main.getPlugin().getServer().getOnlinePlayers()){
                    if(player.getWorld() != w) continue;
                    if(player.getLocation().distance(loc) < 5){
                        if(Tutorial.clearList.contains(player))
                            Tutorial.clearList.remove(player);
                        String WorldName = conf.getConfig().getString("LobbyJump.WorldName");
                        World w = Bukkit.getWorld(WorldName);
                        int ix = conf.getConfig().getInt("LobbyJump.X");
                        int iy = conf.getConfig().getInt("LobbyJump.Y");
                        int iz = conf.getConfig().getInt("LobbyJump.Z");
                        Location loc = new Location(w, ix + 0.5, iy, iz + 0.5);
                        player.teleport(loc);
                    }
                }
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 10);
    }
    
    public static void lobbyRegionRunnable(){
        String WorldName = conf.getConfig().getString("LobbyJump.WorldName");
        World w = Bukkit.getWorld(WorldName);
        int ix = conf.getConfig().getInt("LobbyJump.X");
        int iy = conf.getConfig().getInt("LobbyJump.Y");
        int iz = conf.getConfig().getInt("LobbyJump.Z");
        Location loc = new Location(w, ix + 0.5, iy, iz + 0.5);
        
        BukkitRunnable task = new BukkitRunnable() {
            List<Player> list = new ArrayList<>();
            @Override
            public void run() {
                for(Player player : Main.getPlugin().getServer().getOnlinePlayers()){
                    if(player.getWorld() != w) continue;
                    if(player.getLocation().distance(loc) < 5 && !list.contains(player)){
                        list.add(player);
                        sendPlayerRunnable(player);
                    }
                }
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 10);
    }
    
    public static void lobbySetStatusRunnable(){
        String WorldName = conf.getConfig().getString("TutorialClear.WorldName");
        World w = Bukkit.getWorld(WorldName);
        int ix = conf.getConfig().getInt("TutorialClear.X");
        int iy = conf.getConfig().getInt("TutorialClear.Y");
        int iz = conf.getConfig().getInt("TutorialClear.Z");
        Location loc = new Location(w, ix + 0.5, iy, iz + 0.5);
        
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                for(Player player : Main.getPlugin().getServer().getOnlinePlayers()){
                    if(player.getWorld() != w) continue;
                    if(player.getLocation().distance(loc) < 10){
                        if(PlayerStatusMgr.getTutorialState(player.getUniqueId().toString()) == 1){
                            PlayerStatusMgr.setTutorialState(player.getUniqueId().toString(), 2);
    
                            ItemStack join = new ItemStack(Material.CHEST);
                            ItemMeta joinmeta = join.getItemMeta();
                            joinmeta.setDisplayName(ChatColor.GOLD + "右クリックでメインメニューを開く");
                            join.setItemMeta(joinmeta);
                            player.getInventory().clear();
                            player.getInventory().setItem(0, join);
                            
                            Sclat.sendMessage("§6Sclatへようこそ！", MessageType.PLAYER, player);
                            player.sendMessage("§aチェストをもって右クリックするとメインメニューを開くことができます。");
                            player.sendMessage("§a初期から使える武器がいくつかあります。");
                            player.sendMessage("§aメインメニューの装備変更から武器を選んで、試合に参加してみましょう！");
                            Sclat.sendMessage("§6初回ログインボーナスを受け取りました！ §bMoney +10000", MessageType.PLAYER, player);
                            Sclat.playGameSound(player, SoundType.CONGRATULATIONS);
                        }
                    }
                }
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 10);
    }
    
    public static void sendPlayerRunnable(Player player){
    
        clearPlayerCount++;
        
        List<String> commands = new ArrayList<>();
        commands.add("tutorial " + player.getUniqueId().toString());
        commands.add("stop");
        StatusClient sc = new StatusClient(conf.getConfig().getString("StatusShare.Host"),
                conf.getConfig().getInt("StatusShare.Port"), commands);
        sc.startClient();
        
        player.sendTitle("", "§7ロビーへ転送中...", 10, 40, 10);
        
        BukkitRunnable task = new BukkitRunnable() {
            int i = 0;
            @Override
            public void run() {
                if(!player.isOnline()) {
                    clearPlayerCount--;
                    cancel();
                }
                player.playSound(player.getLocation(), Sound.ENTITY_MINECART_INSIDE, 0.7F, 1F);
                if(i == 2){
                    BungeeCordMgr.PlayerSendServer(player, "sclat");
                    DataMgr.getPlayerData(player).setServerName("Sclat");
                }
                i++;
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 100);
    }
    
    
    public static void inkResetRunnable(int period, Match match){
        BukkitRunnable task = new BukkitRunnable() {
            int time = 0;
            @Override
            public void run() {
                bar.setTitle("§a§lインクリセットまで残り §c§l" + (period - time) + " §a§l秒");
                bar.setProgress(((double)(period - time))/((double)period));
                
                if(time == period) {
                    for(Path path : match.getMapData().getPathList()){
                        path.setTeam(null);
                    }
                    //ロールバック
                    match.getBlockUpdater().stop();
                    //------------------------------------------------------------
                    for(PaintData data : DataMgr.getBlockDataMap().values()){
                        data.getBlock().setType(data.getOriginalType());
                        if(data.getBlockData() != null)
                            data.getBlock().setBlockData(data.getBlockData());
                        data = null;
                    }
                    DataMgr.getBlockDataMap().clear();
                    DataMgr.getSpongeMap().clear();
                    //------------------------------------------------------------
                    for(Player player : Main.getPlugin().getServer().getOnlinePlayers())
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
                    
                    Sclat.sendMessage("§a§lインクがリセットされました！", MessageType.ALL_PLAYER);
                    for(Player op : Main.getPlugin().getServer().getOnlinePlayers())
                        Sclat.playGameSound(op, SoundType.SUCCESS);
                    time = 0;
                }
                time++;
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 20);
    }
    
    public static void setInkResetTimer(Player player){
        bar.addPlayer(player);
    }
}
