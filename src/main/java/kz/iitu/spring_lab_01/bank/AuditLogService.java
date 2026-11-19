package kz.iitu.spring_lab_01.bank;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditLogService {

    private final AuditLogRepository repository;

    public AuditLogService(AuditLogRepository repository) {
        this.repository = repository;
    }

    // Joins the caller's transaction (if there is one).
    @Transactional(propagation = Propagation.REQUIRED)
    public void recordRequired(String message) {
        repository.save(new AuditLog(message));
    }

    // Always runs in its own, separate transaction and commits on return.
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordRequiresNew(String message) {
        repository.save(new AuditLog(message));
    }
}