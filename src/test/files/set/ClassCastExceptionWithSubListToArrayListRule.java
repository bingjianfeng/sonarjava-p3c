import java.util.List;
import java.util.ArrayList;


public class ClassCastExceptionWithSubListToArrayListRule {
    public void check () {
        List<String> clist = new ArrayList<String>();
        clist.add("22");
        List<String> test = (ArrayList<String>) clist.subList(0, 1); // Noncompliant {{【clist】的结果不可强转成ArrayList}}
        List<String> list2 = new ArrayList<String>(list.subList(0, 1)); // Compliant
    }
}