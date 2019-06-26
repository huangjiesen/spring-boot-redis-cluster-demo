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
@RequestMapping("lettuce_cluster")
public class LettuceClusterControlller {
    @Autowired
    @Qualifier("lettuceClusterRedisTemplate")
    private RedisTemplate<String, String> clusterRedisTemplate;

    //@Autowired
    //private ApplicationContext applicationContext;


    @GetMapping("set")
    public String set(@RequestParam(defaultValue = "0") int db, String key, String value) {
        //RedisTemplate<String, String> clusterRedisTemplate = applicationContext.getBean("clusterRedisTemplate", RedisTemplate.class);
        clusterRedisTemplate.opsForValue().set(key, value);
        return "set db:" + db + "," + key + "=" + value;
    }

    @GetMapping("get")
    public String get(@RequestParam(defaultValue = "0") int db, String key) {
        //RedisTemplate<String, String> clusterRedisTemplate = applicationContext.getBean("clusterRedisTemplate", RedisTemplate.class);

        return clusterRedisTemplate.opsForValue().get(key);
    }
}
