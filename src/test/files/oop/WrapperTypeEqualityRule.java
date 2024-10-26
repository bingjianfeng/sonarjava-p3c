public class WrapperTypeEqualityRuleExample {
    public void check() {
        Integer a = 235;
        Integer b = 235;
        if (a == b) { // Noncompliant {{【a】应该作为equals的参数，而不是调用方}}
            return;
        }
        if (a.equals(b)) { // Compliant
            return;
        }
    }
}