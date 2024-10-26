import java.lang.Math;
import java.util.Random;

public class AvoidMissUseOfMathRandomRule {
    public void example() {
        Long randomLong =(long) (Math.random() * 10);  // Noncompliant {{注意 Math.random() 这个方法返回是double类型，注意取值的范围。}}
        Long randomLong2 = new Random().nextLong();  // Compliant
    }
}