class TestCase extends Exception { // Noncompliant {{【TestCase】命名应以Exception结尾}}

}

class TestCaseException extends Exception { // Compliant

}

class ValueNullException extends NullPointerException { // Compliant

}