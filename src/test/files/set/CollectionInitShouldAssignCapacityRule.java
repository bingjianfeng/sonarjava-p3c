import java.util.HashMap;
import java.util.Map;

public class CollectionInitShouldAssignCapacityRule {
    public void check () {
        Map<String, String> map = new HashMap<String, String>(); // Noncompliant {{【HashMap】初始化时，尽量指定初始值大小}}
        Map<String, String> map2 = new HashMap<String, String>(16); // Compliant
    }
}