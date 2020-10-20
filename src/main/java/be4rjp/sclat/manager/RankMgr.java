
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
        return ir >= 0 ? ranks[ir / 500] : "UnRanked";
    }
}
