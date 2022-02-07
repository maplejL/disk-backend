package com.cslg.disk.example.file.dao;

import com.cslg.disk.example.file.entity.MyFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;
import java.util.List;


public interface FileDao extends JpaRepository<MyFile, Integer> {
    @Query(value = "select * from my_file where type_code = :typeCode and is_delete=0 limit :start,:size", nativeQuery = true)
    ArrayList<MyFile> findByPage(int start, int size, int typeCode);

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
}
