package semaphore;

import java.util.concurrent.Semaphore;
import java.util.Arrays;

public class PrinterPoolSemaphore {
    private final boolean[] inUse;
    private final Semaphore available; // counts free printers
    private final Semaphore mutex;     // binary mutex for array operations (fair)
    private final boolean fair;

    public PrinterPoolSemaphore(int printers, boolean fair) {
        if (printers <= 0) throw new IllegalArgumentException("printers > 0");
        this.inUse = new boolean[printers];
        this.available = new Semaphore(printers, fair);
        this.mutex = new Semaphore(1, fair);
        this.fair = fair;
    }

    public int size() {
        return inUse.length;
    }

    public int acquirePrinter(String userName) throws InterruptedException {
        available.acquire();         // wait until some printer is free
        mutex.acquire();             // enter critical section to pick a specific one
        try {
            for (int i = 0; i < inUse.length; i++) {
                if (!inUse[i]) {
                    inUse[i] = true;
                    return i;
                }
            }
            throw new IllegalStateException("No free printer found despite semaphore permit.");
        } finally {
            mutex.release();
        }
    }

    public void releasePrinter(int printerId, String userName) throws InterruptedException {
        mutex.acquire();
        try {
            if (printerId < 0 || printerId >= inUse.length) {
                throw new IllegalArgumentException("Invalid printer id: " + printerId);
            }
            if (!inUse[printerId]) {
                throw new IllegalStateException("Printer " + printerId + " already free.");
            }
            inUse[printerId] = false;
        } finally {
            mutex.release();
        }
        available.release(); // signal a free printer to waiters
    }

    @Override
    public String toString() {
        return "inUse=" + Arrays.toString(inUse) + " (fair=" + fair + ")";
    }
}
