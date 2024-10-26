package com.elvis.sonar.java.checks.concurrent;

import org.junit.jupiter.api.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

/**
 * 单元测试
 *
 * @author fengbingjian
 * @description SimpleDateFormat 是线程不安全的类，一般不要定义为static变量，如果定义为static，必须加锁，或者使用DateUtils工具类。
 * @since 2024/9/26 17:35
 **/
public class AvoidCallStaticSimpleDateFormatRuleTest {

    @Test
    void check() {

        JavaCheckVerifier.newVerifier()
                .onFile("src/test/files/concurrent/AvoidCallStaticSimpleDateFormatRule.java")
                .withCheck(new AvoidCallStaticSimpleDateFormatRule())
                .verifyIssues();
    }
}