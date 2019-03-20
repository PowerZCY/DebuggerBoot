package com.xyz.caofancpu.service.configValue;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

/**
 * FileName: CommonConfigService
 * Author:   caofanCPU
 * Date:     2018/11/24 10:01
 * 该类用于统一封装其他serviceIml所需要的注入变量或 公用配置
 */
@Service("msUrlConfigValueService")
@DependsOn("initContextProperty")
public class MSUrlConfigValueService {
    
    @Value("${ms.file.url}")
    public String fileAccessUrl;
    
}
