package com.elvis.sonar.java.checks.exception;

import org.junit.jupiter.api.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

/**
 * 单元测试
 *
 * @author fengbingjian
 * @description 返回类型为基本数据类型，return包装数据类型的对象时，自动拆箱有可能产生NPE的风险
 * @since 2024/9/26 17:35
 **/
public class MethodReturnWrapperTypeRuleTest {

    @Test
    void check() {

        JavaCheckVerifier.newVerifier()
                .onFile("src/test/files/exception/MethodReturnWrapperTypeRule.java")
                .withCheck(new MethodReturnWrapperTypeRule())
                .verifyIssues();
    }
}