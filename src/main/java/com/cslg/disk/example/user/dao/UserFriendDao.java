package com.cslg.disk.example.user.dao;

import com.cslg.disk.example.user.dto.UserDto;
import com.cslg.disk.example.user.entity.UserRelation;
import io.swagger.models.auth.In;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserFriendDao extends JpaRepository<UserRelation, Integer> {
    @Query(value = "select * from user_relation where (self_id=:id or relate_id=:id) and is_build=1", nativeQuery = true)
    List<UserRelation> getFriendIds(Integer id);

    @Query(value = "select * from user_relation where relate_id = :id and is_build = 0", nativeQuery = true)
    List<UserRelation> getFriendApply(String id);

    @Query(nativeQuery = true, value = "select * from user_relation where self_id=:userId and relate_id=:id")
    UserRelation findIsExist(Integer userId, Integer id);

    @Modifying
    @Query(nativeQuery = true, value = "delete from user_relation where self_id=:userId and relate_id=:id")
    Integer deleteByUserId(Integer userId, Integer id);
}
