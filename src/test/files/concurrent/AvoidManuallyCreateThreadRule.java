package concurrent;
import java.lang.Thread;

public class AvoidManuallyCreateThreadExample {
    public void check() {
        Thread test = new Thread(); // Noncompliant
        test.start();
    }
}

public class AvoidManuallyCreateThreadSharedExample {
    Thread test = new Thread(); // Noncompliant
    public void check() {
        test.start();
    }
}

public class AvoidManuallyCreateThreadCompExample {
    public void check() {
        Thread test = new Thread(new Worker()); // Compliant
        test.start();
    }

    static class Worker implements Runnable {
        @Override
        public void run() {
            Thread.sleep(1000);
        }
    }
}