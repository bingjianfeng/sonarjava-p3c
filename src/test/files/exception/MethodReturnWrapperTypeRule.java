package concurrent;

import java.util.List;
import java.util.ArrayList;

public class MethodReturnWrapperTypeRuleExample {
    public int check() {
        Integer num = null;
        return num; // Noncompliant {{返回类型为基本数据类型【int】，return包装数据类型的对象【java.lang.Integer】时，自动拆箱有可能产生NPE的风险}}
    }
    public boolean check2() {
        Boolean isTrue = true;
        return isTrue; // Noncompliant {{返回类型为基本数据类型【boolean】，return包装数据类型的对象【java.lang.Boolean】时，自动拆箱有可能产生NPE的风险}}
    }
    public long check3() {
        long t = 5L;
        return t; // Compliant
    }
    public Double check4() {
        Double t = 3.5;
        return t; // Compliant
    }
    public int check5(Integer a, Integer b) {
        return a + b; // Compliant
    }
    public int check6(Integer a, Integer b) {
        List<Integer> list = new ArrayList<>();
        list.add(a + b);
        return list.get(0); // Noncompliant {{返回类型为基本数据类型【int】，return包装数据类型的对象【java.lang.Integer】时，自动拆箱有可能产生NPE的风险}}
    }
}