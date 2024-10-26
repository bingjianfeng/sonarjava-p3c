package comment;

/**
 * @description AvoidCommentBehindStatementExample
 * @since 2024/10/08 16:30
 */
public class TestClassExample { // Noncompliant {{【TestClassExample】缺少包含@author的注释信息}}

}

/**
 * @author fengbingjian
 * @description AvoidCommentBehindStatementExample
 * @since 2024/10/08 16:30
 */
public class AvoidCommentBehindStatementExample { // Compliant

}

/**
 * @description AvoidCommentBehindStatementExample
 * @since 2024/10/08 16:30
 */
public interface TestInterfaceExample { // Noncompliant {{【TestInterfaceExample】缺少包含@author的注释信息}}

}