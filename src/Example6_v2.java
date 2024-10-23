import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class BinaryTreeSearchTask implements Callable<Boolean> {
    private final int[] data;
    private final int start, end;
    private final int target;

    public BinaryTreeSearchTask(int[] data, int start, int end, int target) {
        this.data = data;
        this.start = start;
        this.end = end;
        this.target = target;
    }

    @Override
    public Boolean call() {
        for (int i = start; i <= end; i++) {
            if (data[i] == target) {
                return true;
            }
        }
        return false;
    }
}

/**
 * Ví dụ với ExecutorService
 */
public class Example6_v2 {
    public static void main(String[] args) throws Exception {
        int n = 20; // Ví dụ n = 20, không gian tìm kiếm 2^n
        int[] data = new int[(int) Math.pow(2, n)];

        // Khởi tạo dữ liệu
        for (int i = 0; i < data.length; i++) {
            data[i] = i;
        }

        int target = (int) (Math.random() * Math.pow(2, n)); // Số cần tìm

        int numThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<Boolean>> futures = new ArrayList<>();

        int chunkSize = data.length / numThreads;

        // Chia nhỏ công việc và gán cho các luồng
        for (int i = 0; i < numThreads; i++) {
            int start = i * chunkSize;
            int end = (i == numThreads - 1) ? data.length - 1 : (start + chunkSize - 1);
            futures.add(executor.submit(new BinaryTreeSearchTask(data, start, end, target)));
        }

        long startTime = System.nanoTime();
        boolean found = false;
        for (Future<Boolean> future : futures) {
            if (future.get()) {
                found = true;
                break;
            }
        }
        long endTime = System.nanoTime();

        executor.shutdown();

        System.out.println("ExecutorService: Target " + (found ? "found" : "not found"));
        System.out.println("Time taken: " + (endTime - startTime) / 1_000_000 + " ms");
    }
}
