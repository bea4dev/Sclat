
package be4rjp.sclat.manager;

import be4rjp.sclat.Main;
import be4rjp.sclat.Sclat;
import be4rjp.sclat.Sphere;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import be4rjp.sclat.weapon.spweapon.SuperArmor;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftEntity;
import org.bukkit.entity.Item;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 *
 * @author Be4rJP
 */
public class DeathMgr {
    public static void PlayerDeathRunnable(Player target, Player shooter, String type){

        DataMgr.getPlayerData(target).setIsDead(true);
        target.setGameMode(GameMode.SPECTATOR);
        
        DataMgr.getPlayerData(target).setPoison(false);
        
        Item drop1 = target.getWorld().dropItem(target.getEyeLocation(), DataMgr.getPlayerData(target).getWeaponClass().getMainWeapon().getWeaponIteamStack());
        Item drop2 = target.getWorld().dropItem(target.getEyeLocation(), DataMgr.getPlayerData(target).getTeam().getTeamColor().getBougu());
        final double random = 0.4;
        drop1.setVelocity(new Vector(Math.random() * random - random/2, random * 2/3, Math.random() * random - random/2));
        drop2.setVelocity(new Vector(Math.random() * random - random/2, random * 2/3, Math.random() * random - random/2));
    
        Sclat.createInkExplosionEffect(target.getEyeLocation().add(0, -1, 0), 3, 30, shooter);
        
        DataMgr.getPlayerData(target).setSPGauge((int)(DataMgr.getPlayerData(target).getSPGauge() * 0.7));
        
        //半径
        double maxDist = 3;

        //塗る
        for(int i = 0; i <= maxDist; i++){
            List<Location> p_locs = Sphere.getSphere(target.getLocation(), i, 20);
            for(Location loc : p_locs){
                PaintMgr.Paint(loc, shooter, false);
            }
        }
        
        BukkitRunnable clear = new BukkitRunnable(){
            @Override
            public void run(){
                drop1.remove();
                drop2.remove();
            }
        };
        clear.runTaskLater(Main.getPlugin(), 50);
        
        
        if(target.hasPotionEffect(PotionEffectType.GLOWING))
            target.removePotionEffect(PotionEffectType.GLOWING);
        if(target.hasPotionEffect(PotionEffectType.SLOW))
            target.removePotionEffect(PotionEffectType.SLOW);
        
        if(type.equals("killed") || type.equals("subWeapon") || type.equals("spWeapon")){
            DataMgr.getPlayerData(shooter).addKillCount();
            DataMgr.getPlayerData(shooter).getTeam().addKillCount();
            if(!DataMgr.getPlayerData(shooter).getIsUsingSP())
                for(int i = 0; i < 10; i++)
                    SPWeaponMgr.addSPCharge(shooter);
        }
        
        BukkitRunnable task = new BukkitRunnable(){
            Player t = target;
            Player s = shooter;
            Location loc;
            int i = 0;
            
            @Override
            public void run(){
                try{
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
                            DataMgr.getPlayerData(target).setIsDead(false);
                            Location loc = DataMgr.getPlayerData(t).getMatchLocation();
                            t.teleport(loc);
                            t.setGameMode(GameMode.ADVENTURE);
                            t.getInventory().setItem(0, DataMgr.getPlayerData(t).getWeaponClass().getMainWeapon().getWeaponIteamStack());
                            t.getWorld().playSound(DataMgr.getPlayerData(t).getMatchLocation(), Sound.ENTITY_PLAYER_SWIM, 1, 1);
                            t.setExp(0.99F);
                            t.setHealth(20);
                            WeaponClassMgr.setWeaponClass(t);
                            SuperArmor.setArmor(t, Double.MAX_VALUE, 120, false);
                            if(DataMgr.getPlayerData(t).getSPGauge() == 100)
                                SPWeaponMgr.setSPWeapon(t);
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
                            DataMgr.getPlayerData(target).setIsDead(false);
                            Location loc = DataMgr.getPlayerData(t).getMatchLocation();
                            t.teleport(loc);
                            t.setGameMode(GameMode.ADVENTURE);
                            t.getInventory().setItem(0, DataMgr.getPlayerData(t).getWeaponClass().getMainWeapon().getWeaponIteamStack());
                            t.getWorld().playSound(DataMgr.getPlayerData(t).getMatchLocation(), Sound.ENTITY_PLAYER_SWIM, 1, 1);
                            t.setExp(0.99F);
                            t.setHealth(20);
                            WeaponClassMgr.setWeaponClass(t);
                            SuperArmor.setArmor(t, Double.MAX_VALUE, 120, false);
                            if(DataMgr.getPlayerData(t).getSPGauge() == 100)
                                SPWeaponMgr.setSPWeapon(t);
                            cancel();
                        }
                    }

                    if(type.equals("spWeapon")){
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
                        String msg = sdata.getTeam().getTeamColor().getColorCode() + s.getDisplayName() + ChatColor.RESET + "に" + ChatColor.BOLD + sdata.getWeaponClass().getSPWeaponName() + ChatColor.RESET + "でやられた！";
                        if(i == 0){
                            t.sendTitle(ChatColor.GREEN + "復活まであと: 5秒", msg, 0, 21, 0);
                            for(Player player : Main.getPlugin(Main.class).getServer().getOnlinePlayers()){
                                player.sendMessage(sdata.getTeam().getTeamColor().getColorCode() + s.getDisplayName() + ChatColor.RESET + "が" + DataMgr.getPlayerData(t).getTeam().getTeamColor().getColorCode() + t.getDisplayName() + ChatColor.RESET + "を" + ChatColor.BOLD + sdata.getWeaponClass().getSPWeaponName() + ChatColor.RESET + "で倒した！");
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
                            DataMgr.getPlayerData(target).setIsDead(false);
                            Location loc = DataMgr.getPlayerData(t).getMatchLocation();
                            t.teleport(loc);
                            t.setGameMode(GameMode.ADVENTURE);
                            t.getInventory().setItem(0, DataMgr.getPlayerData(t).getWeaponClass().getMainWeapon().getWeaponIteamStack());
                            t.getWorld().playSound(DataMgr.getPlayerData(t).getMatchLocation(), Sound.ENTITY_PLAYER_SWIM, 1, 1);
                            t.setExp(0.99F);
                            t.setHealth(20);
                            WeaponClassMgr.setWeaponClass(t);
                            SuperArmor.setArmor(t, Double.MAX_VALUE, 120, false);
                            if(DataMgr.getPlayerData(t).getSPGauge() == 100)
                                SPWeaponMgr.setSPWeapon(t);
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
                            DataMgr.getPlayerData(target).setIsDead(false);
                            Location loc1 = DataMgr.getPlayerData(t).getMatchLocation();
                            t.teleport(loc1);
                            t.setGameMode(GameMode.ADVENTURE);
                            t.getInventory().setItem(0, DataMgr.getPlayerData(t).getWeaponClass().getMainWeapon().getWeaponIteamStack());
                            t.getWorld().playSound(DataMgr.getPlayerData(t).getMatchLocation(), Sound.ENTITY_PLAYER_SWIM, 1, 1);
                            t.setExp(0.99F);
                            t.setHealth(20);
                            WeaponClassMgr.setWeaponClass(t);
                            SuperArmor.setArmor(t, Double.MAX_VALUE, 120, false);
                            if(DataMgr.getPlayerData(t).getSPGauge() == 100)
                                SPWeaponMgr.setSPWeapon(t);
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
                            t.sendTitle(ChatColor.GREEN + "復活まであと: 5秒", "マップの外に落ちてしまった！", 0, 21, 0);
                        if(i == 20)
                            t.sendTitle(ChatColor.GREEN + "復活まであと: 4秒", "マップの外に落ちてしまった！", 0, 21, 0);
                        if(i == 40)
                            t.sendTitle(ChatColor.GREEN + "復活まであと: 3秒", "マップの外に落ちてしまった！", 0, 21, 0);
                        if(i == 60)
                            t.sendTitle(ChatColor.GREEN + "復活まであと: 2秒", "マップの外に落ちてしまった！", 0, 21, 0);
                        if(i == 80)
                            t.sendTitle(ChatColor.GREEN + "復活まであと: 1秒", "マップの外に落ちてしまった！", 0, 18, 2);
                        if(i == 100){
                            DataMgr.getPlayerData(target).setIsDead(false);
                            Location loc1 = DataMgr.getPlayerData(t).getMatchLocation();
                            t.teleport(loc1);
                            t.setGameMode(GameMode.ADVENTURE);
                            t.getInventory().setItem(0, DataMgr.getPlayerData(t).getWeaponClass().getMainWeapon().getWeaponIteamStack());
                            t.getWorld().playSound(DataMgr.getPlayerData(t).getMatchLocation(), Sound.ENTITY_PLAYER_SWIM, 1, 1);
                            t.setExp(0.99F);
                            t.setHealth(20);
                            WeaponClassMgr.setWeaponClass(t);
                            SuperArmor.setArmor(t, Double.MAX_VALUE, 120, false);
                            if(DataMgr.getPlayerData(t).getSPGauge() == 100)
                                SPWeaponMgr.setSPWeapon(t);
                            cancel();
                        }
                    }

                    if(i == 100)
                        target.getInventory().setHeldItemSlot(1);

                    i++;
                }catch(Exception e){
                    DataMgr.getPlayerData(target).setIsDead(false);
                    Location loc1 = DataMgr.getPlayerData(t).getMatchLocation();
                    t.teleport(loc1);
                    t.setGameMode(GameMode.ADVENTURE);
                    t.getInventory().setItem(0, DataMgr.getPlayerData(t).getWeaponClass().getMainWeapon().getWeaponIteamStack());
                    t.getWorld().playSound(DataMgr.getPlayerData(t).getMatchLocation(), Sound.ENTITY_PLAYER_SWIM, 1, 1);
                    t.setExp(0.99F);
                    t.setHealth(20);
                    WeaponClassMgr.setWeaponClass(t);
                    SuperArmor.setArmor(t, Double.MAX_VALUE, 120, false);
                    if(DataMgr.getPlayerData(t).getSPGauge() == 100)
                        SPWeaponMgr.setSPWeapon(t);
                    cancel();
                    Main.getPlugin().getLogger().warning(e.getMessage());
                }
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 1);
    }
}
