package com.youtube.clone.controller;

import com.youtube.clone.dto.VideoDto;
import com.youtube.clone.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/register")
    @ResponseStatus(HttpStatus.OK)
    public String register(Authentication authentication){
        Jwt jwt = (Jwt) authentication.getPrincipal();

        return userService.registerUser(jwt.getTokenValue());
    }

    @PostMapping("/subscribe/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public boolean subscribeUser(@PathVariable("userId") String userId){
        userService.subscribeUser(userId);

        return true;
    }

    @PostMapping("/unsubscribe/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public boolean unsubscribeUser(@PathVariable("userId") String userId){
        userService.unsubscribeUser(userId);

        return true;
    }

    @GetMapping("/{userId}/history")
    @ResponseStatus(HttpStatus.OK)
    public Set<String> userHistory(@PathVariable("userId") String userId){
        return userService.userHistory(userId);
    }


    @GetMapping("/{userId}/likeHistory")
    @ResponseStatus(HttpStatus.OK)
    public Set<String> likeHistory(@PathVariable("userId") String userId){
        return userService.likeHistory(userId);
    }

    @GetMapping("/{userId}/subscription")
    @ResponseStatus(HttpStatus.OK)
    public Set<String> userSubscriptions(@PathVariable("userId") String userId){
        return userService.userSubscriptions(userId);
    }
}
