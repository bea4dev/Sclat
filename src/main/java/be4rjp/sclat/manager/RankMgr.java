
package be4rjp.sclat.manager;

/**
 *
 * @author Be4rJP
 */
public class RankMgr {

    private static final String[] ranks = {"C-", "C", "C+", "B-", "B",
                                        "B+", "A-", "A", "A+", "S", "S+"};

    //レートを500単位で区切ってランク付けする
    public static String toABCRank(int ir){
        int MaxRate = (ranks.length - 1) * 500;
        return ir >= 0 ? ranks[ir <= MaxRate ? ir / 500 : ranks.length - 1] : "UnRanked";
    }
}
