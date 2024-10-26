package concurrent;

import java.util.concurrent.CountDownLatch;

public class CountDownShouldInFinallyExample {

    public void check(CountDownLatch latch) { // Noncompliant {{使用CountDownLatch时，每个线程退出前必须在finally块中执行countDown()}}
        try{
            System.out.println("test");
        }catch(Exception e){

        }
    }

    public void check2(CountDownLatch latch) {
        try{
            System.out.println("test");
            latch.countDown(); // Noncompliant {{countDown()应该在finally块中调用}}
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void check3(CountDownLatch latch) { // Compliant
        try{
            System.out.println("test");
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            latch.countDown();
        }
    }

    public void check4(CountDownLatch latch) {
        System.out.println("test");
        latch.countDown(); // Noncompliant {{countDown()应该在finally块中调用}}
    }

    public void check5() { // Compliant
        System.out.println("test");
    }
}