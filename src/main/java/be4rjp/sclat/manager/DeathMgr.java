
package be4rjp.sclat.manager;

import be4rjp.sclat.Main;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftEntity;

/**
 *
 * @author Be4rJP
 */
public class DeathMgr {
    public static void PlayerDeathRunnable(Player target, Player shooter, String type){
        
        BukkitRunnable task = new BukkitRunnable(){
            Player t = target;
            Player s = shooter;
            Location loc;
            int i = 0;
            
            @Override
            public void run(){
                if(type == "killed"){
                    t.setGameMode(GameMode.SPECTATOR);
                    ((CraftPlayer) t).getHandle().setSpectatorTarget(((CraftEntity) s).getHandle());
                    PlayerData sdata = DataMgr.getPlayerData(s);
                    String msg = sdata.getTeam().getTeamColor().getColorCode() + s.getDisplayName() + ChatColor.RESET + "に" + ChatColor.BOLD + sdata.getWeaponClass().getMainWeapon().getWeaponIteamStack().getItemMeta().getDisplayName() + ChatColor.RESET + "でやられた！";
                    if(i == 0){
                        t.sendTitle(ChatColor.GREEN + "復活まであと: 5秒", msg, 0, 21, 0);
                        for(Player player : Main.getPlugin(Main.class).getServer().getOnlinePlayers()){
                            if(DataMgr.getPlayerData(t).getMatch() == DataMgr.getPlayerData(player).getMatch())
                                player.sendMessage(sdata.getTeam().getTeamColor().getColorCode() + s.getDisplayName() + ChatColor.RESET + "が" + DataMgr.getPlayerData(s).getTeam().getTeamColor() + ChatColor.RESET + "を" + s.getDisplayName() + ChatColor.BOLD + sdata.getWeaponClass().getMainWeapon().getWeaponIteamStack().getItemMeta().getDisplayName() + ChatColor.RESET + "で倒した！");
                        }
                    }
                    if(i == 20)
                        t.sendTitle(ChatColor.GREEN + "復活まであと: 4秒", msg, 0, 21, 0);
                    if(i == 40)
                        t.sendTitle(ChatColor.GREEN + "復活まであと: 3秒", msg, 0, 21, 0);
                    if(i == 60)
                        t.sendTitle(ChatColor.GREEN + "復活まであと: 2秒", msg, 0, 21, 0);
                    if(i == 80)
                        t.sendTitle(ChatColor.GREEN + "復活まであと: 1秒", msg, 0, 18, 2);
                    //t.sendTitle("", sdata.getTeam().getTeamColor().getColorCode() + s.getDisplayName() + ChatColor.RESET + "に" + ChatColor.BOLD + sdata.getWeaponClass().getMainWeapon().getWeaponIteamStack().getItemMeta().getDisplayName() + ChatColor.RESET + "でやられた！", 0, 5, 2);
                    if(i == 100){
                        t.setGameMode(GameMode.ADVENTURE);
                        Location loc = DataMgr.getPlayerData(t).getMatchLocation();
                        t.teleport(loc);
                        t.getInventory().setItem(0, DataMgr.getPlayerData(t).getWeaponClass().getMainWeapon().getWeaponIteamStack());
                        t.getWorld().playSound(DataMgr.getPlayerData(t).getMatchLocation(), Sound.ENTITY_PLAYER_SWIM, 1, 1);
                        
                        cancel();
                    }
                }
                if(type == "water"){
                    t.setGameMode(GameMode.SPECTATOR);
                    if(i == 0)
                        loc = t.getLocation();
                    t.teleport(loc);
                    PlayerData sdata = DataMgr.getPlayerData(s);
                    if(i == 0)
                        t.sendTitle(ChatColor.GREEN + "復活まであと: 5秒", "溺れてしまった！", 0, 21, 0);
                    if(i == 20)
                        t.sendTitle(ChatColor.GREEN + "復活まであと: 4秒", "溺れてしまった！", 0, 21, 0);
                    if(i == 40)
                        t.sendTitle(ChatColor.GREEN + "復活まであと: 3秒", "溺れてしまった！", 0, 21, 0);
                    if(i == 60)
                        t.sendTitle(ChatColor.GREEN + "復活まであと: 2秒", "溺れてしまった！", 0, 21, 0);
                    if(i == 80)
                        t.sendTitle(ChatColor.GREEN + "復活まであと: 1秒", "溺れてしまった！", 0, 18, 2);
                    if(i == 100){
                        t.setGameMode(GameMode.ADVENTURE);
                        Location loc = DataMgr.getPlayerData(t).getMatchLocation();
                        t.teleport(loc);
                        t.getInventory().setItem(0, DataMgr.getPlayerData(t).getWeaponClass().getMainWeapon().getWeaponIteamStack());
                        t.getWorld().playSound(DataMgr.getPlayerData(t).getMatchLocation(), Sound.ENTITY_PLAYER_SWIM, 1, 1);
                        cancel();
                    }
                }
                if(type == "fall"){
                    t.setGameMode(GameMode.SPECTATOR);
                    if(i == 0)
                        loc = DataMgr.getPlayerData(t).getMatch().getMapData().getIntro();
                    t.teleport(loc);
                    PlayerData sdata = DataMgr.getPlayerData(s);
                    if(i == 0)
                        t.sendTitle(ChatColor.GREEN + "復活まであと: 5秒", "奈落に落ちてしまった！", 0, 21, 0);
                    if(i == 20)
                        t.sendTitle(ChatColor.GREEN + "復活まであと: 4秒", "奈落に落ちてしまった！", 0, 21, 0);
                    if(i == 40)
                        t.sendTitle(ChatColor.GREEN + "復活まであと: 3秒", "奈落に落ちてしまった！", 0, 21, 0);
                    if(i == 60)
                        t.sendTitle(ChatColor.GREEN + "復活まであと: 2秒", "奈落に落ちてしまった！", 0, 21, 0);
                    if(i == 80)
                        t.sendTitle(ChatColor.GREEN + "復活まであと: 1秒", "奈落に落ちてしまった！", 0, 18, 2);
                    if(i == 100){
                        t.setGameMode(GameMode.ADVENTURE);
                        Location loc = DataMgr.getPlayerData(t).getMatchLocation();
                        t.teleport(loc);
                        t.getInventory().setItem(0, DataMgr.getPlayerData(t).getWeaponClass().getMainWeapon().getWeaponIteamStack());
                        t.getWorld().playSound(DataMgr.getPlayerData(t).getMatchLocation(), Sound.ENTITY_PLAYER_SWIM, 1, 1);
                        cancel();
                    }
                }
                i++;
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 1);
    }
}
