package com.tp.opencourse.service.impl;


import com.tp.opencourse.dto.TokenDTO;
import com.tp.opencourse.entity.Token;
import com.tp.opencourse.entity.User;
import com.tp.opencourse.exceptions.BadRequestException;
import com.tp.opencourse.exceptions.InvalidJwtTokenException;
import com.tp.opencourse.repository.TokenRedisRepository;
import com.tp.opencourse.repository.UserRepository;
import com.tp.opencourse.service.TokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenServiceImpl implements TokenService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final TokenRedisRepository tokenRedisRepository;
    private final RedisTemplate redisTemplate;

    @Value(value = "${app.token.expirationTime}")
    private int expirationTime;


    @Override
    @Transactional
    public void save(Token token) {
        tokenRedisRepository.save(token);
    }

    @Override
    @Transactional
    public void delete(String uuid) {
        tokenRedisRepository.deleteById(uuid);
    }

    @Override
    @Transactional
    public void deleteAll(List<Token> tokens) {
        tokenRedisRepository.deleteAll(tokens);
    }

    @Override
    public Token get(String uuid) {
        return tokenRedisRepository.findById(uuid).orElse(null);
    }

    @Override
    public List<Token> findAllByUserKey(String userKey) {
        return tokenRedisRepository.findAllByUserKey(userKey);
    }

    @Override
    @Transactional
    public TokenDTO refreshToken(String refreshToken) {

        String uuid = jwtService.extractUuid(refreshToken);
        String userId = jwtService.extractUserId(refreshToken);
        if(uuid == null)
            throw new InvalidJwtTokenException("Bad refresh token !");

        if (!jwtService.validateToken(refreshToken))
            throw new InvalidJwtTokenException("Refresh token invalid or expired");


        Optional<Token> token = tokenRedisRepository.findById(uuid);
        if (token.isPresent())
            throw new InvalidJwtTokenException("Access key is still valid !");


        if(!tokenRedisRepository.findAllByUserKey((userId)).isEmpty()) {
            throw new BadRequestException("User already has key before !");
        }

        User user = userRepository.findByUsername(jwtService.extractUsername(refreshToken))
                .orElseThrow(() -> new InvalidJwtTokenException("Bad JWT credentials info"));

        TokenDTO tokenResponse = jwtService
                .generateToken(user.getUsername(), user.getId());

        token = Optional.ofNullable(Token.builder()
                .uuid(tokenResponse.getUuid())
                .userKey(user.getId())
                .timeToLive(expirationTime)
                .build());
        log.info("refresh token: {}", uuid);
        tokenRedisRepository.save(token.get());
        tokenRedisRepository.deleteById(uuid);
        return tokenResponse;
    }

}
