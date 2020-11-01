package be4rjp.sclat.tutorial;

import be4rjp.sclat.Main;
import be4rjp.sclat.MessageType;
import be4rjp.sclat.Sclat;
import be4rjp.sclat.SoundType;
import be4rjp.sclat.data.*;
import be4rjp.sclat.manager.BungeeCordMgr;
import be4rjp.sclat.manager.MatchMgr;
import be4rjp.sclat.manager.PathMgr;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static be4rjp.sclat.Main.conf;

public class Tutorial {
    
    public static BossBar bar;
    public static List<Player> clearList = new ArrayList<>();
    
    public static void setupTutorial(Match match){
        final int time = Main.conf.getConfig().getInt("InkResetPeriod");
        bar = Main.getPlugin().getServer().createBossBar("§a§lインクリセットまで残り §c§l" + time + " §a§l秒", BarColor.WHITE, BarStyle.SOLID, BarFlag.CREATE_FOG);
        PathMgr.setupPath(match);
        inkResetRunnable(time, match);
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
            @Override
            public void run() {
                for(Player player : Main.getPlugin().getServer().getOnlinePlayers()){
                    if(player.getWorld() != w) continue;
                    if(player.getLocation().distance(loc) < 15){
                        BungeeCordMgr.PlayerSendServer(player, "sclat");
                        DataMgr.getPlayerData(player).setServerName("Sclat");
                    }
                }
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 10);
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
