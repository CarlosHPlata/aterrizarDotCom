package com.aterrizar.http.external.repositories.redis.flight.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.aterrizar.http.external.repositories.redis.flight.model.RedisFlight;

@Repository
public interface FlightRepository extends CrudRepository<RedisFlight, String> {}
