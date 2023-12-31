package com.api.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.api.common.exception.RpcBusinessException;
import com.api.common.errcode.ErrorCode;
import com.api.project.mapper.UserInterfaceInfoMapper;
import com.api.project.service.UserInterfaceInfoService;
import com.api.common.model.entity.UserInterfaceInfo;
import org.springframework.stereotype.Service;


@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
        implements UserInterfaceInfoService {

    @Override
    public void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add) {
        if (userInterfaceInfo == null) {
            throw new RpcBusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 创建时，所有参数必须非空
        if (add) {
            if (userInterfaceInfo.getInterfaceInfoId() <= 0 || userInterfaceInfo.getUserId() <= 0) {
                throw new RpcBusinessException(ErrorCode.PARAMS_ERROR, "接口或用户不存在");
            }
        }
        if (userInterfaceInfo.getLeftNum() < 0) {
            throw new RpcBusinessException(ErrorCode.PARAMS_ERROR, "剩余次数不能小于 0");
        }
    }

    @Override
    public boolean invokeCount(long interfaceInfoId, long userId) {
        // 判断
        if (interfaceInfoId <= 0 || userId <= 0) {
            throw new RpcBusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 如果不存在 || create 记录
        UserInterfaceInfo one = this.getOne(new QueryWrapper<UserInterfaceInfo>() {{
            eq("interfaceInfoId", interfaceInfoId);
            eq("userId", userId);
        }});

        if (one == null) {
            throw new RpcBusinessException(ErrorCode.NO_AUTH_ERROR, "暂无使用权限,请申请使用权限!");
//            boolean save = this.save(new UserInterfaceInfo() {{
//                setUserId(userId);
//                setInterfaceInfoId(interfaceInfoId);
//                setLeftNum(100);
//            }});
//            if (!save){
//                return false;
//            }
        }

        UpdateWrapper<UserInterfaceInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("interfaceInfoId", interfaceInfoId);
        updateWrapper.eq("userId", userId);
        updateWrapper.gt("leftNum", 0);
        updateWrapper.setSql("leftNum = leftNum - 1, totalNum = totalNum + 1");
        return this.update(updateWrapper);
    }

}




