
package be4rjp.sclat.data;

import com.xxmicloxx.NoteBlockAPI.model.Song;

/**
 *
 * @author Be4rJP
 */
public class NoteBlockSong {
    private String songname;
    private Song song;
    
    public NoteBlockSong(String songname, Song song){this.songname = songname; this.song = song;}
    
    
    public String getSongName(){return this.songname;}
    
    public Song getSong(){return this.song;}
}
