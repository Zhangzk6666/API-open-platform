package com.api.common.service;

import com.api.common.model.entity.InterfaceInfo;

public interface InnerInterfaceInfoService {

    /**
     * 从数据库中查询模拟接口是否存在（请求路径、请求方法、请求参数）
     */
    InterfaceInfo getInterfaceInfo(String path, String method);

//    /**
//     *  从数据库查询接口是否还有使用次数
//     */
//    boolean isStillTimesOfUse(long userId, long interfaceInfoId);
//


}
