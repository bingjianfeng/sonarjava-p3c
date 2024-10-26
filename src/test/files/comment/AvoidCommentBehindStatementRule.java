package comment;

/**
 * Demo
 */
public abstract class AvoidCommentBehindStatementExample {

    private String str = "test"; //测试一下 // Noncompliant

    public void test() {
        int a = 0;
        int b = 1;
        for (int i = 0; i < 10; i++) {
            System.out.println("test");
        }
        if (a > b) {
            b = a;
            System.out.println("test1");
        }
        System.out.println("test"); //行尾 // Noncompliant
    }

}