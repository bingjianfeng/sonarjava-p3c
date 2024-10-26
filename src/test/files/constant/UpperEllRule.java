package concurrent;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UpperEllRuleExample {
    public void check() {
        Long val = 5753l;  // Noncompliant {{【5753l】应使用大写L}}
        Long val2 = 5753L;  // Compliant
        long val3 = 1275l;  // Noncompliant {{【1275l】应使用大写L}}
    }
}