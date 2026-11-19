package kz.iitu.spring_lab_01.bank;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;

class OptimisticLockingTest extends BankTestBase {

    @Autowired
    private AccountRepository repository;

    @Test
    void d1_staleCopyCannotOverwriteNewerData() {
        // two independent copies of the same row (each findById is its own transaction)
        Account first = repository.findById(aliceId).orElseThrow();
        Account second = repository.findById(aliceId).orElseThrow();
        log("D1 loaded two copies: first.version=" + first.getVersion()
                + ", second.version=" + second.getVersion());

        first.credit(new BigDecimal("10.00"));
        repository.save(first);
        log("D1 first copy committed: balance=" + balanceOf(aliceId) + ", version in DB=" + versionOf(aliceId));

        second.credit(new BigDecimal("20.00"));
        Throwable error = catchThrowable(() -> repository.save(second));
        log("D1 stale copy rejected: " + error.getClass().getSimpleName() + " - " + error.getMessage());

        assertThat(error).isInstanceOf(OptimisticLockingFailureException.class);
        assertThat(balanceOf(aliceId)).isEqualByComparingTo("1010.00");
        assertThat(versionOf(aliceId)).isEqualTo(1L);
    }

    @Test
    void d2_conflictIsHandledByReloadAndRetry() {
        Account first = repository.findById(aliceId).orElseThrow();
        Account second = repository.findById(aliceId).orElseThrow();

        first.credit(new BigDecimal("10.00"));
        repository.save(first);

        second.credit(new BigDecimal("20.00"));
        Throwable error = catchThrowable(() -> repository.save(second));
        assertThat(error).isInstanceOf(OptimisticLockingFailureException.class);

        // safe response: reload the fresh row and repeat the operation on it
        Account fresh = repository.findById(aliceId).orElseThrow();
        fresh.credit(new BigDecimal("20.00"));
        repository.save(fresh);

        log("D2 after reload and retry: balance=" + balanceOf(aliceId) + ", version=" + versionOf(aliceId));
        assertThat(balanceOf(aliceId)).isEqualByComparingTo("1030.00");
        assertThat(versionOf(aliceId)).isEqualTo(2L);
    }
}