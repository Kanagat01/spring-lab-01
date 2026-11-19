package kz.iitu.spring_lab_01.bank;

import java.io.IOException;
import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransferService {

    private final AccountRepository repository;

    public TransferService(AccountRepository repository) {
        this.repository = repository;
    }

    // Normal transfer: both changes are committed together or not at all.
    @Transactional
    public void transfer(Long fromId, Long toId, BigDecimal amount) {
        Account from = repository.findById(fromId).orElseThrow();
        Account to = repository.findById(toId).orElseThrow();
        from.debit(amount);
        to.credit(amount);
        repository.save(from);
        repository.save(to);
    }

    // Test case 3: unchecked exception between debit and credit -> rollback.
    @Transactional
    public void transferUncheckedFailure(Long fromId, Long toId, BigDecimal amount) {
        Account from = repository.findById(fromId).orElseThrow();
        Account to = repository.findById(toId).orElseThrow();
        from.debit(amount);
        repository.saveAndFlush(from); // the UPDATE really goes to the database here
        crashUnchecked();
        to.credit(amount);
        repository.save(to);
    }

    // Test case 4a: checked exception, default rules -> NO rollback, debit is
    // committed.
    @Transactional
    public void transferCheckedFailure(Long fromId, Long toId, BigDecimal amount) throws IOException {
        Account from = repository.findById(fromId).orElseThrow();
        Account to = repository.findById(toId).orElseThrow();
        from.debit(amount);
        repository.saveAndFlush(from);
        crashChecked();
        to.credit(amount);
        repository.save(to);
    }

    // Test case 4b: the same, but rollbackFor makes the checked exception roll back
    // too.
    @Transactional(rollbackFor = Exception.class)
    public void transferCheckedFailureWithRollbackFor(Long fromId, Long toId, BigDecimal amount) throws IOException {
        Account from = repository.findById(fromId).orElseThrow();
        Account to = repository.findById(toId).orElseThrow();
        from.debit(amount);
        repository.saveAndFlush(from);
        crashChecked();
        to.credit(amount);
        repository.save(to);
    }

    private void crashUnchecked() {
        throw new IllegalStateException("Simulated crash after debit");
    }

    private void crashChecked() throws IOException {
        throw new IOException("Simulated checked failure after debit");
    }
}