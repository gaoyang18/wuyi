package com.wuyi.wyms;

import cn.hutool.core.lang.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;


@SpringBootTest
class WydpApplicationTests {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public static void main(String[] args) {
        String token = UUID.randomUUID().toString(true);
    }

    @Test
    void contextLoads() {
        String test = "111";
        stringRedisTemplate.opsForValue().set("test",test);
    }

}
