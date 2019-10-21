package com.inetti.matchnight.configuration;

import com.inetti.matchnight.data.repository.RepositoryConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@EnableCaching
@Configuration
public class MatchnightCacheConfiguration extends CachingConfigurerSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(MatchnightCacheConfiguration.class);
    private static final Integer DEFAULT_TTL = 60;


    private final String host;
    private final Integer port;
    private final Integer defaultTTL;
    private final HashMap<String, Long> cacheNameTimeOut;

    public MatchnightCacheConfiguration(@Value("${redis.host}") String host,
                                        @Value("${redis.port}") String port,
                                        @Value("${redis.cache.ttl.default}") String defaultTTL,
                                        @Value("${redis.cache.ttl.request}") String requestCacheTTL,
                                        @Value("${redis.cache.ttl.matchevent}") String matcheventCacheTTL,
                                        @Value("${redis.cache.tt.inetto}") String inettoCacheTTL) {

        this.host = Objects.requireNonNull(host);
        this.port = Integer.parseInt(port);
        this.defaultTTL = Optional.ofNullable(defaultTTL).map(Integer::parseInt).orElse(DEFAULT_TTL);
        cacheNameTimeOut = new HashMap<>();

        //fill cache ttl
        Optional.ofNullable(requestCacheTTL).ifPresent(v -> cacheNameTimeOut.put(RepositoryConstants.REQUEST_CACHE_NAME,
                Long.parseLong(requestCacheTTL)));
        Optional.ofNullable(matcheventCacheTTL).ifPresent(v -> cacheNameTimeOut.put(RepositoryConstants.MATCH_EVENT_CACHE_NAME,
                Long.parseLong(matcheventCacheTTL)));
        Optional.ofNullable(inettoCacheTTL).ifPresent(v -> cacheNameTimeOut.put(RepositoryConstants.INETTO_CACHE_NAME,
                Long.parseLong(inettoCacheTTL)));

    }


    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {

        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(host);
        redisStandaloneConfiguration.setPort(port);
        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(defaultTTL))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setDefaultSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }





    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        for (Map.Entry<String, Long> cacheConfig : cacheNameTimeOut.entrySet()) {
            cacheConfigurations.put(cacheConfig.getKey(),
                    RedisCacheConfiguration
                            .defaultCacheConfig().entryTtl(Duration.ofSeconds(cacheConfig.getValue()))
                            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer())));
        }

        return RedisCacheManager
                .builder(redisConnectionFactory)
                .cacheDefaults(cacheConfiguration())
                .withInitialCacheConfigurations(cacheConfigurations).build();
    }


}
