package monitor;

import java.util.Arrays;

public class PrinterPoolMonitor {
    private final boolean[] inUse;
    private int freeCount;

    public PrinterPoolMonitor(int printers) {
        if (printers <= 0) throw new IllegalArgumentException("printers > 0");
        this.inUse = new boolean[printers];
        this.freeCount = printers;
    }

    public synchronized int size() {
        return inUse.length;
    }

    public synchronized int acquirePrinter(String userName) throws InterruptedException {
        while (freeCount == 0) {
            wait(); // wait until a printer is released
        }
        for (int i = 0; i < inUse.length; i++) {
            if (!inUse[i]) {
                inUse[i] = true;
                freeCount--;
                return i;
            }
        }
        throw new IllegalStateException("Inconsistent state: freeCount > 0 but no free printer found.");
    }

    public synchronized void releasePrinter(int printerId, String userName) {
        if (printerId < 0 || printerId >= inUse.length) {
            throw new IllegalArgumentException("Invalid printer id: " + printerId);
        }
        if (!inUse[printerId]) {
            throw new IllegalStateException("Printer " + printerId + " already free.");
        }
        inUse[printerId] = false;
        freeCount++;
        notifyAll(); // wake up all waiters to contend for the newly free printer
    }

    @Override
    public synchronized String toString() {
        return "inUse=" + Arrays.toString(inUse) + " freeCount=" + freeCount;
    }
}
