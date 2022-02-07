package com.cslg.disk.example.user.dao;

import com.cslg.disk.example.user.entity.UserRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserFriendDao extends JpaRepository<UserRelation, Integer> {
    @Query(value = "select relate_id from user_relation where self_id=:id", nativeQuery = true)
    List<Integer> getFriendIds(Integer id);
}
