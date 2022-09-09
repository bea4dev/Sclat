package be4rjp.sclat.ticks;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncTickThread {
    
    private final ExecutorService executor;
    
    public AsyncTickThread() {
        this.executor = Executors.newSingleThreadExecutor();
    }
    
    public void shutdown() {
        executor.shutdown();
    }
    
    public void runTask(Runnable runnable) {
        executor.submit(runnable);
    }
    
}
