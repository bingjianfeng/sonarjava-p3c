public class PojoExampleDo {
    private String name = "jams"; // Noncompliant {{字段【name】不应该加默认值}}
    private Boolean vip; // Compliant

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getVip() {
        return vip;
    }

    public void setVip(Boolean vip) {
        this.vip = vip;
    }
}