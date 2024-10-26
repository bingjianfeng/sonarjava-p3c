package com.elvis.sonar.java.checks.concurrent;

import org.junit.jupiter.api.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

/**
 * 单元测试
 *
 * @author fengbingjian
 * @description CountDownLatch使用中，每个线程退出前必须调用countDown方法。
 * @since 2024/9/26 17:35
 **/
public class CountDownShouldInFinallyRuleTest {

    @Test
    void check() {
        JavaCheckVerifier.newVerifier()
                .onFile("src/test/files/concurrent/CountDownShouldInFinallyRule.java")
                .withCheck(new CountDownShouldInFinallyRule())
                .verifyIssues();
    }
}