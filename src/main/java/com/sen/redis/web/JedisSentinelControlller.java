package com.sen.redis.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author HuangJS
 * @date 2019-06-23 3:54 PM
 */
@RestController
@RequestMapping("jedis_sentinel")
public class JedisSentinelControlller {
    @Autowired
    @Qualifier("zeroSentinelRedisTemplate")
    private RedisTemplate<String, String> zeroRedisTemplate;
    @Autowired
    @Qualifier("oneSentinelRedisTemplate")
    private RedisTemplate<String, String> oneRedisTemplate;


    private RedisTemplate<String, String> getDB(int db) {
        switch (db) {
            case 0:
                return zeroRedisTemplate;
            case 1:
                return oneRedisTemplate;
        }
        throw new RuntimeException("redis db does not exist:" + db);
    }

    @GetMapping("set")
    public String set(@RequestParam(defaultValue = "0") int db, String key, String value) {
        getDB(db).opsForValue().set(key, value);
        return "set db:" + db + "," + key + "=" + value;
    }

    @GetMapping("get")
    public String get(@RequestParam(defaultValue = "0") int db, String key) {
        return getDB(db).opsForValue().get(key);
    }
}
