package com.cslg.disk.example.chat.dao;

import com.cslg.disk.example.chat.entity.Conversation;
import com.cslg.disk.example.chat.entity.TempChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TempChatDao extends JpaRepository<TempChat, Integer> {
    @Query(value = "select * from temp_chat where off_line_user_ids like CONCAT('%', :id, '%')", nativeQuery = true)
    List<TempChat> findTempChatsById(Integer id);

    @Modifying
    @Query(value = "delete from temp_chat where  id in :ids", nativeQuery = true)
    Integer delete(List<Integer> ids);
}
