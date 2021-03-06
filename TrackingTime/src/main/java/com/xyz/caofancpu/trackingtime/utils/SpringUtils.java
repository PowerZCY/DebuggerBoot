package com.xyz.caofancpu.trackingtime.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;


/**
 * SpringUtils for get Beans from IOC
 */
@Component
public class SpringUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext; // Spring应用上下文环境

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        SpringUtils.applicationContext = applicationContext;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name)
            throws BeansException {
        return (T) applicationContext.getBean(name);
    }

}