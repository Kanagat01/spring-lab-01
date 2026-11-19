package kz.iitu.spring_lab_01.web;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.flywaydb.core.Flyway;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kz.iitu.spring_lab_01.bank.AccountRepository;
import kz.iitu.spring_lab_01.bank.TransferService;

@RestController
@RequestMapping("/api/lab6")
public class Lab6Controller {

    public record AccountView(Long id, String ownerName, BigDecimal balance, Long version) {
    }

    public record MigrationView(String version, String description, String script, String state, Integer checksum) {
    }

    private final AccountRepository accountRepository;
    private final TransferService transferService;
    private final Flyway flyway;

    public Lab6Controller(AccountRepository accountRepository, TransferService transferService, Flyway flyway) {
        this.accountRepository = accountRepository;
        this.transferService = transferService;
        this.flyway = flyway;
    }

    @GetMapping("/accounts")
    public List<AccountView> accounts() {
        return accountRepository.findAll().stream()
                .map(a -> new AccountView(a.getId(), a.getOwnerName(), a.getBalance(), a.getVersion()))
                .toList();
    }

    @PostMapping("/transfer")
    public List<AccountView> transfer(@RequestParam Long from,
            @RequestParam Long to,
            @RequestParam BigDecimal amount) {
        transferService.transfer(from, to, amount);
        return accounts();
    }

    // Reads the same data as the flyway_schema_history table
    @GetMapping("/migrations")
    public List<MigrationView> migrations() {
        return Arrays.stream(flyway.info().all())
                .map(m -> new MigrationView(
                        m.getVersion() == null ? null : m.getVersion().getVersion(),
                        m.getDescription(),
                        m.getScript(),
                        m.getState().name(),
                        m.getChecksum()))
                .toList();
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> conflict(IllegalStateException ex) {
        return error(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> badRequest(IllegalArgumentException ex) {
        return error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> notFound(NoSuchElementException ex) {
        return error(HttpStatus.NOT_FOUND, "Account not found");
    }

    private ResponseEntity<Map<String, String>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of("error", String.valueOf(message)));
    }
}