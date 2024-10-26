package flowcontrol;

class AvoidNegationOperatorRule {

    public int check(int number) {
        return !!(number < 0); // Noncompliant {{禁止使用双重否定运算符}}
    }

    public int check2(int number) {

        boolean flag = !!false; // Noncompliant {{禁止使用双重否定运算符}}
        return number;
    }

    public int check3(int number) {
        boolean flag = false;
        if (!flag && number != 0 && number != 100) { // Noncompliant {{过多否定运算符不利于快速理解}}
            return number + 10;
        }
        return number;
    }

    public int check4(boolean flag) {
        if(!flag){ // Compliant
            return 0;
        }
        return 1;
    }
}