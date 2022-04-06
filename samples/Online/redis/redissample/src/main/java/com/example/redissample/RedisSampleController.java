package com.example.redissample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/SampleRedis")
public class RedisSampleController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @PostMapping
    public void post(@RequestBody RedisSampleData redisSampleData) {
        redisTemplate.delete("redis-tutorial:string");
        redisTemplate.opsForValue()
                .set("redis-tutorial:string", redisSampleData.getString());
        redisTemplate.delete("redis-tutorial:list");
        redisTemplate.opsForList()
                .rightPushAll("redis-tutorial:list",
                        redisSampleData.getList().toArray(new String[0]));
        redisTemplate.delete("redis-tutorial:map");
        redisTemplate.opsForHash()
                .putAll("redis-tutorial:map", redisSampleData.getMap());
    }

    @GetMapping
    public RedisSampleData get() {
        RedisSampleData redisSampleData = new RedisSampleData();
        redisSampleData.setString(
                redisTemplate.opsForValue()
                        .get("redis-tutorial:string")
        );
        redisSampleData.setList(
                redisTemplate.opsForList()
                        .range("redis-tutorial:list", 0, -1)
        );
        redisSampleData.setMap(
                redisTemplate.<String, String>opsForHash()
                        .entries("redis-tutorial:map")
        );
        return redisSampleData;
    }
}