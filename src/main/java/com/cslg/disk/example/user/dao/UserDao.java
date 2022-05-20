package com.cslg.disk.example.user.dao;

import com.cslg.disk.example.user.dto.UserDto;
import com.cslg.disk.example.user.entity.MyUser;
import com.cslg.disk.example.user.entity.UserAvater;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserDao extends JpaRepository<MyUser, Integer> {
    @Query(value = "select * from my_user where username=?1 and is_delete=0", nativeQuery = true)
    MyUser findByUserName(String username);


    @Query(value = "select * from my_user where id=?1 and is_delete=0", nativeQuery = true)
    MyUser findById(String id);

    @Query(value = "select * from my_user where id in :ids and is_delete=0", nativeQuery = true)
    List<MyUser> findByIds(List<Integer> ids);

    @Modifying
    @Query(value = "update my_user set is_delete=1 where  id in :ids", nativeQuery = true)
    Integer deleteByIds(List<String> ids);

    @Query(value = "select * from my_user where is_delete=0", nativeQuery = true)
    List<MyUser> findAllNotDelete();
}
