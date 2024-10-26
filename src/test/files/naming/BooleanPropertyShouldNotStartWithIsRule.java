class ShouldHaveBracketRule {
    private Boolean isTrue; // Noncompliant {{【isTrue】布尔字段不要加is前缀}}
    private boolean isFalse; // Noncompliant {{【isFalse】布尔字段不要加is前缀}}
    private Boolean rightAnswer; // Compliant
}