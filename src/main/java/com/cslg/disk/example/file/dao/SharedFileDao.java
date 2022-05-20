package com.cslg.disk.example.file.dao;

import com.cslg.disk.example.file.entity.ShareRecord;
import io.swagger.models.auth.In;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SharedFileDao extends JpaRepository<ShareRecord, Integer> {
    @Query(nativeQuery = true, value = "select * from share_record where id=:id and extraction_code = :extractionCode")
    ShareRecord findByIdAndExtractionCode(Integer id, String extractionCode);

    @Modifying
    @Query(nativeQuery = true, value = "delete from share_record where id = :id")
    int mydeleteById(Integer id);

    @Query(nativeQuery = true, value = "select * from share_record where file_id=:fileId and shared_ids is not null")
    ShareRecord findByFileId(Integer fileId);

    @Query(nativeQuery = true, value = "select * from share_record " +
            "where shared_ids is not null and (shared_ids like CONCAT('%', :userId, '%') or user_id=:userId) " +
            "and file_id=:fileId and is_delete=0")
    List<ShareRecord> findByUserIdAndFileId(Integer userId, Integer fileId);


    @Query(nativeQuery = true, value = "select * from share_record where user_id = :id and is_delete=0 " +
            "and shared_ids is null limit :start,:pageSize")
    List<ShareRecord> findByUserId(Integer id, int start, int pageSize);
}
