package com.api.apiinterface.service.impl;

import com.api.apiinterface.mapper.PhotoGalleryMapper;
import com.api.apiinterface.model.entity.PhotoGallery;
import com.api.apiinterface.service.PhotoGalleryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;


@Service
@Slf4j
public class PhotoGalleryServiceImpl extends ServiceImpl<PhotoGalleryMapper, PhotoGallery> implements PhotoGalleryService {

    @Resource
    private PhotoGalleryMapper photoGalleryMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean insert(PhotoGallery photoGallery) {
        return this.save(photoGallery);
    }

    @PostConstruct
    void init(){
        String photo_gallery_key = "photo_gallery_ids";
        List<PhotoGallery> photoGalleries = photoGalleryMapper.selectByMap(new HashMap<>());
        for (PhotoGallery p : photoGalleries){
            stringRedisTemplate.opsForZSet().add(photo_gallery_key, String.valueOf(p.getId()),p.getId());
        }
    }

    @Override
    public PhotoGallery getRandomPicture() {
        String photo_gallery_key = "photo_gallery_ids";
        Long count = stringRedisTemplate.opsForZSet().zCard(photo_gallery_key);
        int id_index = (int) (Math.random() * count + 1);
        Set<String> set = stringRedisTemplate.boundZSetOps(photo_gallery_key).range(id_index, id_index);
        ArrayList<String> list = new ArrayList<>(set);
        Long id = Long.valueOf(list.get(0));
        String id_key = "id:" + id;
        String url = stringRedisTemplate.opsForValue().get(id_key);
        if (url != null) {
            System.out.println("url: "+url);
            PhotoGallery photoGallery = new PhotoGallery();
            photoGallery.setId(id);
            photoGallery.setUrl(url);
            return photoGallery;
        }
        PhotoGallery photoGallery = photoGalleryMapper.selectById(id);
        stringRedisTemplate.boundValueOps(id_key).set(photoGallery.getUrl(),10, TimeUnit.MINUTES);
        return photoGallery;

//        List<PhotoGallery> list = photoGalleryMapper.selectByMap(new HashMap<String, Object>() {{
//            put("id", id);
//        }});


//        if (list.size() == 0) {
//            return null;
//        }
//        return list.get(0);
//        PhotoGallery photoGalleryRandOne = photoGalleryMapper.getPhotoGalleryRandOne();
//        return photoGalleryRandOne;
    }
}
