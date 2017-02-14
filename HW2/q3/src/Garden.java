import java.util.concurrent.locks.*;

public class Garden {

    ReentrantLock diggingLock, seedingLock;
    Condition diggingCondition, waitForSlowpokesCondition, slowDiggingCondition, slowSeedingCondition;

    int diggingIndex, seedingIndex, fillingIndex;
    boolean digging;

    public Garden() {
        diggingIndex = 0;
        seedingIndex = 0;
        fillingIndex = 0;
        digging = false;
        diggingLock = new ReentrantLock();
        waitForSlowpokesCondition = diggingLock.newCondition();
        diggingCondition = diggingLock.newCondition();
        slowSeedingCondition = diggingLock.newCondition();
        seedingLock = new ReentrantLock();
        slowDiggingCondition = seedingLock.newCondition();
    }

    public void startDigging() {
        diggingLock.lock();
        try {
            while (diggingIndex - seedingIndex >= 4 || diggingIndex - fillingIndex >= 8) {
                try {
                    waitForSlowpokesCondition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            while (digging) {
                try {
                    diggingCondition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            digging = true;
        } finally {
            diggingLock.unlock();
        }

    }

    public void doneDigging() {
        diggingLock.lock();
        diggingIndex++;
        try {
            digging = false;
            diggingCondition.signalAll();
        } finally {
            diggingLock.unlock();
        }
        seedingLock.lock();
        try {
            slowDiggingCondition.signalAll();
        } finally {
            seedingLock.unlock();
        }
    }

    public void startSeeding() {
        seedingLock.lock();
        try {
            while (seedingIndex >= diggingIndex) {
                try {
                    slowDiggingCondition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } finally {
            seedingLock.unlock();
        }
    }

    public void doneSeeding() {
        diggingLock.lock();
        seedingIndex++;
        try {
            waitForSlowpokesCondition.signalAll();
        } finally {
            diggingLock.unlock();
        }
        diggingLock.lock();
        try {
            slowSeedingCondition.signalAll();
        } finally {
            diggingLock.unlock();
        }
    }

    public void startFilling() {
        diggingLock.lock();
        try {
            while (fillingIndex >= seedingIndex) {
                try {
                    slowSeedingCondition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            while (digging) {
                try {
                    diggingCondition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            digging = true;
        } finally {
            diggingLock.unlock();
        }
    }

    public void doneFilling() {
        diggingLock.lock();
        fillingIndex++;
        try {
            waitForSlowpokesCondition.signalAll();
            digging = false;
            diggingCondition.signalAll();
        } finally {
            diggingLock.unlock();
        }
    }

    /*
    * The following methods return the total number of holes dug, seeded or 
    * filled by Newton, Benjamin or Mary at the time the methods' are 
    * invoked on the garden class. */
    public int totalHolesDugByNewton() {
        return diggingIndex;
    }

    public int totalHolesSeededByBenjamin() {
        return seedingIndex;
    }

    public int totalHolesFilledByMary() {
        return fillingIndex;
    }
}
