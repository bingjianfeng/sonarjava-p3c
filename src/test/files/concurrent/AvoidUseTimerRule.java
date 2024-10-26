import java.util.Timer;
import java.util.TimerTask;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AvoidUseTimerRule {
    public void useTimer() {
        // 使用 Timer
        Timer timer = new Timer();  // Noncompliant
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Timer task executed");
            }
        }, 1000);

        // 另一个 Timer 任务
        Timer anotherTimer = new Timer();  // Noncompliant
        anotherTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Another Timer task executed");
            }
        }, 2000);
    }

    public void useScheduledExecutorService() {
        // 使用 ScheduledExecutorService
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);  // Compliant
        executorService.schedule(() -> {
            System.out.println("Scheduled task executed");
        }, 1, TimeUnit.SECONDS);

        // 另一个 ScheduledExecutorService 任务
        executorService.schedule(() -> {
            System.out.println("Another scheduled task executed");
        }, 2, TimeUnit.SECONDS);

        // 关闭 executorService 以释放资源
        executorService.shutdown();
    }
}