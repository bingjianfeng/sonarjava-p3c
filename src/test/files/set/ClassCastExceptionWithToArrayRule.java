import java.util.ArrayList;
import java.util.List;

public class ClassCastExceptionWithSubListToArrayListRule {
    public void check () {
        List<Integer> c = new ArrayList<>();
        Integer[] a = (Integer [])c.toArray(); // Noncompliant {{【c】应使用大小一致类型一致的数组参数}}
        Integer[] b = (Integer [])c.toArray(new Integer[c.size()]); // Compliant
    }
}