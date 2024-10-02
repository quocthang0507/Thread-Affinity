import net.openhft.affinity.AffinityLock;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Example4 {
    private Example4() {
        throw new InstantiationError("Must not instantiate this class");
    }

    public static void main(String[] args) {
        // Assuming each socket has a known number of cores.
        int coresPerSocket = 4; // Example: 4 cores per socket
        int numberOfSockets = 2; // Example: 2 sockets
        int totalCores = coresPerSocket * numberOfSockets;

        ExecutorService executorService = Executors.newFixedThreadPool(totalCores);

        for (int socketId = 0; socketId < numberOfSockets; socketId++) {
            for (int coreId = 0; coreId < coresPerSocket; coreId++) {
                int coreToBind = (socketId * coresPerSocket) + coreId;
                int finalSocketId = socketId;
                int finalCoreId = coreId;
                executorService.submit(() -> {
                    try (AffinityLock lock = AffinityLock.acquireLock(coreToBind)) {
                        System.out.println("Running on Socket: " + finalSocketId + " Core: " + finalCoreId +
                                " - Thread: " + Thread.currentThread().getName());
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
        }

        executorService.shutdown();
    }
}
