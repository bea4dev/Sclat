
package be4rjp.sclat.manager;

import be4rjp.sclat.Main;
import static be4rjp.sclat.Main.conf;
import be4rjp.sclat.data.DataMgr;
import static be4rjp.sclat.manager.PlayerStatusMgr.getLv;
import static be4rjp.sclat.manager.PlayerStatusMgr.getMoney;
import be4rjp.sclat.raytrace.RayTrace;
import java.util.ArrayList;
import net.minecraft.server.v1_14_R1.EntityArmorStand;
import net.minecraft.server.v1_14_R1.EntityEnderPearl;
import net.minecraft.server.v1_14_R1.EntityPlayer;
import net.minecraft.server.v1_14_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_14_R1.PacketPlayOutEntityVelocity;
import net.minecraft.server.v1_14_R1.PacketPlayOutMount;
import net.minecraft.server.v1_14_R1.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_14_R1.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_14_R1.World;
import net.minecraft.server.v1_14_R1.WorldServer;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_14_R1.util.CraftChatMessage;
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
        if(player.getWorld() != loc.getWorld())
            return;
        
        if(player.getLocation().distance(loc) <= 3){
            player.sendMessage(ChatColor.RED + "目的地が近すぎます！");
            player.playNote(player.getLocation(), Instrument.BASS_GUITAR, Note.flat(0, Note.Tone.G));
            return;
        }
        
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
        
        if(player.getLocation().distance(toloc) <= 3){
            player.setVelocity(new Vector(0, 2, 0));
            return;
        }
        
        Location from = player.getLocation().clone();
        Location to = toloc;
        Vector vec = new Vector(to.getX() - from.getX(), to.getY() - from.getY(), to.getZ() - from.getZ()).normalize();
        player.setGameMode(GameMode.SPECTATOR);
        DataMgr.getPlayerData(player).setIsJumping(true);
        RayTrace rayTrace1 = new RayTrace(from.toVector(), vec);
        ArrayList<Vector> positions = rayTrace1.traverse(from.distance(to), 1);

        double coef = 0.16 / Math.pow(from.distance(to) / 40, 2);
        /*
        ray : for(int i = 1; i < positions.size();i++){
            Location position = positions.get(i).toLocation(player.getLocation().getWorld());
            //double y = (Math.pow(Math.abs((positions.size() / 2) - i), 2) * -1 * coef) + (Math.abs(to.getY() - from.getY()) / 2) + (Math.pow(Math.sqrt(Math.pow(from.distance(to), 2) + (Math.pow(Math.abs(to.getY() - from.getY()) / 2, 2) * -4)) / 2, 2) * coef);
            double y = (Math.pow(Math.abs((positions.size() / 2) - i), 2) * -1 * coef) + (Math.pow(positions.size() / 2, 2) * coef);
            Location tloc = new Location(player.getWorld(), position.getX(), y + position.getY(), position.getZ());
            Particle.DustOptions dustOptions = new Particle.DustOptions(Color.BLUE, 1);
            player.spawnParticle(Particle.REDSTONE, tloc, 1, 0, 0, 0, 1, dustOptions);
        }*/

        BukkitRunnable task = new BukkitRunnable(){
            Player p = player;
            int i = 1;
            int t = 0;
            @Override
            public void run(){
                if(DataMgr.getPlayerData(p).getIsDead())
                    cancel();
                
                Location position = positions.get(i).toLocation(p.getLocation().getWorld());
                double py = (Math.pow(Math.abs((positions.size() / 2) - i), 2) * -1 * coef) + (Math.pow(positions.size() / 2, 2) * coef);
                double y = py > 100 ? 100 + py / 2.8 : py;
                Location tloc = new Location(p.getWorld(), position.getX(), y + position.getY(), position.getZ());
                Vector pvec = new Vector(tloc.getX() - p.getLocation().getX(), tloc.getY() - p.getLocation().getY(), tloc.getZ() - p.getLocation().getZ()).multiply(0.17);
                p.setVelocity(pvec);
                if(tloc.distance(p.getLocation()) < 15)
                    i++;
                if(i == positions.size() - 2){
                    p.setGameMode(GameMode.ADVENTURE);
                    WeaponClassMgr.setWeaponClass(p);
                    p.closeInventory();
                    p.getInventory().setHeldItemSlot(0);
                }
                
                if (t > 200 || (p.isOnGround() && t >= 20)) {//スタック回避
                    p.setGameMode(GameMode.ADVENTURE);
                    WeaponClassMgr.setWeaponClass(p);
                    p.closeInventory();
                    p.getInventory().setHeldItemSlot(0);
                    p.teleport(toloc.clone().add(0, 4, 0));
                    DataMgr.getPlayerData(player).setIsJumping(false);
                    cancel();
                }
                
                if(i == positions.size() || !DataMgr.getPlayerData(p).isInMatch() || !p.isOnline() || DataMgr.getPlayerData(p).getIsUsingTyakuti()){
                    DataMgr.getPlayerData(player).setIsJumping(false);
                    cancel();
                }
                
                t++;
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 1);

        BukkitRunnable effect = new BukkitRunnable(){
            Player p = player;
            int c = 0;
            int id;
            @Override
            public void run(){
                if(c == 0){
                    WorldServer nmsWorld = ((CraftWorld) p.getWorld()).getHandle();
                    EntityArmorStand as = new EntityArmorStand(nmsWorld, toloc.getX(), toloc.getY(), toloc.getZ());
                    as.setPosition(toloc.getX(), toloc.getY(), toloc.getZ());
                    as.setInvisible(true);
                    as.setNoGravity(true);
                    as.setBasePlate(false);
                    as.setCustomName(CraftChatMessage.fromStringOrNull(DataMgr.getPlayerData(p).getTeam().getTeamColor().getColorCode() + "↓↓↓  くコ:彡  ↓↓↓"));
                    as.setCustomNameVisible(true);
                    as.setSmall(true);
                    id = as.getBukkitEntity().getEntityId();
                    for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                        if(p.getWorld() == target.getWorld()){
                            ((CraftPlayer)target).getHandle().playerConnection.sendPacket(new PacketPlayOutSpawnEntityLiving(as));
                        }
                    }
                }
                //エフェクト
                double r = 0.5;
                double x = to.getX() + r * Math.cos(c);
                double y = to.getY() + 0.4;
                double z = to.getZ() + r * Math.sin(c);
                Location tl = new Location(p.getWorld(), x, y, z);
                Particle.DustOptions dustOptions = new Particle.DustOptions(DataMgr.getPlayerData(p).getTeam().getTeamColor().getBukkitColor(), 1);
                p.getWorld().spawnParticle(Particle.REDSTONE, tl, 1, 0, 0.1, 0, 50, dustOptions);
                if(p.getGameMode().equals(GameMode.ADVENTURE) || !DataMgr.getPlayerData(p).isInMatch() || !p.isOnline()){
                    for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                        if(p.getWorld() == target.getWorld()){
                            ((CraftPlayer)target).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(id));
                        }
                    }
                    cancel();
                }
                c++;
            }
        };
        effect.runTaskTimer(Main.getPlugin(), 0, 1);
    }
}
