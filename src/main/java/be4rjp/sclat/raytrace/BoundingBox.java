
package be4rjp.sclat.raytrace;
import net.minecraft.server.v1_13_R1.AxisAlignedBB;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftEntity;

public class BoundingBox {

    Vector max;
    Vector min;

    public BoundingBox(Vector min, Vector max) {
        this.max = max;
        this.min = min;
    }

    

    
    public BoundingBox(Block block) {
        net.minecraft.server.v1_13_R1.BlockPosition bp = new net.minecraft.server.v1_13_R1.BlockPosition(block.getX(), block.getY(), block.getZ());
        net.minecraft.server.v1_13_R1.WorldServer world = ((org.bukkit.craftbukkit.v1_13_R1.CraftWorld) block.getWorld()).getHandle();
        net.minecraft.server.v1_13_R1.IBlockData blockData = (net.minecraft.server.v1_13_R1.IBlockData) (world.getType(bp));
        net.minecraft.server.v1_13_R1.Block blockNative = blockData.getBlock();
        net.minecraft.server.v1_13_R1.AxisAlignedBB aabb = world.getType(bp).g(world, bp).a();
        min = new Vector(aabb.a,aabb.b,aabb.c);
        max = new Vector(aabb.d,aabb.e,aabb.f);
    }

    
    public BoundingBox(Entity entity){
        AxisAlignedBB bb = ((CraftEntity) entity).getHandle().getBoundingBox();
        min = new Vector(bb.a,bb.b,bb.c);
        max = new Vector(bb.d,bb.e,bb.f);
    }

    public BoundingBox (AxisAlignedBB bb){
        min = new Vector(bb.a,bb.b,bb.c);
        max = new Vector(bb.d,bb.e,bb.f);
    }

    public Vector midPoint(){
        return max.clone().add(min).multiply(0.5);
    }

}

 