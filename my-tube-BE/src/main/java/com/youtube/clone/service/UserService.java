package com.youtube.clone.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.youtube.clone.dto.UserInfoDto;
import com.youtube.clone.model.User;
import com.youtube.clone.model.Video;
import com.youtube.clone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Value("${auth0.userInfoEndpoint}")
    private String userInfoEndpoint;


    public String registerUser(String token){
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(userInfoEndpoint))
                .setHeader("Authorization", String.format("Bearer %s", token))
                .build();

        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .build();

        try {
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            String body = response.body();// dobijamo JSON objekat i moramo da ga prevedemo u java objekat

            ObjectMapper objectMapper = new ObjectMapper();
            // posto ne citamo sve propertije iz JSON objekta da ne bi failovao
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            UserInfoDto userInfoDto = objectMapper.readValue(body, UserInfoDto.class);

            Optional<User> userBySubject = userRepository.findBySub(userInfoDto.getSub());
            if(userBySubject.isPresent()){
                return userBySubject.get().getId();
            }else{
                User user = new User();
                user.setFirstName(userInfoDto.getGivenName());
                user.setLastName(userInfoDto.getFamilyName());
                user.setFullName(userInfoDto.getName());
                user.setEmailAddress(userInfoDto.getEmail());
                user.setSub(userInfoDto.getSub());

                return userRepository.save(user).getId();
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Exception occurred while registering user", e);
            //e.printStackTrace();
        }

    }

    public User getCurrentUser(){
        //sub mi sadrzi ID usera
        String sub = ((Jwt) (SecurityContextHolder.getContext().getAuthentication().getPrincipal())).getClaim("sub");

        return userRepository.findBySub(sub)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find user with sub: " + sub));

    }

    public void addToLikedVideos(String videoId) {
        User currentUser = getCurrentUser();
        currentUser.addToLikeVideos(videoId);

        userRepository.save(currentUser);
    }

    public void addToDislikedVideos(String videoId) {
        User currentUser = getCurrentUser();
        currentUser.addToDislikedVideos(videoId);

        userRepository.save(currentUser);
    }

    public boolean ifLikedVideo(String videoId){
        //ako se bilo koji video u likedvideos poklopi sa ovim video vrati true
        return getCurrentUser().getLikedVideos().stream().anyMatch(likedVideo -> likedVideo.equals(videoId));
    }

    public boolean ifDisLikedVideo(String videoId){
        //ako se bilo koji video u likedvideos poklopi sa ovim video vrati true
        return getCurrentUser().getDisLikedVideos().stream().anyMatch(disLikedVideo -> disLikedVideo.equals(videoId));
    }

    public void removeFromLikedVideos(String videoId) {
        User currentUser = getCurrentUser();
        currentUser.removeFromLikedVideos(videoId);

        userRepository.save(currentUser);
    }

    public void removeFromDisLikedVideos(String videoId) {
        User currentUser = getCurrentUser();
        currentUser.removeFromDisLikedVideos(videoId);

        userRepository.save(currentUser);
    }


    public void addVideoToHistory(String videoId) {
        User currentUser = getCurrentUser();
        currentUser.addToVideoHistory(videoId);

        userRepository.save(currentUser);
    }

    public void subscribeUser(String userId){
        User currentUser = getCurrentUser();
        currentUser.addToSubscribedToUsers(userId);
        userRepository.save(currentUser);

        User userToSubscribe = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find user with sub: " + userId));
        userToSubscribe.addToSubscribers(currentUser.getId());
        userRepository.save(userToSubscribe);
    }

    public void unsubscribeUser(String userId) {
        User currentUser = getCurrentUser();
        currentUser.removeFromSubscribedToUsers(userId);
        userRepository.save(currentUser);

        User userToUnsubscribe = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find user with sub: " + userId));
        userToUnsubscribe.removeFromSubscribers(currentUser.getId());
        userRepository.save(userToUnsubscribe);
    }

    public Set<String> userHistory(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find user with sub: " + userId));

        return user.getVideoHistory();
    }

    public Set<String> userSubscriptions(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find user with sub: " + userId));

        return user.getSubscribedToUsers();
    }

    public Set<String> likeHistory(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find user with sub: " + userId));

        return user.getLikedVideos();
    }
}
