package com.api.apiinterface.controller;

import com.api.apiinterface.model.entity.PhotoGallery;
import com.api.apiinterface.service.PhotoGalleryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/random_picture")
@Slf4j
public class PhotoGalleryController {
    @Resource
    private PhotoGalleryService photoGalleryService;
    @GetMapping("/get")
    public String getRandomPicture() {
        PhotoGallery randomPicture = photoGalleryService.getRandomPicture();
        return randomPicture.getUrl();
    }

}
