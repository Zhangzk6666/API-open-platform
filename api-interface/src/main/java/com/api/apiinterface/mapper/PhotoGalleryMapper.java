package com.api.apiinterface.mapper;

import com.api.apiinterface.model.entity.PhotoGallery;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface PhotoGalleryMapper extends BaseMapper<PhotoGallery> {

    PhotoGallery getPhotoGalleryRandOne();
}
