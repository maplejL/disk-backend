package com.cslg.disk.example.chat.dao;

import com.cslg.disk.example.chat.entity.ChatRecord;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * (ChatRecord)表数据库访问层
 *
 * @author zry
 * @since 2022-02-08 11:14:45
 */
public interface ChatRecordDao extends JpaRepository<ChatRecord, Integer> {


}


