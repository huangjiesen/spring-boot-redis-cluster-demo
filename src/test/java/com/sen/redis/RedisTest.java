package com.sen.redis;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author HuangJS
 * @date 2019-06-24 2:11 PM
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {
    @Autowired
    @Qualifier("zeroRedisTemplate")
    private RedisTemplate<String, String> zeroRedisTemplate;
    //@Autowired
    //@Qualifier("oneRedisTemplate")
    //private RedisTemplate<String, String> oneRedisTemplate;


    @Test
    public void add() {
        zeroRedisTemplate.opsForValue().set("hello","world");
        String hello = zeroRedisTemplate.opsForValue().get("hello");
        Assert.assertEquals(hello,"world");

        //oneRedisTemplate.opsForValue().set("name","li shi");
        //String name = oneRedisTemplate.opsForValue().get("name");
        //Assert.assertEquals(name,"li shi");
    }

    @Test
    public void keepWrite() throws InterruptedException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("y-M-d H:m:s.S");
        while (true) {
            String now = LocalDateTime.now().format(formatter);
            try {
                System.out.println(now);
                zeroRedisTemplate.opsForValue().set("now",now);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Thread.sleep(1000);
        }
    }
}
