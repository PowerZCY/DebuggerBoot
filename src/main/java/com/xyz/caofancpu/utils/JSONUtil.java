package com.xyz.caofancpu.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Created by caofanCPU on 2018/8/6.
 */
public class JSONUtil {
    
    /**
     * LOG
     */
    private static final Logger logger = LoggerFactory.getLogger(JSONUtil.class);
    
    /**
     * 转换为标准格式JSON化字符串
     *
     * @param jsonStr
     * @return
     */
    public static String formatStandardJSON(String jsonStr) {
        String start = "    ";
        // 用户标记层级
        int level = 0;
        StringBuilder jsonResultStr = new StringBuilder();
        // 循环遍历每一个字符
        for (int i = 0; i < jsonStr.length(); i++) {
            // 获取当前字符
            char piece = jsonStr.charAt(i);
            // 如果上一个字符是断行，则在本行开始按照level数值添加标记符，排除第一行
            if (i != 0 && '\n' == jsonResultStr.charAt(jsonResultStr.length() - 1)) {
                for (int k = 0; k < level; k++) {
                    jsonResultStr.append(start);
                }
            }
            switch (piece) {
                case '{':
                case '[':
                    // 如果字符是{或者[，则断行，level加1
                    jsonResultStr.append(piece + "\n");
                    level++;
                    break;
                case ',':
                    // 如果是","，则断行
                    jsonResultStr.append(piece + "\n");
                    break;
                case '}':
                case ']':
                    // 如果是"}"或者"]"，则断行，level减1
                    jsonResultStr.append("\n");
                    level--;
                    for (int k = 0; k < level; k++) {
                        jsonResultStr.append(start);
                    }
                    jsonResultStr.append(piece);
                    break;
                default:
                    jsonResultStr.append(piece);
                    break;
            }
        }
        return jsonResultStr.toString().replaceAll("\n\n", "\n");
    }
    
    /**
     * 对象序列化为JSONString
     *
     * @param object
     * @return
     */
    public static String serializeJSON(Object object) {
        return JSONObject.toJSONString(object, SerializerFeature.WriteClassName);
    }
    
    /**
     * 对象序列化为标准格式的JSONString
     *
     * @param object
     * @return
     */
    public static String serializeStandardJSON(Object object) {
        String jsonString = JSONObject.toJSONString(object, SerializerFeature.WriteClassName);
        return formatStandardJSON(jsonString);
    }
    
    /**
     * 对象解析为JSONArray
     * 可能出现:  com.alibaba.fastjson.JSONException: autoType is not support ...
     * 解决办法: 配置JVM启动参数: -Dfastjson.parser.autoTypeAccept=序列化对象包根目录1. , 序列化对象包根目录2. 等
     * 说明: Spring Boot / Spring + Tomcat 启动默认支持 自动类型解析的
     *
     * @param jsonArray
     * @return
     */
    public static JSONArray parseJSONArray(Object jsonArray) {
        String jsonString = serializeJSON(jsonArray);
        return JSONObject.parseArray(jsonString);
    }
    
    /**
     * JSONArray转List, 用Bean接收
     *
     * @param jsonArray
     * @return
     */
    public static <T> List<T> arrayShiftToBean(Object jsonArray, Class<T> clazz) {
        if (Objects.isNull(jsonArray) || Objects.isNull(clazz)) {
            throw new NullPointerException();
        }
        JSONArray array = parseJSONArray(jsonArray);
        List<T> resultList = new ArrayList<>();
        array.stream()
                .filter(Objects::nonNull)
                .forEach(item -> {
                    try {
                        T result = BeanConvertUtil.copyProperties(item, clazz);
                        resultList.add(result);
                    } catch (IllegalAccessException e) {
                        logger.info("JSONArray转换失败! {}", e.getMessage());
                        return;
                    } catch (InstantiationException e) {
                        logger.info("JSONArray转换失败! {}", e.getMessage());
                        return;
                    } catch (InvocationTargetException e) {
                        logger.info("JSONArray转换失败! {}", e.getMessage());
                        return;
                    }
                });
        return resultList;
    }
    
    /**
     * JSONArray转List, 用Map<String, Object>接收
     *
     * @param jsonArray
     * @return
     */
    public static List<Map<String, Object>> arrayShiftToMap(Object jsonArray) {
        if (Objects.isNull(jsonArray)) {
            throw new NullPointerException();
        }
        JSONArray array = parseJSONArray(jsonArray);
        List<Map<String, Object>> resultList = new ArrayList<>();
        array.stream()
                .filter(Objects::nonNull)
                .forEach(item -> {
                    try {
                        Map<String, Object> result = BeanConvertUtil.copyProperties(item, HashMap.class);
                        resultList.add(result);
                    } catch (IllegalAccessException e) {
                        logger.info("JSONArray转换失败! {}", e.getMessage());
                        return;
                    } catch (InstantiationException e) {
                        logger.info("JSONArray转换失败! {}", e.getMessage());
                        return;
                    } catch (InvocationTargetException e) {
                        logger.info("JSONArray转换失败! {}", e.getMessage());
                        return;
                    }
                });
        return resultList;
    }
    
    /**
     * List转JSONArray
     *
     * @param sourceList
     * @return
     */
    public static JSONArray shiftToJSONArray(List<?> sourceList) {
        if (CollectionUtils.isEmpty(sourceList)) {
            throw new IllegalArgumentException("sourceList数据为空!");
        }
        JSONArray resultArray = new JSONArray();
        sourceList.stream()
                .filter(Objects::nonNull)
                .forEach(item -> resultArray.add(item));
        return resultArray;
    }
    
    public static void main(String[] args) {
    
    }
    
}
