package com.cslg.disk.example.file.dao;

import com.cslg.disk.example.file.entity.ShareRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SharedFileDao extends JpaRepository<ShareRecord, Integer> {
    @Query(nativeQuery = true, value = "select * from share_record where file_id=:fileId")
    ShareRecord findByFileId(Integer fileId);
}
