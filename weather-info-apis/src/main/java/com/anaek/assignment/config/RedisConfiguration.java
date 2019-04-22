package com.anaek.assignment.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class RedisConfiguration extends CachingConfigurerSupport implements CachingConfigurer{
	
	@Value("${redis.url:127.0.0.1}")
	private String redisUrl;
	
	@Bean
	public JedisConnectionFactory jedisConnectionFactory() {
		JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
		jedisConnectionFactory.setHostName(redisUrl);
		jedisConnectionFactory.setPort(6379);
		//jedisConnectionFactory.setPoolConfig(getJedisPoolConfig());
		jedisConnectionFactory.setUsePool(true);
		return jedisConnectionFactory;
	}

	/*
	 * private JedisPoolConfig getJedisPoolConfig() { JedisPoolConfig poolConfig =
	 * new JedisPoolConfig(); poolConfig.setMaxTotal(128); return poolConfig; }
	 */
	@Primary
	@Bean
	public RedisTemplate<Object, Object> redisTemplate() {
		RedisTemplate<Object, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(jedisConnectionFactory());
		template.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
		template.setKeySerializer(new StringRedisSerializer());
		template.setHashKeySerializer(new GenericJackson2JsonRedisSerializer());
		template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
		return template;
	}

	@Override
	public RedisCacheManagerConfig cacheManager() {
		RedisCacheManagerConfig redisCacheManager = new RedisCacheManagerConfig(redisTemplate());
		redisCacheManager.setTransactionAware(true);
		redisCacheManager.setUsePrefix(true);
		redisCacheManager.setExpires(getCacheExpireMap());
		return redisCacheManager;
	}
	
	private Map<String, Long> getCacheExpireMap() {
		Map<String, Long> map = new HashMap<String, Long>();
		//5 min TTL
		map.put("MetarInfoCache", 300L);
		return map;
	}
	/*
	 * @Bean
	 * 
	 * @Override public CacheErrorHandler errorHandler() { return new
	 * RedisCacheErrorHandler(); }
	 */

}
