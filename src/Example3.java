import net.openhft.affinity.AffinityLock;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Example3 {
    private Example3() {
        throw new InstantiationError("Must not instantiate this class");
    }

    public static void main(String[] args) {
        int numCores = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(numCores);

        for (int i = 0; i < numCores; i++) {
            final int coreId = i;
            executorService.submit(() -> {
                try (AffinityLock lock = AffinityLock.acquireLock(coreId)) {
                    System.out.println("Running on core: " + coreId + " - Thread: " + Thread.currentThread().getName());
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        executorService.shutdown();
    }
}
