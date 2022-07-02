package com.youtube.clone.config;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

public class AudienceValidator implements OAuth2TokenValidator<Jwt> {
    private final String audience;

    public AudienceValidator(String audience) {
        this.audience = audience;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        //ako je namenjen njemu
        if(token.getAudience().contains(audience)){
            return OAuth2TokenValidatorResult.success(); // token is valid
        }
        return OAuth2TokenValidatorResult.failure(new OAuth2Error("Invalid audience for the given token"));
    }
}
