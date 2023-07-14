package com.api.apiinterface.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 图片库
 */
@TableName(value = "photo_gallery")
@Data
public class PhotoGallery {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;


    /**
     * url
     */
    private String url;



    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
