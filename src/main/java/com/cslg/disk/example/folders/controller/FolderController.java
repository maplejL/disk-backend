package com.cslg.disk.example.folders.controller;

import com.cslg.disk.common.ResponseMessage;
import com.cslg.disk.example.folders.service.FolderService;
import com.cslg.disk.example.user.anno.UserLoginToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/deleteFolder")
    @UserLoginToken
    public ResponseMessage deleteFolder(@RequestParam("ids") List<String> ids) {
        return ResponseMessage.success(folderService.batchDelete(ids));
    }
}
