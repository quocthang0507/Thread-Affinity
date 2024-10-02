import net.openhft.affinity.AffinityLock;
import net.openhft.affinity.AffinityThreadFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static net.openhft.affinity.AffinityStrategies.SAME_CORE;

public class Example1 {
    /**
     * Create a fixed thread pool with 4 threads
     */
    private static final ExecutorService ES = Executors.newFixedThreadPool(4,
            new AffinityThreadFactory("bg", SAME_CORE));

    /**
     * View the current binding state of CPU and thread
     */
    public static void main(String[] args) throws InterruptedException {
        // Submit 12 tasks to the executor service
        // Each task is a Callable<Void> that sleeps for 100 milliseconds
        // Callable allows for tasks to be potentially extended in the future to return values
        for (int i = 0; i < 12; i++) {
            ES.submit(new Callable<Void>() {
                @Override
                public Void call() throws InterruptedException {
                    Thread.sleep(100);
                    return null;
                }
            });
        }
        // The main thread sleeps for 200 ms to allow submitted tasks time to execute
        Thread.sleep(200);
        // Call dumpLocks() to visualize which CPUs the threads are running on
        System.out.println("\nThe assignment of CPUs is\n" + AffinityLock.dumpLocks());
        // Prevent new tasks from being submitted to the ES
        ES.shutdown();
        // Block the main thread until all tasks have completed execution or the timeout (1s) occurs
        ES.awaitTermination(1, TimeUnit.SECONDS);
    }
}
