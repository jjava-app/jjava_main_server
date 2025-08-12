//package org.example.jjava_main.redis;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.redis.core.StringRedisTemplate;
//
//@SpringBootTest
//public class RedisTest {
//    @Autowired
//    private StringRedisTemplate redisTemplate;
//
//    @Test
//    public void testRedis() {
//        redisTemplate.opsForValue().set("hello", "world");
//        String value = redisTemplate.opsForValue().get("hello");
//        System.out.println("값: " + value);  // 값: world
//    }
//
//}
