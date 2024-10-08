import net.openhft.affinity.AffinityLock;

public class Example2 {
    private Example2() {
        throw new InstantiationError("Must not instantiate this class");
    }

    public static void main(String... args) throws InterruptedException {
        AffinityLock al = AffinityLock.acquireLock();
        try {
            new Thread(new SleepRunnable(), "reader").start();
            new Thread(new SleepRunnable(), "writer").start();
            Thread.sleep(200);
        } finally {
            al.release();
        }
        new Thread(new SleepRunnable(), "engine").start();

        Thread.sleep(200);
        System.out.println("\nThe assignment of CPUs is\n" + AffinityLock.dumpLocks());
    }

    private static class SleepRunnable implements Runnable {
        public void run() {
            AffinityLock al = AffinityLock.acquireLock();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                al.release();
            }
        }
    }
}
