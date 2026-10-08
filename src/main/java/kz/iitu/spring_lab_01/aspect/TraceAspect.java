package kz.iitu.spring_lab_01.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(4)
public class TraceAspect {

    private static final Logger log = LoggerFactory.getLogger(TraceAspect.class);

    private final ThreadLocal<Integer> depth = ThreadLocal.withInitial(() -> 0);

    @Before("Pointcuts.serviceOperation()")
    public void enter(JoinPoint jp) {
        int d = depth.get();
        log.info("[TRACE] {}> {}", "  ".repeat(d), jp.getSignature().toShortString());
        depth.set(d + 1);
    }

    @After("Pointcuts.serviceOperation()")
    public void exit(JoinPoint jp) {
        int d = depth.get() - 1;
        depth.set(d);
        log.info("[TRACE] {}< {}", "  ".repeat(d), jp.getSignature().toShortString());
        if (d == 0) {
            depth.remove();
        }
    }
}