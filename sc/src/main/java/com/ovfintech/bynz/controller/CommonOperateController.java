package com.ovfintech.bynz.controller;

import com.github.pagehelper.PageInfo;
import com.ovfintech.bynz.model.Attachment;
import com.ovfintech.bynz.result.GlobalErrorInfoEnum;
import com.ovfintech.bynz.result.GlobalErrorInfoException;
import com.ovfintech.bynz.result.ResultBody;
import com.ovfintech.bynz.service.CommonOperateService;
import com.ovfintech.bynz.service.SysDictService;
import com.ovfintech.bynz.utils.DataHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
public class CommonOperateController {
    
    /**
     * LOG
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonOperateController.class);
    
    @Resource
    private transient CommonOperateService commonOperateService;
    
    @Resource(type = SysDictService.class)
    private transient SysDictService sysDictService;
    
    /**
     * 上传附件
     *
     * @param file
     * @param request
     * @return
     * @throws GlobalErrorInfoException
     */
    @PostMapping("/attachment/upload")
    public ResultBody uploadAttachment(MultipartFile file, HttpServletRequest request)
            throws GlobalErrorInfoException {
        Attachment attachment = new Attachment();
        Map<String, Object> map = DataHelper.getParameterMap(request);
        try {
            DataHelper.putDataIntoEntity(map, attachment);
        } catch (Exception e) {
            LOGGER.error("MAP转换到Attachment出错", e);
            throw new GlobalErrorInfoException(GlobalErrorInfoEnum.INTERNAL_ERROR);
        }
        commonOperateService.uploadAttachment(attachment, file);
        return new ResultBody(attachment);
    }
    
    /**
     * 根据附件名称获取访问url
     *
     * @param attachmentName
     * @return
     * @throws GlobalErrorInfoException
     */
    @PostMapping("/attachment/getAccessUrl")
    public ResultBody getAccessUrl(@RequestParam(required = true) String attachmentName)
            throws GlobalErrorInfoException {
        
        String accessUrl = commonOperateService.getAttachmentAccessUrl(attachmentName);
        
        return new ResultBody(accessUrl);
    }
    
    @PostMapping("/sysDict/listByPage")
    public ResultBody listSysDictByPage() {
        PageInfo<List<Map<String, Object>>> resultPageInfo = sysDictService.getSysDictList();
        return new ResultBody(resultPageInfo);
    }
}
