
package be4rjp.sclat.manager;

import org.bukkit.util.Vector;

/**
 *
 * @author Be4rJP
 */
public class VectorMgr {
    public static Vector GravityVector(Vector from, Vector to, int heightGain)
    {
        double gravity = 0.139;

        int endGain = to.getBlockY() - from.getBlockY();
        double horizDist = Math.sqrt(distance(from, to));

        int gain = heightGain;
 
        double maxGain = gain > (endGain + gain) ? gain : (endGain + gain);

        double a = -horizDist * horizDist / (4 * maxGain);
        double b = horizDist;
        double c = -endGain;
 
        double slope = -b / (2 * a) - Math.sqrt(b * b - 4 * a * c) / (2 * a);
        double vy = Math.sqrt(maxGain * gravity);
        double vh = vy / slope;
 
        int dx = to.getBlockX() - from.getBlockX();
        int dz = to.getBlockZ() - from.getBlockZ();
        double mag = Math.sqrt(dx * dx + dz * dz);
        double dirx = dx / mag;
        double dirz = dz / mag;
 
        double vx = vh * dirx;
        double vz = vh * dirz;
 
        return new Vector(vx, vy, vz);
    }
 
    private static double distance(Vector from, Vector to)
    {
        double dx = to.getBlockX() - from.getBlockX();
        double dz = to.getBlockZ() - from.getBlockZ();
 
        return dx * dx + dz * dz;
    }
}
