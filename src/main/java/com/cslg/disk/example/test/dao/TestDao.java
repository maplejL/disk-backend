package com.cslg.disk.example.test.dao;

import com.cslg.disk.example.test.entity.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestDao extends JpaRepository<Test, Integer> {
}
