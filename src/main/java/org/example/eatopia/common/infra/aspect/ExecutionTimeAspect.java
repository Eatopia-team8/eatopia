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
        long start = System.nanoTime();

        Object result = joinPoint.proceed();

        long executionTimeNano = System.nanoTime() - start;

        double executionTimeMs = (double) executionTimeNano / 1_000_000.0;

        String formattedExecutionTime = String.format("%.2f", executionTimeMs);

        String label = logExecutionTime.value().isEmpty()
                ? joinPoint.getSignature().toShortString()
                : logExecutionTime.value();

        log.info("⏱️ {} 실행 시간: {}ms", label, formattedExecutionTime);

        return result;
    }
}
