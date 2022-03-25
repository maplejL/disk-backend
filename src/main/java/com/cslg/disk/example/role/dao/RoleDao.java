package com.cslg.disk.example.role.dao;

import com.cslg.disk.example.role.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoleDao extends JpaRepository<Role, Integer> {
    @Modifying
    @Query(value = "update role set is_delete=1 where id in :ids", nativeQuery = true)
    int deleteByIds(List<String> ids);
}
