package kz.iitu.spring_lab_01.bank;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
abstract class BankTestBase {

    @Autowired
    protected JdbcTemplate jdbc;

    protected Long aliceId;
    protected Long bobId;

    @BeforeEach
    void resetData() {
        jdbc.update("DELETE FROM audit_log");
        jdbc.update("UPDATE accounts SET balance = 1000.00, version = 0 WHERE owner_name = 'Alice'");
        jdbc.update("UPDATE accounts SET balance = 500.00, version = 0 WHERE owner_name = 'Bob'");
        aliceId = jdbc.queryForObject("SELECT id FROM accounts WHERE owner_name = 'Alice'", Long.class);
        bobId = jdbc.queryForObject("SELECT id FROM accounts WHERE owner_name = 'Bob'", Long.class);
    }

    protected BigDecimal balanceOf(Long id) {
        return jdbc.queryForObject("SELECT balance FROM accounts WHERE id = ?", BigDecimal.class, id);
    }

    protected Long versionOf(Long id) {
        return jdbc.queryForObject("SELECT version FROM accounts WHERE id = ?", Long.class, id);
    }

    protected void log(String message) {
        System.out.println("[LAB6] " + message);
    }
}