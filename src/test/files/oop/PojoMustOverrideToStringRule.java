public class PojoExampleEntity { // Noncompliant {{POJO类必须写toString方法}}
    private String name;
    private Boolean vip;

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

public class PojoToStringExampleEntity { // Compliant
    private String name;
    private Boolean vip;

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

    public String toString(){
        return "name:"+name+",vip:"+vip;
    }
}