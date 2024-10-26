import java.math.BigDecimal;

public class BigDecimalAvoidDoubleConstructorExample {
    public void check() {
        BigDecimal good1 = new BigDecimal(0.1); // Noncompliant
        BigDecimal good2 = new BigDecimal("0.1"); // Compliant
        BigDecimal good3 = BigDecimal.valueOf(0.1); // Compliant
    }
}