package com.anaek.assignment.config;

import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.stereotype.Component;

@Component
@Primary
public class RedisCacheManagerConfig extends RedisCacheManager {

	public RedisCacheManagerConfig(RedisOperations<?, ?> redisOperations) {
		super(redisOperations);
	}
}
