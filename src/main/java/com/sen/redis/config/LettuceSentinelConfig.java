package com.sen.redis.config;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.HashSet;
import java.util.Set;

/**
 * @author HuangJS
 * @date 2019-06-22 6:40 PM
 */
@Configuration
public class LettuceSentinelConfig {
    @Bean("twoSentinelProperties")
    @ConfigurationProperties(prefix = "spring.redis.two")
    public RedisProperties twoSentinelProperties() {
        return new RedisProperties();
    }

    @Bean("twoSentinelConnectionFactory")
    public RedisConnectionFactory twoRedisConnectionFactory(@Qualifier("twoSentinelProperties") RedisProperties properties) {
        return getRedisConnectionFactory(properties);
    }

    @Bean("twoSentinelRedisTemplate")
    public RedisTemplate<String, String> zeroRedisTemplate(
            @Qualifier("twoSentinelConnectionFactory") RedisConnectionFactory redisConnectionFactory
    ) {
        return getRedisTemplate(redisConnectionFactory);
    }


    @Bean("threeSentinelProperties")
    @ConfigurationProperties(prefix = "spring.redis.three")
    public RedisProperties threeSentinelProperties() {
        return new RedisProperties();
    }

    @Bean("threeSentinelConnectionFactory")
    public RedisConnectionFactory threeRedisConnectionFactory(@Qualifier("threeSentinelProperties") RedisProperties properties) {
        return getRedisConnectionFactory(properties);
    }

    @Bean("threeSentinelRedisTemplate")
    public RedisTemplate<String, String> threeRedisTemplate(
            @Qualifier("threeSentinelConnectionFactory") RedisConnectionFactory redisConnectionFactory
    ) {
        return getRedisTemplate(redisConnectionFactory);
    }

    private RedisSentinelConfiguration getRedisSentinelConfiguration(RedisProperties properties) {
        RedisProperties.Sentinel sentinel = properties.getSentinel();
        Set<RedisNode> redisNodeSet = new HashSet<>();
        sentinel.getNodes().forEach(x->{
            String[] link = x.split(":");
            redisNodeSet.add(new RedisNode(link[0],Integer.parseInt(link[1])));
        });

        RedisSentinelConfiguration config = new RedisSentinelConfiguration();
        config.master(sentinel.getMaster());
        config.setDatabase(properties.getDatabase());
        config.setPassword(properties.getPassword());
        if (redisNodeSet.isEmpty()) {
           throw new RuntimeException("sentinel nodes is empty");
        }
        config.setSentinels(redisNodeSet);
        return config;
    }

    private RedisConnectionFactory getRedisConnectionFactory(RedisProperties properties){
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        RedisProperties.Pool pool = properties.getLettuce().getPool();
        poolConfig.setMaxTotal(pool.getMaxActive());
        poolConfig.setMinIdle(pool.getMinIdle());
        poolConfig.setMaxIdle(pool.getMaxIdle());
        poolConfig.setMaxWaitMillis(pool.getMaxWait().toMillis());

        LettucePoolingClientConfiguration.LettucePoolingClientConfigurationBuilder builder = LettucePoolingClientConfiguration.builder();
        builder.poolConfig(poolConfig);
        if (properties.getTimeout() != null) {
            builder.commandTimeout(properties.getTimeout());
        }

        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(getRedisSentinelConfiguration(properties), builder.build());
        return connectionFactory;
    }

    private RedisTemplate<String, String> getRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        RedisSerializer<String> stringSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(stringSerializer);
        redisTemplate.setHashValueSerializer(stringSerializer);
        return redisTemplate;
    }

}
