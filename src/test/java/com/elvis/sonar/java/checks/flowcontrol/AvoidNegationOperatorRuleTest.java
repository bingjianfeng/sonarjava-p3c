package com.elvis.sonar.java.checks.flowcontrol;

import org.junit.jupiter.api.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

/**
 * 单元测试
 *
 * @author fengbingjian
 * @description 运算符不利于快速理解
 * @since 2024/9/26 17:35
 **/
public class AvoidNegationOperatorRuleTest {

    @Test
    void check() {

        JavaCheckVerifier.newVerifier()
                .onFile("src/test/files/flowcontrol/AvoidNegationOperatorRule.java")
                .withCheck(new AvoidNegationOperatorRule())
                .verifyIssues();
    }
}