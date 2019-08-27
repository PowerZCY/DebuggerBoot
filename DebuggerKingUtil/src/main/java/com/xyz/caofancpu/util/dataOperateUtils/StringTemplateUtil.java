package com.xyz.caofancpu.util.dataOperateUtils;

import com.alibaba.fastjson.JSONObject;
import com.xyz.caofancpu.util.commonOperateUtils.NormalUseUtil;
import com.xyz.caofancpu.util.streamOperateUtils.CollectionUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * FileName: StringTemplateUtil
 * Author:   caofanCPU
 * Date:     2018/11/15 17:00
 */
@Slf4j
public class StringTemplateUtil {
    public static final String TEMPLATE_KEY_PRIFIX = "\\$\\{";
    public static final String TEMPLATE_KEY_SUFFIX = "\\}";

    public static String processTemplate(String templateContent, Object source) {
        if (Objects.isNull(templateContent)
                || Objects.isNull(source)) {
            return templateContent;
        }
        Map<String, Object> paramMap = JSONObject.parseObject(JSONObject.toJSONString(source));
        return processTemplate(templateContent, paramMap);
    }

    public static String processTemplate(String templateContent, Map<String, Object> paramMap) {
        if (Objects.isNull(templateContent) || CollectionUtil.isEmpty(paramMap)) {
            return templateContent;
        }
        Set<Map.Entry<String, Object>> entrySet = paramMap.entrySet();
        for (Map.Entry<String, Object> entry : entrySet) {
            String regex = TEMPLATE_KEY_PRIFIX + entry.getKey() + TEMPLATE_KEY_SUFFIX;
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(templateContent);
            templateContent = matcher.replaceAll(NormalUseUtil.convertToString(entry.getValue()));
        }
        return templateContent;
    }

}
