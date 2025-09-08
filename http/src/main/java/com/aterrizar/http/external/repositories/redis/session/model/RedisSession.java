package com.aterrizar.http.external.repositories.redis.session.model;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import com.aterrizar.service.core.model.session.Session;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@RedisHash(value = "Session", timeToLive = 3600) // 1 hour
@AllArgsConstructor
public class RedisSession implements Serializable {
  @Id private final String redisId;
  private final Session session;
}
