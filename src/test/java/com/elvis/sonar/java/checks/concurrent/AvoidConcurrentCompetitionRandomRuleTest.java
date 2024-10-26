package com.elvis.sonar.java.checks.concurrent;

import org.junit.jupiter.api.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

/**
 * 单元测试
 *
 * @author fengbingjian
 * @description 避免Random实例被多线程使用，虽然共享该实例是线程安全的，但会因竞争同一seed，导致的性能下降。
 * @since 2024/9/26 17:35
 **/
public class AvoidConcurrentCompetitionRandomRuleTest {

    @Test
    void check() {

        JavaCheckVerifier.newVerifier()
                .onFile("src/test/files/concurrent/AvoidConcurrentCompetitionRandomRule.java")
                .withCheck(new AvoidConcurrentCompetitionRandomRule())
                .verifyIssues();
    }
}