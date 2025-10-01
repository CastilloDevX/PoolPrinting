package semaphore;

public class MainSemaphore {
    public static void main(String[] args) throws InterruptedException {
        int printers = args.length > 0 ? Integer.parseInt(args[0]) : 3;
        int users = args.length > 1 ? Integer.parseInt(args[1]) : 6;
        int jobsPerUser = args.length > 2 ? Integer.parseInt(args[2]) : 4;
        boolean fair = true; // fair semaphores mitigate starvation

        System.out.printf("[S] Starting with printers=%d, users=%d, jobsPerUser=%d, fair=%s%n",
                printers, users, jobsPerUser, fair);

        PrinterPoolSemaphore pool = new PrinterPoolSemaphore(printers, fair);

        Thread[] threads = new Thread[users];
        for (int u = 0; u < users; u++) {
            threads[u] = new Thread(new UserTaskSemaphore(u + 1, pool, jobsPerUser), "S-User-" + (u + 1));
        }

        long t0 = System.currentTimeMillis();
        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();
        long t1 = System.currentTimeMillis();

        System.out.printf("[S] All jobs done in %.3f seconds%n", (t1 - t0) / 1000.0);
    }
}
