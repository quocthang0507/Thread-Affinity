import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

class BinaryTreeSearchForkJoin extends RecursiveTask<Boolean> {
    private final int[] data;
    private final int start, end;
    private final int target;

    public BinaryTreeSearchForkJoin(int[] data, int start, int end, int target) {
        this.data = data;
        this.start = start;
        this.end = end;
        this.target = target;
    }

    @Override
    protected Boolean compute() {
        // Nếu chỉ còn 1 phần tử, kiểm tra giá trị
        if (start == end) {
            return data[start] == target;
        }

        // Chia cây thành hai nhánh
        int mid = (start + end) / 2;
        BinaryTreeSearchForkJoin leftTask = new BinaryTreeSearchForkJoin(data, start, mid, target);
        BinaryTreeSearchForkJoin rightTask = new BinaryTreeSearchForkJoin(data, mid + 1, end, target);

        // Chạy đồng thời các nhiệm vụ
        leftTask.fork();
        Boolean rightResult = rightTask.compute();
        Boolean leftResult = leftTask.join();

        // Trả về kết quả nếu tìm thấy target
        return leftResult || rightResult;
    }
}

/**
 * Ví dụ với ForkJoinPool
 */
public class Example6 {
    public static void main(String[] args) {
        int n = 20;
        int[] data = new int[(int) Math.pow(2, n)];

        for (int i = 0; i < data.length; i++) {
            data[i] = i;
        }

        int target = (int) (Math.pow(2, n) * Math.random());

        ForkJoinPool pool = new ForkJoinPool();
        BinaryTreeSearchForkJoin task = new BinaryTreeSearchForkJoin(data, 0, data.length - 1, target);

        long startTime = System.nanoTime();
        boolean found = pool.invoke(task);
        long endTime = System.nanoTime();

        System.out.println("ForkJoinPool: Target " + (found ? "found" : "not found"));
        System.out.println("Time taken: " + (endTime - startTime) / 1_000_000 + " ms");
    }
}
