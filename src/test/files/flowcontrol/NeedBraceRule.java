package flowcontrol;

class NeedBraceRule {
    public void check(int number) {
        if (number > 10) return number; // Noncompliant
        if (number > 20) { // Compliant
            return number;
        }
        for (int i = 0; i < 10; i++) number++; // Noncompliant
        for (int i = 0; i < 20; i++) { // Compliant
            number++;
        }
        int size = 0;
        while (size < 10) size++; // Noncompliant
        while (size < 20) { // Compliant
            size++;
        }
        int age = 0;
        do age++; while (age > 10); // Noncompliant
        do { // Compliant
            age++;
        } while (age > 20);

        if (number > 20) {
            return number;
        } else if (number > 30) { // Compliant
            return number + 1;
        } else {
            return number - 1;
        }
    }
}