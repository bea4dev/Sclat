package be4rjp.sclat.ticks;

import be4rjp.sclat.Main;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class AsyncTask implements Runnable {
    
    private boolean canceled = false;
    
    public boolean isCanceled() {return canceled;}
    
    public void cancel() {this.canceled = true;}
    
    public void runTask() {
        AsyncThreadManager.getRandomTickThread().runTask(this);
    }
    
    public void runTaskLater(long delay) {
        AsyncTask runnable = this;
        new BukkitRunnable() {
            @Override
            public void run() {
                AsyncThreadManager.getRandomTickThread().runTask(runnable);
            }
        }.runTaskLater(Main.getPlugin(), delay);
    }
    
    public void runTaskTimer(long delay, long period) {
        AsyncTask runnable = this;
        AsyncTickThread thread = AsyncThreadManager.getRandomTickThread();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (runnable.isCanceled()) {
                    cancel();
                    return;
                }
                thread.runTask(runnable);
            }
        }.runTaskTimer(Main.getPlugin(), delay, period);
    }
    
}
