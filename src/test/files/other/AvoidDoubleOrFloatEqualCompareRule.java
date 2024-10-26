import java.math.BigDecimal;
import java.lang.Double;

public class AvoidDoubleOrFloatEqualCompareRule {
    public void compareFloats(float a, float b) {
        if (a == b) {  // Noncompliant
            System.out.println("a is equal to b");
        }
    }

    public void compareDoubleWrappers(Double totalAmount, Double amount) {
        if (!totalAmount.equals(amount)) {  // Noncompliant
            System.out.println("totalAmount is not equal to amount");
        }
    }

    public void compareFloats(float a, float b) {
        if (Math.abs(a - b) < EPSILON) { // Compliant
            System.out.println("a is approximately equal to b");
        }
    }

    public void compareDoubles(double a, double b) {
        BigDecimal decimal1 = new BigDecimal(String.valueOf(a));
        BigDecimal decimal2 = new BigDecimal(String.valueOf(b));
        if (decimal1.compareTo(decimal2) != 0) { // Compliant
            System.out.println("a is not equal to b");
        }
    }
}