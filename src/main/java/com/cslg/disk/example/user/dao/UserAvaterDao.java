package com.cslg.disk.example.user.dao;

import com.cslg.disk.example.user.entity.UserAvater;
import io.swagger.models.auth.In;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAvaterDao extends JpaRepository<UserAvater, Integer> {
    @Query(nativeQuery = true, value = "select * from user_avater where user_id=:userId")
    UserAvater findByUserId(Integer userId);

    @Query(nativeQuery = true, value = "select * from user_avater where user_id in :userIds")
    List<UserAvater> findByUserIds(List<Integer> userIds);
}
