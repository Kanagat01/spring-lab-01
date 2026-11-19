package kz.iitu.spring_lab_01.bank;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PropagationDemoService {

    private final AuditLogService auditLogService;

    public PropagationDemoService(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @Transactional
    public void outerWithRequired(String message) {
        auditLogService.recordRequired(message);
        throw new IllegalStateException("Outer transaction fails after the inner method returned");
    }

    @Transactional
    public void outerWithRequiresNew(String message) {
        auditLogService.recordRequiresNew(message);
        throw new IllegalStateException("Outer transaction fails after the inner method returned");
    }
}