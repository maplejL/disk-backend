package com.cslg.disk.example.redis;

import java.util.List;
import java.util.Map;

public interface RedisService {

    // 加入元素
    void setValue(String key, Map<String, Object> value);
    // 加入元素
    void setValue(String key, String value);
    // 加入元素
    void setValue(String key, Object value);
    // 获取元素
    Object getValue(String key);

    List<String> getValueList(String key);
}

