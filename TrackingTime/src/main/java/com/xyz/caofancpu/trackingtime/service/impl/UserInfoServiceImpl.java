package com.xyz.caofancpu.trackingtime.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.xyz.caofancpu.trackingtime.mapper.UserInfoMapper;
import com.xyz.caofancpu.trackingtime.model.UserInfoMo;
import com.xyz.caofancpu.trackingtime.service.UserInfoService;
import com.xyz.caofancpu.trackingtime.view.UserInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * UserInfoMo对应的ServiceImpl
 *
 * @author Power+
 */
@Service
@Slf4j
public class UserInfoServiceImpl implements UserInfoService {

    @Resource
    private UserInfoMapper userInfoMapper;

    /**
     * 插入单条记录
     *
     * @param userInfoMo
     * @return
     */
    @Override
    public int add(UserInfoMo userInfoMo) {
        userInfoMo.setId(null);
        return userInfoMapper.insertWithId(userInfoMo);
    }

    /**
     * 批量插入
     *
     * @param userInfoMoList
     * @return
     */
    @Override
    public int batchAdd(List<UserInfoMo> userInfoMoList) {
        userInfoMoList.forEach(item -> item.setId(null));
        return userInfoMapper.insertBatchWithId(userInfoMoList);
    }

    /**
     * 查询列表, 如果携带分页参数则返回分页后的列表
     *
     * @param userInfoMo
     * @param pageParams 可选分页参数
     * @return
     */
    @Override
    public List<UserInfoVo> queryUserInfoMoList(UserInfoMo userInfoMo, Integer... pageParams) {
        if (Objects.nonNull(pageParams) && pageParams.length > 0) {
            int pageNum = pageParams[0];
            int pageSize = pageParams.length > 1 ? pageParams[1] : 10;
            PageHelper.startPage(pageNum, pageSize);
        }
        List<UserInfoMo> userInfoMoList = userInfoMapper.queryUserInfoMoList(userInfoMo);
        List<UserInfoVo> resultList = new ArrayList<>(userInfoMoList.size());
        for (UserInfoMo mo : userInfoMoList) {
            UserInfoVo vo = JSONObject.parseObject(JSONObject.toJSONString(mo), UserInfoVo.class);
            vo.setPageNum(null).setPageSize(null);
            resultList.add(vo);
        }
        return resultList;
    }

    /**
     * 根据id更新非null字段
     *
     * @param userInfoMo
     * @return
     */
    @Override
    public int updateSelectiveById(UserInfoMo userInfoMo) {
        return userInfoMapper.updateByPrimaryKeySelective(userInfoMo);
    }

    /**
     * 批量根据id更新非null字段
     *
     * @param userInfoMoList
     * @return
     */
    @Override
    public int batchUpdateSelectiveById(List<UserInfoMo> userInfoMoList) {
        return userInfoMapper.updateBatchByPrimaryKeySelective(userInfoMoList);
    }

    /**
     * 根据id物理删除
     *
     * @param id
     * @return
     */
    @Override
    public <T extends Number> int delete(T id) {
        return userInfoMapper.deleteByPrimaryKey(id);
    }

}