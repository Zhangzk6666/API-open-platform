package com.api.apiinterface.service;

import com.api.apiinterface.model.entity.PhotoGallery;
import com.baomidou.mybatisplus.extension.service.IService;

public interface PhotoGalleryService extends IService<PhotoGallery> {
    /**
     * 插入数据 | 不常用仅导入数据时使用
     */
    boolean insert(PhotoGallery photoGallery);



    /**
     * 随机获取一张图片
     */

    PhotoGallery  getRandomPicture();
}
