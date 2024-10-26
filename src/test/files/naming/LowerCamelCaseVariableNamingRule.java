class LowerCamelCaseVariableNamingRule {
    private static final int CheckNum;
    private void CheckMe(  // Noncompliant {{方法名【CheckMe】不符合lowerCamelCase命名风格}}
            String check1, // Compliant
                         String Check2) {  // Noncompliant {{变量名【Check2】不符合lowerCamelCase命名风格}}
        String checkPoint1 = "test";
        String CheckPoint2 = "test2";  // Noncompliant {{变量名【CheckPoint2】不符合lowerCamelCase命名风格}}
    }
}