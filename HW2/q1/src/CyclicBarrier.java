/*
 * amt3639, Ali Tejani
 * tam2983, Travis McClure
 */

import java.util.concurrent.Semaphore; // for implementation using Semaphores
import java.util.concurrent.atomic.AtomicInteger;

public class CyclicBarrier {

    private int parties; // size of barrier
    private AtomicInteger currIndex; // index of last thread waiting
    private Semaphore awaitSemaphore; // wait for barrier to fill
    private Semaphore emptyingSemaphore; // wait for barrier to finish emptying

    public CyclicBarrier(int parties) {
        this.parties = parties;
        this.currIndex = new AtomicInteger(parties);
        this.awaitSemaphore = new Semaphore(0);
        this.emptyingSemaphore = new Semaphore(parties);
    }

    /**
     * waits for enough threads to call, then releases them before accepting more threads
     * @return index in barrier of current thread
     * @throws InterruptedException
     */
    public int await() throws InterruptedException {
        // wait if currently emptying
        emptyingSemaphore.acquire();
        //get index
        int index = currIndex.decrementAndGet();

        if (index == 0){
            // start emptying if barrier is full
            awaitSemaphore.release(parties-1);
        } else {
            // wait for barrier to fill
            awaitSemaphore.acquire();
        }
        // allow threads to continue once barrier is empty
        if (currIndex.incrementAndGet() == parties) {
            emptyingSemaphore.release(parties);
        }
        return index;
    }
}
