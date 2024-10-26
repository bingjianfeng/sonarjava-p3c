package com.elvis.sonar.java.checks.comment;

import org.junit.jupiter.api.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

/**
 * 单元测试
 *
 * @author fengbingjian
 * @description 请不要使用行尾注释
 * @since 2024/9/26 17:35
 **/
public class AvoidCommentBehindStatementRuleTest {

    @Test
    void check() {

        JavaCheckVerifier.newVerifier()
                .onFile("src/test/files/comment/AvoidCommentBehindStatementRule.java")
                .withCheck(new AvoidCommentBehindStatementRule())
                .verifyIssues();
    }
}