package com.cslg.disk.example.file.dao;

import com.cslg.disk.example.file.entity.MyFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;


public interface FileDao extends JpaRepository<MyFile, Integer> {
    @Query(value = "select * from my_file where type_code = :typeCode limit :start,:size", nativeQuery = true)
    ArrayList<MyFile> findByPage(int start, int size, int typeCode);

//    @Query(value = "insert into (select relation_table from support_dict where type_code = :typeCode)('url', 'file_name', 'size') values()", nativeQuery = true)
//    <S extends Picture> S saveByType(File file, int typeCode);
}
