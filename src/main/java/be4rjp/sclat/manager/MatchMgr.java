
package be4rjp.sclat.manager;

import be4rjp.sclat.Animation;
import be4rjp.sclat.Main;
import static be4rjp.sclat.Main.conf;
import be4rjp.sclat.data.Color;
import org.bukkit.entity.Player;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.MapData;
import be4rjp.sclat.data.Match;
import be4rjp.sclat.data.PaintData;
import be4rjp.sclat.data.PlayerData;
import be4rjp.sclat.data.Team;
import be4rjp.sclat.data.TeamLoc;
import be4rjp.sclat.data.WeaponClass;
import be4rjp.sclat.weapon.Shooter;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftEntity;
import net.minecraft.server.v1_13_R1.PacketPlayOutEntityDestroy;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import net.minecraft.server.v1_13_R1.*;
import static org.bukkit.Bukkit.getServer;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_13_R1.CraftServer;
import org.bukkit.craftbukkit.v1_13_R1.CraftWorld;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.ScoreboardManager;

/**
 *
 * @author Be4rJP
 */
public class MatchMgr {
    
    public static int matchcount = 0;
    
    
    
    
    public static void PlayerJoinMatch(Player player){
        PlayerData data = DataMgr.getPlayerData(player);
        if(!data.isInMatch()){
            
        
        Match match = DataMgr.getMatchFromId(matchcount);
        if(match.canJoin()){
            
        
            
        match.addPlayerCount();
        int playercount = match.getPlayerCount();
        if(playercount <= 20){
            data.setPlayerNumber(playercount);
            if(playercount%2==0){
                data.setTeam(match.getTeam1());
                //data.setMatchLocation(DataMgr.getTeamLoc(match.getMapData()).getTeam1Loc(matchcount/2));
            }else{
                data.setTeam(match.getTeam0());
                //data.setMatchLocation(DataMgr.getTeamLoc(match.getMapData()).getTeam0Loc((matchcount+1)/2));
            }
            
            data.setMatch(match);
            if(playercount == 1){//-------------------test--------------------//
                BukkitRunnable task = new BukkitRunnable(){
                    int s = 0;
                    Player p = player;
                    @Override
                    public void run(){
                        if(s == 0)
                            Main.getPlugin().getServer().broadcastMessage("§a試合開始まで後10秒");
                        if(s == 5)
                            Main.getPlugin().getServer().broadcastMessage("§a試合開始まで後5秒");
                        if(s == 6)
                            Main.getPlugin().getServer().broadcastMessage("§a試合開始まで後4秒");
                        if(s == 7)
                            Main.getPlugin().getServer().broadcastMessage("§a試合開始まで後3秒");
                        if(s == 8)
                            Main.getPlugin().getServer().broadcastMessage("§a試合開始まで後2秒");
                        if(s == 9)
                            Main.getPlugin().getServer().broadcastMessage("§a試合開始まで後1秒");
                        if(s == 10){
                            match.setCanJoin(false);
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
        int id = matchcount;
        Match match = new Match(id);
        Team team0 = new Team(id * 2);
        Team team1 = new Team(id * 2 + 1);
        DataMgr.setTeam(id * 2, team0);
        DataMgr.setTeam(id * 2 + 1, team1);
        
        DataMgr.ColorShuffle();
        Color color0 = DataMgr.getColorRandom(0);
        Color color1 = DataMgr.getColorRandom(1);
        team0.setTeamColor(color0);
        team1.setTeamColor(color1);
        
        match.setTeam0(team0);
        match.setTeam1(team1);
        
        Main.getPlugin().getLogger().info(team0.getTeamColor().getColorCode() + "Team0SetColor");
        Main.getPlugin().getLogger().info(team1.getTeamColor().getColorCode() + "Team1SetColor");
        
        DataMgr.MapDataShuffle();
        MapData map = DataMgr.getMapRandom(0);
        match.setMapData(map);
        
        DataMgr.setMatch(id, match);
        
        //TeamLoc teamloc = new TeamLoc(map);
        //teamloc.SetupTeam0Loc();
        //teamloc.SetupTeam1Loc();
        //DataMgr.setTeamLoc(map, teamloc);
    }
    
    public static void RollBack(Match match){
        for(PaintData data : DataMgr.getBlockDataMap().values()){
            if(data.getMatch() == match){
                data.getBlock().setType(data.getOriginalType());
                data = null;
            }
        }
        DataMgr.getBlockDataMap().clear();
    }
    
    public static void StartCount(Player player){
        BukkitRunnable task = new BukkitRunnable(){
            Player p = player;
            int i = 0;
            @Override
            public void run(){
                if(i == 10)
                    p.sendTitle("R§7E    ", "", 0, 56, 0);
                if(i == 12)
                    p.sendTitle("RE§7A   ", "", 0, 46, 0);
                if(i == 14)
                    p.sendTitle("REA§7D  ", "", 0, 36, 0);
                if(i == 16)
                    p.sendTitle("READ§7Y ", "", 0, 26, 0);
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
    
    public static void StartMatch(Match match){
        for(Player player : Main.getPlugin(Main.class).getServer().getOnlinePlayers()){
            PlayerData data = DataMgr.getPlayerData(player);
            if(data.getMatch() == match){
                
                
                
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
                            if(DataMgr.getPlayerData(p).getTeam() == match.getTeam0()){
                                Location l = DataMgr.getPlayerData(p).getMatch().getMapData().getTeam0Loc();
                                int i = (DataMgr.getPlayerData(p).getPlayerNumber()+1)/2;
                                if(i == 1)
                                    DataMgr.getPlayerData(p).setMatchLocation(new Location(l.getWorld(), l.getBlockX() + 1.5D, l.getBlockY(), l.getBlockZ() + 1.5D));
                                if(i == 2)
                                    DataMgr.getPlayerData(p).setMatchLocation(new Location(l.getWorld(), l.getBlockX() - 0.5D, l.getBlockY(), l.getBlockZ() + 1.5D));
                                if(i == 3)
                                    DataMgr.getPlayerData(p).setMatchLocation(new Location(l.getWorld(), l.getBlockX() + 1.5D, l.getBlockY(), l.getBlockZ() - 0.5D));
                                if(i == 4)
                                    DataMgr.getPlayerData(p).setMatchLocation(new Location(l.getWorld(), l.getBlockX() - 0.5D, l.getBlockY(), l.getBlockZ() - 0.5D));
                            }
                            if(DataMgr.getPlayerData(p).getTeam() == match.getTeam1()){
                                Location l = DataMgr.getPlayerData(p).getMatch().getMapData().getTeam1Loc();
                                int i = DataMgr.getPlayerData(p).getPlayerNumber()/2;
                                if(i == 1)
                                    DataMgr.getPlayerData(p).setMatchLocation(new Location(l.getWorld(), l.getBlockX() + 1.5D, l.getBlockY(), l.getBlockZ() + 1.5D));
                                if(i == 2)
                                    DataMgr.getPlayerData(p).setMatchLocation(new Location(l.getWorld(), l.getBlockX() - 0.5D, l.getBlockY(), l.getBlockZ() + 1.5D));
                                if(i == 3)
                                    DataMgr.getPlayerData(p).setMatchLocation(new Location(l.getWorld(), l.getBlockX() + 1.5D, l.getBlockY(), l.getBlockZ() - 0.5D));
                                if(i == 4)
                                    DataMgr.getPlayerData(p).setMatchLocation(new Location(l.getWorld(), l.getBlockX() - 0.5D, l.getBlockY(), l.getBlockZ() - 0.5D));
                            }
                            
                            if(DataMgr.getPlayerData(p).getPlayerNumber() < 8){

                            Entity e = DataMgr.getPlayerData(p).getMatchLocation().getWorld().spawnEntity(DataMgr.getPlayerData(p).getMatchLocation(), EntityType.SQUID);
                            squid = (LivingEntity)e;
                            squid.setAI(false);
                            squid.setSwimming(true);
                            squid.setCustomName(p.getDisplayName());
                            squid.setCustomNameVisible(true);
                            }else{
                                Location l = DataMgr.getPlayerData(p).getMatch().getMapData().getTeam1Loc();
                                DataMgr.getPlayerData(p).setMatchLocation(new Location(l.getWorld(), l.getBlockX() + 0.5D, l.getBlockY(), l.getBlockZ() + 0.5D));
                            }
                          
                            p.setGameMode(GameMode.SPECTATOR);
                            Location introl = match.getMapData().getIntro();
                            p.teleport(introl);
                            Location location = DataMgr.getPlayerData(p).getMatchLocation();
                            
                            /*
                            MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
                            WorldServer nmsWorld = ((CraftWorld) location.getWorld()).getHandle();
                            squid = new EntitySquid(nmsWorld);
                            squid.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), 0);
                            squid.setCustomName(new ChatMessage(p.getDisplayName()));
                            squid.setCustomNameVisible(true);
                            squid.setSwimming(true);
                            
                            
                            for(Player player : Main.getPlugin(Main.class).getServer().getOnlinePlayers()){
                                PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
                                connection.sendPacket(new PacketPlayOutSpawnEntityLiving(squid));
                                //connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
                            }
                            */


                            
                            p.sendTitle("§l" + match.getMapData().getMapName(), "§7ナワバリバトル", 10, 70, 20);
                            
                            StartCount(p);
                            ScoreboardManager manager = Bukkit.getScoreboardManager();
                            org.bukkit.scoreboard.Scoreboard board = manager.getNewScoreboard();
                            Objective objective = board.registerNewObjective("MapName", "Time", "Match");
                            
                            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                            objective.setDisplayName("マップ名: " + ChatColor.GOLD + DataMgr.getPlayerData(p).getMatch().getMapData().getMapName());
                            Score score = objective.getScore(ChatColor.YELLOW + "残り時間: " + ChatColor.GREEN + "3:00");
                            score.setScore(0);
                            
                            p.setScoreboard(board);
                            for(Player player : Main.getPlugin(Main.class).getServer().getOnlinePlayers()){
                                p.hidePlayer(Main.getPlugin(), player);
                            }
                            //Score score = objective.getScore("3:00");
                            //score.setScore(0);
                        }
                        if(s >= 1 && s <= 100){
                            if(s == 1)
                                intromove = match.getMapData().getIntro();
                            MapData map = DataMgr.getPlayerData(p).getMatch().getMapData();
                            intromove.add(map.getIntroMoveX(), map.getIntroMoveY(), map.getIntroMoveZ());
                            p.teleport(intromove);
                        }
                        if(s >= 100 && s <= 160){
                            Location introl = match.getMapData().getTeam0Intro();
                            p.teleport(introl);
                            if(DataMgr.getPlayerData(p).getTeam() == match.getTeam0()){
                                if(s >= 101 && s <= 120){
                                    //Packet55BlockBreakAnimation packet = new Packet55BlockBreakAnimation(0, block.getX(), block.getY(), block.getZ(), damage);
                                    //introl.getWorld().spawnParticle(org.bukkit.Particle.REDSTONE, DataMgr.getPlayerData(p).getMatchLocation(), 8, 0.4, 0.4, 0.4, 1, new org.bukkit.Particle.DustOptions(DataMgr.getPlayerData(p).getTeam().getTeamColor().getBukkitColor(), 2.0F));
                                    org.bukkit.block.data.BlockData bd = DataMgr.getPlayerData(p).getTeam().getTeamColor().getWool().createBlockData();
                                    introl.getWorld().spawnParticle(org.bukkit.Particle.BLOCK_DUST, DataMgr.getPlayerData(p).getMatchLocation(), 10, 0.3, 0.4, 0.3, 1, bd);
                                    
                                }
                                if(s == 120){
                                    if(DataMgr.getPlayerData(p).getPlayerNumber() < 8)
                                        squid.remove();
                                    /*
                                    for(Player player : Main.getPlugin(Main.class).getServer().getOnlinePlayers()){
                                        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
                                        connection.sendPacket(new PacketPlayOutEntityDestroy(squid.getBukkitEntity().getEntityId()));
                                    }*/
                                }
                                if(s == 100){
                                    if(DataMgr.getPlayerData(p).getPlayerNumber() < 8){
                                    introl.getWorld().playSound(DataMgr.getPlayerData(p).getMatchLocation(), Sound.ENTITY_PLAYER_SWIM, 1, 1);
                                    NPCMgr.createNPC(p, p.getDisplayName(), DataMgr.getPlayerData(p).getMatchLocation());
                                    }
                                    //p.getWorld().playEffect(introl, Effect.CLICK2, DataMgr.getPlayerData(p).getTeam().getTeamColor().getWool());
                                }
                                //npcle = (LivingEntity)npc.getEntity();
                                //npcle.getEquipment().setItemInMainHand(new ItemStack(Material.WOODEN_HOE));
                            }
                        }
                        if(s >= 160 && s <= 220){
                            Location introl = match.getMapData().getTeam1Intro();
                            p.teleport(introl);
                            if(DataMgr.getPlayerData(p).getTeam() == match.getTeam1()){
                                if(s >= 161 && s <= 180){
                                    //Packet55BlockBreakAnimation packet = new Packet55BlockBreakAnimation(0, block.getX(), block.getY(), block.getZ(), damage);
                                    org.bukkit.block.data.BlockData bd = DataMgr.getPlayerData(p).getTeam().getTeamColor().getWool().createBlockData();
                                    introl.getWorld().spawnParticle(org.bukkit.Particle.BLOCK_DUST, DataMgr.getPlayerData(p).getMatchLocation(), 10, 0.3, 0.4, 0.3, 1, bd);
                                }
                                if(s == 180){
                                    if(DataMgr.getPlayerData(p).getPlayerNumber() < 8)
                                        squid.remove();
                                    /*
                                    for(Player player : Main.getPlugin(Main.class).getServer().getOnlinePlayers()){
                                        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
                                        connection.sendPacket(new PacketPlayOutEntityDestroy(squid.getBukkitEntity().getEntityId()));
                                    }*/
                                }
                                if(s == 160){
                                    if(DataMgr.getPlayerData(p).getPlayerNumber() < 8){
                                    introl.getWorld().playSound(DataMgr.getPlayerData(p).getMatchLocation(), Sound.ENTITY_PLAYER_SWIM, 1, 1);
                                    NPCMgr.createNPC(p, p.getDisplayName(), DataMgr.getPlayerData(p).getMatchLocation());
                                    }
                                }
                            }
                            
                            
                            //npcle = (LivingEntity)npc.getEntity();
                            //npcle.getEquipment().setItemInMainHand(new ItemStack(Material.WOODEN_HOE));
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
                            for(Player player : Main.getPlugin(Main.class).getServer().getOnlinePlayers()){
                                p.showPlayer(Main.getPlugin(), player);
                            }
                            p.getInventory().setItem(0, DataMgr.getPlayerData(p).getWeaponClass().getMainWeapon().getWeaponIteamStack());
                            SubWeaponMgr.setSubWeapon(p);
                            Shooter.ShooterRunnable(p);
                            if(DataMgr.getPlayerData(p).getWeaponClass().getMainWeapon().getShootTick() < 5){
                                DataMgr.getPlayerData(p).setTick(10);
                                //Shooter.ShooterRunnable(p);
                            }
                            //SquidMgr.SquidRunnable(p);
                            DataMgr.getPlayerData(p).setIsInMatch(true);
                            InMatchCounter(p);
                            p.playSound(p.getLocation(), Sound.ENTITY_ZOMBIE_INFECT, 10.0F, 2.0F);
                            cancel();
                        }
                        

                        s++;
                        
                        
                        
                    }
                };
                task.runTaskTimer(Main.getPlugin(), 0, 1);
            }
        }
        BukkitRunnable task2 = new BukkitRunnable(){
            @Override
            public void run(){
                //Animation.ResultAnimation(match);
            }
        };
        task2.runTaskTimer(Main.getPlugin(), 3881, 1);
        
    }
        
    public static void InMatchCounter(Player player){
        
            BukkitRunnable task = new BukkitRunnable(){
                int s = 180;
                Player p = player;
                @Override
                public void run(){
                    ScoreboardManager manager = Bukkit.getScoreboardManager();
                    org.bukkit.scoreboard.Scoreboard board = manager.getNewScoreboard();
                    //board.clearSlot(DisplaySlot.SIDEBAR);
                    Objective objective = board.registerNewObjective("MapName", "Time", "Match");
                    objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                    objective.setDisplayName("マップ名: " + ChatColor.GOLD + DataMgr.getPlayerData(p).getMatch().getMapData().getMapName());
                    String min = String.format("%02d", s%60);
                    
                    Score score = objective.getScore(ChatColor.YELLOW + "残り時間: " + ChatColor.GREEN + ChatColor.GREEN + String.valueOf(s/60) + ":" + min);
                    score.setScore(0);
                    p.setScoreboard(board);

                    if(s == 60)
                        p.sendTitle(ChatColor.GOLD + "残り1分！", "", 4, 28, 4);
                    if(s == 0){
                        p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                        FinishMatch(p);
                        cancel();
                    }
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
                    p.sendTitle(ChatColor.AQUA + "試合終了！", "", 3, 30, 10);
                    loc = p.getLocation();
                    DataMgr.getPlayerData(p).setIsInMatch(false);
                }
                if(i >= 1 && i <= 45){
                    p.teleport(loc);
                }
                if(i == 46){
                    Match match = DataMgr.getPlayerData(p).getMatch();
                    int team0;
                    int team1;
                    double dper;
                    int per;
                    String team0code;
                    String team1code;
                    Team winteam = match.getTeam0();
                    Boolean hikiwake = false;
                    if(match.getTeam0() == DataMgr.getPlayerData(p).getTeam()){
                        team0 = match.getTeam0().getPoint();
                        team1 = match.getTeam1().getPoint();
                        team0code = match.getTeam0().getTeamColor().getColorCode();
                        team1code = match.getTeam1().getTeamColor().getColorCode();
                        dper =  (double)team0/(double)(team0 + team1)*100.0;
                        per = (int)dper;
                        
                        if(match.getTeam0().getPoint() - match.getTeam1().getPoint() > 0){
                            winteam = match.getTeam0();
                        }else if(match.getTeam0().getPoint() == match.getTeam1().getPoint()){
                            hikiwake = true;
                        }else{
                            winteam = match.getTeam1();
                        }
                    }else{
                        team0 = match.getTeam1().getPoint();
                        team1 = match.getTeam0().getPoint();
                        team0code = match.getTeam1().getTeamColor().getColorCode();
                        team1code = match.getTeam0().getTeamColor().getColorCode();
                        dper =  (double)team0/(double) (team0 + team1)*100.0;
                        per = (int)dper;
                        if(match.getTeam1().getPoint() - match.getTeam0().getPoint() > 0){
                            winteam = match.getTeam1();
                        }else if(match.getTeam1().getPoint() == match.getTeam0().getPoint()){
                            hikiwake = true;
                        }else{
                            winteam = match.getTeam0();
                        }
                    }
                    for(Player player : Main.getPlugin(Main.class).getServer().getOnlinePlayers()){
                        p.hidePlayer(Main.getPlugin(), player);
                    }
                    Animation.ResultAnimation(p, per, 100 - per, team0code, team1code, winteam, hikiwake);
                }
                if(i >= 46 && i <= 156){
                    p.teleport(DataMgr.getPlayerData(p).getMatch().getMapData().getResultLoc());
                }
                if(i == 157){
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
                    p.getInventory().setItem(0, join);
                    if(DataMgr.getPlayerData(p).getPlayerNumber() == 1){
                        RollBack(DataMgr.getPlayerData(p).getMatch());
                        matchcount++;
                        MatchSetup();
                        DataMgr.getPlayerData(p).reset();
                    }
                    
                    
                    
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
