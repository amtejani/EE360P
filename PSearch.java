//UT-EID=tam2983 and amt3639
// Ali Tejani
// Travis McClure


import java.util.*;
import java.util.concurrent.*;


public class PSearch implements Callable<Integer> {
    public static ExecutorService threadPool;
    private int start;
    private int end;
    private int k;
    private int[] A;

    /**
     * parallel search
     * @param k element to search for
     * @param A array to search in
     * @param numThreads number of threads to use
     * @return index of k if found
     */
    public static int parallelSearch(int k, int[] A, int numThreads) {
        try {
            int returnIndex = -1;
            ExecutorService es = Executors.newSingleThreadExecutor();
            threadPool = Executors.newCachedThreadPool();
            int numElements = A.length / numThreads;
            int numExtras = A.length % numThreads;
            int i = 0;
            HashSet<Future<Integer>> futureHash = new HashSet();
            // create threads to search in array
            while (i < A.length) {
                if (numExtras > 0) {
                    futureHash.add(threadPool.submit(new PSearch(k, i, i + numElements + 1, A)));
                    numExtras--;
                    i += numElements + 1;
                } else {
                    futureHash.add(threadPool.submit(new PSearch(k, i, i + numElements, A)));
                    i += numElements;
                }
            }
            // wait for threads to finish
            for (Future<Integer> f : futureHash) {
                returnIndex = f.get();
                if (returnIndex != -1)
                    break;

            }
            es.shutdown();
            PSearch.threadPool.shutdown();
            return returnIndex;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1; // if not found
    }

    public PSearch(int k, int start, int end, int[] A) {
        this.k = k;
        this.start = start;
        this.end = end;
        this.A = A;
    }

    /**
     * perform sequential search
     * @return index of value k if found
     */
    @Override
    public Integer call() {

        for (int counter = start; counter < end; counter++) {
            if (A[counter] == k) {//found the number
                return counter;
            }
        }
        return -1;
    }
}
