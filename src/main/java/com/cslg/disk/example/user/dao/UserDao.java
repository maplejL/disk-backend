package com.cslg.disk.example.user.dao;

import com.cslg.disk.example.user.entity.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserDao extends JpaRepository<MyUser, Integer> {
    @Query(value = "select * from my_user where username=?1 and is_delete=0", nativeQuery = true)
    public MyUser findByUserName(String username);

    @Query(value = "select * from my_user where id=?1 and is_delete=0", nativeQuery = true)
    public MyUser findById(String id);

    @Query(value = "select * from my_user where id in :ids", nativeQuery = true)
    public List<MyUser> findByIds(List<Integer> ids);
}
