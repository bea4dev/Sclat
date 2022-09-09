package be4rjp.sclat.ticks;

import be4rjp.sclat.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class AsyncThreadManager {
    
    private static final List<AsyncTickThread> tickThreads = new CopyOnWriteArrayList<>();
    
    public static AsyncTickThread getRandomTickThread() {
        return tickThreads.get(new Random().nextInt(tickThreads.size()));
    }
    
    public static void setup(int numberOfThread) {
        for (int i = 0; i < numberOfThread; i++) {
            AsyncTickThread thread = new AsyncTickThread();
            tickThreads.add(thread);
        }
    }
    
    public static void shutdownAll() {
        for (AsyncTickThread thread : tickThreads) {
            thread.shutdown();
        }
    }
    
    
    
    public static Set<Player> onlinePlayers = ConcurrentHashMap.newKeySet();
    
    public static void toOnline(Player player) {
        onlinePlayers.add(player);
    }
    
    public static void toOffline(Player player) {
        onlinePlayers.add(player);
    }
    
    public static void sync(Runnable runnable) {
        Bukkit.getScheduler().runTask(Main.getPlugin(), runnable);
    }
    
}
