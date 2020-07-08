
package be4rjp.sclat.data;

import org.bukkit.entity.Player;

/**
 *
 * @author Be4rJP
 */
public class PlayerSettings {
    
    private Player player;
    
    private boolean ShowEffect_Shooter = true;
    private boolean ShowEffect_ChargerLine = true;
    private boolean ShowEffect_ChargerShot = true;
    private boolean ShowEffect_RollerRoll = true;
    private boolean ShowEffect_RollerShot = true;
    private boolean ShowEffect_Squid = true;
    private boolean ShowEffect_BombEx = true;
    private boolean ShowEffect_Bomb = true;
    private boolean PlayBGM = true;
    private boolean doChargeKeep = true;
    
    public PlayerSettings (Player player){this.player = player;}
    
    public Player getPlayer(){return this.player;}
    
    public boolean ShowEffect_Shooter(){return this.ShowEffect_Shooter;}
    public boolean ShowEffect_ChargerLine(){return this.ShowEffect_ChargerLine;}
    public boolean ShowEffect_ChargerShot(){return this.ShowEffect_ChargerShot;}
    public boolean ShowEffect_RollerRoll(){return this.ShowEffect_RollerRoll;}
    public boolean ShowEffect_RollerShot(){return this.ShowEffect_RollerShot;}
    public boolean ShowEffect_Squid(){return this.ShowEffect_Squid;}
    public boolean ShowEffect_BombEx(){return this.ShowEffect_BombEx;}
    public boolean ShowEffect_Bomb(){return this.ShowEffect_Bomb;}
    public boolean PlayBGM(){return this.PlayBGM;}
    public boolean doChargeKeep(){return this.doChargeKeep;}
    
    public void S_ShowEffect_Shooter(){this.ShowEffect_Shooter = !ShowEffect_Shooter;}
    public void S_ShowEffect_ChargerLine(){this.ShowEffect_ChargerLine = !ShowEffect_ChargerLine;}
    public void S_ShowEffect_ChargerShot(){this.ShowEffect_ChargerShot = !ShowEffect_ChargerShot;}
    public void S_ShowEffect_RollerRoll(){this.ShowEffect_RollerRoll = !ShowEffect_RollerRoll;}
    public void S_ShowEffect_RollerShot(){this.ShowEffect_RollerShot = !ShowEffect_RollerShot;}
    public void S_ShowEffect_Squid(){this.ShowEffect_Squid = !ShowEffect_Squid;}
    public void S_ShowEffect_BombEx(){this.ShowEffect_BombEx = !ShowEffect_BombEx;}
    public void S_ShowEffect_Bomb(){this.ShowEffect_Bomb = !ShowEffect_Bomb;}
    public void S_PlayBGM(){this.PlayBGM = !PlayBGM;}
    public void S_doChargeKeep(){this.doChargeKeep = !doChargeKeep;}
}
