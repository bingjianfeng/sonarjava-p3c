package com.elvis.sonar.java.checks.exception;

import com.elvis.sonar.java.checks.concurrent.AvoidCallStaticSimpleDateFormatRule;
import org.junit.jupiter.api.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

/**
 * 单元测试
 *
 * @author fengbingjian
 * @description 请不要在finally中使用return
 * @since 2024/9/26 17:35
 **/
public class AvoidReturnInFinallyRuleTest {

    @Test
    void check() {

        JavaCheckVerifier.newVerifier()
                .onFile("src/test/files/exception/AvoidReturnInFinallyRule.java")
                .withCheck(new AvoidReturnInFinallyRule())
                .verifyIssues();
    }
}