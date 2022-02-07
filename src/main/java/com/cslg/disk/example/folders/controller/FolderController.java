package com.cslg.disk.example.folders.controller;

import com.cslg.disk.common.ResponseMessage;
import com.cslg.disk.example.folders.service.FolderService;
import com.cslg.disk.example.user.anno.UserLoginToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/folders")
public class FolderController {
    @Autowired
    private FolderService folderService;

    @GetMapping("/getFolders")
    @UserLoginToken
    public ResponseMessage getFolders() {
        return ResponseMessage.success(folderService.getFolders());
    }

    @GetMapping("/addFolder")
    @UserLoginToken
    public ResponseMessage addFolder(@RequestParam(value = "name")String name) {
        return ResponseMessage.success(folderService.addFolder(name));
    }
}
