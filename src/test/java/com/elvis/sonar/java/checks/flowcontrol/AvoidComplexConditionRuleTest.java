package com.elvis.sonar.java.checks.flowcontrol;

import org.junit.jupiter.api.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

/**
 * 单元测试
 *
 * @author fengbingjian
 * @description 请不要在条件中使用复杂的表达式
 * @since 2024/9/26 17:35
 **/
public class AvoidComplexConditionRuleTest {

    @Test
    void check() {

        JavaCheckVerifier.newVerifier()
                .onFile("src/test/files/flowcontrol/AvoidComplexConditionRule.java")
                .withCheck(new AvoidComplexConditionRule())
                .verifyIssues();
    }
}