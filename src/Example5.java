import net.openhft.affinity.Affinity;
import net.openhft.affinity.AffinityLock;

import java.util.concurrent.*;

/**
 * Ví dụ với ExecutorService
 */
public class Example5 {
    // Sử dụng ExecutorService để tạo pool thread
    private static final ExecutorService executorService = Executors.newFixedThreadPool(4);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        int n = 10;

        // Đo thời gian thực hiện với đa luồng và ThreadAffinity
        long startParallel = System.nanoTime();
        Future<Long> resultFuture = executorService.submit(new RecursiveTask(n));
        long parallelResult = resultFuture.get();
        long endParallel = System.nanoTime();

        // Đóng ExecutorService sau khi hoàn thành công việc
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        // Đo thời gian thực hiện với tuần tự đệ quy thông thường
        long startSequential = System.nanoTime();
        long sequentialResult = recursiveFactorialSequential(n);
        long endSequential = System.nanoTime();

        // In ra kết quả và thời gian thực hiện
        System.out.println("Kết quả (Đa luồng): " + parallelResult);
        System.out.println("Thời gian thực hiện (Đa luồng, nano giây): " + (endParallel - startParallel));

        System.out.println("Kết quả (Tuần tự): " + sequentialResult);
        System.out.println("Thời gian thực hiện (Tuần tự, nano giây): " + (endSequential - startSequential));
    }

    private static Long recursiveFactorialSequential(int n) {
        if (n <= 1) {
            return 1L;
        }
        return n * recursiveFactorialSequential(n - 1);
    }

    /**
     * Class RecursiveTask để thực thi hàm đệ quy tính toán song song
     */
    static class RecursiveTask implements Callable<Long> {
        private final int n;

        public RecursiveTask(int n) {
            this.n = n;
        }

        @Override
        public Long call() throws Exception {
            try (AffinityLock lock = AffinityLock.acquireLock()) {
                Affinity.setAffinity(lock.cpuId());

                // Sử dụng hàm đệ quy để tính toán (ví dụ giai thừa)
                return recursiveFactorial(n);
            }
        }

        /**
         * Hàm tính giai thừa có thể chạy đệ quy song song
         *
         * @param n
         * @return
         * @throws Exception
         */
        private Long recursiveFactorial(int n) throws Exception {
            if (n <= 1) {
                return 1L;
            }

            // Tạo task song song cho n-1
            Future<Long> subTask = executorService.submit(new RecursiveTask(n - 1));
            Long subResult = subTask.get();

            // Tính toán phần kết quả
            return n * subResult;
        }
    }
}
