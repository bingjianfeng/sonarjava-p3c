package concurrent;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AvoidReturnInFinallyRuleExample {
    public String check() {
        String test = "";
        try {
            test = "hello";
        } finally {
            return test; // Noncompliant {{请不要在finally中使用return}}
        }
    }

    public String check2() {
        String test = "";
        try {
            test = "hello";
            return test; // Compliant
        } finally {
        }
    }
}