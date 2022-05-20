package com.cslg.disk.example.file.dao;

import com.cslg.disk.example.file.entity.MyFile;
import io.swagger.models.auth.In;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;
import java.util.List;


public interface FileDao extends JpaRepository<MyFile, Integer> {
    @Query(value = "select * from my_file " +
            "where user_id=:userId " +
            "and type_code = :typeCode and is_delete=0 limit :start,:size", nativeQuery = true)
    ArrayList<MyFile> findByPage(int start, int size, int typeCode, int userId);

    //获取分享的常见时间使用
//    @Query(value = "select mf.url,mf.size,mf.file_name,mf.type_code,mf.thumbnail_name," +
//            "mf.type_name,mf.user_id,mf.id, sr.created_date, mf.created_by, mf.modified_by, mf.modified_date, mf.is_delete from my_file mf " +
//            "join share_record sr " +
//            "on sr.shared_ids like CONCAT('%', :userId, '%') " +
//            "where sr.file_id=mf.id " +
//            "and type_code = :typeCode and sr.is_delete=0 limit :start,:size", nativeQuery = true)
//    ArrayList<MyFile> findSharedFilesByPage(int start, int size, int typeCode, int userId);

    @Query(value = "select mf.* from my_file mf " +
            "join share_record sr " +
            "on sr.shared_ids like CONCAT('%', :userId, '%') or sr.user_id=:userId " +
            "where sr.shared_ids is not null and sr.file_id=mf.id " +
            "and type_code = :typeCode and sr.is_delete=0 limit :start,:size", nativeQuery = true)
    ArrayList<MyFile> findSharedFilesByPage(int start, int size, int typeCode, int userId);

    @Modifying
    @Query(value = "update my_file set is_delete = 1 where id in :ids", nativeQuery = true)
    Integer batchDelete(List<Integer> ids);

    @Query(value = "select * from my_file where user_id=:userId and is_delete=1 limit :start,:size", nativeQuery = true)
    ArrayList<MyFile> findDeleteByPage(int start, int size, int userId);

    @Query(value = "update my_file set is_delete=0 where id in :ids", nativeQuery = true)
    @Modifying
    Integer recoverFiles(List<Integer> ids);

    @Modifying
    @Query(value = "delete from my_file where id in :ids", nativeQuery = true)
    Integer completelyDelete(List<Integer> ids);

    @Query(nativeQuery = true, value = "select * from my_file where user_id=:userId and file_name like CONCAT('%', :input, '%') and type_code = :typeCode and is_delete=0 limit :start,:pageSize")
    List<MyFile> findByPageWithInput(int start, int pageSize, int typeCode, String input, int userId);

    @Query(nativeQuery = true, value = "select count(*) from my_file where user_id=:userId and is_delete=0 and file_name like CONCAT('%', :input, '%') and type_code = :typeCode")
    Integer findTotalWithInput(int typeCode, String input, int userId);

    @Query(nativeQuery = true, value = "select count(*) from my_file where user_id=:userId and is_delete=:isDelete and type_code = :typeCode")
    Integer findTotal(int typeCode, int userId, int isDelete);

    @Query(nativeQuery = true, value = "select count(*) from my_file mf " +
            "join share_record sr " +
            "on sr.shared_ids like CONCAT('%', :userId, '%') " +
            "where sr.file_id=mf.id and mf.type_code=:typeCode " +
            "and mf.file_name like CONCAT('%', :input, '%') and sr.is_delete=0")
    Integer findSharedTotalWithInput(int typeCode, String input, int userId);

    @Query(nativeQuery = true, value = "select count(*) from my_file mf " +
            "join share_record sr " +
            "on sr.shared_ids like CONCAT('%', :userId, '%') " +
            "where sr.file_id=mf.id and sr.is_delete=:isDelete and mf.type_code = :typeCode")
    Integer findSharedTotal(int typeCode, int userId, int isDelete);

    @Query(nativeQuery = true, value = "select count(*) from my_file where user_id=:userId and is_delete=:isDelete")
    Integer findTotal(int userId, int isDelete);

    @Query(nativeQuery = true, value = "select * from my_file where user_id=:userId and is_delete=0")
    List<MyFile> findByUserId(Integer userId);

    @Query(nativeQuery = true, value = "select mf.* from my_file mf" +
            "join share_record sr" +
            "on sr.shared_ids like CONCAT('%', :userId, '%') " +
            "where sr.file_id=mf.id and mf.file_name like CONCAT('%', :input, '%')" +
            "and type_code = :typeCode and sr.is_delete=0 limit :start,:pageSize")
    List<MyFile> findSharedFilesByPageWithInput(int start, int pageSize, int typeCode, String input, Integer userId);

    @Query(value = "select * from my_file where id in :ids", nativeQuery = true)
    List<MyFile> findByIds(List<Integer> ids);

}
