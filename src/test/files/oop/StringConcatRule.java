import java.lang.StringBuilder;


public class StringConcatRuleExample {
    public void check() {
        String result;
        for (String string : tagNameList) {
            result = result + string; // Noncompliant {{请不要在循环体内使用"+"连接字符串}}
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String string : tagNameList) {
            stringBuilder.append(string); // Compliant
        }
    }
}