package com.cslg.disk.example.file.dao;

import com.cslg.disk.example.file.entity.ShareRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface SharedFileDao extends JpaRepository<ShareRecord, Integer> {
    @Query(nativeQuery = true, value = "select * from share_record where id=:id and extraction_code = :extractionCode")
    ShareRecord findByIdAndExtractionCode(Integer id, String extractionCode);

    @Modifying
    @Query(nativeQuery = true, value = "delete from share_record where id = :id")
    int mydeleteById(Integer id);

    @Query(nativeQuery = true, value = "select * from share_record where file_id=:fileId")
    ShareRecord findByFileId(Integer fileId);
}
