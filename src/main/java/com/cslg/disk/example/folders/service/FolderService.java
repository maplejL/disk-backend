package com.cslg.disk.example.folders.service;

import com.cslg.disk.example.folders.entity.Folder;

import java.util.List;

public interface FolderService {
    void initFolders();

    List<Folder> getFolders();

    Object addFolder(String name);
}
