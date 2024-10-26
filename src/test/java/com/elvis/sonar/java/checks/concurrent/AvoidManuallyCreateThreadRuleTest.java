package com.elvis.sonar.java.checks.concurrent;

import org.junit.jupiter.api.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

/**
 * 单元测试
 *
 * @author fengbingjian
 * @description 不要显式创建线程，请使用线程池。
 * @since 2024/9/26 17:35
 **/
public class AvoidManuallyCreateThreadRuleTest {

    @Test
    void check() {

        JavaCheckVerifier.newVerifier()
                .onFile("src/test/files/concurrent/AvoidManuallyCreateThreadRule.java")
                .withCheck(new AvoidManuallyCreateThreadRule())
                .verifyIssues();
    }
}