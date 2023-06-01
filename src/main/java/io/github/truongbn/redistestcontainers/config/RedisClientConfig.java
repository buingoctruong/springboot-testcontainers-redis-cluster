package io.github.truongbn.redistestcontainers.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;

@Configuration
public class RedisClientConfig {
    @Profile("!local")
    @Bean(destroyMethod = "shutdown")
    public ClientResources redisClientResources() {
        return DefaultClientResources.create();
    }

    @Bean
    public ClientOptions redisClientOptions() {
        return ClientOptions.builder()
                .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
                .autoReconnect(true).build();
    }

    @Bean
    public LettucePoolingClientConfiguration lettucePoolingClientConfiguration(
            ClientOptions redisClientOptions, ClientResources redisClientResources,
            RedisProperties redisProperties) {
        return LettucePoolingClientConfiguration.builder()
                .commandTimeout(redisProperties.getReadTimeout())
                .poolConfig(redisProperties.getPoolConfig()).clientOptions(redisClientOptions)
                .clientResources(redisClientResources).build();
    }

    @Bean
    public RedisClusterConfiguration customRedisCluster(RedisProperties redisProperties) {
        return new RedisClusterConfiguration(redisProperties.getNodes());
    }

    @Bean
    public RedisConnectionFactory lettuceConnectionFactory(
            RedisClusterConfiguration customRedisCluster,
            LettucePoolingClientConfiguration lettucePoolingClientConfiguration) {
        LettuceConnectionFactory factory = new LettuceConnectionFactory(customRedisCluster,
                lettucePoolingClientConfiguration);
        factory.afterPropertiesSet();
        return factory;
    }

    @Bean
    public <T> RedisTemplate<String, T> redisTemplate(ObjectMapper objectMapper,
            RedisConnectionFactory lettuceConnectionFactory) {
        RedisTemplate<String, T> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setDefaultSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
