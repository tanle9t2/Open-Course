package com.tp.opencourse.service.impl;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.tp.opencourse.dto.TokenDTO;
import com.tp.opencourse.dto.request.OAuthAuthorizationRequest;
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
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RequiredArgsConstructor
@Slf4j
@Service
public class TokenServiceImpl implements TokenService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final TokenRedisRepository tokenRedisRepository;
    private final RedisTemplate redisTemplate;

    @Value(value = "${spring.security.oauth2.client.registration.google.prefix-uri}")
    private String prefixUri;

    @Value(value = "${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value(value = "${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value(value = "${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    @Value(value = "${spring.security.oauth2.client.registration.google.scope}")
    private String scope;

    @Value(value = "${spring.security.oauth2.client.registration.google.user-info-uri}")
    private String userInfoUri;

    @Value(value = "${app.token.expirationTime}")
    private int expirationTime;

    @Value(value = "${app.token.refreshTime}")
    private int refreshTime;

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
        if (token.isPresent()) {
            int gap = refreshTime - expirationTime;
            if(token.get().getTimeToLive() < gap)
                throw new InvalidJwtTokenException("Access key is still valid !");
        } else {
            throw new InvalidJwtTokenException("Refresh token expired !");
        }


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

    @Override
    public Map<String, Object> getOauthAccessToken(OAuthAuthorizationRequest oauthAuthorizationRequest) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        String accessToken = new GoogleAuthorizationCodeTokenRequest(
                new NetHttpTransport(),
                new GsonFactory(),
                clientId,
                clientSecret,
                oauthAuthorizationRequest.getAuthorizationCode(),
                redirectUri).execute().getAccessToken();

        restTemplate.getInterceptors().add((req, body, executionContext) -> {
            req.getHeaders().set("Authorization", String.format("Bearer %s", accessToken));
            return executionContext.execute(req, body);
        });

        return new ObjectMapper().readValue(
                restTemplate.getForEntity(userInfoUri, String.class).getBody(),
                new TypeReference<>() {});
    }


    @Override
    public String getOauthUrl() {
        return String.format("%s?client_id=%s&redirect_uri=%s&scope=%s&response_type=code&prompt=consent&access_type=offline&include_granted_scopes=true&state=abcxyz123", prefixUri, clientId, redirectUri, scope);
    }
}