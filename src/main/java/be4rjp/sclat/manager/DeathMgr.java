
package be4rjp.sclat.manager;

import be4rjp.sclat.Main;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import be4rjp.sclat.weapon.spweapon.SuperArmor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
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
        
        if(type.equals("killed") || type.equals("subWeapon"))
            DataMgr.getPlayerData(shooter).addKillCount();
        
        BukkitRunnable task = new BukkitRunnable(){
            Player t = target;
            Player s = shooter;
            Location loc;
            int i = 0;
            
            @Override
            public void run(){
                if(!DataMgr.getPlayerData(t).isInMatch()){
                    cancel();
                    return;
                }
                if(type.equals("killed")){
                    t.setGameMode(GameMode.SPECTATOR);
                    t.getInventory().clear();
                    DataMgr.getPlayerData(t).setTick(10);
                    if(s.getGameMode().equals(GameMode.ADVENTURE)){
                        t.setSpectatorTarget(s);
                    }else{
                        loc = DataMgr.getPlayerData(t).getMatch().getMapData().getIntro();
                        t.teleport(loc);
                    }
                        
                    PlayerData sdata = DataMgr.getPlayerData(s);
                    String msg = sdata.getTeam().getTeamColor().getColorCode() + s.getDisplayName() + ChatColor.RESET + "に" + ChatColor.BOLD + sdata.getWeaponClass().getMainWeapon().getWeaponIteamStack().getItemMeta().getDisplayName() + ChatColor.RESET + "でやられた！";
                    if(i == 0){
                        t.sendTitle(ChatColor.GREEN + "復活まであと: 5秒", msg, 0, 21, 0);
                        for(Player player : Main.getPlugin(Main.class).getServer().getOnlinePlayers()){
                            player.sendMessage(sdata.getTeam().getTeamColor().getColorCode() + s.getDisplayName() + ChatColor.RESET + "が" + DataMgr.getPlayerData(t).getTeam().getTeamColor().getColorCode() + t.getDisplayName() + ChatColor.RESET + "を" + ChatColor.BOLD + sdata.getWeaponClass().getMainWeapon().getWeaponIteamStack().getItemMeta().getDisplayName() + ChatColor.RESET + "で倒した！");
                            s.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(DataMgr.getPlayerData(t).getTeam().getTeamColor().getColorCode() + t.getDisplayName() + ChatColor.RESET + "を倒した！"));
                            s.playSound(s.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 10);
                            
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
                        
                        Location loc = DataMgr.getPlayerData(t).getMatchLocation();
                        t.teleport(loc);
                        t.setGameMode(GameMode.ADVENTURE);
                        t.getInventory().setItem(0, DataMgr.getPlayerData(t).getWeaponClass().getMainWeapon().getWeaponIteamStack());
                        t.getWorld().playSound(DataMgr.getPlayerData(t).getMatchLocation(), Sound.ENTITY_PLAYER_SWIM, 1, 1);
                        t.setExp(0.99F);
                        t.setHealth(20);
                        WeaponClassMgr.setWeaponClass(t);
                        SuperArmor.setArmor(t, 100, 100, false);
                        cancel();
                    }
                }
                
                if(type.equals("subWeapon")){
                    t.setGameMode(GameMode.SPECTATOR);
                    t.getInventory().clear();
                    DataMgr.getPlayerData(t).setTick(10);
                    if(s.getGameMode().equals(GameMode.ADVENTURE)){
                        t.setSpectatorTarget(s);
                    }else{
                        loc = DataMgr.getPlayerData(t).getMatch().getMapData().getIntro();
                        t.teleport(loc);
                    }
                        
                    PlayerData sdata = DataMgr.getPlayerData(s);
                    String msg = sdata.getTeam().getTeamColor().getColorCode() + s.getDisplayName() + ChatColor.RESET + "に" + ChatColor.BOLD + sdata.getWeaponClass().getSubWeaponName() + ChatColor.RESET + "でやられた！";
                    if(i == 0){
                        t.sendTitle(ChatColor.GREEN + "復活まであと: 5秒", msg, 0, 21, 0);
                        for(Player player : Main.getPlugin(Main.class).getServer().getOnlinePlayers()){
                            player.sendMessage(sdata.getTeam().getTeamColor().getColorCode() + s.getDisplayName() + ChatColor.RESET + "が" + DataMgr.getPlayerData(t).getTeam().getTeamColor().getColorCode() + t.getDisplayName() + ChatColor.RESET + "を" + ChatColor.BOLD + sdata.getWeaponClass().getSubWeaponName() + ChatColor.RESET + "で倒した！");
                            s.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(DataMgr.getPlayerData(t).getTeam().getTeamColor().getColorCode() + t.getDisplayName() + ChatColor.RESET + "を倒した！"));
                            s.playSound(s.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 10);
                            
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
                        
                        Location loc = DataMgr.getPlayerData(t).getMatchLocation();
                        t.teleport(loc);
                        t.setGameMode(GameMode.ADVENTURE);
                        t.getInventory().setItem(0, DataMgr.getPlayerData(t).getWeaponClass().getMainWeapon().getWeaponIteamStack());
                        t.getWorld().playSound(DataMgr.getPlayerData(t).getMatchLocation(), Sound.ENTITY_PLAYER_SWIM, 1, 1);
                        t.setExp(0.99F);
                        t.setHealth(20);
                        WeaponClassMgr.setWeaponClass(t);
                        SuperArmor.setArmor(t, 100, 100, false);
                        cancel();
                    }
                }
                
                if(type.equals("water")){
                    t.setGameMode(GameMode.SPECTATOR);
                    DataMgr.getPlayerData(t).setTick(10);
                    t.getInventory().clear();
                    if(i == 0){
                        loc = t.getLocation();
                        for(Player player : Main.getPlugin(Main.class).getServer().getOnlinePlayers()){
                            player.sendMessage(DataMgr.getPlayerData(t).getTeam().getTeamColor().getColorCode() + t.getDisplayName() + ChatColor.RESET + "は溺れてしまった！");
                        }
                    }
                    t.teleport(loc);
                    
                    
                    
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
                        
                        Location loc = DataMgr.getPlayerData(t).getMatchLocation();
                        t.teleport(loc);
                        t.setGameMode(GameMode.ADVENTURE);
                        t.getInventory().setItem(0, DataMgr.getPlayerData(t).getWeaponClass().getMainWeapon().getWeaponIteamStack());
                        t.getWorld().playSound(DataMgr.getPlayerData(t).getMatchLocation(), Sound.ENTITY_PLAYER_SWIM, 1, 1);
                        t.setExp(0.99F);
                        t.setHealth(20);
                        WeaponClassMgr.setWeaponClass(t);
                        SuperArmor.setArmor(t, 100, 100, false);
                        cancel();
                    }
                }
                if(type.equals("fall")){
                    t.setGameMode(GameMode.SPECTATOR);
                    DataMgr.getPlayerData(t).setTick(10);
                    t.getInventory().clear();
                    if(i == 0){
                        loc = DataMgr.getPlayerData(t).getMatch().getMapData().getIntro();
                        for(Player player : Main.getPlugin(Main.class).getServer().getOnlinePlayers()){
                            player.sendMessage(DataMgr.getPlayerData(t).getTeam().getTeamColor().getColorCode() + t.getDisplayName() + ChatColor.RESET + "は奈落に落ちてしまった！");
                        }
                    }
                    t.teleport(loc);
                    
                    
                    
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
                        
                        Location loc = DataMgr.getPlayerData(t).getMatchLocation();
                        t.teleport(loc);
                        t.setGameMode(GameMode.ADVENTURE);
                        t.getInventory().setItem(0, DataMgr.getPlayerData(t).getWeaponClass().getMainWeapon().getWeaponIteamStack());
                        t.getWorld().playSound(DataMgr.getPlayerData(t).getMatchLocation(), Sound.ENTITY_PLAYER_SWIM, 1, 1);
                        t.setExp(0.99F);
                        t.setHealth(20);
                        WeaponClassMgr.setWeaponClass(t);
                        SuperArmor.setArmor(t, 20, 100, false);
                        cancel();
                    }
                }
                i++;
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 1);
    }
}
