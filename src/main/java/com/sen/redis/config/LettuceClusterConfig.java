package com.sen.redis.config;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
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
//@Configuration
public class LettuceClusterConfig {
    @Bean("lettuceClusterProperties")
    @ConfigurationProperties(prefix = "spring.redis")
    public RedisProperties lettuceClusterProperties() {
        return new RedisProperties();
    }


    @Bean("lettuceClusterConnectionFactory")
    public RedisConnectionFactory lettuceClusterConnectionFactory(@Qualifier("lettuceClusterProperties") RedisProperties properties) {
        RedisProperties.Cluster cluster = properties.getCluster();
        Set<RedisNode> redisNodeSet = new HashSet<>();
        cluster.getNodes().forEach(x->{
            String[] link = x.split(":");
            redisNodeSet.add(new RedisNode(link[0],Integer.parseInt(link[1])));
        });

        RedisClusterConfiguration config = new RedisClusterConfiguration();
        config.setPassword(properties.getPassword());
        config.setMaxRedirects(cluster.getMaxRedirects());
        config.setClusterNodes(redisNodeSet);

        RedisProperties.Pool pool = properties.getLettuce().getPool();
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxTotal(pool.getMaxActive());
        poolConfig.setMinIdle(pool.getMinIdle());
        poolConfig.setMaxIdle(pool.getMaxIdle());
        poolConfig.setMaxWaitMillis(pool.getMaxWait().toMillis());
        LettucePoolingClientConfiguration.LettucePoolingClientConfigurationBuilder builder = LettucePoolingClientConfiguration.builder();
        builder.poolConfig(poolConfig);
        if (properties.getTimeout() != null) {
            builder.commandTimeout(properties.getTimeout());
        }
        return new LettuceConnectionFactory(config, builder.build());
    }

    @Bean("lettuceClusterRedisTemplate")
    public RedisTemplate<String, String> zeroRedisTemplate(
            @Qualifier("lettuceClusterConnectionFactory") RedisConnectionFactory redisConnectionFactory
    ) {
        return getRedisTemplate(redisConnectionFactory);
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
