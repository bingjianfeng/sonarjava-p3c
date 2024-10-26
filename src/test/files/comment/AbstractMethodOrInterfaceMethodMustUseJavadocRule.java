package comment;

/**
 * Demo
 */
public abstract class BaseExample {

    public abstract String testNoComment(String str) throws Exception; // Noncompliant {{抽象方法【testNoComment】必须使用javadoc注释}}

    /**
     * test
     *
     * @return
     * @throws Exception
     */
    public abstract String test(String str) throws Exception; // Noncompliant {{方法【test】的参数【str】缺少javadoc注释}}

    /**
     * testCompliant
     *
     * @param str
     * @return
     * @throws Exception
     */
    public abstract String testCompliant(String str) throws Exception; // Compliant

}

public interface InterfaceExample {

    public abstract String testNoCommentForInterface(String str) throws Exception; // Noncompliant {{接口方法【testNoCommentForInterface】必须使用javadoc注释}}

    /**
     * @param str
     * @return
     * @throws Exception
     */
    public abstract String testInterface(String str) throws Exception; // Noncompliant {{请详细描述方法【testInterface】的功能与意图}}


    /**
     * testCompliant
     *
     * @param str
     * @param str2
     * @return
     * @throws Exception
     */
    public abstract String testCompliantForInterface(String str, String str2) throws Exception; // Compliant

}