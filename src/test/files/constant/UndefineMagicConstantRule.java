package concurrent;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UndefineMagicConstantRuleExample {
    public String check() {
        String key = "alibaba";
        if (key.equals("Id#taobao_1")) { // Noncompliant {{魔法值【"Id#taobao_1"】}}
            return "0";  // Noncompliant {{魔法值【"0"】}}
        }
        return key;
    }

    public String check2() {
        String key = "alibaba";
        String idtaobao = "Id#taobao_1";
        if (key.equals(idtaobao)) { // Compliant
            return idtaobao;  // Compliant
        }
        return key;
    }
}