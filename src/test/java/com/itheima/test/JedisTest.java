package com.itheima.test;

import com.itheima.reggie.ReggieTakeOutApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;

import java.util.List;

@SpringBootTest(classes = ReggieTakeOutApplication.class)
@RunWith(SpringRunner.class)
public class JedisTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testString() {
        redisTemplate.opsForValue().set("city12345", "beijing");
        List clientList = redisTemplate.getClientList();
        System.out.println(clientList);


        Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent("city12345", "nanjing");
        System.out.println(aBoolean);

    }

    @Test
    public void testHash() {
        HashOperations hashOperations = redisTemplate.opsForHash();


    }


}
