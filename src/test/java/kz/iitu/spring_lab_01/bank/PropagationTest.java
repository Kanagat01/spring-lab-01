package kz.iitu.spring_lab_01.bank;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PropagationTest extends BankTestBase {

    @Autowired
    private PropagationDemoService demoService;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Test
    void b1_requiredInnerIsRolledBackWithOuter() {
        assertThatThrownBy(() -> demoService.outerWithRequired("audit written with REQUIRED"))
                .isInstanceOf(IllegalStateException.class);

        long rows = auditLogRepository.count();
        log("B1 REQUIRED: outer failed, audit rows left = " + rows);
        assertThat(rows).isZero();
    }

    @Test
    void b2_requiresNewInnerSurvivesOuterRollback() {
        assertThatThrownBy(() -> demoService.outerWithRequiresNew("audit written with REQUIRES_NEW"))
                .isInstanceOf(IllegalStateException.class);

        long rows = auditLogRepository.count();
        log("B2 REQUIRES_NEW: outer failed, audit rows left = " + rows);
        assertThat(rows).isEqualTo(1);
    }
}