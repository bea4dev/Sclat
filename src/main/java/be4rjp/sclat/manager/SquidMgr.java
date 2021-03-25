package be4rjp.sclat.manager;

import be4rjp.sclat.Main;
import static be4rjp.sclat.Main.conf;

import be4rjp.sclat.ServerType;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import be4rjp.sclat.weapon.Gear;
import net.minecraft.server.v1_14_R1.*;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftSquid;
import org.bukkit.craftbukkit.v1_14_R1.util.CraftChatMessage;
import org.bukkit.entity.Squid;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

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
            int i = 0;
            //LivingEntity squid;
            @Override
            public void run(){
                PlayerData data = DataMgr.getPlayerData(p);
    
                if(!p.isOnline()){
                    cancel();
                }
                
                if(!data.isInMatch()){
                    if(p.hasPotionEffect(PotionEffectType.REGENERATION))
                        p.removePotionEffect(PotionEffectType.REGENERATION);
                    if(p.hasPotionEffect(PotionEffectType.INVISIBILITY))
                        p.removePotionEffect(PotionEffectType.INVISIBILITY);
                    p.setWalkSpeed(0.2F);
                    p.setMaxHealth(20);
                    
                    if(Main.type == ServerType.MATCH) {
                        p.setFoodLevel(20);
                        p.setExp(0F);
                    }
                    
                    if(data.getCanFly()){
                        p.setAllowFlight(true);
                        p.setFlying(true);
                    }else {
                        if (p.hasPermission("sclat.lobbyfly") || Main.flyList.contains(p.getName())) {
                            p.setAllowFlight(true);
                        } else {
                            p.setAllowFlight(false);
                            p.setFlying(false);
                        }
                    }
                    return;
                }
                
                //Sponge
                Location pl = p.getLocation().add(0, 0.5, 0);
                Block b1 = pl.getBlock();
                Block b2 = pl.clone().add(0, 1, 0).getBlock();
                if(b1.getType().toString().contains("POWDER") || b2.getType().toString().contains("POWDER")){
                    p.teleport(pl.add(0, 0.5, 0));
                    p.setVelocity(new Vector(0, 0.5, 0));
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
                    if(Main.tutorial && down.getType().toString().contains("WOOL")){
                        if(down.getType() != data.getTeam().getTeamColor().getWool()){
                            if(data.getArmor() <= 0 && !data.getIsPoisonCoolTime()){
                                p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 3));
                            }
                        }
                    }else if(p.hasPotionEffect(PotionEffectType.POISON))
                        p.removePotionEffect(PotionEffectType.POISON);
                }
                
                if(i > 2){
                    i = 0;
                    if(player.getInventory().getItemInMainHand().getType() != Material.AIR){
                        data.setIsSquid(false);
                    }else{
                        data.setIsSquid(true);
                    }
                }
                i++;
                /*
                if(Main.tutorial && down.getType().toString().contains("WOOL")){
                    if(down.getType() != data.getTeam().getTeamColor().getWool()){
                        if(data.getArmor() <= 0 && !data.getIsPoisonCoolTime()){
                            p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 3));
                        }
                    }
                }*/
                
                if(data.getIsPoisonCoolTime())
                    if(p.hasPotionEffect(PotionEffectType.POISON))
                        p.removePotionEffect(PotionEffectType.POISON);
                    
                
            
                if((data.getIsOnInk() && data.getIsSquid()) || data.getIsOnPath()){
                    is2 = false;
                    if(!is){
                        p.playSound(p.getLocation(), Sound.ITEM_BUCKET_FILL, 0.5F, 1F);
                        is = true;
                        p.setFoodLevel(20);
                    }                                                                      
                    if(data.getIsUsingJetPack())
                        p.setFlySpeed(0.1F);
                    
                    if(p.getExp() <= (0.99F - (float)(conf.getConfig().getDouble("SquidRecovery") * Gear.getGearInfluence(p, Gear.Type.INK_RECOVERY_UP)))){
                        if(data.getCanUseSubWeapon())
                            p.setExp(p.getExp() + (float)(conf.getConfig().getDouble("SquidRecovery") * Gear.getGearInfluence(p, Gear.Type.INK_RECOVERY_UP)));
                    }
                    p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 3));
                    //p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 200, 1));
                    
                    Location loc = p.getLocation();
                    Location gro = data.getPlayerGroundLocation();
                    if(gro == null) gro = loc;
                    if(loc.getX() != gro.getX() || loc.getX() != gro.getX() || loc.getX() != gro.getX()) {
                        p.setSprinting(true);
                        org.bukkit.block.data.BlockData bd = DataMgr.getPlayerData(p).getTeam().getTeamColor().getWool().createBlockData();
                        p.getLocation().getWorld().spawnParticle(org.bukkit.Particle.BLOCK_DUST, p.getLocation(), 2, 0.1, 0.1, 0.1, 1, bd);
                    }else {
                        p.setSprinting(false);
                    }
                    
                    double speed = conf.getConfig().getDouble("SquidSpeed") * Gear.getGearInfluence(p, Gear.Type.IKA_SPEED_UP);
    
                    if(data.getSpeed() != 0)
                        speed = data.getSpeed();
                    
                    if(!DataMgr.getPlayerData(p).getPoison())
                        p.setWalkSpeed((float)speed);
                    else
                        p.setWalkSpeed((float)(speed - speed / 3));
                        
                    
                }else{
                    if(!is2){
                        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_SWIM, 0.3F, 5F);  
                        is2 = true;
                        p.setSprinting(false);
                        p.setFoodLevel(4);
                    }
                    is = false;
                    if(p.hasPotionEffect(PotionEffectType.REGENERATION))
                        p.removePotionEffect(PotionEffectType.REGENERATION);
                    //if(p.hasPotionEffect(PotionEffectType.INVISIBILITY))
                        //p.removePotionEffect(PotionEffectType.INVISIBILITY);
                    
                    
                    double speed = 0.2;
                    
                    if(p.getInventory().getItemInMainHand().getType().equals(data.getWeaponClass().getMainWeapon().getWeaponIteamStack().getType()))
                        speed = (double)data.getWeaponClass().getMainWeapon().getInHoldSpeed() * Gear.getGearInfluence(p, Gear.Type.HITO_SPEED_UP);
                    else
                        speed = conf.getConfig().getDouble("PlayerWalkSpeed") * Gear.getGearInfluence(p, Gear.Type.HITO_SPEED_UP);
                    
                    if(data.getSpeed() != 0)
                        speed = data.getSpeed();
                    
                    if(p.getExp() <= (0.99F - (float)conf.getConfig().getDouble("NormalRecovery"))){
                        p.setExp(p.getExp() + (float)conf.getConfig().getDouble("NormalRecovery"));
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
    
                //プレイヤーが最後に立っていた地面を記録する
                if(p.isOnGround())
                    data.setPlayerGroundLocation(p.getLocation());
            } 
        };
        task.runTaskTimer(Main.getPlugin(), 0, 2);
        
    }
    
    public static void SquidShowRunnable(Player player){
        PlayerData data = DataMgr.getPlayerData(player);
        
        /*
        Squid squid = (Squid)player.getWorld().spawnEntity(player.getLocation(), EntityType.SQUID);
        squid.setAI(false);
        squid.setSilent(true);
        squid.setRemainingAir(Integer.MAX_VALUE);
        squid.setMaximumAir(Integer.MAX_VALUE);
        ((LivingEntity)squid).setCollidable(false);
        ((LivingEntity)player).setCollidable(false);
        
        if(conf.getConfig().getString("WorkMode").equals("Trial")){
            ScoreboardManager manager = Bukkit.getScoreboardManager();
            Scoreboard scoreboard = manager.getNewScoreboard();

            org.bukkit.scoreboard.Team bteam0 = scoreboard.registerNewTeam(data.getTeam().getTeamColor().getColorName());
            bteam0.setColor(data.getTeam().getTeamColor().getChatColor());
            //bteam0.setNameTagVisibility(NameTagVisibility.HIDE_FOR_OTHER_TEAMS);
            bteam0.setOption(org.bukkit.scoreboard.Team.Option.COLLISION_RULE, org.bukkit.scoreboard.Team.OptionStatus.NEVER);
            
            player.setScoreboard(scoreboard);
            bteam0.addEntry(player.getName());
            
            bteam0.addEntry(squid.getUniqueId().toString());
        }
        
        if(data.getTeam() != null){
            squid.setCustomName(player.getName());
            squid.setCustomNameVisible(true);
            if(!conf.getConfig().getString("WorkMode").equals("Trial"))
                data.getTeam().getTeam().addEntry(squid.getUniqueId().toString());
        }*/
        
        BukkitRunnable task = new BukkitRunnable() {
            Player p = player;
            boolean is = false;
            boolean is2 = true;
            boolean is3 = false;
            boolean is4 = true;
            boolean set = false;
            boolean death = false;
            net.minecraft.server.v1_14_R1.World nmsWorld = ((CraftWorld) p.getWorld()).getHandle();
            EntitySquid es = new EntitySquid(EntityTypes.SQUID, nmsWorld);
            
            @Override
            public void run() {
                
                if(!set){
                    set = true;
                    es.setNoAI(true);
                    es.setNoGravity(true);
                    es.setCustomName(CraftChatMessage.fromStringOrNull(player.getName()));
                    es.setCustomNameVisible(true);
                    ((LivingEntity)es.getBukkitEntity()).setCollidable(false);
                    //data.getTeam().getTeam().addEntry(es.getBukkitEntity().getUniqueId().toString());
                    
                    if(conf.getConfig().getString("WorkMode").equals("Trial")){
                        
                        ScoreboardManager manager = Bukkit.getScoreboardManager();
                        Scoreboard scoreboard = manager.getNewScoreboard();

                        org.bukkit.scoreboard.Team bteam0 = scoreboard.registerNewTeam(data.getTeam().getTeamColor().getColorName());
                        bteam0.setColor(data.getTeam().getTeamColor().getChatColor());
                        //bteam0.setNameTagVisibility(NameTagVisibility.HIDE_FOR_OTHER_TEAMS);
                        bteam0.setPrefix(data.getTeam().getTeamColor().getColorCode());
                        bteam0.setOption(org.bukkit.scoreboard.Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
                        
                        bteam0.addPlayer(player);
                        player.setScoreboard(scoreboard);
                        
                        bteam0.addEntry(es.getBukkitEntity().getUniqueId().toString());
                        
                        //player.setScoreboard(data.getMatch().getScoreboard());
                        //data.getTeam().getTeam().addEntry(player.getName());
                        
                        //data.getTeam().getTeam().addEntry(es.getBukkitEntity().getUniqueId().toString());
                    }else {
                        data.getTeam().getTeam().addEntry(es.getBukkitEntity().getUniqueId().toString());
                    }
                }
                
                try {
                    
                    Location loc = player.getLocation();
                    es.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), 0);

                    if(!data.getIsOnInk() && data.getIsSquid() && !data.getIsOnPath()){
                        is2 = false;
                        if(!is){
                            is = true;
                            PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(es);
                            for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                                if(p.getWorld() == target.getWorld()){
                                    ((CraftPlayer)target).getHandle().playerConnection.sendPacket(packet);
                                }
                            }
                        }
                        //squid.teleport(p);
                        
                        PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(es);
                        for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                            if(p.getWorld() == target.getWorld()){
                                ((CraftPlayer)target).getHandle().playerConnection.sendPacket(packet);
                            }
                        }
                    }else{
                        is = false;
                        if(!is2){ 
                            is2 = true;
                            PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(es.getBukkitEntity().getEntityId());
                            for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                                if(p.getWorld() == target.getWorld()){
                                    ((CraftPlayer)target).getHandle().playerConnection.sendPacket(packet);
                                }
                            }
                        }
                    }
                    
                }catch(Exception e){}
                
                if(data.getIsSquid()){
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
                
                if(p.getGameMode().equals(GameMode.SPECTATOR) && !data.getIsJumping()){
                    try {
                        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(es.getBukkitEntity().getEntityId());
                        for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                            if(p.getWorld() == target.getWorld()){
                                ((CraftPlayer)target).getHandle().playerConnection.sendPacket(packet);
                            }
                        }
                    } catch (Exception e) {}
                }
                
                if(!data.isInMatch() || !p.isOnline()){
                    try {
                        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(es.getBukkitEntity().getEntityId());
                        for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                            if(p.getWorld() == target.getWorld()){
                                ((CraftPlayer)target).getHandle().playerConnection.sendPacket(packet);
                            }
                        }
                    } catch (Exception e) {
                    }
                    //squid.remove();
                    cancel();
                }
            }
        };
        task.runTaskTimer(Main.getPlugin(), 30, 3);
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
