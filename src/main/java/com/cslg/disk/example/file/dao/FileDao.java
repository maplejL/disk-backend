package com.cslg.disk.example.file.dao;

import com.cslg.disk.example.file.entity.MyFile;
import io.swagger.models.auth.In;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;
import java.util.List;


public interface FileDao extends JpaRepository<MyFile, Integer> {
    @Query(value = "select * from my_file where user_id=:userId and type_code = :typeCode and is_delete=0 limit :start,:size", nativeQuery = true)
    ArrayList<MyFile> findByPage(int start, int size, int typeCode, int userId);

    @Modifying
    @Query(value = "update my_file set is_delete = 1 where id in :ids", nativeQuery = true)
    Integer batchDelete(List<Integer> ids);

    @Query(value = "select * from my_file where is_delete=1 limit :start,:size", nativeQuery = true)
    ArrayList<MyFile> findDeleteByPage(int start, int size);

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

    @Query(nativeQuery = true, value = "select count(*) from my_file where user_id=:userId and is_delete=0 and type_code = :typeCode")
    Integer findTotal(int typeCode, int userId);

    @Query(nativeQuery = true, value = "select * from my_file where user_id=:userId and is_delete=0")
    List<MyFile> findByUserId(Integer userId);
}
