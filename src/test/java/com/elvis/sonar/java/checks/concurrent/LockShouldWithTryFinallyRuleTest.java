package com.elvis.sonar.java.checks.concurrent;

import org.sonar.java.checks.verifier.JavaCheckVerifier;

/**
 * 单元测试
 *
 * @author fengbingjian
 * @description 锁必须紧跟try代码块，且unlock要放到finally第一行
 * @since 2024/9/26 17:35
 **/
public class LockShouldWithTryFinallyRuleTest {

    @org.junit.jupiter.api.Test
    void check() {
        JavaCheckVerifier.newVerifier()
                .onFile("src/test/files/concurrent/LockShouldWithTryFinallyRule.java")
                .withCheck(new LockShouldWithTryFinallyRule())
                .verifyIssues();
    }
}