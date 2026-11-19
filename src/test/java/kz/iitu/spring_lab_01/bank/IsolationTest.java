package kz.iitu.spring_lab_01.bank;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

class IsolationTest extends BankTestBase {

    record Observation(int isolation, BigDecimal firstRead, BigDecimal secondRead) {
    }

    @Autowired
    private PlatformTransactionManager transactionManager;

    // Transaction 1 reads the balance twice. Between the reads, transaction 2
    // (a separate transaction on a separate connection) adds 100 and commits.
    private Observation readTwice(int isolationLevel) {
        TransactionTemplate reader = new TransactionTemplate(transactionManager);
        reader.setIsolationLevel(isolationLevel);

        TransactionTemplate writer = new TransactionTemplate(transactionManager);
        writer.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        return reader.execute(status -> {
            int isolation = jdbc.execute((ConnectionCallback<Integer>) con -> con.getTransactionIsolation());
            BigDecimal firstRead = balanceOf(aliceId);

            writer.executeWithoutResult(
                    s -> jdbc.update("UPDATE accounts SET balance = balance + 100 WHERE id = ?", aliceId));

            BigDecimal secondRead = balanceOf(aliceId);
            return new Observation(isolation, firstRead, secondRead);
        });
    }

    @Test
    void c1_readCommittedSeesCommittedChange() {
        Observation o = readTwice(TransactionDefinition.ISOLATION_READ_COMMITTED);

        log("C1 READ_COMMITTED (connection isolation=" + o.isolation() + "): first read="
                + o.firstRead() + ", second read=" + o.secondRead());
        assertThat(o.firstRead()).isEqualByComparingTo("1000.00");
        assertThat(o.secondRead()).isEqualByComparingTo("1100.00");
    }

    @Test
    void c2_repeatableReadObservation() {
        Observation o = readTwice(TransactionDefinition.ISOLATION_REPEATABLE_READ);

        log("C2 REPEATABLE_READ (connection isolation=" + o.isolation() + "): first read="
                + o.firstRead() + ", second read=" + o.secondRead());
        assertThat(o.firstRead()).isEqualByComparingTo("1000.00");
        assertThat(balanceOf(aliceId)).isEqualByComparingTo("1100.00");
    }
}