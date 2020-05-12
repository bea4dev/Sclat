
package be4rjp.sclat.manager;

import be4rjp.sclat.GaugeAPI;
import be4rjp.sclat.Main;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import be4rjp.sclat.weapon.spweapon.AirStrike;
import be4rjp.sclat.weapon.spweapon.Amehurasi;
import be4rjp.sclat.weapon.spweapon.Barrier;
import be4rjp.sclat.weapon.spweapon.BombRush;
import be4rjp.sclat.weapon.spweapon.MultiMissile;
import be4rjp.sclat.weapon.spweapon.SuperArmor;
import be4rjp.sclat.weapon.spweapon.SuperSensor;
import be4rjp.sclat.weapon.subweapon.QuickBomb;
import be4rjp.sclat.weapon.subweapon.SplashBomb;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Be4rJP
 */
public class SPWeaponMgr {
    
    public static void addSPCharge(Player player){
        PlayerData data = DataMgr.getPlayerData(player);
        if(data.getSPGauge() < 100)
            data.addSPGauge();
    }
    
    public static void resetSPCharge(Player player){
        PlayerData data = DataMgr.getPlayerData(player);
        if(data.getSPGauge() > 20)
            data.setSPGauge(20);
    }
    
    public static String getSPGauge(Player player){
        PlayerData data = DataMgr.getPlayerData(player);
        if(data.getSPGauge() == 100)
            return "§b§n! READY !";
        return GaugeAPI.toGauge(data.getSPGauge() / 5, 20, "§a", "§7");
    }
    
    public static void SPWeaponRunnable(Player player){
        BukkitRunnable task = new BukkitRunnable(){
            Player p = player;
            
            @Override
            public void run(){
                PlayerData data = DataMgr.getPlayerData(p);
                if(data.getSPGauge() == 100){
                    if(!DataMgr.getPlayerData(p).getIsSP()){
                        setSPWeapon(p);
                        p.playSound(p.getLocation(), Sound.BLOCK_CHEST_OPEN, 0.8F, 2);
                        p.sendMessage("§6§l！ スペシャルウエポン使用可能 ！");
                    }
                    DataMgr.getPlayerData(p).setIsSP(true);
                }else{
                    if(!data.getWeaponClass().getSPWeaponName().equals("インクストライク"))
                        p.getInventory().setItem(4, new ItemStack(Material.AIR));
                    DataMgr.getPlayerData(p).setIsSP(false);
                }
                if(!DataMgr.getPlayerData(p).isInMatch())
                    cancel();
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 5);
        
        BossBar bar = Main.getPlugin().getServer().createBossBar("§6§lSPGauge", BarColor.GREEN, BarStyle.SOLID, BarFlag.CREATE_FOG);
        bar.setProgress(0);
        bar.addPlayer(player);
        
        BukkitRunnable anime = new BukkitRunnable(){
            Player p = player;
            @Override
            public void run(){
                bar.setProgress((double)(DataMgr.getPlayerData(p).getSPGauge()) / 100D);
                if(DataMgr.getPlayerData(p).getSPGauge() == 100)
                    bar.setTitle("§b§lREADY");
                else if(DataMgr.getPlayerData(p).getIsUsingSP())
                    bar.setTitle("§6§lIn Use...");
                else
                    bar.setTitle("§6§lSPGauge : §r" + String.valueOf(DataMgr.getPlayerData(p).getSPGauge() + "%"));
                if(!DataMgr.getPlayerData(p).isInMatch()){
                    DataMgr.getPlayerData(p).setIsUsingSP(false);
                    bar.removeAll();
                    cancel();
                }
            }
        };
        anime.runTaskTimer(Main.getPlugin(), 0, 1);
    }
    
    public static void setSPCoolTimeAnimation(Player player, int tick){
        BukkitRunnable task = new BukkitRunnable(){
            Player p = player;
            double t = tick;
            double i = tick;
            @Override
            public void run(){
                if(t == tick)
                    DataMgr.getPlayerData(p).setIsUsingSP(true);
                t--;
                int sp = (int)(t / i * 100);
                DataMgr.getPlayerData(p).setSPGauge(sp);
                if(t <= 0){
                    DataMgr.getPlayerData(p).setIsUsingSP(false);
                    cancel();
                }
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 1);
    }
    
    public static void setSPWeapon(Player p){
        PlayerData data = DataMgr.getPlayerData(p);
        switch (data.getWeaponClass().getSPWeaponName()){
            case "インクアーマー":
                ItemStack is = new ItemStack(Material.TOTEM_OF_UNDYING);
                ItemMeta ism = is.getItemMeta();
                ism.setDisplayName("インクアーマー");
                is.setItemMeta(ism);
                p.getInventory().setItem(4, is); 
                break;
            case "バリア":
                ItemStack b = new ItemStack(Material.END_CRYSTAL);
                ItemMeta bm = b.getItemMeta();
                bm.setDisplayName("バリア");
                b.setItemMeta(bm);
                p.getInventory().setItem(4, b); 
                break;
            case "ボムラッシュ":
                ItemStack is1 = new ItemStack(Material.FEATHER);
                ItemMeta ism1 = is1.getItemMeta();
                ism1.setDisplayName("ボムラッシュ");
                is1.setItemMeta(ism1);
                p.getInventory().setItem(4, is1);
                break;
            case "スーパーセンサー":
                ItemStack is2 = new ItemStack(Material.NETHER_STAR);
                ItemMeta ism2 = is2.getItemMeta();
                ism2.setDisplayName("スーパーセンサー");
                is2.setItemMeta(ism2);
                p.getInventory().setItem(4, is2);
                break;
            case "インクストライク":
                ItemStack is3 = new ItemStack(Material.ARROW);
                ItemMeta ism3 = is3.getItemMeta();
                ism3.setDisplayName("インクストライク");
                is3.setItemMeta(ism3);
                p.getInventory().setItem(4, is3);
                break;
            case "アメフラシ":
                ItemStack is4 = new ItemStack(Material.BEACON);
                ItemMeta ism4 = is4.getItemMeta();
                ism4.setDisplayName("アメフラシ");
                is4.setItemMeta(ism4);
                p.getInventory().setItem(4, is4);
                break;
            case "マルチミサイル":
                ItemStack is5 = new ItemStack(Material.PRISMARINE_SHARD);
                ItemMeta ism5 = is5.getItemMeta();
                ism5.setDisplayName("マルチミサイル");
                is5.setItemMeta(ism5);
                p.getInventory().setItem(4, is5);
                break;
        }
    }
    
    public static void UseSPWeapon(Player player, String name){
        PlayerData data = DataMgr.getPlayerData(player);
        switch (name) {
            case "インクアーマー":
                SuperArmor.setArmor(player, 20, 160, true);
                for (Player op : Main.getPlugin().getServer().getOnlinePlayers()) {
                    if(player != op && DataMgr.getPlayerData(player).getTeam() == DataMgr.getPlayerData(op).getTeam()){
                        SuperArmor.setArmor(op, 10, 80, true);
                    }
                }
                player.getInventory().setItem(4, new ItemStack(Material.AIR));
                player.setExp(0.99F);
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 2);
                break;
            case "バリア":
                Barrier.BarrierRunnable(player);
                player.getInventory().setItem(4, new ItemStack(Material.AIR));
                player.setExp(0.99F);
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 2);
                break;
            case "ボムラッシュ":
                BombRush.BombRushRunnable(player);
                player.getInventory().setItem(4, new ItemStack(Material.AIR));
                player.setExp(0.99F);
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 2);
                break;
            case "スーパーセンサー":
                SuperSensor.SuperSensorRunnable(player);
                player.getInventory().setItem(4, new ItemStack(Material.AIR));
                player.setExp(0.99F);
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 2);
                break;
            case "インクストライク":
                MapKitMgr.setMapKit(player);
                player.setExp(0.99F);
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 2);
                break;
            case "アメフラシ":
                Amehurasi.AmehurasiDropRunnable(player);
                player.getInventory().setItem(4, new ItemStack(Material.AIR));
                player.setExp(0.99F);
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 2);
                break;
            case "マルチミサイル":
                MultiMissile.MMLockRunnable(player);
                player.getInventory().setItem(4, new ItemStack(Material.AIR));
                player.setExp(0.99F);
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 2);
                break;
            case "カーソルを合わせて右クリックで発射":
                AirStrike.AirStrikeRunnable(player);
                break;
            case "プレイヤーを狙って右クリックで発射":
                DataMgr.getPlayerData(player).setIsUsingMM(false);
                break;
        }
    }
}
