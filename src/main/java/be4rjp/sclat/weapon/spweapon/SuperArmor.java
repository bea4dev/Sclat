
package be4rjp.sclat.weapon.spweapon;

import be4rjp.sclat.Main;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import org.bukkit.GameMode;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Be4rJP
 */
public class SuperArmor {
    public static void setArmor(Player player, double armor, long delay, boolean effect){
        
        PlayerData data = DataMgr.getPlayerData(player);
        data.setArmor(armor);
        
        //エフェクト
        BukkitRunnable effect_r = new BukkitRunnable(){
            @Override
            public void run(){
                if(!data.isInMatch() || !player.getGameMode().equals(GameMode.ADVENTURE))
                    cancel();
                for (Player o_player : Main.getPlugin().getServer().getOnlinePlayers()) {
                    if(DataMgr.getPlayerData(o_player).getSettings().ShowEffect_Shooter() && !o_player.equals(player)){
                        Particle.DustOptions dustOptions = new Particle.DustOptions(data.getTeam().getTeamColor().getBukkitColor(), 1);
                        o_player.spawnParticle(Particle.REDSTONE, player.getEyeLocation(), 5, 0.5, 0.4, 0.5, 5, dustOptions);
                    }
                }
                if(data.getArmor() <= 0){
                    player.playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 3F, 1.8F);
                    player.sendMessage("§c§l！ アーマーが破壊された ！");
                    cancel();
                }
            }
        };
        if(effect)
            effect_r.runTaskTimer(Main.getPlugin(), 0, 1);
        
        BukkitRunnable task = new BukkitRunnable(){
            @Override
            public void run(){
                data.setArmor(0);
                effect_r.cancel();
                player.playSound(player.getLocation(), Sound.BLOCK_CHEST_CLOSE, 1, 2);
            }
        };
        task.runTaskLater(Main.getPlugin(), delay);
        
    }  
}
