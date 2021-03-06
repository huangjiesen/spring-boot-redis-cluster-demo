package com.sen.redis;

import com.sen.redis.config.JedisClusterConfig;
import com.sen.redis.config.JedisSentinelConfig;
import com.sen.redis.config.LettuceClusterConfig;
import com.sen.redis.config.LettuceSentinelConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({
    JedisSentinelConfig.class,
    LettuceSentinelConfig.class,
    JedisClusterConfig.class,
    LettuceClusterConfig.class,
})
public class RedisDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisDemoApplication.class, args);
    }

}
