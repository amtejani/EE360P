//UT-EID=amt3639


import java.util.*;
import java.util.concurrent.*;

public class PSort implements Runnable{

    public static ExecutorService threadPool;

    public static void parallelSort(int[] A, int begin, int end){
        try {
            ExecutorService es = Executors.newSingleThreadExecutor();
            threadPool = Executors.newCachedThreadPool();
            PSort p = new PSort(A, begin, end);
            Future<?> f = es.submit(p);
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

    @Override
    public void run() {
        try {
            if(end - begin <= 4) {
                for(int i = begin; i < end; i++) {
                    for(int j = i; j > begin && arr[j-1] > arr[j]; j--) {
                        swap(j,j-1);
                    }
                }

            } else {
                int pivot = arr[end-1];
                int i = begin;
                for(int j = begin; j < end - 1; j++) {
                    if(arr[j] <= pivot) {
                        swap(i,j);
                        i++;
                    }
                }
                swap(i,end-1);
                Future<?> f1 = threadPool.submit(new PSort(arr, begin, i));
                Future<?> f2 = threadPool.submit(new PSort(arr, i, end));
                f1.get();
                f2.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void swap(int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}
