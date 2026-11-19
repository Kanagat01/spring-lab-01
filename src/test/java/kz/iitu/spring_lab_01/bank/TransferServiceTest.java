package kz.iitu.spring_lab_01.bank;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TransferServiceTest extends BankTestBase {

    @Autowired
    private TransferService transferService;

    @Test
    void a1_successfulTransfer() {
        transferService.transfer(aliceId, bobId, new BigDecimal("100.00"));

        log("A1 success: Alice=" + balanceOf(aliceId) + ", Bob=" + balanceOf(bobId));
        assertThat(balanceOf(aliceId)).isEqualByComparingTo("900.00");
        assertThat(balanceOf(bobId)).isEqualByComparingTo("600.00");
    }

    @Test
    void a2_insufficientFundsChangesNothing() {
        assertThatThrownBy(() -> transferService.transfer(aliceId, bobId, new BigDecimal("5000.00")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Insufficient funds");

        log("A2 insufficient funds: Alice=" + balanceOf(aliceId) + ", Bob=" + balanceOf(bobId));
        assertThat(balanceOf(aliceId)).isEqualByComparingTo("1000.00");
        assertThat(balanceOf(bobId)).isEqualByComparingTo("500.00");
    }

    @Test
    void a3_uncheckedFailureAfterDebitRollsBack() {
        assertThatThrownBy(() -> transferService.transferUncheckedFailure(aliceId, bobId, new BigDecimal("100.00")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Simulated crash after debit");

        log("A3 unchecked exception: Alice=" + balanceOf(aliceId) + ", Bob=" + balanceOf(bobId));
        assertThat(balanceOf(aliceId)).isEqualByComparingTo("1000.00");
        assertThat(balanceOf(bobId)).isEqualByComparingTo("500.00");
    }

    @Test
    void a4_checkedFailureDoesNotRollBackByDefault() {
        assertThatThrownBy(() -> transferService.transferCheckedFailure(aliceId, bobId, new BigDecimal("100.00")))
                .isInstanceOf(IOException.class);

        log("A4a checked exception, default rules: Alice=" + balanceOf(aliceId) + ", Bob=" + balanceOf(bobId)
                + " (debit was COMMITTED, money disappeared)");
        assertThat(balanceOf(aliceId)).isEqualByComparingTo("900.00");
        assertThat(balanceOf(bobId)).isEqualByComparingTo("500.00");
    }

    @Test
    void a4_checkedFailureWithRollbackForRollsBack() {
        assertThatThrownBy(() -> transferService.transferCheckedFailureWithRollbackFor(aliceId, bobId,
                new BigDecimal("100.00")))
                .isInstanceOf(IOException.class);

        log("A4b checked exception, rollbackFor=Exception.class: Alice=" + balanceOf(aliceId)
                + ", Bob=" + balanceOf(bobId));
        assertThat(balanceOf(aliceId)).isEqualByComparingTo("1000.00");
        assertThat(balanceOf(bobId)).isEqualByComparingTo("500.00");
    }
}