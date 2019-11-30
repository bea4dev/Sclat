
package be4rjp.sclat.data;

import org.bukkit.entity.Player;

/**
 *
 * @author Be4rJP
 */
public class PlayerSettings {
    
    private Player player;
    
    private boolean ShowEffect_Sooter = true;
    private boolean ShowEffect_ChargerLine = true;
    private boolean ShowEffect_ChargerShot = true;
    private boolean ShowEffect_RollerRoll = true;
    private boolean ShowEffect_RollerShot = true;
    private boolean ShowEffect_Squid = true;
    
    public PlayerSettings (Player player){this.player = player;}
    
    public Player getPlayer(){return this.player;}
    
    public boolean ShowEffect_Sooter(){return this.ShowEffect_Sooter;}
    public boolean ShowEffect_ChargerLine(){return this.ShowEffect_ChargerLine;}
    public boolean ShowEffect_ChargerShot(){return this.ShowEffect_ChargerShot;}
    public boolean ShowEffect_RollerRoll(){return this.ShowEffect_RollerRoll;}
    public boolean ShowEffect_RollerShot(){return this.ShowEffect_RollerShot;}
    public boolean ShowEffect_Squid(){return this.ShowEffect_Squid;}
    
    public void S_ShowEffect_Sooter(){this.ShowEffect_Sooter = !ShowEffect_Sooter;}
    public void S_ShowEffect_ChargerLine(){this.ShowEffect_ChargerLine = !ShowEffect_ChargerLine;}
    public void S_ShowEffect_ChargerShot(){this.ShowEffect_ChargerShot = !ShowEffect_ChargerShot;}
    public void S_ShowEffect_RollerRoll(){this.ShowEffect_RollerRoll = !ShowEffect_RollerRoll;}
    public void S_ShowEffect_RollerShot(){this.ShowEffect_RollerShot = !ShowEffect_RollerShot;}
    public void S_ShowEffect_Squid(){this.ShowEffect_Squid = !ShowEffect_Squid;}

    
}
