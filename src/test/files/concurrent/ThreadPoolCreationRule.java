package concurrent;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolCreationExample {
    public void check(){
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                5, 10, 60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100)
        );  // Compliant
        for (int i = 0; i < 10; i++) {
            final int index = i;
            executor.execute(() -> {
                System.out.println("Task " + index + " is running");
            });
        }
        executor.shutdown();
    }
}

public class ThreadPoolCreationErrorExample {
    public void check(){
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5); // Noncompliant {{线程池应该手工创建，避免使用Executors自动创建}}
        for (int i = 0; i < 10; i++) {
            final int index = i;
            fixedThreadPool.execute(() -> {
                System.out.println("Fixed Pool Task " + index + " is running");
            });
        }
        fixedThreadPool.shutdown();
    }
}

public class ThreadPoolCreationButOkExample {
    public void check(){
        ExecutorService fixedThreadPool = Executors.newScheduledThreadPool(5); // Compliant
        for (int i = 0; i < 10; i++) {
            final int index = i;
            fixedThreadPool.execute(() -> {
                System.out.println("Pool Task " + index + " is running");
            });
        }
        fixedThreadPool.shutdown();
    }
}