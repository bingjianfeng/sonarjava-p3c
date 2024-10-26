import java.util.List;
import java.util.ArrayList;

public class ConcurrentExceptionWithModifyOriginSubListRule {
    public void check() {
        List<String> originList = new ArrayList<String>();
        originList.add("22"); // Compliant
        List<String> subList = originList.subList(0, 1);
        originList.add("22"); // Noncompliant {{【add】在这里可能会导致ConcurrentModificationException}}
    }
}