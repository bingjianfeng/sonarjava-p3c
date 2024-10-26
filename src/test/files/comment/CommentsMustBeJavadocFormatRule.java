package comment;

/*
 * Demo
 */
public class MultiLineClassExample { // Noncompliant {{类【MultiLineClassExample】必须使用javadoc形式的注释}}

}

// Demo
public class SingleLineClassExample { // Noncompliant {{类【SingleLineClassExample】必须使用javadoc形式的注释}}

}

/**
 * Demo
 */
public class DocsLineClassExample { // Compliant

}

/**
 * Demo
 */
public class SingleLineAttbExample {

    //demo
    private String demo; // Noncompliant {{字段【demo】必须使用javadoc形式的注释}}

    /**
     * Test
     */
    private void test() {
        private String testStr; // Compliant
    }

    /*
     * Test
     */
    private void multiLineMethod() { // Noncompliant {{方法【multiLineMethod】必须使用javadoc形式的注释}}

    }


    /*
     * Test
     */
    public enum CommentEnum { // Noncompliant {{类【CommentEnum】必须使用javadoc形式的注释}}

        // 单行注释
        SINGLE_LINE("//", "单行注释"), // Noncompliant {{枚举【SINGLE_LINE】必须使用javadoc形式的注释}}

        /*
         * 多行注释
         */
        MULTI_LINE("/* ... */", "多行注释"), // Noncompliant {{枚举【MULTI_LINE】必须使用javadoc形式的注释}}

        /**
         * 文档注释
         */
        DOCUMENTATION("/** ... */", "文档注释"), //Compliant
        UNKNOW("", "未知");

        private final String syntax;
        private final String description;

        CommentEnum(String syntax, String description) {
            this.syntax = syntax;
            this.description = description;
        }

        public String getSyntax() {
            return syntax;
        }

        public String getDescription() {
            return description;
        }

    }
}

