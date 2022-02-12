package com.cslg.disk.example.chat.dao;

import com.cslg.disk.example.chat.entity.Conversation;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * (Conversation)表数据库访问层
 *
 * @author zry
 * @since 2022-02-08 10:46:08
 */
@Repository
public interface ConversationDao extends JpaRepository<Conversation, Integer> {

    @Query(value = "select * from conversation where user_ids like CONCAT('%', :id, '%')", nativeQuery = true)
    List<Conversation> getallById(Integer id);
}


