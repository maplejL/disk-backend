package com.cslg.disk.example.file.dao;

import com.cslg.disk.example.file.entity.Thumbnail;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ThumbnailDao extends CrudRepository<Thumbnail, Integer> {
    @Query(value = "select url from thumbnail where video_url = :videoUrl", nativeQuery = true)
    String findByVideoUrl(String videoUrl);
}
