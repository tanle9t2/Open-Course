package com.tp.opencourse.service;

import com.tp.opencourse.dto.TokenDTO;
import com.tp.opencourse.entity.Token;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface TokenService {
    void save(Token token);
    void delete(String uuid);
    void deleteAll(List<Token> tokens);
    Token get(String uuid);
    List<Token> findAllByUserKey(String userKey);
    TokenDTO refreshToken(String refreshToken);
}