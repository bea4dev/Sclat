package be4rjp.sclat.manager;

import be4rjp.sclat.Main;
import static be4rjp.sclat.Main.conf;
import be4rjp.sclat.data.Color;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.MapData;
import static org.bukkit.Bukkit.getServer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;

/**
 *
 * @author Be4rJP
 */
public class MapDataMgr {
    public synchronized static void SetupMap(){
        for (String mapname : conf.getMapConfig().getConfigurationSection("Maps").getKeys(false)){
            MapData map = new MapData(mapname);
            String WorldName = conf.getMapConfig().getString("Maps." + mapname + ".WorldName");
            World w = getServer().getWorld(WorldName);
            
            int ix = conf.getMapConfig().getInt("Maps." + mapname + ".Intro.X");
            int iy = conf.getMapConfig().getInt("Maps." + mapname + ".Intro.Y");
            int iz = conf.getMapConfig().getInt("Maps." + mapname + ".Intro.Z");
            int iyaw = conf.getMapConfig().getInt("Maps." + mapname + ".Intro.Yaw");
            Location il = new Location(w, ix, iy, iz);
            il.setYaw(iyaw);
            
            double intromovex = conf.getMapConfig().getDouble("Maps." + mapname + ".Intro.MoveX");
            double intromovey = conf.getMapConfig().getDouble("Maps." + mapname + ".Intro.MoveY");
            double intromovez = conf.getMapConfig().getDouble("Maps." + mapname + ".Intro.MoveZ");
            
            int t0x = conf.getMapConfig().getInt("Maps." + mapname + ".Team0.X");
            int t0y = conf.getMapConfig().getInt("Maps." + mapname + ".Team0.Y");
            int t0z = conf.getMapConfig().getInt("Maps." + mapname + ".Team0.Z");
            int t0yaw = conf.getMapConfig().getInt("Maps." + mapname + ".Team0.Yaw");
            Location t0l = new Location(w, t0x, t0y, t0z);
            t0l.setX(t0l.getX() + 0.5);
            t0l.setZ(t0l.getZ() + 0.5);
            t0l.setYaw(t0yaw);
            
            int t1x = conf.getMapConfig().getInt("Maps." + mapname + ".Team1.X");
            int t1y = conf.getMapConfig().getInt("Maps." + mapname + ".Team1.Y");
            int t1z = conf.getMapConfig().getInt("Maps." + mapname + ".Team1.Z");
            int t1yaw = conf.getMapConfig().getInt("Maps." + mapname + ".Team1.Yaw");
            Location t1l = new Location(w, t1x, t1y, t1z);
            t1l.setX(t1l.getX() + 0.5);
            t1l.setZ(t1l.getZ() + 0.5);
            t1l.setYaw(t1yaw);
            
            int t0intx = conf.getMapConfig().getInt("Maps." + mapname + ".Team0IntroLoc.X");
            int t0inty = conf.getMapConfig().getInt("Maps." + mapname + ".Team0IntroLoc.Y");
            int t0intz = conf.getMapConfig().getInt("Maps." + mapname + ".Team0IntroLoc.Z");
            int t0intyaw = conf.getMapConfig().getInt("Maps." + mapname + ".Team0IntroLoc.Yaw");
            Location t0intl = new Location(w, t0intx, t0inty, t0intz);
            t0intl.setYaw(t0intyaw);
            
            int t1intx = conf.getMapConfig().getInt("Maps." + mapname + ".Team1IntroLoc.X");
            int t1inty = conf.getMapConfig().getInt("Maps." + mapname + ".Team1IntroLoc.Y");
            int t1intz = conf.getMapConfig().getInt("Maps." + mapname + ".Team1IntroLoc.Z");
            int t1intyaw = conf.getMapConfig().getInt("Maps." + mapname + ".Team1IntroLoc.Yaw");
            Location t1intl = new Location(w, t1intx, t1inty, t1intz);
            t1intl.setYaw(t1intyaw);
            
            int rlocx = conf.getMapConfig().getInt("Maps." + mapname + ".ResultLoc.X");
            int rlocy = conf.getMapConfig().getInt("Maps." + mapname + ".ResultLoc.Y");
            int rlocz = conf.getMapConfig().getInt("Maps." + mapname + ".ResultLoc.Z");
            int rlocyaw = conf.getMapConfig().getInt("Maps." + mapname + ".ResultLoc.Yaw");
            Location rloc = new Location(w, rlocx, rlocy, rlocz);
            rloc.setYaw(rlocyaw);
            rloc.setPitch(90);
            
            
            map.setIntro(il);
            map.setTeam0Loc(t0l);
            map.setTeam1Loc(t1l);
            map.setTeam0Intro(t0intl);
            map.setTeam1Intro(t1intl);
            map.setResultLoc(rloc);
            
            map.setIntroMoveX(intromovex);
            map.setIntroMoveY(intromovey);
            map.setIntroMoveZ(intromovez);
            
            //Main.getPlugin().getServer().createWorld(new WorldCreator(WorldName));
            
            Main.getPlugin().getLogger().info(mapname);
            
            map.setWorldName(WorldName);
            
            DataMgr.setMap(mapname, map);
            DataMgr.addMapList(map);
        }
    }
}
