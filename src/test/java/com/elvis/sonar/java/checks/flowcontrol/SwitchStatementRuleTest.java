package com.elvis.sonar.java.checks.flowcontrol;

import org.junit.jupiter.api.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

/**
 * 单元测试
 *
 * @author fengbingjian
 * @description 在一个switch块内，每个case都要break/return来终止，或注释说明执行到哪一个case为止；且必须包含default语句。
 * @since 2024/9/26 17:35
 **/
public class SwitchStatementRuleTest {

    @Test
    void check() {

        JavaCheckVerifier.newVerifier()
                .onFile("src/test/files/flowcontrol/SwitchStatementRule.java")
                .withCheck(new SwitchStatementRule())
                .verifyIssues();
    }
}