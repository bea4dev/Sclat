
package be4rjp.sclat.manager;

import be4rjp.sclat.Animation;
import be4rjp.sclat.GUI.OpenGUI;
import be4rjp.sclat.Main;
import static be4rjp.sclat.Main.conf;
import be4rjp.sclat.Sclat;
import be4rjp.sclat.data.BlockUpdater;
import be4rjp.sclat.data.Color;
import org.bukkit.entity.Player;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.MapData;
import be4rjp.sclat.data.Match;
import be4rjp.sclat.data.NoteBlockSong;
import be4rjp.sclat.data.PaintData;
import be4rjp.sclat.data.Path;
import be4rjp.sclat.data.PlayerData;

import be4rjp.sclat.data.WeaponClass;
import be4rjp.sclat.weapon.Charger;
import be4rjp.sclat.weapon.Roller;
import be4rjp.sclat.weapon.Shooter;
import be4rjp.sclat.weapon.spweapon.SuperArmor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import static org.bukkit.Bukkit.getServer;
import org.bukkit.Material;
import be4rjp.sclat.data.Team;
import be4rjp.sclat.raytrace.RayTrace;
import be4rjp.sclat.weapon.Spinner;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.util.Vector;

/**
 *
 * @author Be4rJP
 */
public class MatchMgr {
    
    public static int matchcount = 0;
    public static int mapcount = 0;
    public static byte volume = 22;
    
    public static boolean canRollback = true;
    
    
    
    public static void PlayerJoinMatch(Player player){
        PlayerData data = DataMgr.getPlayerData(player);
        
        if(DataMgr.getPlayerIsQuit(player.getUniqueId().toString())){
            player.sendMessage("§c§n途中で退出した場合再参加はできません");
            return;
        }
        
        if(!data.getIsJoined()){
            
        Match match = DataMgr.getMatchFromId(matchcount);
        if(match.canJoin()){
            match.addPlayerCount();
            int playercount = match.getPlayerCount();
        if(playercount <= conf.getConfig().getInt("MaxPlayerCount")){
            Main.getPlugin().getServer().broadcastMessage("§b§n" + player.getDisplayName() + " joined the match");

            if(playercount == 1)
                match.setLeaderPlayer(player);
        
            data.setPlayerNumber(playercount);
            
            player.teleport(match.getMapData().getTaikibayso());
            OpenGUI.MatchTohyoGUI(player);
            
            if(playercount%2==0){
                data.setTeam(match.getTeam1());
                //data.setMatchLocation(DataMgr.getTeamLoc(match.getMapData()).getTeam1Loc(matchcount/2));
            }else{
                data.setTeam(match.getTeam0());
                //data.setMatchLocation(DataMgr.getTeamLoc(match.getMapData()).getTeam0Loc((matchcount+1)/2));
            }
            
            data.setMatch(match);
            data.setIsJoined(true);
            if(playercount == conf.getConfig().getInt("StartPlayerCount")){
                BukkitRunnable task = new BukkitRunnable(){
                    int s = 0;
                    Player p = player;
                    @Override
                    public void run(){
                        if(s == 0)
                            Main.getPlugin().getServer().broadcastMessage("§a試合開始まで後20秒");
                        if(s == 10)
                            Main.getPlugin().getServer().broadcastMessage("§a試合開始まで後10秒");
                        if(s == 15)
                            Main.getPlugin().getServer().broadcastMessage("§a試合開始まで後5秒");
                        if(s == 16)
                            Main.getPlugin().getServer().broadcastMessage("§a試合開始まで後4秒");
                        if(s == 17)
                            Main.getPlugin().getServer().broadcastMessage("§a試合開始まで後3秒");
                        if(s == 18)
                            Main.getPlugin().getServer().broadcastMessage("§a試合開始まで後2秒");
                        if(s == 19)
                            Main.getPlugin().getServer().broadcastMessage("§a試合開始まで後1秒");
                        if(s == 20){
                            match.setCanJoin(false);
                            if(match.getNawabari_T_Count() >= match.getTDM_T_Count()){
                                conf.getConfig().set("WorkMode", "Nomal");
                            }else{
                                conf.getConfig().set("WorkMode", "TDM");
                            }
                            StartMatch(match);
                            for(Entity entity : p.getWorld().getEntities()){
                                if(!(entity instanceof Player)){
                                    entity.remove();
                                }
                            }
                            cancel();
                        }
                        s++;
                    }
                };
                task.runTaskTimer(Main.getPlugin(), 0, 20);
                
            }
        }else{
            player.sendMessage("§c§n上限人数を超えているため参加できません");
        }
        }else{
            player.sendMessage("§c§nこのマッチには既に開始しているため参加できません");
            return;
        }
        }else{
            player.sendMessage("§c§n既にチームに参加しています");
            return;
        }
        
    }
    
    public static synchronized void MatchSetup(){
        final int id = matchcount;
        Match match = new Match(id);
        Team team0 = new Team(id * 2);
        Team team1 = new Team(id * 2 + 1);
        DataMgr.setTeam(id * 2, team0);
        DataMgr.setTeam(id * 2 + 1, team1);
        
        BlockUpdater bur = new BlockUpdater();
        bur.start();
        match.setBlockUpdater(bur);
        
        DataMgr.ColorShuffle();
        Color color0 = DataMgr.getColorRandom(0);
        Color color1 = DataMgr.getColorRandom(1);
        team0.setTeamColor(color0);
        team1.setTeamColor(color1);
        
        match.setTeam0(team0);
        match.setTeam1(team1);
        
        MapData map = DataMgr.getMapRandom(mapcount);
        match.setMapData(map);
        
        
        mapcount++;
        
        if(mapcount == MapDataMgr.allmapcount){
            mapcount = 0;
            //DataMgr.MapDataShuffle();
        }
        
        DataMgr.setMatch(id, match);
        
        //lobby待機者用
        final int id2 = Integer.MAX_VALUE;
        Match lobby_m = new Match(id2);
        Team lobby_t0 = new Team(id2);
        Team lobby_t1 = new Team(id2 - 1);
        DataMgr.setTeam(id2, lobby_t0);
        DataMgr.setTeam(id2 - 1, lobby_t1);
        
        DataMgr.ColorShuffle();
        Color lc0 = DataMgr.getColorRandom(0);
        Color lc1 = DataMgr.getColorRandom(1);
        team0.setTeamColor(lc0);
        team1.setTeamColor(lc1);
        
        lobby_m.setTeam0(lobby_t0);
        lobby_m.setTeam1(lobby_t1);
        
        MapData map1 = DataMgr.getMapRandom(0);
        lobby_m.setMapData(map);
        
        
        DataMgr.setMatch(id2, lobby_m);
        
        //TeamLoc teamloc = new TeamLoc(map);
        //teamloc.SetupTeam0Loc();
        //teamloc.SetupTeam1Loc();
        //DataMgr.setTeamLoc(map, teamloc);
    }
    
    public static void RollBack(){
        if(!canRollback) return;
        for(PaintData data : DataMgr.getBlockDataMap().values()){
            Sclat.setBlockByNMS(data.getBlock(), data.getOriginalType(), false);
            //data.getBlock().setType(data.getOriginalType());
            data = null;
        }
        DataMgr.getBlockDataMap().clear();
        DataMgr.getSpongeMap().clear();
        canRollback = false;
        BukkitRunnable task = new BukkitRunnable(){
            @Override
            public void run(){
                canRollback = true;
            }
        };
        task.runTaskLater(Main.getPlugin(), 3600);
    }
    
    public static void StartCount(Player player){
        BukkitRunnable task = new BukkitRunnable(){
            Player p = player;
            int i = 0;
            @Override
            public void run(){
                if(i == 10)
                    p.sendTitle("R§7EADY?", "", 0, 56, 0);
                if(i == 12)
                    p.sendTitle("RE§7ADY?", "", 0, 46, 0);
                if(i == 14)
                    p.sendTitle("REA§7DY?", "", 0, 36, 0);
                if(i == 16)
                    p.sendTitle("READ§7Y?", "", 0, 26, 0);
                if(i == 18)
                    p.sendTitle("READY§7?", "", 0, 16, 0);
                if(i == 20)
                    p.sendTitle("READY?", "", 0, 6, 2);
                if(i == 47)
                    p.sendTitle(DataMgr.getPlayerData(p).getTeam().getTeamColor().getColorCode() + "GO!", "", 2, 6, 2);
                i++;
            }
        };
        task.runTaskTimer(Main.getPlugin(), 230, 1);
    }
    
    public static void MatchRunnable(Player player, Match match){
        BukkitRunnable task;
        task = new BukkitRunnable(){
            int s = 0;
            Player p = player;
            World w = Main.getPlugin().getServer().getWorld(match.getMapData().getWorldName());
            Location intromove;
            //EntitySquid squid;

            LivingEntity squid;
            //LivingEntity npcle;


            @Override
            public void run(){

                if(s == 0){
                    if(DataMgr.getPlayerData(p).getPlayerNumber() == 1)
                        PaintMgr.PaintGlass(match);
                    
                    if(DataMgr.getPlayerData(p).getTeam() == match.getTeam0()){
                        Location l = DataMgr.getPlayerData(p).getMatch().getMapData().getTeam0Loc();
                        int i = (DataMgr.getPlayerData(p).getPlayerNumber()+1)/2;
                        Location sl = null;
                        if(i == 1)
                            sl = new Location(l.getWorld(), l.getBlockX() + 1.5D, l.getBlockY(), l.getBlockZ() + 1.5D);
                        else if(i == 2)
                            sl = new Location(l.getWorld(), l.getBlockX() - 0.5D, l.getBlockY(), l.getBlockZ() + 1.5D);
                        else if(i == 3)
                            sl = new Location(l.getWorld(), l.getBlockX() + 1.5D, l.getBlockY(), l.getBlockZ() - 0.5D);
                        else if(i == 4)
                            sl = new Location(l.getWorld(), l.getBlockX() - 0.5D, l.getBlockY(), l.getBlockZ() - 0.5D);
                        else
                            sl = new Location(l.getWorld(), l.getBlockX() + 0.5D, l.getBlockY(), l.getBlockZ() + 0.5D);
                        sl.setYaw(l.getYaw());
                        DataMgr.getPlayerData(p).setMatchLocation(sl);
                    }
                    if(DataMgr.getPlayerData(p).getTeam() == match.getTeam1()){
                        Location l = DataMgr.getPlayerData(p).getMatch().getMapData().getTeam1Loc();
                        int i = DataMgr.getPlayerData(p).getPlayerNumber()/2;
                        Location sl = null;
                        if(i == 1)
                            sl = new Location(l.getWorld(), l.getBlockX() + 1.5D, l.getBlockY(), l.getBlockZ() + 1.5D);
                        else if(i == 2)
                            sl = new Location(l.getWorld(), l.getBlockX() - 0.5D, l.getBlockY(), l.getBlockZ() + 1.5D);
                        else if(i == 3)
                            sl = new Location(l.getWorld(), l.getBlockX() + 1.5D, l.getBlockY(), l.getBlockZ() - 0.5D);
                        else if(i == 4)
                            sl = new Location(l.getWorld(), l.getBlockX() - 0.5D, l.getBlockY(), l.getBlockZ() - 0.5D);
                        else
                            sl = new Location(l.getWorld(), l.getBlockX() + 0.5D, l.getBlockY(), l.getBlockZ() + 0.5D);
                        sl.setYaw(l.getYaw());
                        DataMgr.getPlayerData(p).setMatchLocation(sl);
                    }

                    if(DataMgr.getPlayerData(p).getPlayerNumber() <= 8){
                        Entity e = DataMgr.getPlayerData(p).getMatchLocation().getWorld().spawnEntity(DataMgr.getPlayerData(p).getMatchLocation(), EntityType.SQUID);
                        squid = (LivingEntity)e;
                        squid.setAI(false);
                        squid.setSwimming(true);
                        squid.setCustomName(p.getDisplayName());
                        squid.setCustomNameVisible(true);
                    }

                    p.setGameMode(GameMode.SPECTATOR);
                    p.getInventory().clear();
                    Location introl = match.getMapData().getIntro();
                    p.teleport(introl);
                    Location location = DataMgr.getPlayerData(p).getMatchLocation();

                    if(conf.getConfig().getString("WorkMode").equals("TDM"))
                        p.sendTitle("§l" + match.getMapData().getMapName(), "§7チームデスマッチ", 10, 70, 20);
                    else
                        p.sendTitle("§l" + match.getMapData().getMapName(), "§7ナワバリバトル", 10, 70, 20);

                    StartCount(p);


                    ScoreboardManager manager = Bukkit.getScoreboardManager();
                    Scoreboard scoreboard = manager.getNewScoreboard();

                    Objective objective = scoreboard.registerNewObjective("Title", "dummy");
                    objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                    objective.setDisplayName("MapName:  " + ChatColor.GOLD + DataMgr.getPlayerData(p).getMatch().getMapData().getMapName());

                    Score score = objective.getScore(ChatColor.YELLOW + "TimeLeft:    " + ChatColor.GREEN + "3:00"); 
                    Score s2 = objective.getScore("");
                    if(conf.getConfig().getString("WorkMode").equals("TDM"))
                        s2 = objective.getScore(ChatColor.YELLOW + "GameMode:  §7チームデスマッチ"); 
                    else
                        s2 = objective.getScore(ChatColor.YELLOW + "GameMode:  §7ナワバリバトル"); 

                    s2.setScore(2);
                    score.setScore(1);

                    p.setScoreboard(scoreboard);

                    for(Player player : Main.getPlugin(Main.class).getServer().getOnlinePlayers()){
                        p.hidePlayer(Main.getPlugin(), player);
                    }

                }
                if(s >= 1 && s <= 100){
                    if(s == 1)
                        intromove = match.getMapData().getIntro().clone();
                    MapData map = DataMgr.getPlayerData(p).getMatch().getMapData();
                    intromove.add(map.getIntroMoveX(), map.getIntroMoveY(), map.getIntroMoveZ());
                    p.teleport(intromove);
                }
                
                
                
                if(s >= 100 && s <= 160){
                    Location introl = match.getMapData().getTeam0Intro().clone().add(0.5, 0, 0.5);
                    p.teleport(introl);
                    if(DataMgr.getPlayerData(p).getTeam() == match.getTeam0()){
                        if(s >= 101 && s <= 120){
                            org.bukkit.block.data.BlockData bd = DataMgr.getPlayerData(p).getTeam().getTeamColor().getWool().createBlockData();
                            introl.getWorld().spawnParticle(org.bukkit.Particle.BLOCK_DUST, DataMgr.getPlayerData(p).getMatchLocation(), 10, 0.3, 0.4, 0.3, 1, bd);

                        }
                        if(s == 120){
                            if(DataMgr.getPlayerData(p).getPlayerNumber() <= 8)
                                squid.remove();
                        }
                        if(s == 100){
                            if(DataMgr.getPlayerData(p).getPlayerNumber() <= 8){
                            introl.getWorld().playSound(DataMgr.getPlayerData(p).getMatchLocation(), Sound.ENTITY_PLAYER_SWIM, 1, 1);
                            NPCMgr.createNPC(p, p.getDisplayName(), DataMgr.getPlayerData(p).getMatchLocation());
                            }
                        }
                    }
                }
                if(s >= 160 && s <= 220){
                    Location introl = match.getMapData().getTeam1Intro().clone().add(0.5, 0, 0.5);
                    p.teleport(introl);
                    if(DataMgr.getPlayerData(p).getTeam() == match.getTeam1()){
                        if(s >= 161 && s <= 180){
                            org.bukkit.block.data.BlockData bd = DataMgr.getPlayerData(p).getTeam().getTeamColor().getWool().createBlockData();
                            introl.getWorld().spawnParticle(org.bukkit.Particle.BLOCK_DUST, DataMgr.getPlayerData(p).getMatchLocation(), 10, 0.3, 0.4, 0.3, 1, bd);
                        }
                        if(s == 180){
                            if(DataMgr.getPlayerData(p).getPlayerNumber() <= 8)
                                squid.remove();
                        }
                        if(s == 160){
                            if(DataMgr.getPlayerData(p).getPlayerNumber() <= 8){
                            introl.getWorld().playSound(DataMgr.getPlayerData(p).getMatchLocation(), Sound.ENTITY_PLAYER_SWIM, 1, 1);
                            NPCMgr.createNPC(p, p.getDisplayName(), DataMgr.getPlayerData(p).getMatchLocation());
                            }
                        }
                    }
                }

                if(s == 221){
                    for(Player player : Main.getPlugin(Main.class).getServer().getOnlinePlayers()){
                        p.showPlayer(Main.getPlugin(), player);
                    }
                }


                if(s >= 221 && s <= 280){
                    p.getInventory().setItem(0, new ItemStack(org.bukkit.Material.AIR));
                    p.setGameMode(GameMode.ADVENTURE);
                    p.setExp(0.99F);
                    Location introl = DataMgr.getPlayerData(p).getMatchLocation();
                    p.teleport(introl);
                }
                

                if(s == 281){
                    //playerclass
                    if(DataMgr.getPlayerData(p).getWeaponClass().getSubWeaponName().equals("ビーコン"))
                        ArmorStandMgr.BeaconArmorStandSetup(p);
                    if(DataMgr.getPlayerData(p).getWeaponClass().getSubWeaponName().equals("スプリンクラー"))
                        ArmorStandMgr.SprinklerArmorStandSetup(p);
                    WeaponClassMgr.setWeaponClass(p);



                    if(DataMgr.getPlayerData(p).getWeaponClass().getMainWeapon().getWeaponType().equals("Shooter"))
                        Shooter.ShooterRunnable(p);
                    if(DataMgr.getPlayerData(p).getWeaponClass().getMainWeapon().getWeaponType().equals("Charger"))
                        Charger.ChargerRunnable(p);
                    if(DataMgr.getPlayerData(p).getWeaponClass().getMainWeapon().getWeaponType().equals("Spinner"))
                        Spinner.SpinnerRunnable(p);
                    if(DataMgr.getPlayerData(p).getWeaponClass().getMainWeapon().getWeaponType().equals("Roller")){
                        Roller.HoldRunnable(p);
                        Roller.RollPaintRunnable(p);
                    }

                    p.getEquipment().setHelmet(DataMgr.getPlayerData(p).getTeam().getTeamColor().getBougu());

                    SuperArmor.setArmor(p, 20, 100, false);
                    SPWeaponMgr.SPWeaponRunnable(p);

                    DataMgr.getPlayerData(p).setTick(10);
                        //Shooter.ShooterRunnable(p);

                    //SquidMgr.SquidRunnable(p);
                    DataMgr.getPlayerData(p).setIsInMatch(true);
                    p.setExp(0.99F);
                    if(DataMgr.getPlayerData(p).getPlayerNumber() == 1)
                        InMatchCounter(p);
                    p.playSound(p.getLocation(), Sound.ENTITY_ZOMBIE_INFECT, 10.0F, 2.0F);
                    
                    

                    //p.setPlayerListName(DataMgr.getPlayerData(p).getTeam().getTeamColor().getColorCode() + p.getDisplayName());
                    
                    if(DataMgr.getPlayerData(p).getPlayerNumber() == 1){
                        NoteBlockSong nbs = NoteBlockAPIMgr.getRandomNomalSong();
                        Song song = nbs.getSong();
                        RadioSongPlayer radio = new RadioSongPlayer(song);
                        radio.setVolume(volume);
                        for(Player oplayer : Main.getPlugin(Main.class).getServer().getOnlinePlayers()){
                            if(DataMgr.getPlayerData(oplayer).getSettings().PlayBGM() && DataMgr.getPlayerData(oplayer).getIsJoined()){
                                radio.addPlayer(oplayer);
                                oplayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§7Now playing : §6" + nbs.getSongName() + ""));
                            }
                        }
                        radio.setPlaying(true);
                        StopMusic(radio, 2400);
                    }
                    
                    
                    if(DataMgr.getPlayerData(p).getPlayerNumber() == 1){
                        PathMgr.setupPath(match);
                    }

                    cancel();
                }
                s++;
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 1);
    }
    
    public static void StopMusic(RadioSongPlayer radio, long delay){
        BukkitRunnable task = new BukkitRunnable(){
            @Override
            public void run(){
                radio.setPlaying(false);
            }
        };
        task.runTaskLater(Main.getPlugin(), delay);
    }
    
    public static void StartMatch(Match match){
        for(Player player : Main.getPlugin(Main.class).getServer().getOnlinePlayers()){
            PlayerData data = DataMgr.getPlayerData(player);
            if(data.getMatch() == match){
                MatchRunnable(player, match);
            }
        }
        Player leader = match.getLeaderPlayer();
        if(DataMgr.getPlayerIsQuit(leader.getUniqueId().toString()))
            MatchRunnable(leader, match);
    }
        
    public static void InMatchCounter(Player player){
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = manager.getNewScoreboard();
            
        Match match = DataMgr.getPlayerData(player).getMatch();
        
        org.bukkit.scoreboard.Team bteam0 = scoreboard.registerNewTeam(match.getTeam0().getTeamColor().getColorName());
        bteam0.setColor(match.getTeam0().getTeamColor().getChatColor());
        bteam0.setNameTagVisibility(NameTagVisibility.HIDE_FOR_OTHER_TEAMS);

        org.bukkit.scoreboard.Team bteam1 = scoreboard.registerNewTeam(match.getTeam1().getTeamColor().getColorName());
        bteam1.setColor(match.getTeam1().getTeamColor().getChatColor());
        bteam1.setNameTagVisibility(NameTagVisibility.HIDE_FOR_OTHER_TEAMS);

        for(Player oplayer : Main.getPlugin(Main.class).getServer().getOnlinePlayers()){
            if(DataMgr.getPlayerData(oplayer).getIsJoined()){
                if(match.getTeam0() == DataMgr.getPlayerData(oplayer).getTeam())
                    bteam0.addPlayer(oplayer);
                if(match.getTeam1() == DataMgr.getPlayerData(oplayer).getTeam())
                    bteam1.addPlayer(oplayer);
            }
        }
        
            BukkitRunnable task = new BukkitRunnable(){
                Scoreboard sb = scoreboard;
                int s = 180;
                Player p = player;
                @Override
                public void run(){
                    Objective objective = sb.registerNewObjective(String.valueOf(s), String.valueOf(s));
                    objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                    objective.setDisplayName("MapName:  " + ChatColor.GOLD + DataMgr.getPlayerData(p).getMatch().getMapData().getMapName());

                    String min = String.format("%02d", s%60);
                    
                    Score score = objective.getScore(ChatColor.YELLOW + "TimeLeft:    " + ChatColor.GREEN + ChatColor.GREEN + String.valueOf(s/60) + ":" + min);
                    Score s2 = objective.getScore("");
                    if(conf.getConfig().getString("WorkMode").equals("TDM"))
                        s2 = objective.getScore(ChatColor.YELLOW + "GameMode:  §7チームデスマッチ"); 
                    else
                        s2 = objective.getScore(ChatColor.YELLOW + "GameMode:  §7ナワバリバトル"); 
                    s2.setScore(2);
                    score.setScore(1);
                    
                    for(Player oplayer : Main.getPlugin(Main.class).getServer().getOnlinePlayers()){
                        if(DataMgr.getPlayerData(oplayer).getIsJoined())
                            oplayer.setScoreboard(sb);
                    }

                    if(s == 60){
                        for(Player oplayer : Main.getPlugin(Main.class).getServer().getOnlinePlayers()){
                            if(DataMgr.getPlayerData(oplayer).getIsJoined()){
                                oplayer.sendMessage("");
                                oplayer.sendMessage("§6§l残り1分！");
                                oplayer.sendMessage("");
                            }
                        }
                        if(DataMgr.getPlayerData(p).getPlayerNumber() == 1){
                            NoteBlockSong nbs = NoteBlockAPIMgr.getRandomFinalSong();
                            Song song = nbs.getSong();
                            RadioSongPlayer radio = new RadioSongPlayer(song);
                            radio.setVolume(volume);
                            for(Player oplayer : Main.getPlugin(Main.class).getServer().getOnlinePlayers()){
                                if(DataMgr.getPlayerData(oplayer).getSettings().PlayBGM() && DataMgr.getPlayerData(oplayer).getIsJoined()){
                                    radio.addPlayer(oplayer);
                                    oplayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§7Now playing : §6" + nbs.getSongName() + ""));
                                }
                            }
                            radio.setPlaying(true);
                            //StopMusic(radio, 1200);
                        }
                    }
                    if(s == 0){
                        for(Player oplayer : Main.getPlugin(Main.class).getServer().getOnlinePlayers()){
                            if(DataMgr.getPlayerData(oplayer).getIsJoined() && p != oplayer){
                                oplayer.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                                oplayer.getInventory().clear();
                                FinishMatch(oplayer);
                            }
                        }
                        FinishMatch(p);
                        cancel();
                    }
                    
                    if(s <= 5 && s > 0){
                        for(Player oplayer : Main.getPlugin(Main.class).getServer().getOnlinePlayers()){
                            oplayer.sendTitle(ChatColor.GRAY + String.valueOf(s), "", 0, 30, 4);
                        }
                    }
                        //Main.getPlugin().getServer().broadcastMessage(ChatColor.GOLD + "試合終了まで: " + String.valueOf(s));
                    
                    s--;
                }
            };
            task.runTaskTimer(Main.getPlugin(), 0, 20);
        
        
    }
    
    public static void FinishMatch(Player player){
        BukkitRunnable task = new BukkitRunnable(){
            Player p = player;
            Location loc;
            int i = 0;
            @Override
            public void run(){
                if(i == 0){
                    DataMgr.getPlayerData(p).getMatch().setIsFinished(true);
                    if(DataMgr.getPlayerData(p).getPlayerNumber() == 1){
                        for(Path path : DataMgr.getPlayerData(p).getMatch().getMapData().getPathList()){
                            path.stop();
                            path.reset();
                        }
                        for(Player oplayer : Main.getPlugin(Main.class).getServer().getOnlinePlayers()){
                            DataMgr.setPlayerIsQuit(oplayer.getUniqueId().toString(), false);
                        }
                        DataMgr.getPlayerData(p).getMatch().getBlockUpdater().stop();
                    }
                    for(ArmorStand as : DataMgr.getBeaconMap().values())
                        as.remove();
                    for(ArmorStand as : DataMgr.getSprinklerMap().values())
                        as.remove();
                    DataMgr.getBeaconMap().clear();
                    DataMgr.getSprinklerMap().clear();
                    DataMgr.getArmorStandMap().clear();
                    DataMgr.getPlayerData(p).setIsInMatch(false);
                    if(p.hasPotionEffect(PotionEffectType.SLOW))
                        p.removePotionEffect(PotionEffectType.SLOW);
                    p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 3, 1.3F);
                    loc = p.getLocation();
                    
                    p.getInventory().clear();
                    //p.setPlayerListName(p.getDisplayName());
                    
                    ScoreboardManager manager = Bukkit.getScoreboardManager();
                    Scoreboard scoreboard = manager.getNewScoreboard();

                    /*
                    org.bukkit.scoreboard.Team team = scoreboard.registerNewTeam("");
                    team.addPlayer(p);
                    team.setColor(ChatColor.WHITE);*/

                    p.setScoreboard(scoreboard);
                    
                    
                }
                if(i == 2){
                    p.resetTitle();
                    p.sendTitle(ChatColor.YELLOW + "=========================== Finish! ===========================", "", 3, 30, 10);
                }
                    
                if(i >= 1 && i <= 45){
                    p.teleport(loc);
                    p.getInventory().clear();
                    if(p.hasPotionEffect(PotionEffectType.POISON))
                        p.removePotionEffect(PotionEffectType.POISON);
                }
                if(i == 46){
                    for(Player player : Main.getPlugin(Main.class).getServer().getOnlinePlayers()){
                        p.hidePlayer(Main.getPlugin(), player);
                    }
                }
                if(i == 46 && DataMgr.getPlayerData(p).getPlayerNumber() == 1){
                    if(conf.getConfig().getString("WorkMode").equals("TDM")){
                        Match match = DataMgr.getPlayerData(p).getMatch();
                        int team0c;
                        int team1c;
                        String team0code;
                        String team1code;
                        Team winteam = match.getTeam0();
                        Boolean hikiwake = false;

                        team0c = match.getTeam0().getKillCount();
                        team1c = match.getTeam1().getKillCount();
                        team0code = match.getTeam0().getTeamColor().getColorCode();
                        team1code = match.getTeam1().getTeamColor().getColorCode();
                        
                        if(team0c < team1c)
                            winteam = match.getTeam1();
                        else if(team0c == team1c)
                            hikiwake = true; 
                        
                        for(Player player : Main.getPlugin(Main.class).getServer().getOnlinePlayers()){
                            Animation.TDMResultAnimation(player, team0c, team1c, team0code, team1code, winteam, hikiwake);
                        }
                    }else{
                        Match match = DataMgr.getPlayerData(p).getMatch();
                        int team0;
                        int team1;
                        double dper;
                        int per;
                        String team0code;
                        String team1code;
                        Team winteam = match.getTeam0();
                        Boolean hikiwake = false;

                        team0 = match.getTeam0().getPoint();
                        team1 = match.getTeam1().getPoint();
                        team0code = match.getTeam0().getTeamColor().getColorCode();
                        team1code = match.getTeam1().getTeamColor().getColorCode();
                        dper =  (double)team0/(double)(team0 + team1)*100;
                        per = (int)dper;

                        if(match.getTeam0().getPoint() > match.getTeam1().getPoint()){
                            winteam = match.getTeam0();
                            per++;
                            //match.getTeam0().addPaintCount();
                        }else if(match.getTeam0().getPoint() == match.getTeam1().getPoint()){
                            hikiwake = true;
                        }else{
                            winteam = match.getTeam1();
                            per--;
                        }

                        if(per > 100)
                            per = 100;
                        if(per < 0)
                            per = 0;


                        for(Player oplayer : Main.getPlugin(Main.class).getServer().getOnlinePlayers()){
                            if(DataMgr.getPlayerData(oplayer).getTeam() != null)
                                if(DataMgr.getPlayerData(oplayer).getTeam() == DataMgr.getPlayerData(p).getTeam())
                                    Animation.ResultAnimation(oplayer, per, 100 - per, team0code, team1code, winteam, hikiwake);
                        }
                    }
                }
                
                if(i == 46 && p.isOnline())
                    p.setGameMode(GameMode.ADVENTURE);
                    
                if(i >= 46 && i <= 156){
                    p.teleport(DataMgr.getPlayerData(p).getMatch().getMapData().getResultLoc());
                }
                
                if(i == 137){
                    PlayerData data = DataMgr.getPlayerData(p);
                    
                    //int kill = data.getKillCount();
                    //int paint = data.getPaintCount();
                    
                    
                    
                    p.sendMessage(ChatColor.GREEN + "");
                    p.sendMessage("§a§l§n_______________________________");
                    p.sendMessage("");
                    p.sendMessage("§a§l                  [ 試合結果 ]");
                    p.sendMessage(ChatColor.GREEN + "");
                    
                    for(Player op : Main.getPlugin(Main.class).getServer().getOnlinePlayers()){
                        PlayerData odata = DataMgr.getPlayerData(op);
                        if(!odata.getIsJoined())
                            continue;
                        if(odata.getTeam().getID() == data.getTeam().getID()){
                            if(op.equals(p)){
                                p.sendMessage(odata.getTeam().getTeamColor().getColorCode() + "§l[ §l" + op.getDisplayName() + "§l ]" + ChatColor.RESET + "Kills : " + ChatColor.YELLOW + odata.getKillCount() + "   " + ChatColor.RESET + "Points : " + ChatColor.YELLOW + odata.getPaintCount());
                            }else{
                                p.sendMessage(odata.getTeam().getTeamColor().getColorCode() + "[ " + op.getDisplayName() + " ]" + ChatColor.RESET + "Kills : " + ChatColor.YELLOW + odata.getKillCount() + "   " + ChatColor.RESET + "Points : " + ChatColor.YELLOW + odata.getPaintCount());
                            }
                        }
                    }
                    
                    p.sendMessage(ChatColor.GREEN + "");       
                    
                    for(Player op : Main.getPlugin(Main.class).getServer().getOnlinePlayers()){
                        PlayerData odata = DataMgr.getPlayerData(op);
                        if(!odata.getIsJoined())
                            continue;
                        if(odata.getTeam().getID() != data.getTeam().getID()){
                            if(op.equals(p)){
                                p.sendMessage(odata.getTeam().getTeamColor().getColorCode() + "§l[ §l" + op.getDisplayName() + "§l ]" + ChatColor.RESET + "Kills : " + ChatColor.YELLOW + odata.getKillCount() + "   " + ChatColor.RESET + "Points : " + ChatColor.YELLOW + odata.getPaintCount());
                            }else{
                                p.sendMessage(odata.getTeam().getTeamColor().getColorCode() + "[ " + op.getDisplayName() + " ]" + ChatColor.RESET + "Kills : " + ChatColor.YELLOW + odata.getKillCount() + "   " + ChatColor.RESET + "Points : " + ChatColor.YELLOW + odata.getPaintCount());
                            }
                        }
                    }
                    
                    
                    p.sendMessage(ChatColor.GREEN + "");
                    p.sendMessage(ChatColor.GREEN + "");
                    p.sendMessage("§a§l§n_______________________________");
                    p.sendMessage(ChatColor.GREEN + "");
                
                }
                
                if(i == 157){
                    
                    
                    
                    /*
                    p.sendMessage(ChatColor.GREEN + "##########################");
                    p.sendMessage(ChatColor.GREEN + "          試合結果");
                    p.sendMessage(ChatColor.GOLD + "     Kills  : " + ChatColor.YELLOW + kill);
                    p.sendMessage(ChatColor.GOLD + "     Points : " + ChatColor.YELLOW + paint);
                    p.sendMessage(ChatColor.GREEN + "##########################");
                    */
                    

                    String WorldName = conf.getConfig().getString("Lobby.WorldName");
                    World w = getServer().getWorld(WorldName);
            
                    int ix = conf.getConfig().getInt("Lobby.X");
                    int iy = conf.getConfig().getInt("Lobby.Y");
                    int iz = conf.getConfig().getInt("Lobby.Z");
                    int iyaw = conf.getConfig().getInt("Lobby.Yaw");
                    Location il = new Location(w, ix, iy, iz);
                    il.setYaw(iyaw);
                    WeaponClass wc = DataMgr.getPlayerData(p).getWeaponClass();
                    p.teleport(il);
                    ItemStack join = new ItemStack(org.bukkit.Material.CHEST);
                    ItemMeta joinmeta = join.getItemMeta();
                    joinmeta.setDisplayName("メインメニュー");
                    join.setItemMeta(joinmeta);
                    p.getInventory().clear();
                    p.getInventory().setItem(0, join);
                    if(DataMgr.getPlayerData(p).getPlayerNumber() == 1){
                        RollBack();
                        matchcount++;
                        
                        MatchSetup();
                        //DataMgr.getPlayerData(p).reset();
                        for(boolean is : DataMgr.getPlayerIsQuitMap().values())
                            is = false;
                        
                    }
                    
                    DataMgr.getPlayerData(p).reset();
                    DataMgr.getPlayerData(p).setWeaponClass(wc);
                    
                    
                    
                    p.setWalkSpeed(0.2F);
                    p.setHealth(20);
                    
                    p.setGameMode(GameMode.ADVENTURE);
                    //PlayerData data = new PlayerData(p);
                    //data.setWeaponClass(wc);
                    //DataMgr.setPlayerData(p, data);
                    for(Player player : Main.getPlugin(Main.class).getServer().getOnlinePlayers()){
                        p.showPlayer(Main.getPlugin(), player);
                    }
                    
                    
                    
                    cancel();
                    
                }
                                    
                    
                i++;
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 1);
    }
    
}
