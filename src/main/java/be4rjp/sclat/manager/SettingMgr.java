package be4rjp.sclat.manager;

import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerSettings;
import org.bukkit.entity.Player;

import static be4rjp.sclat.Main.conf;

public class SettingMgr {
    
    public static void setSettings(PlayerSettings settings, Player player){
        String uuid = player.getUniqueId().toString();
        String def = "011111111";
        if(conf.getPlayerSettings().contains("Settings." + uuid)){
            if(conf.getPlayerSettings().getString("Settings." + uuid).substring(1,2).equals("0"))
                settings.S_ShowEffect_MainWeaponInk();
            if(conf.getPlayerSettings().getString("Settings." + uuid).substring(2,3).equals("0"))
                settings.S_ShowEffect_ChargerLine();
            if(conf.getPlayerSettings().getString("Settings." + uuid).substring(3,4).equals("0"))
                settings.S_ShowEffect_SPWeapon();
            if(conf.getPlayerSettings().getString("Settings." + uuid).substring(4,5).equals("0"))
                settings.S_ShowEffect_SPWeaponRegion();
            if(conf.getPlayerSettings().getString("Settings." + uuid).substring(5,6).equals("0"))
                settings.S_ShowSnowBall();
            if(conf.getPlayerSettings().getString("Settings." + uuid).substring(0,1).equals("0"))
                settings.S_PlayBGM();
            if(conf.getPlayerSettings().getString("Settings." + uuid).substring(6,7).equals("0"))
                settings.S_ShowEffect_Bomb();
            if(conf.getPlayerSettings().getString("Settings." + uuid).substring(7,8).equals("0"))
                settings.S_ShowEffect_BombEx();
            if(conf.getPlayerSettings().getString("Settings." + uuid).substring(8,9).equals("0"))
                settings.S_doChargeKeep();
        }else{
            conf.getPlayerSettings().set("Settings." + uuid, def);
            settings.S_PlayBGM();
        }
        DataMgr.getPlayerData(player).setSettings(settings);
    }
}
