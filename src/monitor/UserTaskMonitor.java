package monitor;

import java.util.concurrent.ThreadLocalRandom;

public class UserTaskMonitor implements Runnable {
    private final PrinterPoolMonitor pool;
    private final int userId;
    private final int jobs;

    public UserTaskMonitor(int userId, PrinterPoolMonitor pool, int jobs) {
        this.userId = userId;
        this.pool = pool;
        this.jobs = jobs;
    }

    @Override
    public void run() {
        String user = "User-" + userId;
        for (int j = 1; j <= jobs; j++) {
            int printerId = -1;
            try {
                printerId = pool.acquirePrinter(user);
                log(String.format("[M] %s acquired printer #%d (job %d/%d)", user, printerId, j, jobs));
                Thread.sleep(ThreadLocalRandom.current().nextInt(200, 601)); // simulate printing
                log(String.format("[M] %s finished job %d/%d on printer #%d", user, j, jobs, printerId));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            } finally {
                if (printerId >= 0) {
                    pool.releasePrinter(printerId, user);
                    log(String.format("[M] %s released printer #%d", user, printerId));
                }
            }
            // think time between jobs
            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(50, 151));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private void log(String msg) {
        System.out.println(msg);
    }
}
