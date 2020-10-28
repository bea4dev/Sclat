
package be4rjp.sclat.weapon;

import be4rjp.sclat.data.DataMgr;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 *
 * @author Be4rJP
 */
public class Gear {
    public static class Type {
        public static final int NO_GEAR = 0;
        public static final int IKA_SPEED_UP = 1;
        public static final int HITO_SPEED_UP = 2;
        public static final int MAIN_SPEC_UP = 3;
        public static final int SUB_SPEC_UP = 4;
        public static final int INK_RECOVERY_UP = 5;
        public static final int MAIN_INK_EFFICIENCY_UP = 6;
        public static final int SPECIAL_UP = 7;
        public static final int MAX_HEALTH_UP = 8;
    }
    
    public static Material getGearMaterial(int gearN){
        switch(gearN){
            case 1:
                return Material.INK_SAC;
            case 2:
                return Material.GOLDEN_BOOTS;
            case 3:
                return Material.IRON_HOE;
            case 4:
                return Material.WHITE_STAINED_GLASS;
            case 5:
                return Material.WATER_BUCKET;
            case 6:
                return Material.LAVA_BUCKET;
            case 7:
                return Material.PRISMARINE_SHARD;
            case 8:
                return Material.GOLDEN_APPLE;
            default:
                return Material.IRON_BARS;
        }
    }
    
    public static String getGearName(int gearN){
        switch(gearN){
            case 1:
                return "イカダッシュ速度アップ";
            case 2:
                return "ヒト移動速度アップ";
            case 3:
                return "メイン性能アップ";
            case 4:
                return "サブ性能アップ";
            case 5:
                return "インク回復量アップ";
            case 6:
                return "メインインク効率アップ";
            case 7:
                return "スペシャル増加量アップ";
            case 8:
                return "最大体力アップ";
            default:
                return "ギアなし";
        }
    }
    
    public static int getGearPrice(int gearN){
        switch(gearN){
            case 1:
                return 65000;
            case 2:
                return 60000;
            case 3:
                return 120000;
            case 4:
                return 55000;
            case 5:
                return 50000;
            case 6:
                return 100000;
            case 7:
                return 200000;
            case 8:
                return 110000;
            default:
                return 0;
        }
    }
    
    public static double getGearInfluence(Player player, int gearN){
        if(DataMgr.getPlayerData(player).getGearNumber() == gearN){
            switch(DataMgr.getPlayerData(player).getGearNumber()){
                case 1:
                    return 1.1;
                case 2:
                    return 1.3;
                case 3:
                    return 1.1;
                case 4:
                    return 1.2;
                case 5:
                    return 1.15;
                case 6:
                    return 1.1;
                case 7:
                    return 1.5;
                case 8:
                    return 1.2;
                default:
                    return 1;
            }
        }else
            return 1;
    }
}
