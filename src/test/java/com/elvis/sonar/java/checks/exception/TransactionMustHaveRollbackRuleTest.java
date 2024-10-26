//package com.elvis.sonar.java.checks.exception;
//
//import org.junit.jupiter.api.Test;
//import org.sonar.java.checks.verifier.JavaCheckVerifier;
//
///**
// * 单元测试
// *
// * @author fengbingjian
// * @description 注解【Transactional】需要设置rollbackFor属性
// * @since 2024/9/26 17:35
// **/
//public class TransactionMustHaveRollbackRuleTest {
//
//    @Test
//    void check() {
//
//        JavaCheckVerifier.newVerifier()
//                .onFile("src/test/files/exception/TransactionMustHaveRollbackRule.java")
//                .withCheck(new TransactionMustHaveRollbackRule())
//                .verifyIssues();
//    }
//}