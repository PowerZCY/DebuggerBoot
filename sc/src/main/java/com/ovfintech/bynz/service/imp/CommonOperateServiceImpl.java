package com.ovfintech.bynz.service.imp;


import com.ovfintech.bynz.model.Attachment;
import com.ovfintech.bynz.result.GlobalErrorInfoEnum;
import com.ovfintech.bynz.result.GlobalErrorInfoException;
import com.ovfintech.bynz.result.ResultBody;
import com.ovfintech.bynz.service.CommonOperateService;
import com.ovfintech.bynz.utils.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * ServiceImpl CommonOperate
 * This code is generated by the Hen, strongly recommended not to modify directly.
 */
@Service("commonOperateService")
public class CommonOperateServiceImpl implements CommonOperateService {
    
    /**
     * LOG
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonOperateServiceImpl.class);
    
    private static final String COMMA_SEPARATOR = ",";
    
    @Value("${app.name}")
    private String appName;
    
    @Value("${file.url}")
    private String fileAccessUrl;
    
    @Value("${authorization.user-profile.key}")
    private String authKey;
    
    @Resource
    private transient RestTemplate restTemplate;
    
    @Override
    public void uploadAttachment(Attachment attachment, MultipartFile file)
            throws GlobalErrorInfoException {
        if (file == null
                || file.isEmpty()) {
            throw new GlobalErrorInfoException(GlobalErrorInfoEnum.ILLEGAL_PARAMETER);
        } else {
            byte[] bytes;
            try {
                bytes = file.getBytes();
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
                throw new GlobalErrorInfoException(GlobalErrorInfoEnum.INTERNAL_ERROR);
            }
            
            String type = file.getOriginalFilename().toLowerCase();
            type = type.substring(type.lastIndexOf("."));
            // 上传服务器开始
            String path = UUID.randomUUID().toString().replaceAll("-", "") + type;
            String fileBase64 = Base64.getEncoder().encodeToString(bytes);
            Map<String, Object> map = new HashMap<>(8, 0.5f);
            map.put("appname", appName);
            map.put("filename", path);
            map.put("contents", fileBase64);
            map.put("open", false);
            
            ResultBody rb = restTemplate.postForObject(fileAccessUrl + "/file/upload", map, ResultBody.class);
            
            if (!"200".equals(rb.getCode())) {
                LOGGER.error("调用上传文件失败: {}", rb.getMsg());
                throw new GlobalErrorInfoException(GlobalErrorInfoEnum.CALL_SERVICE_ERROR);
            }
            attachment.setType(type);
            attachment.setName(path);
            attachment.setCreateTime(DateUtil.date2Str(new Date(), DateUtil.FORMAT_ALL));
            
            LOGGER.info("\n上传文件：[" + attachment.getCreateTime() + "]" + attachment.getName());
        }
    }
    
    @Override
    public String getAttachmentAccessUrl(String attachmentName)
            throws GlobalErrorInfoException {
        if (StringUtils.isEmpty(attachmentName)) {
            throw new GlobalErrorInfoException(GlobalErrorInfoEnum.ILLEGAL_PARAMETER);
        }
        Map<String, Object> map = new HashMap<>(4, 0.5f);
        map.put("appname", appName);
        map.put("filename", attachmentName);
        
        ResultBody rb = restTemplate.postForObject(fileAccessUrl + "/file/generateUrl", map, ResultBody.class);
        
        if (!"200".equals(rb.getCode())) {
            LOGGER.error("调用上传文件失败: {}", rb.getMsg());
            throw new GlobalErrorInfoException(GlobalErrorInfoEnum.CALL_SERVICE_ERROR);
        }
        String accessUrl = (String) rb.getData();
        LOGGER.info("查询文件访问Url: " + "\n输入文件名[" + attachmentName + "]\n输出Url[" + accessUrl + "]");
        return accessUrl;
    }
    
    @Override
    public String loadToken() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader(authKey);
        return token;
    }
    
}

