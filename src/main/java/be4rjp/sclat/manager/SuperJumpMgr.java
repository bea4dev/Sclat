
package be4rjp.sclat.manager;

import be4rjp.sclat.Main;
import static be4rjp.sclat.Main.conf;
import be4rjp.sclat.data.DataMgr;
import net.minecraft.server.v1_13_R1.EntityEnderPearl;
import net.minecraft.server.v1_13_R1.EntityPlayer;
import net.minecraft.server.v1_13_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_13_R1.PacketPlayOutEntityVelocity;
import net.minecraft.server.v1_13_R1.PacketPlayOutMount;
import net.minecraft.server.v1_13_R1.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_13_R1.World;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 *
 * @author Be4rJP
 */
public class SuperJumpMgr {
    public static void SuperJumpCollTime(Player player, Location loc){
        player.getInventory().clear();
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40000, 10));
        BukkitRunnable task = new BukkitRunnable(){
            Player p = player;
            @Override
            public void run(){
                if(player.hasPotionEffect(PotionEffectType.SLOW))
                    player.removePotionEffect(PotionEffectType.SLOW);
                if(!p.getGameMode().equals(GameMode.SPECTATOR)){
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 2, 1.3F);
                    SuperJumpRunnable(p, loc);
                }
            }
        };
        task.runTaskLater(Main.getPlugin(), 15);
    }
    
    public static void SuperJumpRunnable(Player player, Location toloc){
        player.setGameMode(GameMode.SPECTATOR);
        EntityPlayer packetPlayer = ((CraftPlayer) player).getHandle();
        World world = packetPlayer.getWorld();

        EntityEnderPearl ball = new EntityEnderPearl(world);
        Location location = player.getEyeLocation();
        ball.setPosition(location.getX(), location.getY(), location.getZ());
        final int fakeSnowballID = ball.getBukkitEntity().getEntityId();
        PacketPlayOutSpawnEntity packet = new PacketPlayOutSpawnEntity(ball, 65);
        Vector vec = toloc.toVector();
        Vector velocity = VectorMgr.GravityVector(location.toVector(), vec, 30);
        PacketPlayOutEntityVelocity packet2 = new PacketPlayOutEntityVelocity(fakeSnowballID, velocity.getX(), velocity.getY(), velocity.getZ());
        //snowball.passengers.add(packetPlayer);
        packetPlayer.startRiding(ball);
        PacketPlayOutMount packet3 = new PacketPlayOutMount(ball);
        packetPlayer.playerConnection.sendPacket(packet);
        packetPlayer.playerConnection.sendPacket(packet2);
        packetPlayer.playerConnection.sendPacket(packet3);

        Location bl;
                
        if(conf.getConfig().getString("WorkMode").equals("Trial"))
            bl = Main.lobby;
        else
            bl = DataMgr.getPlayerData(player).getMatch().getMapData().getNoBlockLocation();
        EnderPearl ball2;
        if(conf.getConfig().getString("WorkMode").equals("Trial"))
            ball2 = (EnderPearl)player.getWorld().spawnEntity(new Location(bl.getWorld(), bl.getX(), vec.getY() + 2, bl.getZ()), EntityType.ENDER_PEARL);
        else
            ball2 = (EnderPearl)player.getWorld().spawnEntity(new Location(bl.getWorld(), bl.getX(), vec.getY(), bl.getZ()), EntityType.ENDER_PEARL);
        ball2.setVelocity(new Vector(0, velocity.getY(), 0));
        packetPlayer.playerConnection.sendPacket(new PacketPlayOutEntityDestroy(ball2.getEntityId()));

        BukkitRunnable task = new BukkitRunnable(){
            Player p = player;
            int c = 0;
            @Override
            public void run(){

                if(ball2.getLocation().getY() <= vec.getY() + 13 || ball2.isDead() || !DataMgr.getPlayerData(p).isInMatch()){
                    packetPlayer.stopRiding();
                    PacketPlayOutMount packet4 = new PacketPlayOutMount(ball);
                    PacketPlayOutEntityDestroy packet5 = new PacketPlayOutEntityDestroy(ball.getBukkitEntity().getEntityId());
                    for(Player pp : player.getWorld().getPlayers()){
                        EntityPlayer op = ((CraftPlayer) pp).getHandle();
                        op.playerConnection.sendPacket(packet4);
                        op.playerConnection.sendPacket(packet5);
                    }
                    p.setGameMode(GameMode.ADVENTURE);
                    Location loc = new Location(p.getWorld(), vec.getX(), vec.getY() + 12, vec.getZ());
                    p.teleport(loc);
                    p.setVelocity(new Vector(0, -1, 0));
                    WeaponClassMgr.setWeaponClass(p);
                    p.closeInventory();
                    p.getInventory().setHeldItemSlot(0);
                    cancel();
                }
                
                //エフェクト
                double r = 0.5;
                double x = vec.getX() + r * Math.cos(c);
                double y = vec.getY() + 0.4;
                double z = vec.getZ() + r * Math.sin(c);
                Location tl = new Location(p.getWorld(), x, y, z);
                Particle.DustOptions dustOptions = new Particle.DustOptions(DataMgr.getPlayerData(p).getTeam().getTeamColor().getBukkitColor(), 1);
                p.getWorld().spawnParticle(Particle.REDSTONE, tl, 1, 0, 0, 0, 50, dustOptions);
                c++;
            }
        };
        task.runTaskTimer(Main.getPlugin(), 60, 1);
    }
}
