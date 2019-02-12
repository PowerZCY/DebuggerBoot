package com.xyz.caofancpu.interceptor;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * FileName: MapperAspect
 *
 * @author: caofanCPU
 * @date: 2019/1/25 11:26
 */
@Aspect
@Component
public class MapperAspect {
    
    @Around(value = "execution(* com.xyz.*.mapper..*Mapper.*(..))")
    public Object aroundMethod(ProceedingJoinPoint pjp)
            throws Throwable {
        Object result;
        MethodSignature joinPointObject = (MethodSignature) pjp.getSignature();
        Method method = joinPointObject.getMethod();
        Transaction t = Cat.newTransaction("SQL", method.getName());
        try {
            result = pjp.proceed();
            t.setStatus(Transaction.SUCCESS);
            t.complete();
        } catch (Throwable e) {
            t.setStatus(e);
            Cat.logError(e);
            t.complete();
            throw e;
        }
        return result;
    }
}