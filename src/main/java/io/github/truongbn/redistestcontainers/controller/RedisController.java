package io.github.truongbn.redistestcontainers.controller;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/redis")
@RequiredArgsConstructor
public class RedisController {
    private final RedisTemplate<String, String> redisTemplate;
    @GetMapping(path = "/object/{key}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getObject(@PathVariable("key") String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @PostMapping(path = "/object")
    public void pushObject(@RequestBody RedisRequest redisRequest) {
        redisTemplate.opsForValue().set(redisRequest.getKey(), redisRequest.getValue(),
                Duration.ofMinutes(1));
    }

    @DeleteMapping(path = "/object/{key}")
    public boolean deleteObject(@PathVariable("key") String key) {
        return redisTemplate.delete(key);
    }
    @Data
    public static class RedisRequest {
        private final String key;
        private final String value;
    }
}
