
package be4rjp.sclat.manager;

import static be4rjp.sclat.Main.conf;
import be4rjp.sclat.data.NoteBlockSong;
import be4rjp.sclat.data.PlayerData;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.bukkit.entity.Player;

/**
 *
 * @author Be4rJP
 */
public class NoteBlockAPIMgr {
    private static int nBgm_C = 0;
    private static int fBgm_C = 0;
    //private static byte volume = 22;
    private static List<Song> nsList = new ArrayList<>();
    private static List<String> nsnList = new ArrayList<>();
    private static List<Song> fsList = new ArrayList<>();
    private static List<String> fsnList = new ArrayList<>();
    
    public static void LoadSongFiles(){
        for (String songname : conf.getConfig().getConfigurationSection("nBGM").getKeys(false)){
            Song song = NBSDecoder.parse(new File("plugins/Sclat/BGM", conf.getConfig().getString("nBGM." + songname)));
            nsList.add(song);
            nsnList.add(songname);
            nBgm_C++;
        }
        
        for (String songname : conf.getConfig().getConfigurationSection("fBGM").getKeys(false)){
            Song song = NBSDecoder.parse(new File("plugins/Sclat/BGM", conf.getConfig().getString("fBGM." + songname)));
            fsList.add(song);
            fsnList.add(songname);
            fBgm_C++;
        }
    }
    
    public static NoteBlockSong getRandomNormalSong(){
        int random = new Random().nextInt(nBgm_C);
        String songname = nsnList.get(random);
        Song song = nsList.get(random);
        NoteBlockSong nbs = new NoteBlockSong(songname, song);
        return nbs;
    }
    
    public static NoteBlockSong getRandomFinalSong(){
        int random = new Random().nextInt(fBgm_C);
        String songname = fsnList.get(random);
        Song song = fsList.get(random);
        NoteBlockSong nbs = new NoteBlockSong(songname, song);
        return nbs;
    }
    
}
