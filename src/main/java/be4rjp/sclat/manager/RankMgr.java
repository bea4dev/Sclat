
package be4rjp.sclat.manager;

/**
 *
 * @author Be4rJP
 */
public class RankMgr {
    public static String toABCRank(int ir){
        String rank = "UnRanked";
        if(ir >= 0 && ir <= 400)
            rank = "C-";
        if(ir >= 401 && ir <= 800)
            rank = "C";
        if(ir >= 801 && ir <= 1200)
            rank = "C";
        if(ir >= 1201 && ir <= 1600)
            rank = "C+";
        if(ir >= 1601 && ir <= 2000)
            rank = "B-";
        if(ir >= 2001 && ir <= 2400)
            rank = "B";
        if(ir >= 2401 && ir <= 2800)
            rank = "B+";
        if(ir >= 2801 && ir <= 3200)
            rank = "A-";
        if(ir >= 3201 && ir <= 3600)
            rank = "A";
        if(ir >= 3601 && ir <= 4000)
            rank = "A+";
        if(ir >= 4001 && ir <= 4400)
            rank = "S";
        if(ir >= 4401)
            rank = "X";
        return rank;
    }
}
