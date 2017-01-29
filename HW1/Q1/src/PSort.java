//UT-EID=amt3639


import java.util.*;
import java.util.concurrent.*;

public class PSort implements Runnable{

    public static ExecutorService threadPool;

    /**
     * parallel quicksort
     * @param A array to sort
     * @param begin start index
     * @param end end index
     */
    public static void parallelSort(int[] A, int begin, int end){
        try {
            // allocate threads
            ExecutorService es = Executors.newSingleThreadExecutor();
            threadPool = Executors.newCachedThreadPool();
            // create new sort
            PSort p = new PSort(A, begin, end);
            // start sort
            Future<?> f = es.submit(p);
            // wait for sort to end
            f.get();
            es.shutdown();
            PSort.threadPool.shutdown();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private int[] arr;
    private int begin, end;

    public PSort( int[] input, int begin, int end) {
        this.arr = input;
        this.begin = begin;
        this.end = end;
    }

    /**
     * Quicksort algorithm using threads
     */
    @Override
    public void run() {
        try {
            // if 4 or fewer elements
            if(end - begin <= 4) {
                // insert sort
                for(int i = begin; i < end; i++) {
                    for (int j = i; j > begin && arr[j - 1] > arr[j]; j--) {
                        swap(j, j - 1);
                    }
                }
            } else {
                // partition into (< pivot) and (> pivot)
                int pivot = arr[end-1];
                int i = begin;
                for(int j = begin; j < end - 1; j++) {
                    if(arr[j] <= pivot) {
                        swap(i,j);
                        i++;
                    }
                }
                swap(i,end-1);
                // call recursively
                Future<?> f1 = threadPool.submit(new PSort(arr, begin, i));
                Future<?> f2 = threadPool.submit(new PSort(arr, i, end));
                // wait for recursive call to finish to finish
                f1.get();
                f2.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * swaps two elements in an array
     * @param i index 1
     * @param j index 2
     */
    private void swap(int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}
