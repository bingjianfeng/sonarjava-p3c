package com.elvis.sonar.java.checks.comment;

import org.junit.jupiter.api.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

/**
 * 单元测试
 *
 * @author fengbingjian
 * @description 所有的抽象方法必须要用javadoc注释、除了返回值、参数、异常说明外，还必须指出该方法的用途。
 * @since 2024/9/26 17:35
 **/
public class AbstractMethodOrInterfaceMethodMustUseJavadocRuleTest {

    @Test
    void check() {

        JavaCheckVerifier.newVerifier()
                .onFile("src/test/files/comment/AbstractMethodOrInterfaceMethodMustUseJavadocRule.java")
                .withCheck(new AbstractMethodOrInterfaceMethodMustUseJavadocRule())
                .verifyIssues();
    }
}