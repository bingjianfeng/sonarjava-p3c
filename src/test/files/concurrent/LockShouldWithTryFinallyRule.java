package concurrent;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockInTryBlockExample {
    public void check() {
        Lock lock = new ReentrantLock();
        try {
            doSomething();
            lock.lock(); // Noncompliant
            doOthers();
        } finally {
            lock.unlock();
        }
    }

    private void doSomething() {
    }

    private void doOthers() {
    }
}

public class LockBeforeTryExample {
    public void check() {
        Lock lock = new ReentrantLock();
        lock.lock(); // Compliant



        try {
            doSomething();
            doOthers();
        } finally {
            lock.unlock();
        }
    }

    private void doSomething() {
    }

    private void doOthers() {
    }
}

public class LockWithoutUnlockExample {
    public void check() {
        Lock lock = new ReentrantLock();
        lock.lock(); // Noncompliant
        try {
            doSomething();
            doOthers();
        } finally {
        }
    }

    private void doSomething() {
    }

    private void doOthers() {
    }
}

public class UnlockNotInFinallyExample {
    public void check() {
        Lock lock = new ReentrantLock();
        lock.lock(); // Noncompliant
        try {
            doSomething();
            doOthers();
            lock.unlock();
        } finally {
        }
    }

    private void doSomething() {
    }

    private void doOthers() {
    }
}

public class UnlockNotInFinallyFirstLineExample {
    public void check() {
        Lock lock = new ReentrantLock();
        lock.lock(); // Noncompliant
        try {
            doSomething();
        } finally {
            doOthers();
            lock.unlock();
        }
    }

    private void doSomething() {
    }

    private void doOthers() {
    }
}