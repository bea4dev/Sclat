
package be4rjp.sclat.data;

import org.bukkit.entity.Player;

/**
 *
 * @author Be4rJP
 */
public class PlayerSettings {
    
    private Player player;
    
    private boolean ShowEffect_MainWeaponInk = true;
    private boolean ShowEffect_ChargerLine = true;
    private boolean ShowEffect_SPWeapon = true;
    private boolean ShowEffect_SPWeaponRegion = true;
    private boolean ShowSnowBall = true;
    private boolean ShowEffect_Squid = true;
    private boolean ShowEffect_BombEx = true;
    private boolean ShowEffect_Bomb = true;
    private boolean PlayBGM = true;
    private boolean doChargeKeep = true;
    
    public PlayerSettings (Player player){this.player = player;}
    
    public Player getPlayer(){return this.player;}
    
    public boolean ShowEffect_MainWeaponInk(){return this.ShowEffect_MainWeaponInk;}
    public boolean ShowEffect_ChargerLine(){return this.ShowEffect_ChargerLine;}
    public boolean ShowEffect_SPWeapon(){return this.ShowEffect_SPWeapon;}
    public boolean ShowEffect_SPWeaponRegion(){return this.ShowEffect_SPWeaponRegion;}
    public boolean ShowSnowBall(){return this.ShowSnowBall;}
    public boolean ShowEffect_Squid(){return this.ShowEffect_Squid;}
    public boolean ShowEffect_BombEx(){return this.ShowEffect_BombEx;}
    public boolean ShowEffect_Bomb(){return this.ShowEffect_Bomb;}
    public boolean PlayBGM(){return this.PlayBGM;}
    public boolean doChargeKeep(){return this.doChargeKeep;}
    
    public void S_ShowEffect_MainWeaponInk(){this.ShowEffect_MainWeaponInk = !ShowEffect_MainWeaponInk;}
    public void S_ShowEffect_ChargerLine(){this.ShowEffect_ChargerLine = !ShowEffect_ChargerLine;}
    public void S_ShowEffect_SPWeapon(){this.ShowEffect_SPWeapon = !ShowEffect_SPWeapon;}
    public void S_ShowEffect_SPWeaponRegion(){this.ShowEffect_SPWeaponRegion = !ShowEffect_SPWeaponRegion;}
    public void S_ShowSnowBall(){this.ShowSnowBall = !ShowSnowBall;}
    public void S_ShowEffect_Squid(){this.ShowEffect_Squid = !ShowEffect_Squid;}
    public void S_ShowEffect_BombEx(){this.ShowEffect_BombEx = !ShowEffect_BombEx;}
    public void S_ShowEffect_Bomb(){this.ShowEffect_Bomb = !ShowEffect_Bomb;}
    public void S_PlayBGM(){this.PlayBGM = !PlayBGM;}
    public void S_doChargeKeep(){this.doChargeKeep = !doChargeKeep;}
}
