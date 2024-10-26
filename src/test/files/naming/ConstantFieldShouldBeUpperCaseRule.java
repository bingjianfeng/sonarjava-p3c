class ConstantFieldShouldBeUpperCaseRule {
    public static final Long maxStockCount = 50000L; // Noncompliant {{常量【maxStockCount】命名应全部大写并以下划线分隔}}
    public static final Long MAX_STOCK_COUNT = 50000L; // Compliant
}