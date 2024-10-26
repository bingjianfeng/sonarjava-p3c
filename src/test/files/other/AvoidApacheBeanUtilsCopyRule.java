import org.apache.commons.beanutils.BeanUtils;

public class AvoidApacheBeanUtilsCopyRule {
    public void test () {
        User user = new User();
        Persion persion = new Persion();
        persion.setName("Chess Zhang"); // Compliant
        BeanUtils.copyProperties(user,persion); // Noncompliant
    }
}