import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * Ví dụ với ThreadAffinity
 */
public class Example5_v2 {
    public static void main(String[] args) {
        int n = 40; // thử với n lớn hơn

        ForkJoinPool forkJoinPool = new ForkJoinPool();

        // Đo thời gian thực hiện với đa luồng ForkJoin
        long startParallel = System.nanoTime();
        Long result = forkJoinPool.invoke(new FactorialTask(n));
        long endParallel = System.nanoTime();

        System.out.println("Kết quả (Đa luồng ForkJoin): " + result);
        System.out.println("Thời gian thực hiện (Đa luồng ForkJoin, ms): " + (endParallel - startParallel) / 1_000_000.0);

        // Đo thời gian thực hiện với thuật toán tuần tự
        long startSequential = System.nanoTime();
        long sequentialResult = factorialSequential(n);
        long endSequential = System.nanoTime();

        System.out.println("Kết quả (Tuần tự): " + sequentialResult);
        System.out.println("Thời gian thực hiện (Tuần tự, ms): " + (endSequential - startSequential) / 1_000_000.0);
    }

    // Hàm tính giai thừa theo cách tuần tự
    private static long factorialSequential(int n) {
        if (n <= 1) {
            return 1;
        }
        return n * factorialSequential(n - 1);
    }

    // Task tính giai thừa bằng ForkJoinPool
    static class FactorialTask extends RecursiveTask<Long> {
        private final int n;

        public FactorialTask(int n) {
            this.n = n;
        }

        @Override
        protected Long compute() {
            if (n <= 1) {
                return 1L;
            }

            FactorialTask subTask = new FactorialTask(n - 1);
            subTask.fork();  // Tạo tác vụ con

            // Tính toán giai thừa cho n
            return n * subTask.join(); // Đợi kết quả từ tác vụ con
        }
    }
}
