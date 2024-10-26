import java.util.Date;

public class AvoidNewDateGetTimeRule {
    public void loadDate() {
        long ts1 = new Date().getTime();  // Noncompliant {{请使用System.currentTimeMillis()代替new Date().getTime()}}
        long ts2 = System.currentTimeMillis();  // Compliant
    }
}