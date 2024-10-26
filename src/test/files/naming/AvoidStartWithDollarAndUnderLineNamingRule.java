class ShouldHaveBracketRule {
    private String _array2; // Noncompliant {{【_array2】变量命名不能以_或$开始}}
    private String[] $array1; // Noncompliant {{【$array1】变量命名不能以_或$开始}}
    private String array; // Compliant
}