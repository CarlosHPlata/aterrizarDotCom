package com.aterrizar.http.external.repository;

import java.io.Serializable;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.aterrizar.http.external.model.RedisSession;

@Repository
public interface CheckinSessionRepository extends CrudRepository<RedisSession, Serializable> {}
