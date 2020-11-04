package com.sen.redis.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Set;

/**
 * @author HuangJS
 * @date 2019-06-26 2:37 PM
 */
//@Configuration
public class JedisClusterConfig {

    @Bean("clusterPoolConfig")
    @ConfigurationProperties(prefix = "spring.redis.jedis.pool")
    public JedisPoolConfig jedisClusterPoolConfig() {
        return new JedisPoolConfig();
    }

    @Bean("clusterProperties")
    @ConfigurationProperties(prefix = "spring.redis")
    public RedisProperties clusterProperties() {
        return new RedisProperties();
    }


    @Bean("clusterConnectionFactory")
    public JedisConnectionFactory oneJedisConnectionFactory(
            @Qualifier("clusterProperties") RedisProperties redisProperties,
            @Qualifier("clusterPoolConfig") JedisPoolConfig jedisPoolConfig) {
        RedisProperties.Cluster cluster = redisProperties.getCluster();
        Set<RedisNode> redisNodeSet = new HashSet<>();
        cluster.getNodes().forEach(x->{
            String[] link = x.split(":");
            redisNodeSet.add(new RedisNode(link[0],Integer.parseInt(link[1])));
        });

        RedisClusterConfiguration config = new RedisClusterConfiguration();
        config.setPassword(redisProperties.getPassword());
        config.setMaxRedirects(cluster.getMaxRedirects());
        config.setClusterNodes(redisNodeSet);
        return new JedisConnectionFactory(config,jedisPoolConfig);
    }

    @Bean("clusterRedisTemplate")
    public RedisTemplate<String, String> clusterRedisTemplate(@Qualifier("clusterConnectionFactory") JedisConnectionFactory connectionFactory) {
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
