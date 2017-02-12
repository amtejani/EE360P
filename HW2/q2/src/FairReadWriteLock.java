import org.junit.Test;

public class FairReadWriteLock {
    int numReaders = 0;
    int accessRequests = 0;
	int currRequest = 0;
    boolean reading = false;
    boolean writing = false;

    public synchronized void beginRead() {
        int thisReq = accessRequests++;
        while(writing || thisReq > currRequest) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        currRequest += 1;
        numReaders++;
        notify();
    }

    public synchronized void endRead() {
        numReaders--;
        notify();
    }

    public synchronized void beginWrite() {
        int thisRequest = accessRequests++;
        while(numReaders > 0 || writing || thisRequest > currRequest) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        writing = true;
        currRequest++;
        notify();
    }

    public synchronized void endWrite() {
        writing = false;
        notify();
    }
}

