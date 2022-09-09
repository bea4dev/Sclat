
package be4rjp.sclat.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;

/**
 *
 * @author Be4rJP
 */
public class DataMgr {
    private static Map<Player, PlayerData> playerdata = new ConcurrentHashMap<>();
    private static Map<String, PlayerData> uuiddata = new HashMap<>();
    private static Map<Integer, Match> matchdata = new HashMap<>();
    private static Map<Integer, Team> teamdata = new HashMap<>();
    private static Map<String, Color> colordata = new HashMap<>();  
    private static Map<String, WeaponClass> weaponclassdata = new HashMap<>();  
    private static Map<String, MainWeapon> weapondata = new HashMap<>();  
    private static Map<String, MapData> mapdata = new HashMap<>(); 
    private static Map<MapData, TeamLoc> locdata = new HashMap<>(); 
    private static Map<Block, PaintData> blockdata = new HashMap<>(); 
    private static Map<String, Boolean> playerquit = new HashMap<>(); 
    private static Map<ArmorStand, Player> armorstand = new HashMap<>();
    private static Map<Player, ArmorStand> beacon = new HashMap<>();
    private static Map<Player, ArmorStand> sprinkler = new HashMap<>();
    private static Map<Projectile, Boolean> snowball = new HashMap<>();
    private static Map<Projectile, Boolean> msnowball = new HashMap<>();
    private static Map<Block, Sponge> spongemap = new HashMap<>();
    private static Map<String, Snowball> sb = new HashMap<>();
    private static Map<String, Snowball> msb = new HashMap<>();
    private static Map<String, Integer> msbn = new HashMap<>();
    private static Map<Player, SplashShieldData> sspm = new HashMap<>();
    private static Map<ArmorStand, SplashShieldData> ssam = new HashMap<>();
    private static Map<Player, KasaData> kpm = new HashMap<>();
    private static Map<ArmorStand, KasaData> kam = new HashMap<>();
    private static Map<Player, RankingHolograms> rankingHologramsMap = new HashMap<>();
    //private static Map<Match, PaintData> paintdata = new HashMap<>(); 
    private static List<Color> list = new ArrayList<>();
    public static Map<String, Integer> oto = new HashMap<>();
    public static List<Block> rblist = new ArrayList<>();
    public static List<ArmorStand> al = new ArrayList<>();
    public static List<ArmorStand> ssa = new ArrayList<>();
    public static List<String> mws = new ArrayList<>();
    public static List<String> pul = new ArrayList<>();
    public static List<String> tsl = new ArrayList<>();
    public static List<MapData> maplist = new ArrayList<>();
    public static List<String> uuids = new ArrayList<>();
    public static List<Player> joinedList = new ArrayList<>();
    
    public static PlayerData getPlayerData(Player player){return playerdata.get(player);}
    public static PlayerData getUUIDData(String uuid){return uuiddata.get(uuid);}
    public static Match getMatchFromId(int id){return matchdata.get(id);}
    public static Team getTeamFromId(int id){return teamdata.get(id);}
    public static Color getColor(String name){return colordata.get(name);}
    public static WeaponClass getWeaponClass(String weaponclass){return weaponclassdata.get(weaponclass);}
    public static MainWeapon getWeapon(String name){return weapondata.get(name);}
    public static MapData getMap(String name){return mapdata.get(name);}
    public static TeamLoc getTeamLoc(MapData map){return locdata.get(map);}
    public static PaintData getPaintDataFromBlock(Block block){return blockdata.get(block);}
    public static boolean getPlayerIsQuit(String uuid){return playerquit.get(uuid);}
    public static Player getArmorStandPlayer(ArmorStand as){return armorstand.get(as);}
    public static boolean getSnowballIsHit(Projectile ball){return snowball.get(ball);}
    public static boolean getMainSnowballIsHit(Projectile ball){return msnowball.get(ball);}
    public static ArmorStand getBeaconFromplayer(Player player){return beacon.get(player);}
    public static ArmorStand getSprinklerFromplayer(Player player){return sprinkler.get(player);}
    public static Sponge getSpongeFromBlock(Block block){return spongemap.get(block);}
    public static SplashShieldData getSplashShieldDataFromPlayer(Player player){return sspm.get(player);}
    public static SplashShieldData getSplashShieldDataFromArmorStand(ArmorStand as){return ssam.get(as);}
    public static KasaData getKasaDataFromPlayer(Player player){return kpm.get(player);}
    public static KasaData getKasaDataFromArmorStand(ArmorStand as){return kam.get(as);}
    public static int getSnowballHitCount(String name){return msbn.get(name);}
    public static RankingHolograms getRankingHolograms(Player player){return rankingHologramsMap.get(player);}
    //public static PaintData getPaintDataFromMatch(Match match){return paintdata.get(match);}
    
    
    public static void setPlayerData(Player player, PlayerData data){playerdata.put(player, data);}
    public static void setUUIDData(String uuid, PlayerData data){uuiddata.put(uuid, data);}
    public static void setMatch(int id, Match match){matchdata.put(id, match);}
    public static void setColor(String name, Color color){colordata.put(name, color);}
    public static void setTeam(int id, Team team){teamdata.put(id, team);}
    public static void setWeaponClass(String WCname, WeaponClass weaponclass){weaponclassdata.put(WCname, weaponclass);}
    public static void setMainWeapon(String MWname, MainWeapon mw){weapondata.put(MWname, mw);}
    public static void setMap(String Mname, MapData map){mapdata.put(Mname, map);}
    public static void setTeamLoc(MapData map, TeamLoc loc){locdata.put(map, loc);}
    public static void setPaintDataFromBlock(Block block, PaintData data){blockdata.put(block, data);}
    public static void setPlayerIsQuit(String uuid, boolean is){playerquit.put(uuid, is);}
    public static void setArmorStandPlayer(ArmorStand as, Player player){armorstand.put(as, player);}
    public static void setSnowballIsHit(Projectile ball, boolean is){snowball.put(ball, is);}
    public static void setMainSnowballIsHit(Projectile ball, boolean is){msnowball.put(ball, is);}
    public static void setBeaconFromPlayer(Player player, ArmorStand as){beacon.put(player, as);}
    public static void setSprinklerFromPlayer(Player player, ArmorStand as){sprinkler.put(player, as);}
    public static void setSpongeWithBlock(Block block, Sponge sponge){spongemap.put(block, sponge);}
    public static void setSplashShieldDataWithPlayer(Player player, SplashShieldData data){sspm.put(player, data);}
    public static void setSplashShieldDataWithARmorStand(ArmorStand as, SplashShieldData data){ssam.put(as, data);}
    public static void setKasaDataWithPlayer(Player player, KasaData data){kpm.put(player, data);}
    public static void setKasaDataWithARmorStand(ArmorStand as, KasaData data){kam.put(as, data);}
    public static void setSnowballHitCount(String name, int coount){msbn.putIfAbsent(name, coount);}
    public static void setRankingHolograms(Player player, RankingHolograms rankingHolograms){rankingHologramsMap.put(player, rankingHolograms);}
    
    
    public static void addColorList(Color color){list.add(color);}
    public static void addPathArmorStandList(ArmorStand as){al.add(as);}
    public static void addMapList(MapData map){maplist.add(map);}
    public static void addSnowballHitCount(String name){msbn.put(name, getSnowballHitCount(name) + 1);}
    //public static void setPaintDataFromMatch(Match match, PaintData data){paintdata.put(match, data);}
    
    
    
    
    public static Map<Block, PaintData> getBlockDataMap(){return blockdata;}
    public static Map<Player, PlayerData> getPlayerDataMap(){return playerdata;}
    public static Map<String, PlayerData> getUUIDDataMap(){return uuiddata;}
    public static Map<String, Boolean> getPlayerIsQuitMap(){return playerquit;}
    public static Map<ArmorStand, Player> getArmorStandMap(){return armorstand;}
    public static Map<Projectile, Boolean> getSnowballIsHitMap(){return snowball;}
    public static Map<Projectile, Boolean> getMainSnowballIsHitMap(){return msnowball;}
    public static Map<Player, ArmorStand> getBeaconMap(){return beacon;}
    public static Map<Player, ArmorStand> getSprinklerMap(){return sprinkler;}
    public static Map<Block, Sponge> getSpongeMap(){return spongemap;}
    public static Map<String, Snowball> getSnowballNameMap(){return sb;}
    public static Map<String, Snowball> getMainSnowballNameMap(){return msb;}
    public static Map<Player, SplashShieldData> getSplashShieldDataMapWithPlayer(){return sspm;}
    public static Map<ArmorStand, SplashShieldData> getSplashShieldDataMapWithArmorStand(){return ssam;}
    public static Map<Player, KasaData> getKasaDataMapWithPlayer(){return kpm;}
    public static Map<ArmorStand, KasaData> getKAsaDataMapWithArmorStand(){return kam;}
    //public static Map<Match, PaintData> getPaintDataMap(){return paintdata;}
    
    public static Color getColorRandom(int number){
        
        Color color = list.get(number);
        color.setIsUsed(true);
        return color;   //RandomColor
    }
    
    public static void ColorShuffle(){
        Collections.shuffle(list);
    }
    
    public static void MapDataShuffle(){
        Collections.shuffle(maplist);
    }
    
    public static MapData getMapRandom(int i){
        MapData map = maplist.get(i);
        return map;   //RandomMap
    }
    
}
