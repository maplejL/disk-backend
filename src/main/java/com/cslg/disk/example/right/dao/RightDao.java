package com.cslg.disk.example.right.dao;

import com.cslg.disk.example.right.entity.MyRight;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RightDao extends JpaRepository<MyRight, Integer> {
}
