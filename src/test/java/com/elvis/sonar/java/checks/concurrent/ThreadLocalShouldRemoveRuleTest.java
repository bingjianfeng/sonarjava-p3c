package com.elvis.sonar.java.checks.concurrent;

import org.junit.jupiter.api.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

/**
 * 单元测试
 *
 * @author fengbingjian
 * @description ThreadLocal字段应该至少调用一次remove方法
 * @since 2024/9/26 17:35
 **/
public class ThreadLocalShouldRemoveRuleTest {

    @Test
    void check() {
        JavaCheckVerifier.newVerifier()
                .onFile("src/test/files/concurrent/ThreadLocalShouldRemoveRule.java")
                .withCheck(new ThreadLocalShouldRemoveRule())
                .verifyIssues();
    }
}