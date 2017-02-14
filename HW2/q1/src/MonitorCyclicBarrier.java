/**
 * Ali Tejani, amt3639
 * Travis McClure, tam2983
 */

import java.util.concurrent.atomic.AtomicInteger;

public class MonitorCyclicBarrier {

    private int parties; // size of barrier
    private AtomicInteger currIndex; // index of last thread waiting
    private boolean emptying; // start emptying barrier

    public MonitorCyclicBarrier(int parties) {
        this.parties = parties;
        this.currIndex = new AtomicInteger(parties);
        this.emptying = false;
    }

    /**
     * waits for enough threads to call, then releases them before accepting more threads
     * @return index in barrier of current thread
     * @throws InterruptedException
     */
    public synchronized int await() throws InterruptedException {
        // wait if currently emptying
        while (emptying) {
            wait();
        }
        //get index
        int index = currIndex.decrementAndGet();

        if (index == 0) {
            // start emptying if barrier is full
            emptying = true;
            notifyAll();
        } else {
            // wait for barrier to fill
            while (!emptying) {
                wait();
            }
        }
        // allow threads to continue once barrier is empty
        if (currIndex.incrementAndGet() == parties) {
            emptying = false;
            notifyAll();
        }
        return index;
    }
}
