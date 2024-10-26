package concurrent;

import java.util.concurrent.CountDownLatch;
import java.util.Map;

public class ThreadLocalShouldRemoveExample {

    private static final ThreadLocal<Map> mapThreadLocal = new ThreadLocal<Map>();  // Compliant

    public static void set(Map map) {
        mapThreadLocal.set(map);
    }

    public static Map get() {
        return mapThreadLocal.get();
    }

    public static void remove() {
        mapThreadLocal.remove();
    }

}

public class ThreadLocalShouldRemoveErrorExample {

    private static final ThreadLocal<Map> mapThreadLocal = new ThreadLocal<Map>();  // Noncompliant {{ThreadLocal字段【mapThreadLocal】应该至少调用一次remove()方法。}}

    public static void set(Map map) {
        mapThreadLocal.set(map);
    }

    public static Map get() {
        return mapThreadLocal.get();
    }

}