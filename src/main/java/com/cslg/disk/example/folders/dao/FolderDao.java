package com.cslg.disk.example.folders.dao;

import com.cslg.disk.example.folders.entity.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FolderDao extends JpaRepository<Folder, Integer> {
}
