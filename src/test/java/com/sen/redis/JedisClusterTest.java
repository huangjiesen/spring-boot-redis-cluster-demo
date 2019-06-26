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
public class JedisClusterTest {
    private final Logger logger = LoggerFactory.getLogger(JedisClusterTest.class);
    @Autowired
    @Qualifier("clusterRedisTemplate")
    private RedisTemplate<String, String> clusterRedisTemplate;

    @Test
    public void add() {
        clusterRedisTemplate.opsForValue().set("hello","world");
        String hello = clusterRedisTemplate.opsForValue().get("hello");
        Assert.assertEquals(hello,"world");
    }

    @Test
    public void keepWrite() throws InterruptedException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("y-M-d H:m:s.S");
        String now = null;
        while (true) {
            try {
                now = LocalDateTime.now().format(formatter);
                logger.info("new:{}", now);
                clusterRedisTemplate.opsForValue().set("now",now);
            } catch (Exception e) {
                logger.info("new:{},errMsg:{}", now, e.getMessage());
            }
            Thread.sleep(1000);
        }
    }
}
