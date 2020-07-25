package be4rjp.sclat.manager;

import be4rjp.sclat.Main;
import static be4rjp.sclat.Main.conf;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import be4rjp.sclat.weapon.Gear;
import net.minecraft.server.v1_13_R1.EntitySquid;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_13_R1.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import net.minecraft.server.v1_13_R1.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_13_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_13_R1.PacketPlayOutEntityTeleport;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_13_R1.util.CraftChatMessage;

/**
 *
 * @author Be4rJP
 */
public class SquidMgr {
    public static void SquidRunnable(Player player){
        BukkitRunnable task = new BukkitRunnable(){
            Player p = player;
            boolean is = false;
            boolean is2 = true;
            //LivingEntity squid;
            @Override
            public void run(){
                PlayerData data = DataMgr.getPlayerData(p);
                if(!data.isInMatch()){
                    if(p.hasPotionEffect(PotionEffectType.REGENERATION))
                        p.removePotionEffect(PotionEffectType.REGENERATION);
                    if(p.hasPotionEffect(PotionEffectType.INVISIBILITY))
                        p.removePotionEffect(PotionEffectType.INVISIBILITY);
                    p.setFoodLevel(20);
                    p.setHealth(20);
                    p.setWalkSpeed(0.2F);
                    p.setExp(0);
                    p.setMaxHealth(20);
                    if(!p.isOp()){
                        p.setAllowFlight(false);
                        p.setFlying(false);
                    }else{
                        p.setAllowFlight(true);
                    }
                    return;
                }
                
                
                if(data.getWeaponClass().getMainWeapon().getIsManeuver()){
                    if(p.getInventory().getItemInMainHand().getType().equals(data.getWeaponClass().getMainWeapon().getWeaponIteamStack().getType())){
                        if(!p.getInventory().getItemInOffHand().getType().equals(data.getWeaponClass().getMainWeapon().getWeaponIteamStack().getType()))
                            p.getInventory().setItem(40, data.getWeaponClass().getMainWeapon().getWeaponIteamStack().clone());
                    }else{
                        p.getInventory().setItem(40, new ItemStack(Material.AIR));
                    }
                }
                
                Block down = p.getLocation().getBlock().getRelative(BlockFace.DOWN);
                if(DataMgr.getBlockDataMap().containsKey(down) && p.getGameMode().equals(GameMode.ADVENTURE)){
                    if(DataMgr.getBlockDataMap().get(down).getTeam() != data.getTeam()){
                        if(data.getArmor() <= 0 && !data.getIsPoisonCoolTime()){
                            p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 3));
                        }
                    }else{
                        if(p.hasPotionEffect(PotionEffectType.POISON))
                            p.removePotionEffect(PotionEffectType.POISON);
                    }
                }else{
                    if(p.hasPotionEffect(PotionEffectType.POISON))
                        p.removePotionEffect(PotionEffectType.POISON);
                }
                
                if(data.getIsPoisonCoolTime())
                    if(p.hasPotionEffect(PotionEffectType.POISON))
                        p.removePotionEffect(PotionEffectType.POISON);
                
                if(p.getInventory().getItemInMainHand().getType().equals(Material.AIR)){
                    data.setIsSquid(true);
                    
                }else{
                    data.setIsSquid(false);
                }
            
                if((data.getIsOnInk() && data.getIsSquid()) || data.getIsOnPath()){
                    is2 = false;
                    if(!is){
                        p.playSound(p.getLocation(), Sound.ITEM_BUCKET_FILL, 0.5F, 1F);
                        is = true;
                    }                                                                      
                    if(data.getIsUsingJetPack())
                        p.setFlySpeed(0.1F);
                    
                    if(p.getExp() <= (0.99F - (float)(conf.getConfig().getDouble("SquidRecovery") * Gear.getGearInfluence(p, Gear.Type.INK_RECOVERY_UP)))){
                        p.setExp(p.getExp() + (float)(conf.getConfig().getDouble("SquidRecovery") * Gear.getGearInfluence(p, Gear.Type.INK_RECOVERY_UP)));
                    }
                    p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 3));
                    //p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 200, 1));
                    p.setFoodLevel(20);                                                   
                    p.setSprinting(true);
                    final double speed = conf.getConfig().getDouble("SquidSpeed") * Gear.getGearInfluence(p, Gear.Type.IKA_SPEED_UP);
                    if(!DataMgr.getPlayerData(p).getPoison())
                        p.setWalkSpeed((float)speed);
                    else
                        p.setWalkSpeed((float)(speed - speed / 3));
                        
                        
                    
                }else{
                    if(!is2){
                        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_SWIM, 0.3F, 5F);  
                        is2 = true;
                        p.setSprinting(false);
                    }
                    is = false;
                    if(p.hasPotionEffect(PotionEffectType.REGENERATION))
                        p.removePotionEffect(PotionEffectType.REGENERATION);
                    //if(p.hasPotionEffect(PotionEffectType.INVISIBILITY))
                        //p.removePotionEffect(PotionEffectType.INVISIBILITY);

                    p.setFoodLevel(4);
                    
                    double speed = 0.2;
                    
                    if(p.getInventory().getItemInMainHand().getType().equals(data.getWeaponClass().getMainWeapon().getWeaponIteamStack().getType()))
                        speed = (double)data.getWeaponClass().getMainWeapon().getInHoldSpeed() * Gear.getGearInfluence(p, Gear.Type.HITO_SPEED_UP);
                    else
                        speed = conf.getConfig().getDouble("PlayerWalkSpeed") * Gear.getGearInfluence(p, Gear.Type.HITO_SPEED_UP);
                    
                    if(p.getExp() <= (0.99F - (float)conf.getConfig().getDouble("NomalRecovery"))){
                        p.setExp(p.getExp() + (float)conf.getConfig().getDouble("NomalRecovery"));
                    }
                    
                    if(data.getIsHolding() && data.getCanPaint() && p.getExp() >= data.getWeaponClass().getMainWeapon().getNeedInk()){
                        p.setSprinting(true);
                    }else{
                        if(!DataMgr.getPlayerData(p).getPoison())
                            p.setWalkSpeed((float)speed);
                        if(DataMgr.getPlayerData(p).getPoison())
                            p.setWalkSpeed((float)(speed - speed / 3));
                    }
                    

                    if(!p.getGameMode().equals(GameMode.CREATIVE) && !data.getIsUsingJetPack()){
                        p.setAllowFlight(false);
                        p.setFlying(false);
                    }
                    
                }
            } 
        };
        task.runTaskTimer(Main.getPlugin(), 0, 1);
    }
    
    public static void SquidShowRunnable(Player player){
        BukkitRunnable task = new BukkitRunnable() {
            Player p = player;
            boolean is = false;
            boolean is2 = true;
            boolean is3 = false;
            boolean is4 = true;
            boolean set = false;
            boolean death = false;
            net.minecraft.server.v1_13_R1.World nmsWorld = ((CraftWorld) p.getWorld()).getHandle();
            EntitySquid es = new EntitySquid(nmsWorld);
            
            @Override
            public void run() {
                PlayerData data = DataMgr.getPlayerData(p);
                boolean Bslot = false;
                if(p.getInventory().getItemInMainHand().getType().equals(Material.AIR))
                    Bslot = true;
                
                if(!set){
                    set = true;
                    es.setNoAI(true);
                    es.setNoGravity(true);
                    //es.setCustomName(CraftChatMessage.fromStringOrNull(player.getName()));
                    //data.getTeam().getTeam().addEntry(String.valueOf(es.getBukkitEntity().getEntityId()));
                }
                
                try {
                    Location loc = player.getLocation();
                    es.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), 0);

                    if(!data.getIsOnInk() && Bslot && !data.getIsOnPath()){
                        is2 = false;
                        if(!is){
                            is = true;
                            PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(es);
                            for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                                if(p != target && p.getWorld() == target.getWorld()){
                                    ((CraftPlayer)target).getHandle().playerConnection.sendPacket(packet);
                                }
                            }
                        }

                        PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(es);
                        for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                            if(p != target && p.getWorld() == target.getWorld()){
                                ((CraftPlayer)target).getHandle().playerConnection.sendPacket(packet);
                            }
                        }
                    }else{
                        is = false;
                        if(!is2){ 
                            is2 = true;
                            PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(es.getBukkitEntity().getEntityId());
                            for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                                if(p != target && p.getWorld() == target.getWorld()){
                                    ((CraftPlayer)target).getHandle().playerConnection.sendPacket(packet);
                                }
                            }
                        }
                    }
                }catch(Exception e){}
                
                if(Bslot){
                    is4 = false;
                    if(!is3){
                        is3 = true;
                        p.getEquipment().setHelmet(new ItemStack(Material.AIR));
                        if(data.getWeaponClass().getMainWeapon().getIsManeuver())
                            p.getInventory().setItem(40, new ItemStack(Material.AIR));
                    }
                    p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 200, 1));
                }else{
                    is3 = false;
                    if(!is4){ 
                        is4 = true;
                        p.getEquipment().setHelmet(DataMgr.getPlayerData(p).getTeam().getTeamColor().getBougu());
                        if(data.getWeaponClass().getMainWeapon().getIsManeuver())
                            p.getInventory().setItem(40, DataMgr.getPlayerData(p).getWeaponClass().getMainWeapon().getWeaponIteamStack().clone());
                    }
                    if(p.hasPotionEffect(PotionEffectType.INVISIBILITY))
                        p.removePotionEffect(PotionEffectType.INVISIBILITY);
                }
                
                if(p.getGameMode().equals(GameMode.SPECTATOR)){
                    try {
                        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(es.getBukkitEntity().getEntityId());
                        for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                            if(p != target && p.getWorld() == target.getWorld()){
                                ((CraftPlayer)target).getHandle().playerConnection.sendPacket(packet);
                            }
                        }
                    } catch (Exception e) {}
                }
                
                if(!data.isInMatch() || !p.isOnline()){
                    try {
                        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(es.getBukkitEntity().getEntityId());
                        for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                            if(p != target && p.getWorld() == target.getWorld()){
                                ((CraftPlayer)target).getHandle().playerConnection.sendPacket(packet);
                            }
                        }
                    } catch (Exception e) {
                    }
                    cancel();
                }
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 1);
    }
    
    
    public static void PoisonCoolTime(Player player){
        BukkitRunnable task = new BukkitRunnable(){
            Player p = player;
            @Override
            public void run(){
                DataMgr.getPlayerData(p).setIsPoisonCoolTime(false);
            }
        };
        task.runTaskLater(Main.getPlugin(), 10);
    }
}
