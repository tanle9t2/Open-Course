package com.tp.opencourse.repository;

import com.tp.opencourse.entity.Token;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TokenRedisRepository extends CrudRepository<Token, String> {
    List<Token> findAllByUserKey(String userKey);
    void deleteAllByUserKey(String userKey);
    void deleteAll(List<String> uuid);
}