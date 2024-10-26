public class EqualsAvoidNullRuleExample {

    public void check () {
        boolean result = "expectedValue".equals(input); // Compliant
        if (result) {
            System.out.println("Input matches the expected value.");
        }
    }

    public void check2(String input) {
        // 反例：直接在可能为null的对象上调用equals
        boolean result = input.equals("expectedValue"); // Noncompliant {{【input】应该作为equals的参数，而不是调用方}}
        if (result) {
            System.out.println("Input matches the expected value.");
        }
    }
}