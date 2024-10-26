package flowcontrol;

class SwitchStatementRule {
    public int check(int number) {
        switch (number) { // Compliant
            case 1:
                number = number + 10;
                break;
            case 2:
                number = number + 100;
                break;
            default:
        }
        return number;
    }

    public int check2(int number) {
        switch (number) { // Noncompliant {{switch块缺少default语句}}
            case 1:
                number = number + 10;
                break;
            case 2:
                number = number + 100;
                break;
        }
        return number;
    }

    public int check3(int number) {
        switch (number) {
            case 1:
                number = number + 10;
                break;
            case 2: // Noncompliant {{switch中每个case需要通过break/return等来终止}}
                number = number + 100;
            default:
        }
        return number;
    }
}