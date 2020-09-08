package be4rjp.sclat.weapon.subweapon;

import be4rjp.sclat.Main;
import static be4rjp.sclat.Main.conf;
import be4rjp.sclat.Sphere;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.manager.ArmorStandMgr;
import be4rjp.sclat.manager.DamageMgr;
import be4rjp.sclat.manager.DeathMgr;
import be4rjp.sclat.manager.PaintMgr;
import be4rjp.sclat.weapon.Gear;
import java.util.List;
import net.minecraft.server.v1_13_R1.EnumItemSlot;
import net.minecraft.server.v1_13_R1.PacketPlayOutEntityEquipment;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_13_R1.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 *
 * @author Be4rJP
 */
public class CurlingBomb {
    public static void CurlingBombRunnable(Player player){
        Vector pVector = player.getEyeLocation().getDirection();
        Vector vec = new Vector(pVector.getX(), 0, pVector.getZ()).normalize().multiply(0.5);
        BukkitRunnable task = new BukkitRunnable() {
            Vector aVec = vec.clone();
            Location bloc;
            int i = 0;
            ArmorStand as1;
            ArmorStand as2;
            ArmorStand as3;
            FallingBlock fb;
            @Override
            public void run() {
                try {
                    if(i == 0){
                        if(!DataMgr.getPlayerData(player).getIsBombRush())
                            player.setExp(player.getExp() - 0.59F);

                        as1 = player.getWorld().spawn(player.getLocation(), ArmorStand.class, armorStand -> {
                            armorStand.setVisible(false);
                            armorStand.setSmall(true);
                        });
                        as2 = player.getWorld().spawn(player.getLocation().add(0, 0, 0), ArmorStand.class, armorStand -> {
                            armorStand.setVisible(false);
                            armorStand.setGravity(false);
                            armorStand.setMarker(true);
                        });
                        Location loc = player.getLocation().add(0, -0.4, 0);
                        loc.setYaw(90);
                        as3 = player.getWorld().spawn(loc, ArmorStand.class, armorStand -> {
                            armorStand.setVisible(false);
                            armorStand.setGravity(false);
                            armorStand.setSmall(true);
                        });

                        fb = player.getWorld().spawnFallingBlock(player.getLocation(), Material.QUARTZ_SLAB.createBlockData());
                        fb.setGravity(false);
                        fb.setDropItem(false);
                        fb.setHurtEntities(false);

                        as2.addPassenger(fb);
                    }

                    Location aloc = as1.getLocation().add(0, -0.4, 0);
                    aloc.setYaw(90);
                    Location as1l = as1.getLocation();
                    ((CraftArmorStand)as2).getHandle().setPositionRotation(as1l.getX(), as1l.getY(), as1l.getZ(), 0, 0);
                    as3.teleport(aloc);
                    fb.setTicksLived(1);

                    if(i >= 2 && as1.isOnGround()){
                        if(bloc.getX() == as1l.getX())
                            aVec = new Vector(aVec.getX() * -1, 0, aVec.getZ());
                        if(bloc.getZ() == as1l.getZ())
                            aVec = new Vector(aVec.getX(), 0, aVec.getZ() * -1);
                    }

                    PaintMgr.PaintHightestBlock(as1l, player, false, true);

                    bloc = as1l.clone();

                    if(i % 10 == 0){
                        for (Player o_player : Main.getPlugin().getServer().getOnlinePlayers())
                            ((CraftPlayer)o_player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(as3.getEntityId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(DataMgr.getPlayerData(player).getTeam().getTeamColor().getWool()))));
                    }

                    if(i >= 130 && i <= 140){
                        if(i % 2 == 0)
                            player.getWorld().playSound(as1l, Sound.BLOCK_NOTE_BLOCK_PLING, 1F, 1.6F);
                    }

                    //エフェクト
                    if(i % 2 == 0){
                        org.bukkit.block.data.BlockData bd = DataMgr.getPlayerData(player).getTeam().getTeamColor().getWool().createBlockData();
                        for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                            if(DataMgr.getPlayerData(target).getSettings().ShowEffect_RollerRoll())
                                if(target.getWorld() == player.getWorld())
                                    if(target.getLocation().distance(as1l) < conf.getConfig().getInt("ParticlesRenderDistance"))
                                        target.spawnParticle(org.bukkit.Particle.BLOCK_DUST, as1l, 2, 0, 0, 0, 1, bd);
                        }
                        //攻撃判定
                        for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                            if(DataMgr.getPlayerData(target).getSettings().ShowEffect_RollerRoll()){
                                if(target.getWorld() == player.getWorld()){
                                    if(target.getLocation().distance(as1l) <= 1.2){
                                        double damage = 2;
                                        if(DataMgr.getPlayerData(player).getTeam() != DataMgr.getPlayerData(target).getTeam() && target.getGameMode().equals(GameMode.ADVENTURE)){
                                            if(target.getHealth() + DataMgr.getPlayerData(target).getArmor() > damage){
                                                DamageMgr.SclatGiveDamage(target, damage);
                                                PaintMgr.Paint(target.getLocation(), player, true);
                                            }else{
                                                target.setGameMode(GameMode.SPECTATOR);
                                                DeathMgr.PlayerDeathRunnable(target, player, "subWeapon");
                                                PaintMgr.Paint(target.getLocation(), player, true);
                                            }

                                            //AntiNoDamageTime
                                            BukkitRunnable task = new BukkitRunnable(){
                                                Player p = target;
                                                @Override
                                                public void run(){
                                                    target.setNoDamageTicks(0);
                                                }
                                            };
                                            task.runTaskLater(Main.getPlugin(), 1);
                                        }
                                    }
                                }
                            } 
                        }
                    }

                    if(i == 150 || !player.isOnline() || !DataMgr.getPlayerData(player).isInMatch()){
                        //半径
                        double maxDist = 4;

                        //爆発音
                        player.getWorld().playSound(as1l, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1, 1);

                        //爆発エフェクト
                        List<Location> s_locs = Sphere.getSphere(as1l, maxDist, 25);
                        for (Player o_player : Main.getPlugin().getServer().getOnlinePlayers()) {
                            if(DataMgr.getPlayerData(o_player).getSettings().ShowEffect_BombEx()){
                                for(Location loc : s_locs){
                                    if(o_player.getWorld() == loc.getWorld()){
                                        if(o_player.getLocation().distance(loc) < conf.getConfig().getInt("ParticlesRenderDistance")){
                                            org.bukkit.block.data.BlockData bd = DataMgr.getPlayerData(player).getTeam().getTeamColor().getWool().createBlockData();
                                            o_player.spawnParticle(org.bukkit.Particle.BLOCK_DUST, loc, 1, 0, 0, 0, 1, bd);
                                        }
                                    }
                                }
                            }
                        }

                        //塗る
                        for(int i = 0; i <= maxDist; i++){
                            List<Location> p_locs = Sphere.getSphere(as1l, i, 20);
                            for(Location loc : p_locs){
                                PaintMgr.Paint(loc, player, false);
                            }
                        }



                        //攻撃判定の処理

                        for(Entity as : player.getWorld().getEntities()){
                            if (as.getLocation().distance(as1l) <= maxDist){
                                if(as instanceof ArmorStand){
                                    if(as.getCustomName() != null){
                                        if(as.getCustomName().equals("Kasa")){
                                            return;
                                        }
                                    }
                                }
                            }
                        }

                        for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                            if(!DataMgr.getPlayerData(target).isInMatch() || target.getWorld() != player.getWorld())
                                continue;
                            if (target.getLocation().distance(as1l) <= maxDist) {
                                double damage = (maxDist - target.getLocation().distance(as1l)) * 5 * Gear.getGearInfluence(player, Gear.Type.SUB_SPEC_UP);
                                if(DataMgr.getPlayerData(player).getTeam() != DataMgr.getPlayerData(target).getTeam() && target.getGameMode().equals(GameMode.ADVENTURE)){
                                    if(target.getHealth() + DataMgr.getPlayerData(target).getArmor() > damage){
                                        DamageMgr.SclatGiveDamage(target, damage);
                                        PaintMgr.Paint(target.getLocation(), player, true);
                                    }else{
                                        target.setGameMode(GameMode.SPECTATOR);
                                        DeathMgr.PlayerDeathRunnable(target, player, "subWeapon");
                                        PaintMgr.Paint(target.getLocation(), player, true);
                                    }

                                    //AntiNoDamageTime
                                    BukkitRunnable task = new BukkitRunnable(){
                                        Player p = target;
                                        @Override
                                        public void run(){
                                            target.setNoDamageTicks(0);
                                        }
                                    };
                                    task.runTaskLater(Main.getPlugin(), 1);


                                }
                            }
                        }

                        for(Entity as : player.getWorld().getEntities()){
                            if (as.getLocation().distance(as1l) <= maxDist){
                                if(as instanceof ArmorStand){
                                    double damage = (maxDist - as.getLocation().distance(as1l)) * 7;
                                    ArmorStandMgr.giveDamageArmorStand((ArmorStand)as, damage, player);
                                    if(as.getCustomName() != null){
                                        if(as.getCustomName().equals("SplashShield") || as.getCustomName().equals("Kasa"))
                                            break;
                                    }
                                }
                            }
                        }

                        as1.remove();
                        as2.remove();
                        as3.remove();
                        fb.remove();
                        cancel();
                    }

                    if(as1.isOnGround())
                        as1.setVelocity(aVec);

                    i++;
                }catch(Exception e){
                    as1.remove();
                    as2.remove();
                    as3.remove();
                    fb.remove();
                    cancel();
                }
            }
        };
        if(player.getExp() > 0.6 || DataMgr.getPlayerData(player).getIsBombRush())
            task.runTaskTimer(Main.getPlugin(), 0, 1);
        else
            player.sendTitle("", ChatColor.RED + "インクが足りません", 0, 5, 2);
        
        BukkitRunnable cooltime = new BukkitRunnable(){
            @Override
            public void run(){
                DataMgr.getPlayerData(player).setCanUseSubWeapon(true);
            }
        };
        cooltime.runTaskLater(Main.getPlugin(), 10);
    }
}
