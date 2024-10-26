package com.elvis.sonar.java.checks.flowcontrol;

import org.junit.jupiter.api.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

/**
 * 单元测试
 *
 * @author fengbingjian
 * @description 语句缺少大括号
 * @since 2024/9/26 17:35
 **/
public class NeedBraceRuleTest {

    @Test
    void check() {
        JavaCheckVerifier.newVerifier()
                .onFile("src/test/files/flowcontrol/NeedBraceRule.java")
                .withCheck(new NeedBraceRule())
                .verifyIssues();
    }
}