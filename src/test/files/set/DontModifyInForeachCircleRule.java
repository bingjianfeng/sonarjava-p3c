import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class DontModifyInForeachCircleRule {
    public void check() {
        List<String> originList = new ArrayList<String>();
        originList.add("22");
        for (String item : originList) {
            originList.add("bb"); // Noncompliant {{不要在foreach遍历中使用【add】}}
        }
    }

    public void check2() {
        List<String> originList = new ArrayList<>();
        originList.add("11");
        originList.add("22");
        originList.add("33");
        boolean delCondition = false;
        Iterator<String> it = originList.iterator();
        while(it.hasNext()){
            Integer temp =  it.next();
            if (delCondition) {
                it.remove(); // Compliant
            }
        }
    }
}