/*
 * EID's of group members
 * 
 */

import java.util.concurrent.Semaphore; // for implementation using Semaphores
import java.util.concurrent.atomic.AtomicInteger;

public class CyclicBarrier {

    private int parties;
    private AtomicInteger currIndex;
    private Semaphore awaitSemaphore;
    private Semaphore emptyingSemaphore;

    public CyclicBarrier(int parties) {
        this.parties = parties;
        this.currIndex = new AtomicInteger(parties);
        this.awaitSemaphore = new Semaphore(0);
        this.emptyingSemaphore = new Semaphore(parties);
    }

    public int await() throws InterruptedException {
        emptyingSemaphore.acquire();
        int index = currIndex.decrementAndGet();

        if (index == 0){
            awaitSemaphore.release(parties-1);
        } else {
            awaitSemaphore.acquire();
        }
        if (currIndex.incrementAndGet() == parties) {
            emptyingSemaphore.release(parties);
        }
        return index;
    }
}
