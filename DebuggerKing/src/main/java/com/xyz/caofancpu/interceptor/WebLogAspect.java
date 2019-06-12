package com.xyz.caofancpu.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.dianping.cat.message.Transaction;
import com.xyz.caofancpu.util.dataOperateUtils.JSONUtil;
import com.xyz.caofancpu.util.streamOperateUtils.StreamUtil;
import com.xyz.caofancpu.utils.DataHelper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

@Component
@Aspect
@Order(2)
public class WebLogAspect {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    /**
     * 日志切面
     */
    @Pointcut("execution(public * com.xyz..*.controller..*Controller.*(..))")
    public void webLog() {
        // do something
    }
    
    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) {
    }
    
    @Around("webLog()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint)
            throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String requestInterface = proceedingJoinPoint.getSignature().getDeclaringTypeName()
                + "."
                + proceedingJoinPoint.getSignature().getName();
        // 入参为文件时, 不打印log
        Map<String, Object> originRequestParamMap = DataHelper.getParameterMap(request);
        Map<String, Object> filteredFileValueMap = StreamUtil.removeSpecifiedElement(originRequestParamMap,
                new Class[]{MultipartFile.class, File.class});
        String requestParam = JSONUtil.formatStandardJSON(JSONObject.toJSONString(filteredFileValueMap));
        // 入参为文件时, 不打印log
        Object[] originBodyParamArray = proceedingJoinPoint.getArgs();
        Object[] filteredFileValueArray = StreamUtil.removeSpecifiedElement(originBodyParamArray,
                new Class[]{MultipartFile.class, File.class});
        String requestBody;
        try {
            requestBody = JSONUtil.formatStandardJSON(JSONObject.toJSONString(filteredFileValueArray));
        } catch (Exception e) {
            logger.info("入参为文件(InputStreamSource)或HttpRequest等类型, 打印对象地址信息");
            requestBody = Arrays.toString(proceedingJoinPoint.getArgs());
        }
        StringBuilder requestSb = new StringBuilder();
        requestSb.append("\n[前端页面请求]:\n"
                + "请求IP=" + getIpAddress(request) + "\n"
                + "请求方式=" + request.getMethod() + "\n"
                + "请求地址=" + request.getRequestURL().toString() + "\n"
                + "请求接口=" + requestInterface + "\n"
                + "请求Param参数=" + requestParam + "\n"
                + "请求Body对象=" + requestBody + "\n");
        logger.info(requestSb.toString());
        // CAT监控埋点
        Transaction requestT = Cat.newTransaction("URL.BS", request.getRequestURL().toString());
        Cat.logEvent("BS.请求", request.getRequestURL().toString(), Event.SUCCESS, requestSb.toString().replaceAll("\\s", ""));
        // 开始时间
        long startTime = System.currentTimeMillis();
        // 耗时, 字符串标识, @#为了便于标志区分
        final String execTime = "@Time#";
        Object result;
        
        StringBuilder responseSb = new StringBuilder();
        responseSb.append("\n[后台响应结果]:\n"
                + "URL地址=" + request.getRequestURL().toString() + "\n"
                + "后台接口=" + requestInterface + "\n"
                + "响应耗时[" + execTime + "ms]" + "\n"
                + "响应数据结果:\n"
        );
        try {
            result = proceedingJoinPoint.proceed();
        } catch (Throwable throwable) {
            String errMsg = Objects.nonNull(throwable.getMessage()) ? throwable.getMessage() : throwable.toString();
            Cat.logEvent("BS.响应", request.getRequestURL().toString(), Event.SUCCESS,
                    responseSb.append(errMsg).toString().replaceAll("\\s", "").replace(execTime, String.valueOf(System.currentTimeMillis() - startTime))
            );
            Cat.logError(throwable);
            // 结束请求埋点
            requestT.setStatus(errMsg);
            requestT.complete();
            // 抛出异常
            throw throwable;
        }
        // 处理完请求，返回内容
        responseSb.append(JSONUtil.formatStandardJSON(JSONObject.toJSONString(result)));
        logger.info(responseSb.toString().replace(execTime, String.valueOf(System.currentTimeMillis() - startTime)));
        // CAT监控埋点
        Transaction responseT = Cat.newTransaction("URL.BS", request.getRequestURL().toString());
        Cat.logEvent("BS.响应", request.getRequestURL().toString(), Event.SUCCESS,
                responseSb.toString().replaceAll("\\s", "").replace(execTime, String.valueOf(System.currentTimeMillis() - startTime))
        );
        responseT.setStatus(Transaction.SUCCESS);
        responseT.complete();
        requestT.setStatus(Transaction.SUCCESS);
        requestT.complete();
        return result;
    }
    
    @AfterReturning("webLog()")
    public void doAfterReturning(JoinPoint joinPoint) {
    
    }
    
    /**
     * 获取真实IP
     *
     * @param request
     * @return
     */
    private String getIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if ("127.0.0.1".equals(ipAddress) || "0:0:0:0:0:0:0:1".equals(ipAddress)) {
                //根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    logger.error("获取IP异常 : {}", e);
                }
                if (Objects.nonNull(inet)) {
                    ipAddress = inet.getHostAddress();
                }
            }
        }
        if (ipAddress != null && ipAddress.length() > 15) {
            if (ipAddress.indexOf(",") > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }
        return ipAddress;
    }
}