package concurrent;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AvoidCallStaticSimpleDateFormatRule {
    private static SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
    public String format(Date date) {
        return dateFormat1.format(date); // Noncompliant {{SimpleDateFormat可能导致线程安全问题}}
    }
}

public class AvoidCallSimpleDateFormatInMethodRule {
    public String format(Date date) {
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd"); // Noncompliant {{SimpleDateFormat可能导致线程安全问题}}
        return dateFormat1.format(date);
    }
}

public class SynchronizedSimpleDateFormatExample {
    private static final SimpleDateFormat DATE_FORAMT = new SimpleDateFormat("yyyy-MM-dd");
    public synchronized String format(Date date) {
        return DATE_FORAMT.format(date);  // Compliant
    }
}

public class NotSynchronizedSimpleDateFormatExample {
    private static final SimpleDateFormat DATE_FORAMT = new SimpleDateFormat("yyyy-MM-dd");
    public String format(Date date) {
        return DATE_FORAMT.format(date);  // Noncompliant {{SimpleDateFormat可能导致线程安全问题}}
    }
}

public class AvoidCallStaticSimpleDateFormatWithFinalArgsRule {
    private static final String FORMAT = "yyyy-MM-dd";
    public String format(Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT);  // Compliant
        return dateFormat.format(date);
    }
}

public class SynchronizedBlockSimpleDateFormatExample {
    private static final SimpleDateFormat DATE_FORAMT = new SimpleDateFormat("yyyy-MM-dd");
    public String format(Date date) {
        synchronized (DATE_FORAMT){
            return DATE_FORAMT.format(date);  // Compliant
        }
    }
}