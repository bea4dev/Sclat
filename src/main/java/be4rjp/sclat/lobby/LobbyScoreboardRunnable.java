package be4rjp.sclat.lobby;

import be4rjp.sclat.Main;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import be4rjp.sclat.data.ServerStatus;
import be4rjp.sclat.manager.PlayerStatusMgr;
import be4rjp.sclat.manager.RankMgr;
import be4rjp.sclat.manager.ServerStatusManager;
import be4rjp.sclat.utils.ObjectiveUtil;
import be4rjp.sclat.utils.TextAnimation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.ArrayList;
import java.util.List;

public class LobbyScoreboardRunnable extends BukkitRunnable {
    
    private final Player player;
    private final PlayerData playerData;
    private final Scoreboard scoreboard;
    private final TextAnimation textAnimation;
    private Objective objective;
    
    public LobbyScoreboardRunnable(Player player){
        this.player = player;
        this.playerData = DataMgr.getPlayerData(player);
    
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        this.scoreboard = scoreboardManager.getNewScoreboard();
        this.objective = scoreboard.registerNewObjective("Lobby", player.getName(), "§6§lSclat §r" + Main.VERSION);
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        
        player.setScoreboard(scoreboard);
        
        String text = ChatColor.translateAlternateColorCodes('&', Main.news.getConfig().getString("news-message"));
        
        this.textAnimation = new TextAnimation(text, Main.news.getConfig().getInt("scoreboard-length"));
    }
    
    @Override
    public void run() {
        objective.unregister();
        if(!player.isOnline()) cancel();
    
        List<String> lines = new ArrayList<>();
        lines.add("§7§m                                  ");
        lines.add("");
        lines.add("§6§lステータス »");
        lines.add("§e COIN: §r" + PlayerStatusMgr.getMoney(player));
        lines.add("§b RANK: §r" + RankMgr.toABCRank(PlayerStatusMgr.getRank(player)) + " [" + PlayerStatusMgr.getRank(player) + "]");
        lines.add(" ");
        lines.add("§9§lサーバー »");
        for(ServerStatus serverStatus : ServerStatusManager.serverList){
            if(serverStatus.isMaintenance()) continue;
            if(!serverStatus.isOnline()) continue;
            
            String line = "";
            if(serverStatus.getRunningMatch()) {
                long time = System.currentTimeMillis() / 1000 - serverStatus.getMatchStartTime();
                String min = String.format("%02d", time%60);
                line = serverStatus.getPlayerCount() + "§e人が試合中" + (time < 10000 ? " §r(" + time/60 + ":" + min + ")" : "");
            }else {
                if(serverStatus.getWaitingEndTime() != 0) {
                    line = serverStatus.getPlayerCount() + "§a人が待機中" + " §r(§b" + (serverStatus.getWaitingEndTime() - (System.currentTimeMillis() / 1000) + "§r秒後に開始)");
                }else{
                    line = serverStatus.getPlayerCount() + "§a人が待機中";
                }
            }
            
            lines.add(" " + serverStatus.getDisplayName() + ": §r" + line);
        }
        lines.add("  ");
        lines.add("§a§lNews »");
        lines.add(textAnimation.next());
        lines.add("   ");
        lines.add("§7§m                                  §r");
    
        objective = scoreboard.registerNewObjective("Lobby", player.getName(), "§6§lSclat §r" + Main.VERSION);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        ObjectiveUtil.setLine(objective, lines);
    }
}
