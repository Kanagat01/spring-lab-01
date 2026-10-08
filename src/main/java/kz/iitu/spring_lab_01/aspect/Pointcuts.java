package kz.iitu.spring_lab_01.aspect;

import org.aspectj.lang.annotation.Pointcut;

public class Pointcuts {

    @Pointcut("within(kz.iitu.spring_lab_01.service..*)")
    public void serviceLayer() {
    }

    @Pointcut("execution(public * *(..))")
    public void publicMethod() {
    }

    @Pointcut("serviceLayer() && publicMethod()")
    public void serviceOperation() {
    }
}