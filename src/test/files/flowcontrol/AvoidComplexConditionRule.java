package flowcontrol;

class AvoidComplexConditionRuleExample {

    public int check(int number) {
        int a = 10;
        int b = 100;
        if ((a + b > 10) && !(b % a > 0) && (b - a < 5)) {  // Noncompliant {{请不要在条件中使用复杂的表达式}}
            return number;
        }
        boolean numMatch = (a + b > 10) && !(b % a > 0) && (b - a < 5);
        if (numMatch) {  // Compliant
            return number;
        }
        if (a > 5 && (b > 10 || b - a > 50)) {  // Compliant
            return number;
        }
    }
}