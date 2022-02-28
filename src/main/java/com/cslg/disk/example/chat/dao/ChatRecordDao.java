package com.cslg.disk.example.chat.dao;

import com.cslg.disk.example.chat.entity.ChatRecord;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * (ChatRecord)表数据库访问层
 *
 * @author zry
 * @since 2022-02-08 11:14:45
 */
public interface ChatRecordDao extends JpaRepository<ChatRecord, Integer> {

    @Query(nativeQuery = true, value = "select chat_record.*,my_user.username as sendUserName from chat_record \n" +
            "join my_user on my_user.id = chat_record.send_user\n" +
            "where conversation_id = :id order by chat_record.created_date")
    List<ChatRecord> findByConversationId(String id);
}


