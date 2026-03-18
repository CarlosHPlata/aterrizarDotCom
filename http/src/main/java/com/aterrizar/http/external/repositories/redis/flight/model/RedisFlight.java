package com.aterrizar.http.external.repositories.redis.flight.model;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import com.aterrizar.service.core.model.session.FlightData;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@RedisHash(value = "Flight", timeToLive = 600)
@AllArgsConstructor
public class RedisFlight implements Serializable {
    @Id private final String redisId;
    private final FlightData flightData;
}
