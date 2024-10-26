
import java.util.regex.Pattern;

public class CanonEqFlagInRegexCheck {
    private static Pattern numberPattern = Pattern.compile("[0-9]+");  // Compliant

    public Pattern getNumberPattern() {
        Pattern localPattern = Pattern.compile("[0-9]+");  // Noncompliant {{【localPattern】变量应定义为常量或者字段}}
        return localPattern;
    }
}