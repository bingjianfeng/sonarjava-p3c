package com.elvis.sonar.java.checks.comment;

import org.junit.jupiter.api.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

/**
 * 单元测试
 *
 * @author fengbingjian
 * @description 所有的类都必须添加创建者信息
 * @since 2024/9/26 17:35
 **/
public class ClassMustHaveAuthorRuleTest {

    @Test
    void check() {

        JavaCheckVerifier.newVerifier()
                .onFile("src/test/files/comment/ClassMustHaveAuthorRule.java")
                .withCheck(new ClassMustHaveAuthorRule())
                .verifyIssues();
    }
}