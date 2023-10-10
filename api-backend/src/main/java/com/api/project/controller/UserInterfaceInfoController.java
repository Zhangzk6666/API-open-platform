package com.api.project.controller;

import com.api.common.model.entity.InterfaceInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.api.project.annotation.AuthCheck;
import com.api.project.common.*;
import com.api.project.constant.CommonConstant;
import com.api.project.constant.UserConstant;
import com.api.project.exception.BusinessException;
import com.api.project.model.dto.userinterfaceinfo.UserInterfaceInfoAddRequest;
import com.api.project.model.dto.userinterfaceinfo.UserInterfaceInfoQueryRequest;
import com.api.project.model.dto.userinterfaceinfo.UserInterfaceInfoUpdateRequest;
import com.api.project.service.UserInterfaceInfoService;
import com.api.project.service.InterfaceInfoService;

import com.api.project.service.UserService;
import com.api.common.model.entity.User;
import com.api.common.model.entity.UserInterfaceInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 接口管理
 */
@RestController
@RequestMapping("/userInterfaceInfo")
@Slf4j
public class UserInterfaceInfoController {

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Resource
    private UserService userService;
    @Resource
    private InterfaceInfoService interfaceInfoService;


    /**
     * 申请使用次数
     */
    @PostMapping("/applyForLeftNum")
    public BaseResponse applyFor(@RequestBody UserInterfaceInfoUpdateRequest userInterfaceInfoQueryRequest, HttpServletRequest request) {
        if (userInterfaceInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long userId = user.getId();
        Long interfaceInfoId = userInterfaceInfoQueryRequest.getId();

        // 判断是否存在
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(interfaceInfoId);
        if (interfaceInfo == null) {
            log.info("interfaceInfoId: {}, 不存在",interfaceInfo);
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        // 如果不存在 || create 记录
        UserInterfaceInfo one = userInterfaceInfoService.getOne(new QueryWrapper<UserInterfaceInfo>() {{
            eq("interfaceInfoId", interfaceInfoId);
            eq("userId", userId);
        }});

        if (one == null){
            boolean save = userInterfaceInfoService.save(new UserInterfaceInfo() {{
                setUserId(userId);
                setInterfaceInfoId(interfaceInfoId);
                setLeftNum(0);
            }});
        }


        UpdateWrapper<UserInterfaceInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("interfaceInfoId", interfaceInfoId);
        updateWrapper.eq("userId", userId);
        updateWrapper.setSql("leftNum = leftNum + 100");
        userInterfaceInfoService.update(updateWrapper);
        return ResultUtils.success("success");
    }

    /**
     * 分页获取列表
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<UserInterfaceInfo>> listUserInterfaceInfoByPage(UserInterfaceInfoQueryRequest userInterfaceInfoQueryRequest, HttpServletRequest request) {
        if (userInterfaceInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInfo userInterfaceInfoQuery = new UserInterfaceInfo();
        BeanUtils.copyProperties(userInterfaceInfoQueryRequest, userInterfaceInfoQuery);
        long current = userInterfaceInfoQueryRequest.getCurrent();
        long size = userInterfaceInfoQueryRequest.getPageSize();
        String sortField = userInterfaceInfoQueryRequest.getSortField();
        String sortOrder = userInterfaceInfoQueryRequest.getSortOrder();
        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>(userInterfaceInfoQuery);
        User user = userService.getLoginUser(request);
        queryWrapper.eq("userId", user.getId());
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        Page<UserInterfaceInfo> userInterfaceInfoPage = userInterfaceInfoService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(userInterfaceInfoPage);
    }


}
