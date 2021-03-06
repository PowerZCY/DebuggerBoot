package com.xyz.caofancpu.util.commonoperateutils;

import com.xyz.caofancpu.util.result.GlobalErrorInfoException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * @author caofanCPU
 */
@Slf4j
public class UrlUtil {
    public static final String UTF_TYPE = "utf-8";

    /**
     * 对URL进行指定格式的转码
     *
     * @param url
     * @param contextType
     * @return
     */
    public static String encodeUrl(String url, final String contextType)
            throws GlobalErrorInfoException {
        if (StringUtils.isNotEmpty(url)) {
            try {
                return URLEncoder.encode(url, contextType);
            } catch (UnsupportedEncodingException e) {
                log.error("url编码失败, 原因: {}", e);
            }
        }
        throw new GlobalErrorInfoException("URL参数不能为空!");
    }

    /**
     * 对url地址以特定的编码进行解码
     *
     * @param url
     * @param contextType
     * @return
     */
    private static String decodeUrl(String url, final String contextType)
            throws GlobalErrorInfoException {
        if (StringUtils.isNotEmpty(url)) {
            try {
                return URLDecoder.decode(url, contextType);
            } catch (UnsupportedEncodingException e) {
                log.error("URL解码失败, 原因: {}", e);
            }
        }
        throw new GlobalErrorInfoException("URL参数不能为空!");
    }

}
