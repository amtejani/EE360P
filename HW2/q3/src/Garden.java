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
        // shovel lock
        diggingLock = new ReentrantLock();
        // digger waits for seeder and filler if too far ahead
        waitForSlowpokesCondition = diggingLock.newCondition();
        // shovel condition
        diggingCondition = diggingLock.newCondition();
        // filler waits for seeder
        slowSeedingCondition = diggingLock.newCondition();
        // seeder lock
        seedingLock = new ReentrantLock();
        // seeder waits for digger
        slowDiggingCondition = seedingLock.newCondition();
    }

    /**
     * request digging sequence
     */
    public void startDigging() {
        diggingLock.lock();
        try {
            // while seeder and filler are slow
            while (diggingIndex - seedingIndex >= 4 || diggingIndex - fillingIndex >= 8) {
                try {
                    waitForSlowpokesCondition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // while filler is using shovel
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


    /**
     * end digging sequence
     */
    public void doneDigging() {
        diggingLock.lock();
        diggingIndex++;
        try {
            // let filler use shovel
            digging = false;
            diggingCondition.signalAll();
        } finally {
            diggingLock.unlock();
        }
        seedingLock.lock();
        try {
            // tell seeder that there are more holes
            slowDiggingCondition.signalAll();
        } finally {
            seedingLock.unlock();
        }
    }

    /**
     * request seeding sequence
     */
    public void startSeeding() {
        seedingLock.lock();
        try {
            // while digger is slow
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

    /**
     * end seeding sequence
     */
    public void doneSeeding() {
        diggingLock.lock();
        seedingIndex++;
        try {
            // tell digger that hole is seeded
            waitForSlowpokesCondition.signalAll();
        } finally {
            diggingLock.unlock();
        }
        diggingLock.lock();
        try {
            // tell seeder there are holes to be filled
            slowSeedingCondition.signalAll();
        } finally {
            diggingLock.unlock();
        }
    }

    /**
     * request filling sequence
     */
    public void startFilling() {
        diggingLock.lock();
        try {
            // while seeder is slow
            while (fillingIndex >= seedingIndex) {
                try {
                    slowSeedingCondition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // while shovel is being used
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

    /**
     * end seeding sequence
     */
    public void doneFilling() {
        diggingLock.lock();
        fillingIndex++;
        try {
            // tell digger that a hole is filled
            waitForSlowpokesCondition.signalAll();
            // let digger to use shovel
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
