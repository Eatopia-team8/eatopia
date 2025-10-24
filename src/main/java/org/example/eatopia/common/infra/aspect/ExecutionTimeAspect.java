package org.example.eatopia.common.infra.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.eatopia.common.core.annotation.LogExecutionTime;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ExecutionTimeAspect {

    @Around("@annotation(logExecutionTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint, LogExecutionTime logExecutionTime) throws Throwable {
        long start = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long executionTime = System.currentTimeMillis() - start;

        String label = logExecutionTime.value().isEmpty()
                ? joinPoint.getSignature().toShortString()
                : logExecutionTime.value();

        log.info("⏱️ {} 실행 시간: {}ms", label, executionTime);

        return result;
    }
}
