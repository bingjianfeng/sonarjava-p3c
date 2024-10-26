package com.elvis.sonar.java.checks.comment;

import org.junit.jupiter.api.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

/**
 * 单元测试
 *
 * @author fengbingjian
 * @description 类、类属性、类方法的注释必须使用javadoc规范
 * @since 2024/9/26 17:35
 **/
public class CommentsMustBeJavadocFormatRuleTest {

    @Test
    void check() {

        JavaCheckVerifier.newVerifier()
                .onFile("src/test/files/comment/CommentsMustBeJavadocFormatRule.java")
                .withCheck(new CommentsMustBeJavadocFormatRule())
                .verifyIssues();
    }
}