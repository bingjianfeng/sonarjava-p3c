package exception;

import java.util.List;
import java.util.ArrayList;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

@Transactional() // Noncompliant
public class TransactionOfClassMustHaveRollbackForExample {
    public int check() {
        Integer num = null;
    }
}


public class TransactionOfMethodMustHaveRollbackOrRollbackForExample {
    @Transactional() // Noncompliant
    public int check1() {
        Integer num = null;
    }
}


public class TransactionOfMethodHaveRollbackForExample {
    @Transactional(rollbackFor = Exception.class) // Compliant
    public int check2() {
        Integer num = null;
    }
}


public class TransactionOfMethodHaveRollbackMethodExample {

    @Autowired
    private DataSourceTransactionManager transactionManager;

    @Transactional() // Compliant
    public int check3() {
        TransactionStatus status = transactionManager.getTransaction(def);
        try {
            // execute your business logic here
            //db operation
        } catch (Exception ex) {
            transactionManager.rollback(status);
            throw ex;
        }
    }
}