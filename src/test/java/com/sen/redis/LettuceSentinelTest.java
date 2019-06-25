package com.sen.redis;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class LettuceSentinelTest {
    private final Logger logger = LoggerFactory.getLogger(LettuceSentinelTest.class);
    @Autowired
    @Qualifier("twoSentinelRedisTemplate")
    private RedisTemplate<String, String> twoSentinelRedisTemplate;
    @Autowired
    @Qualifier("threeSentinelRedisTemplate")
    private RedisTemplate<String, String> threeSentinelRedisTemplate;


    @Test
    public void add() {
        twoSentinelRedisTemplate.opsForValue().set("hello","world");
        String hello = twoSentinelRedisTemplate.opsForValue().get("hello");
        Assert.assertEquals(hello,"world");

        threeSentinelRedisTemplate.opsForValue().set("name","li shi");
        String name = threeSentinelRedisTemplate.opsForValue().get("name");
        Assert.assertEquals(name,"li shi");
    }

    @Test
    public void keepWrite() throws InterruptedException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("y-M-d H:m:s.S");
        String now = null;
        while (true) {
            try {
                now = LocalDateTime.now().format(formatter);
                logger.info("new:{}", now);
                twoSentinelRedisTemplate.opsForValue().set("now",now);
            } catch (Exception e) {
                logger.info("new:{},errMsg:{}", now, e.getMessage());
            }
            Thread.sleep(1000);
        }
    }
}
