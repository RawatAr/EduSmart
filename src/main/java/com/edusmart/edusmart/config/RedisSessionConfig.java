package com.edusmart.edusmart.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@org.springframework.context.annotation.Profile("prod")
@EnableRedisHttpSession
public class RedisSessionConfig {
}