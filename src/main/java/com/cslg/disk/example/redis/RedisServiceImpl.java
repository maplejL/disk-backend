package com.cslg.disk.example.redis;

import com.cslg.disk.example.user.entity.MyUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class RedisServiceImpl implements RedisService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void setValue(String key, Map<String, Object> value) {
        ValueOperations<String, Object> vo = redisTemplate.opsForValue();
        vo.set(key, value);
        redisTemplate.expire(key, 3, TimeUnit.SECONDS);
    }

    @Override
    public Object getValue(String key) {
        ValueOperations<String, String> vo = redisTemplate.opsForValue();
        return vo.get(key);
    }

    @Override
    public List<String> getValueList(String key) {
        Map<String, List<String>> value = (Map<String, List<String>>) getValue(key);
        return value.get(key);
    }

    @Override
    public void setValue(String key, String value) {
        ValueOperations<String, Object> vo = redisTemplate.opsForValue();
        vo.set(key, value);
        if ("token".equals(key)) {
            redisTemplate.expire(key, 1, TimeUnit.HOURS);
        } else {
            redisTemplate.expire(key, 1, TimeUnit.HOURS); // 这里指的是1小时后失效
        }
    }

    @Override
    public void setValue(String key, Object value) {
        ValueOperations<String, Object> vo = redisTemplate.opsForValue();
        vo.set(key, value);
        redisTemplate.expire(key, 1, TimeUnit.HOURS); // 这里指的是1小时后失效
    }

    public void setToken(MyUser user, String token) {
        ValueOperations<String, Object> vo = redisTemplate.opsForValue();
        vo.set("user:"+user.getId(), token);
        redisTemplate.expire("user:" + user.getId(),1, TimeUnit.HOURS);
    }

    @Override
    public void deleteValue(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public List<String> getOnlineUsers() {
        List<String> values = new ArrayList<>();
        Set<String> keys = redisTemplate.keys("user:*");
        if (null != keys){
            // 批量获取数据
            values = redisTemplate.opsForValue().multiGet(keys);
        }
        return values;
    }

}
