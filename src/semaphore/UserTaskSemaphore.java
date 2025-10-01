package semaphore;

import java.util.concurrent.ThreadLocalRandom;

public class UserTaskSemaphore implements Runnable {
    private final PrinterPoolSemaphore pool;
    private final int userId;
    private final int jobs;

    public UserTaskSemaphore(int userId, PrinterPoolSemaphore pool, int jobs) {
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
                log(String.format("[S] %s acquired printer #%d (job %d/%d)", user, printerId, j, jobs));
                Thread.sleep(ThreadLocalRandom.current().nextInt(200, 601)); // simulate printing
                log(String.format("[S] %s finished job %d/%d on printer #%d", user, j, jobs, printerId));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            } finally {
                if (printerId >= 0) {
                    try {
                        pool.releasePrinter(printerId, user);
                        log(String.format("[S] %s released printer #%d", user, printerId));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
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
