class _ShouldHaveBracketRule { // Noncompliant {{【_ShouldHaveBracketRule】类命名不能以_或$开始}}
}

class $ShouldHaveBracketRule { // Noncompliant {{【$ShouldHaveBracketRule】类命名不能以_或$开始}}
}

class ShouldH$aveBracketRule { // Compliant
}

class ShouldHaveBracketRule { // Compliant
}