package com.aterrizar.http.external.repositories.redis.session.repository;

import java.io.Serializable;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.aterrizar.http.external.repositories.redis.session.model.RedisSession;

@Repository
public interface CheckinSessionRepository extends CrudRepository<RedisSession, Serializable> {}
