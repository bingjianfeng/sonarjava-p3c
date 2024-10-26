package concurrent;

import java.lang.Math;
import java.lang.Thread;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

public class MathInThread {
    public void check() {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 100; i++) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    double rand = Math.random(); // Noncompliant {{【Math.random()】应避免在多线程并发环境下使用。}}
                    System.out.println("Generated random value: " + randomValue);
                }
            });
        }
        executor.shutdown();
    }
}

public class SharedRandom extends Thread {
    private static final Random RANDOM = new Random(); // Noncompliant {{不要在多线程并发环境下使用同一个Random对象【RANDOM】}}

    public void run() {
        for (int i = 0; i < 1000; i++) {
            int number = RANDOM.nextInt(100);
            System.out.println(Thread.currentThread().getName() + " generated: " + number);
        }
    }
}

public class RandomUseInMethod extends Thread {
    public void run() {
        for (int i = 0; i < 1000; i++) {
            Random random = new Random(); // Compliant
            int number = RANDOM.nextInt(100);
            System.out.println(Thread.currentThread().getName() + " generated: " + number);
        }
    }
}

public class SharedRandomButNotFinally extends Thread {
    private Random random = new Random(); // Compliant
    public void run() {
        for (int i = 0; i < 1000; i++) {
            int number = random.nextInt(100);
            System.out.println(Thread.currentThread().getName() + " generated: " + number);
        }
    }
}

public class ThreadLocalRandomInThread extends Thread {
    private Random random = ThreadLocalRandom.current(); // Compliant
    @Override
    public void run() {
        long t = random.nextLong();
    }
}