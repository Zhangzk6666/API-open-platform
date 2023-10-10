package com.api.common.service;


import com.api.common.exception.RpcBusinessException;

public interface InnerUserInterfaceInfoService {

    /**
     * 调用接口统计 || 只有有额度才能计算并扣减
     */
    boolean invokeCount(long interfaceInfoId, long userId) throws RpcBusinessException;
}
