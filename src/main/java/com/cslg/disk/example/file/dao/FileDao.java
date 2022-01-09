package com.cslg.disk.example.file.dao;

import com.cslg.disk.example.file.entity.File;
import com.cslg.disk.example.file.entity.Picture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;


public interface FileDao extends JpaRepository<File, Integer> {
    @Query(value = "select * from file where type_code = :typeCode limit :start,:end", nativeQuery = true)
    ArrayList<File> findByPage(int start, int end, int typeCode);

//    @Query(value = "insert into (select relation_table from support_dict where type_code = :typeCode)('url', 'file_name', 'size') values()", nativeQuery = true)
//    <S extends Picture> S saveByType(File file, int typeCode);
}
