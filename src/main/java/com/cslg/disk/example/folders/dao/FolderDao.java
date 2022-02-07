package com.cslg.disk.example.folders.dao;

import com.cslg.disk.example.folders.entity.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FolderDao extends JpaRepository<Folder, Integer> {

    @Query(value = "update folder set is_delete=1 where id in :ids", nativeQuery = true)
    @Modifying
    Integer batchDelete(List<String> ids);
}
