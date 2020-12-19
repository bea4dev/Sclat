package be4rjp.sclat;

import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import be4rjp.sclat.data.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import be4rjp.sclat.manager.BungeeCordMgr;
import be4rjp.sclat.manager.DeathMgr;
import be4rjp.sclat.manager.MatchMgr;
import be4rjp.sclat.server.StatusClient;
import net.minecraft.server.v1_13_R2.Chunk;
import net.minecraft.server.v1_13_R2.World;
import net.minecraft.server.v1_13_R2.WorldBorder;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.craftbukkit.v1_13_R2.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import net.minecraft.server.v1_13_R2.PacketPlayOutBlockChange;
import net.minecraft.server.v1_13_R2.PacketPlayOutMultiBlockChange;
import net.minecraft.server.v1_13_R2.PacketPlayOutMapChunk;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import static be4rjp.sclat.Main.conf;


/**
 *
 * @author Be4rJP
 * 
 * 全体的に使いそうなメソッドをここに置いておく
 * 
 */

public class Sclat {
    
    public static void setBlockByNMS(org.bukkit.block.Block b, org.bukkit.Material material, boolean applyPhysics) {
        Location loc = b.getLocation();
        Block block = ((CraftBlockData) Bukkit.createBlockData(material)).getState().getBlock();
        net.minecraft.server.v1_13_R2.World nmsWorld = ((CraftWorld) loc.getWorld()).getHandle();
        BlockPosition bp = new BlockPosition(loc.getX(), loc.getY(), loc.getZ());
        IBlockData ibd = block.getBlockData();
        nmsWorld.setTypeAndData(bp, ibd, applyPhysics ? 3 : 2);
    }
    
    public static void setBlockByNMSChunk(org.bukkit.block.Block b, org.bukkit.Material material, boolean applyPhysics) {
        Location loc = b.getLocation();
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        Block block = ((CraftBlockData) Bukkit.createBlockData(material)).getState().getBlock();
        net.minecraft.server.v1_13_R2.World nmsWorld = ((CraftWorld) loc.getWorld()).getHandle();
        net.minecraft.server.v1_13_R2.Chunk nmsChunk = nmsWorld.getChunkAt(x >> 4, z >> 4);
        BlockPosition bp = new BlockPosition(x, y, z);
        IBlockData ibd = block.getBlockData();
        nmsChunk.setType(bp, ibd, applyPhysics);
    }
    
    public static void sendBlockChangeForAllPlayer(org.bukkit.block.Block b, org.bukkit.Material material){
        Location loc = b.getLocation();
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        BlockPosition bp = new BlockPosition(x, y, z);
        net.minecraft.server.v1_13_R2.World nmsWorld = ((CraftWorld) loc.getWorld()).getHandle();
        net.minecraft.server.v1_13_R2.Chunk nmsChunk = nmsWorld.getChunkAt(x >> 4, z >> 4);
        Block block = ((CraftBlockData) Bukkit.createBlockData(material)).getState().getBlock();
        IBlockAccess iba = (IBlockAccess)nmsWorld;
        PacketPlayOutBlockChange packet = new PacketPlayOutBlockChange(iba, bp);
        for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
            if(target.getWorld() == b.getWorld()){
                ((CraftPlayer)target).getHandle().playerConnection.sendPacket(packet);
            }
        }
    }
    
    public static void sendWorldBorderWarningPacket(Player player){
        EntityPlayer nmsPlayer = ((CraftPlayer)player).getHandle();
        net.minecraft.server.v1_13_R2.WorldBorder wb = new WorldBorder();
        wb.world = nmsPlayer.getWorldServer();
        wb.setSize(1);
        wb.setCenter(player.getLocation().getX() + 10_000, player.getLocation().getZ() + 10_000);
        PacketPlayOutWorldBorder packet = new PacketPlayOutWorldBorder(wb, PacketPlayOutWorldBorder.EnumWorldBorderAction.INITIALIZE);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
    }
    
    public static void sendWorldBorderWarningClearPacket(Player player){
        EntityPlayer nmsPlayer = ((CraftPlayer)player).getHandle();
        net.minecraft.server.v1_13_R2.WorldBorder wb = new WorldBorder();
        wb.world = nmsPlayer.getWorldServer();
        wb.setSize(30_000_000);
        wb.setCenter(player.getLocation().getX(), player.getLocation().getZ());
        PacketPlayOutWorldBorder packet = new PacketPlayOutWorldBorder(wb, PacketPlayOutWorldBorder.EnumWorldBorderAction.INITIALIZE);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
    }
    
    /*
    public static void setBlockByNMS(org.bukkit.block.Block b, org.bukkit.Material material, boolean applyPhysics) {
        Location loc = b.getLocation();
        Block block = ((CraftBlockData) Bukkit.createBlockData(material)).getState().getBlock();
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        net.minecraft.server.v1_13_R2.World nmsWorld = ((CraftWorld) loc.getWorld()).getHandle();
        net.minecraft.server.v1_13_R2.Chunk nmsChunk = nmsWorld.getChunkAt(x >> 4, z >> 4);
        ChunkSection cs = nmsChunk.getSections()[y >> 4];
        IBlockData ibd = block.getBlockData();
        if (cs == nmsChunk.a()) {
            cs = new ChunkSection(y >> 4 << 4, false);
            nmsChunk.getSections()[y >> 4] = cs;
        }

        cs.getBlocks().setBlock(x & 15, y & 15, z & 15, ibd);
    }*/
    
    public static void restartServer(){
        List<String> commands = new ArrayList<>();
        commands.add("restart " + conf.getServers().getString("ServerName"));
        commands.add("stop");
        StatusClient sc = new StatusClient(conf.getConfig().getString("StatusShare.Host"),
                conf.getConfig().getInt("StatusShare.Port"), commands);
        sc.startClient();
        
        for(Player player : Main.getPlugin().getServer().getOnlinePlayers()){
            BungeeCordMgr.PlayerSendServer(player, "sclat");
            DataMgr.getPlayerData(player).setServerName("Sclat");
        }
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
            }
        };
        task.runTaskLater(Main.getPlugin(), 100);
    }
    
    public static void sendRestartedServerInfo(){
        List<String> commands = new ArrayList<>();
        commands.add("restarted " + conf.getServers().getString("ServerName"));
        commands.add("map " + conf.getServers().getString("ServerName") + " " + DataMgr.getMapRandom(MatchMgr.mapcount == 0 ? 0 : MatchMgr.mapcount - 1).getMapName());
        commands.add("stop");
        StatusClient sc = new StatusClient(conf.getConfig().getString("StatusShare.Host"),
                conf.getConfig().getInt("StatusShare.Port"), commands);
        sc.startClient();
    }
    
    public static void setPlayerPrefix(Player player, String prefix) {
        String name = prefix + player.getDisplayName();
        CraftPlayer cp = (CraftPlayer)player;
        EntityPlayer ep = cp.getHandle();
        String origName = ep.getName();
        for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
            if(player != target){
                CraftPlayer ct = (CraftPlayer)player;
                ct.getHandle().playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(ep));
            }
        }
        
    }
    
    
    public static void sendMessage(String message, MessageType type){
        String sclat = "[§bSclat§r] ";
        StringBuilder buff = new StringBuilder();
        buff.append(sclat);
        buff.append(message);
        switch(type){
            case ALL_PLAYER:{
                for (Player player : Main.getPlugin().getServer().getOnlinePlayers()){
                    player.sendMessage(buff.toString());
                }
                break;
            }
            case CONSOLE:{
                Main.getPlugin().getServer().getLogger().info(buff.toString());
                break;
            }
            case BROADCAST:{
                Main.getPlugin().getServer().broadcastMessage(buff.toString());
                break;
            }
        }
    }
    
    public static void sendMessage(String message, MessageType type, Team team){
        String sclat = "[§6Sclat§r] ";
        StringBuilder buff = new StringBuilder();
        buff.append(sclat);
        buff.append(message);
        if(type == MessageType.TEAM){
            for (Player player : Main.getPlugin().getServer().getOnlinePlayers()) {
                Team playerTeam = DataMgr.getPlayerData(player).getTeam();
                if(playerTeam == null) continue;
                if(team == null) continue;
                if(playerTeam != team) continue;
                player.sendMessage(buff.toString());
            }
        }
    }
    
    public static void sendMessage(String message, MessageType type, Player player){
        String sclat = "[§6Sclat§r] ";
        StringBuilder buff = new StringBuilder();
        buff.append(sclat);
        buff.append(message);
        if(type == MessageType.PLAYER)
            player.sendMessage(buff.toString());
    }
    
    public static void playGameSound(Player player, SoundType type){
        switch(type){
            case ERROR:
                player.playNote(player.getLocation(), Instrument.BASS_GUITAR, Note.flat(0, Note.Tone.G));
                player.playNote(player.getLocation(), Instrument.BASS_GUITAR, Note.flat(0, Note.Tone.G));
                break;
            case SUCCESS:
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1F, 2F);
                break;
            case CONGRATULATIONS:
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1F, 1F);
                break;
        }
    }
    
    public static void sendSclatLobby(Player player){
        BukkitRunnable send = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    BungeeCordMgr.PlayerSendServer(player, "sclat");
                    DataMgr.getPlayerData(player).setServerName("Sclat");
                }catch (Exception e){}
            }
        };
        send.runTaskLater(Main.getPlugin(), 20);
    }
    
    public static void createInkExplosionEffect(Location center, double radius, int accuracy, Player player){
        List<Location> s_locs = Sphere.getSphere(center, radius - 0.5, accuracy);
        org.bukkit.block.data.BlockData bd = DataMgr.getPlayerData(player).getTeam().getTeamColor().getWool().createBlockData();
        for (Player o_player : Main.getPlugin().getServer().getOnlinePlayers()) {
            if(DataMgr.getPlayerData(o_player).getSettings().ShowEffect_BombEx()){
                for(Location loc : s_locs){
                    if(o_player.getWorld() == loc.getWorld()){
                        if(o_player.getLocation().distance(loc) < conf.getConfig().getInt("ParticlesRenderDistance")){
                            o_player.spawnParticle(org.bukkit.Particle.BLOCK_DUST,
                                    loc, 0, loc.getX() - center.getX(), loc.getY() - center.getY(), loc.getZ() - center.getZ(), 1, bd);
                        }
                    }
                }
            }
        }
    }
    
    public static boolean giveDamage(Player player, Player target, double damage, String damageType){
        PlayerData targetData = DataMgr.getPlayerData(target);
        PlayerData playerData = DataMgr.getPlayerData(player);
        
        if(target.getHealth() + targetData.getArmor() > damage){
            if(targetData.getArmor() > damage){
                targetData.setArmor(targetData.getArmor() - damage);
            }else{
                target.damage(damage - targetData.getArmor());
                targetData.setArmor(0.0);
            }
        }else{
            target.setGameMode(GameMode.SPECTATOR);
            DeathMgr.PlayerDeathRunnable(target, player, damageType);
            targetData.setArmor(0.0);
            return true;
        }
        return false;
    }
    
    public static boolean isNumber(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
