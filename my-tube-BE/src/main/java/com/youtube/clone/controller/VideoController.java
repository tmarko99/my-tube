package com.youtube.clone.controller;

import com.youtube.clone.dto.CommentDto;
import com.youtube.clone.dto.UploadVideoResponse;
import com.youtube.clone.dto.VideoDto;
import com.youtube.clone.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UploadVideoResponse uploadVideo(@RequestParam("file") MultipartFile file) throws IOException {
        return videoService.uploadVideo(file);
    }

    @PostMapping("/thumbnail")
    @ResponseStatus(HttpStatus.CREATED)
    public String uploadThumbnail(@RequestParam("file") MultipartFile file, @RequestParam("videoId") String videoId) throws IOException {
        return videoService.uploadThumbnail(file, videoId);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public VideoDto editVideo(@RequestBody VideoDto videoDto){
        return videoService.editVideo(videoDto);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public VideoDto getVideoDetails(@PathVariable("id") String videoId){
        return videoService.getVideoDetails(videoId);
    }

    @GetMapping("/edit/{id}")
    @ResponseStatus(HttpStatus.OK)
    public VideoDto getVideoById(@PathVariable("id") String videoId){
        return videoService.getVideo(videoId);
    }

    @PostMapping("/{id}/like")
    @ResponseStatus(HttpStatus.OK)
    public VideoDto likeVideo(@PathVariable("id") String videoId){
        return videoService.likeVideo(videoId);
    }

    @PostMapping("/{id}/dislike")
    @ResponseStatus(HttpStatus.OK)
    public VideoDto dislikeVideo(@PathVariable("id") String videoId){
        return videoService.dislikeVideo(videoId);
    }

    @PostMapping("/{id}/comment")
    @ResponseStatus(HttpStatus.OK)
    public void addComment(@PathVariable("id") String videoId, @RequestBody CommentDto commentDto){
        videoService.addComment(videoId, commentDto);
    }

    @DeleteMapping("/{videoId}/comment/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteComment(@PathVariable String videoId, @PathVariable String commentId){
        videoService.deleteComment(videoId, commentId);
    }

    @GetMapping("/{videoId}/comment")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getAllComments(@PathVariable("videoId") String videoId){
        return videoService.getAllComments(videoId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<VideoDto> getAllVideos(){
        return videoService.getAllVideos();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteById(@PathVariable("id") String id){
        return videoService.deleteById(id);
    }
}
