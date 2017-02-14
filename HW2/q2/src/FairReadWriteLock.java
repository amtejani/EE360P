/**
 * Ali Tejani, amt3639
 * Travis McClure, tam2983
 */

public class FairReadWriteLock {
    int numReaders = 0;
    int accessRequests = 0;
	int currRequest = 0;
    boolean reading = false;
    boolean writing = false;

    /**
     * request a read sequence
     */
    public synchronized void beginRead() {
        // get request number
        int thisReq = accessRequests++;
        // wait for turn or wait for writing to finish
        while(writing || thisReq > currRequest) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // fill request
        currRequest += 1;
        numReaders++;
        notify();
    }

    /**
     * end a read sequence
     */
    public synchronized void endRead() {
        // end read
        numReaders--;
        notify();
    }

    /**
     * request a write sequence
     */
    public synchronized void beginWrite() {
        // get request number
        int thisRequest = accessRequests++;
        // wait for turn or wait for reading/writing to finish
        while(numReaders > 0 || writing || thisRequest > currRequest) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // fill request
        writing = true;
        currRequest++;
        notify();
    }

    /**
     * end a write sequence
     */
    public synchronized void endWrite() {
        // end write
        writing = false;
        notify();
    }
}

