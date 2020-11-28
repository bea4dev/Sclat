package be4rjp.sclat.commands;

import be4rjp.sclat.Main;
import be4rjp.sclat.Sclat;
import be4rjp.sclat.SoundType;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

//sclat Command
public class sclatCommandExecutor implements CommandExecutor , TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if(args == null) return false;
        if(args.length == 0) return false;

        //------------------------Check sender type-----------------------------
        CommanderType type = CommanderType.CONSOLE;
        if(sender instanceof Player){
            if(((Player)sender).hasPermission("sclat.admin"))
                type = CommanderType.ADMIN;
            else
                type = CommanderType.MEMBER;
        }
        //----------------------------------------------------------------------

        //-------------------------/sclat setUpdateRate----------------------------
        if(args[0].equalsIgnoreCase("setUpdateRate") || args[0].equalsIgnoreCase("sur")) {
            if(args.length != 2) return false;

            if(type == CommanderType.MEMBER){
                sender.sendMessage(ChatColor.RED + "You don't have permission.");
                Sclat.playGameSound((Player)sender, SoundType.ERROR);
                return false;
            }

            String num = args[1];
            if (Sclat.isNumber(num)) {
                Main.conf.getConfig().set("BlockUpdateRate", Integer.valueOf(num));
                sender.sendMessage("setConfig [BlockUpdateRate]  :  " + num);
                return true;
            } else {
                sender.sendMessage("Please type with number");
                return false;
            }
        }
        //-------------------------------------------------------------------------
    
        //----------------------------/sclat fly-----------------------------------
        if(args[0].equalsIgnoreCase("fly")) {
            if(args.length != 2) return false;
        
            if(type == CommanderType.MEMBER){
                sender.sendMessage(ChatColor.RED + "You don't have permission.");
                Sclat.playGameSound((Player)sender, SoundType.ERROR);
                return false;
            }
        
            String playerName = args[1];
            for(Player player : Main.getPlugin().getServer().getOnlinePlayers()){
                if(playerName.equals(player.getName())){
                    Main.flyList.add(playerName);
                    return true;
                }
            }
        }
        //-------------------------------------------------------------------------
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        //------------------------Check sender type-----------------------------
        CommanderType type = CommanderType.CONSOLE;
        if(sender instanceof Player){
            if(((Player)sender).hasPermission("sclat.admin"))
                type = CommanderType.ADMIN;
            else
                type = CommanderType.MEMBER;
        }
        //----------------------------------------------------------------------

        //-----------------------------Tab complete-----------------------------
        if (args.length == 1){
            List<String> list = new ArrayList<>();

            list.add("help");

            if(type != CommanderType.MEMBER) {
                list.add("setUpdateRate");
                list.add("sur");
                list.add("fly");
            }

            return list;

        }else if(args.length == 2){
            List<String> list = new ArrayList<>();
            return list;
        }
        return null;
        //----------------------------------------------------------------------
    }
}
