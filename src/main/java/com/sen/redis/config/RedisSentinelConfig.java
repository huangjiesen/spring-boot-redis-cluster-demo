package com.sen.redis.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Set;

/**
 * @author HuangJS
 * @date 2019-06-22 6:40 PM
 */
@Configuration
public class RedisSentinelConfig {
    @Bean("jedisPoolConfig")
    @ConfigurationProperties(prefix = "spring.redis.jedis.pool")
    public JedisPoolConfig jedisPoolConfig() {
        return new JedisPoolConfig();
    }

    @Primary
    @Bean("zeroSentinelProperties")
    @ConfigurationProperties(prefix = "spring.redis.zero")
    public RedisProperties zeroSentinelProperties() {
        return new RedisProperties();
    }

    @Primary
    @Bean("zeroSentinelConfiguration")
    @ConfigurationProperties(prefix = "spring.redis.zero")
    public RedisSentinelConfiguration zeroSentinelConfiguration(@Qualifier("zeroSentinelProperties")RedisProperties properties){
        return getRedisSentinelConfiguration(properties);
    }

    @Primary
    @Bean("zeroJedisConnectionFactory")
    public JedisConnectionFactory zeroJedisConnectionFactory(
          @Qualifier("zeroSentinelConfiguration")  RedisSentinelConfiguration configuration,
          @Qualifier("jedisPoolConfig")   JedisPoolConfig jedisPoolConfig) {
        return new JedisConnectionFactory(configuration, jedisPoolConfig);
    }

    @Bean("zeroRedisTemplate")
    public RedisTemplate<String, String> zeroRedisTemplate(
            @Qualifier("zeroJedisConnectionFactory") JedisConnectionFactory jedisConnectionFactory
    ) {
        return getRedisTemplate(jedisConnectionFactory);
    }



    @Bean("oneSentinelProperties")
    @ConfigurationProperties(prefix = "spring.redis.one")
    public RedisProperties oneSentinelProperties() {
        return new RedisProperties();
    }

    @Bean("oneSentinelConfiguration")
    public RedisSentinelConfiguration oneSentinelConfiguration(@Qualifier("oneSentinelProperties")RedisProperties properties){
        return getRedisSentinelConfiguration(properties);
    }
    @Bean("oneJedisConnectionFactory")
    public JedisConnectionFactory oneJedisConnectionFactory(
         @Qualifier("oneSentinelConfiguration")   RedisSentinelConfiguration configuration,
         @Qualifier("jedisPoolConfig")   JedisPoolConfig jedisPoolConfig) {
        return new JedisConnectionFactory(configuration, jedisPoolConfig);
    }

    @Bean("oneRedisTemplate")
    public RedisTemplate<String, String> oneRedisTemplate(
            @Qualifier("oneJedisConnectionFactory") JedisConnectionFactory jedisConnectionFactory
    ) {
        return getRedisTemplate(jedisConnectionFactory);
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

    private RedisTemplate<String, String> getRedisTemplate(JedisConnectionFactory connectionFactory) {
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
