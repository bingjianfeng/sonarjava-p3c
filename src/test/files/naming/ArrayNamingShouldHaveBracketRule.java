class ArrayNamingShouldHaveBracketRule {
    private String array2[]; // Noncompliant {{数组变量【array2】中括号位置错误}}
    private String[] array1; // Compliant
    public void f(){
        String[] array3;
    }
}