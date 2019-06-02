
package be4rjp.sclat.manager;

import be4rjp.sclat.Main;
import be4rjp.sclat.data.Color;
import org.bukkit.entity.Player;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.MapData;
import be4rjp.sclat.data.Match;
import be4rjp.sclat.data.PlayerData;
import be4rjp.sclat.data.Team;
import be4rjp.sclat.data.TeamLoc;
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
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftEntity;
import net.minecraft.server.v1_13_R1.PacketPlayOutEntityDestroy;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import net.minecraft.server.v1_13_R1.*;
import org.bukkit.Sound;

/**
 *
 * @author Be4rJP
 */
public class MatchMgr {
    
    public static int matchcount = 0;
    
    
    public static void PlayerJoinMatch(Player player){
        PlayerData data = DataMgr.getPlayerData(player);
        if(data.isInMatch()){
            player.sendMessage("§c§n既にチームに参加しています");
            System.exit(0);
        }
        Match match = DataMgr.getMatchFromId(matchcount);
        match.addPlayerCount();
        int playercount = match.getPlayerCount();
        if(playercount <= 8){
            data.setPlayerNumber(playercount);
            if(playercount%2==0){
                data.setTeam(match.getTeam1());
                //data.setMatchLocation(DataMgr.getTeamLoc(match.getMapData()).getTeam1Loc(matchcount/2));
            }else{
                data.setTeam(match.getTeam0());
                //data.setMatchLocation(DataMgr.getTeamLoc(match.getMapData()).getTeam0Loc((matchcount+1)/2));
            }
            data.setIsInMatch(true);
            data.setMatch(match);
            if(playercount == 1){//-------------------test--------------------//
                BukkitRunnable task = new BukkitRunnable(){
                    int s = 0;
                    @Override
                    public void run(){
                        if(s == 0)
                            player.sendTitle("","§a試合開始まで後10秒", 10, 70, 20);
                        if(s == 5)
                            player.sendTitle("","§a試合開始まで後5秒", 5, 5, 10);
                        if(s == 6)
                            player.sendTitle("","§a試合開始まで後4秒", 5, 5, 10);
                        if(s == 7)
                            player.sendTitle("","§a試合開始まで後3秒", 5, 5, 10);
                        if(s == 8)
                            player.sendTitle("","§a試合開始まで後2秒", 5, 5, 10);
                        if(s == 9)
                            player.sendTitle("","§a試合開始まで後1秒", 5, 5, 10);
                        if(s == 10){
                            StartMatch(match);
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
        
    }
    
    public static synchronized void MatchSetup(){
        int id = matchcount;
        Match match = new Match(id);
        DataMgr.setMatch(id, match);
        Team team0 = new Team(id * 2);
        Team team1 = new Team(id * 2 + 1);
        DataMgr.setTeam(id * 2, team0);
        DataMgr.setTeam(id * 2 + 1, team1);
        match.setTeam0(team0);
        match.setTeam1(team1);
        
        Color color0 = DataMgr.getColorRandom(0);
        Color color1 = DataMgr.getColorRandom(1);
        team0.setTeamColor(color0);
        team1.setTeamColor(color1);
        
        MapData map = DataMgr.getMapRandom(id);
        match.setMapData(map);
        
        //TeamLoc teamloc = new TeamLoc(map);
        //teamloc.SetupTeam0Loc();
        //teamloc.SetupTeam1Loc();
        //DataMgr.setTeamLoc(map, teamloc);
    }
    
    public static void StartMatch(Match match){
        for(Player player : Main.getPlugin(Main.class).getServer().getOnlinePlayers()){
            PlayerData data = DataMgr.getPlayerData(player);
            if(data.getMatch() == match){
                
                
                
                BukkitRunnable task = new BukkitRunnable(){
                    int s = 0;
                    Player p = player;
                    World w = Main.getPlugin().getServer().getWorld(match.getMapData().getWorldName());
                    
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
                                    DataMgr.getPlayerData(p).setMatchLocation(new Location(l.getWorld(), l.getBlockX() - 1.5D, l.getBlockY(), l.getBlockZ() + 1.5D));
                                if(i == 3)
                                    DataMgr.getPlayerData(p).setMatchLocation(new Location(l.getWorld(), l.getBlockX() + 1.5D, l.getBlockY(), l.getBlockZ() - 1.5D));
                                if(i == 4)
                                    DataMgr.getPlayerData(p).setMatchLocation(new Location(l.getWorld(), l.getBlockX() - 1.5D, l.getBlockY(), l.getBlockZ() - 1.5D));
                            }
                            if(DataMgr.getPlayerData(p).getTeam() == match.getTeam1()){
                                Location l = DataMgr.getPlayerData(p).getMatch().getMapData().getTeam1Loc();
                                int i = DataMgr.getPlayerData(p).getPlayerNumber()/2;
                                if(i == 1)
                                    DataMgr.getPlayerData(p).setMatchLocation(new Location(l.getWorld(), l.getBlockX() + 1.5D, l.getBlockY(), l.getBlockZ() + 1.5D));
                                if(i == 2)
                                    DataMgr.getPlayerData(p).setMatchLocation(new Location(l.getWorld(), l.getBlockX() - 1.5D, l.getBlockY(), l.getBlockZ() + 1.5D));
                                if(i == 3)
                                    DataMgr.getPlayerData(p).setMatchLocation(new Location(l.getWorld(), l.getBlockX() + 1.5D, l.getBlockY(), l.getBlockZ() - 1.5D));
                                if(i == 4)
                                    DataMgr.getPlayerData(p).setMatchLocation(new Location(l.getWorld(), l.getBlockX() - 1.5D, l.getBlockY(), l.getBlockZ() - 1.5D));
                            }
                            
                            Entity e = DataMgr.getPlayerData(p).getMatchLocation().getWorld().spawnEntity(DataMgr.getPlayerData(p).getMatchLocation(), EntityType.SQUID);
                            squid = (LivingEntity)e;
                            squid.setAI(false);
                            squid.setSwimming(true);
                            squid.setCustomName(p.getDisplayName());
                            squid.setCustomNameVisible(true);
                            
                            Location introl = match.getMapData().getIntro();
                            
                            p.setGameMode(GameMode.SPECTATOR);
                            p.teleport(introl);
                            
                            p.sendTitle("§l" + match.getMapData().getMapName(), "§7ナワバリバトル", 10, 70, 20);
                        }
                        if(s >= 1 && s <= 100){
                            Location introl = match.getMapData().getIntro();
                            p.teleport(introl);
                        }
                        if(s >= 101 && s <= 160){
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
                                    squid.remove();
                                }
                                if(s == 101){
                                    introl.getWorld().playSound(DataMgr.getPlayerData(p).getMatchLocation(), Sound.ENTITY_PLAYER_SWIM, 1, 1);
                                    NPCMgr.createNPC(p, p.getDisplayName(), DataMgr.getPlayerData(p).getMatchLocation());
                                    //p.getWorld().playEffect(introl, Effect.CLICK2, DataMgr.getPlayerData(p).getTeam().getTeamColor().getWool());
                                }
                                //npcle = (LivingEntity)npc.getEntity();
                                //npcle.getEquipment().setItemInMainHand(new ItemStack(Material.WOODEN_HOE));
                            }
                        }
                        if(s >= 161 && s <= 220){
                            Location introl = match.getMapData().getTeam1Intro();
                            p.teleport(introl);
                            if(DataMgr.getPlayerData(p).getTeam() == match.getTeam1()){
                                if(s >= 161 && s <= 180){
                                    //Packet55BlockBreakAnimation packet = new Packet55BlockBreakAnimation(0, block.getX(), block.getY(), block.getZ(), damage);
                                    org.bukkit.block.data.BlockData bd = DataMgr.getPlayerData(p).getTeam().getTeamColor().getWool().createBlockData();
                                    introl.getWorld().spawnParticle(org.bukkit.Particle.BLOCK_DUST, DataMgr.getPlayerData(p).getMatchLocation(), 10, 0.3, 0.4, 0.3, 1, bd);
                                }
                                if(s == 180){
                                    squid.remove();
                                }
                                if(s == 161){
                                    introl.getWorld().playSound(DataMgr.getPlayerData(p).getMatchLocation(), Sound.ENTITY_PLAYER_SWIM, 1, 1);
                                    NPCMgr.createNPC(p, p.getDisplayName(), DataMgr.getPlayerData(p).getMatchLocation());
                                }
                            }
                                
                                
                                //npcle = (LivingEntity)npc.getEntity();
                                //npcle.getEquipment().setItemInMainHand(new ItemStack(Material.WOODEN_HOE));
                        }
                        if(s == 301){
                            //playerclass
                            p.getInventory().setItem(0, DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getWeaponIteamStack());
                            //Shooter.ShooterRunnable(p);
                        }
                        
                        if(s >= 221 && s <= 300){
                            p.setGameMode(GameMode.ADVENTURE);
                            
                            Location introl = DataMgr.getPlayerData(p).getMatchLocation();
                            p.teleport(introl); 
                        }  
                            
                        
                            s++;
                        
                        
                        
                    }
                };
                task.runTaskTimer(Main.getPlugin(), 0, 1);
            }
        }
    }
}
