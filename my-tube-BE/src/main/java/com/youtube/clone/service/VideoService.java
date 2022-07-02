package com.youtube.clone.service;

import com.youtube.clone.dto.CommentDto;
import com.youtube.clone.dto.UploadVideoResponse;
import com.youtube.clone.dto.VideoDto;
import com.youtube.clone.model.Comment;
import com.youtube.clone.model.User;
import com.youtube.clone.model.Video;
import com.youtube.clone.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoRepository videoRepository;
    private final UserService userService;
    private final S3Service s3Service;

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public UploadVideoResponse uploadVideo(MultipartFile file) throws IOException {
        String videoUrl = s3Service.uploadFile(file);
        var video = new Video();
        video.setVideoUrl(videoUrl);
        User currentUser = userService.getCurrentUser();
        video.setUserId(currentUser.getId());

        Video savedVideo = videoRepository.save(video);

        return new UploadVideoResponse(savedVideo.getId(), savedVideo.getVideoUrl());
    }

    public String uploadThumbnail(MultipartFile file, String videoId) throws IOException {
        Video savedVideo = getVideoById(videoId);
        String thumbnailUrl = s3Service.uploadFile(file);
        savedVideo.setThumbnailUrl(thumbnailUrl);

        videoRepository.save(savedVideo);

        return thumbnailUrl;
    }

    public VideoDto editVideo(VideoDto videoDto){
        Video savedVideo = getVideoById(videoDto.getId());

        savedVideo.setTitle(videoDto.getTitle());
        savedVideo.setDescription(videoDto.getDescription());
        savedVideo.setTags(videoDto.getTags());
        //savedVideo.setThumbnailUrl(videoDto.getThumbnailUrl());
        savedVideo.setVideoStatus(videoDto.getVideoStatus());

        videoRepository.save(savedVideo);

        return videoDto;
    }

    public VideoDto getVideoDetails(String videoId){
        Video video = getVideoById(videoId);

        increaseVideoCount(video);
        if(!SecurityContextHolder.getContext().getAuthentication().getName().equals("anonymousUser")){
            userService.addVideoToHistory(video.getId());
        }

        VideoDto videoDto = mapToDto(video);
        //videoDto.setViewCount(video.getViewCount().get());

        return videoDto;
    }

    public VideoDto getVideo(String videoId){
        Video video = getVideoById(videoId);

        VideoDto videoDto = mapToDto(video);
        //videoDto.setViewCount(video.getViewCount().get());

        return videoDto;
    }

    public VideoDto likeVideo(String videoId) {
        Video videoById = getVideoById(videoId);

        if(userService.ifLikedVideo(videoId)){
            videoById.decrementLikes();
            userService.removeFromLikedVideos(videoId);
        }
        else if(userService.ifDisLikedVideo(videoId)){
            videoById.decrementDisLikes();
            userService.removeFromDisLikedVideos(videoId);
            videoById.incrementLikes();
            userService.addToLikedVideos(videoId);
        }
        else{
            videoById.incrementLikes();
            userService.addToLikedVideos(videoId);
        }

        videoRepository.save(videoById);

        return mapToDto(videoById);
    }

    public VideoDto dislikeVideo(String videoId) {
        Video videoById = getVideoById(videoId);

        if(userService.ifDisLikedVideo(videoId)){
            videoById.decrementDisLikes();
            userService.removeFromDisLikedVideos(videoId);
        }
        else if(userService.ifLikedVideo(videoId)){
            videoById.decrementLikes();
            userService.removeFromLikedVideos(videoId);
            videoById.incrementDisLikes();
            userService.addToDislikedVideos(videoId);
        }
        else{
            videoById.incrementDisLikes();
            userService.addToDislikedVideos(videoId);
        }

        videoRepository.save(videoById);

        return mapToDto(videoById);
    }


    public void addComment(String videoId, CommentDto commentDto) {
        Video videoById = getVideoById(videoId);
        Comment comment = new Comment();

        ObjectId objectId = new ObjectId();

        comment.setId(objectId.toHexString());
        comment.setAuthor(commentDto.getAuthor());
        comment.setText(commentDto.getText());

        videoById.addComment(comment);

        videoRepository.save(videoById);
    }

    public void deleteComment(String videoId, String commentId){
        Video videoById = getVideoById(videoId);
        User currentUser = userService.getCurrentUser();

        for (Comment comment : videoById.getCommentList()){
            if(comment.getAuthor().equals(currentUser.getId()) && commentId.equals(comment.getId())){
                videoById.deleteComment(comment);
                break;
            }
        }

//        if (videoById.getCommentList().stream().anyMatch(comment -> comment.getAuthor().equals(currentUser.getId()))){
//            videoById.deleteComment(commentId);
//        }
        videoRepository.save(videoById);
    }

    public List<CommentDto> getAllComments(String videoId) {
        Video videoById = getVideoById(videoId);

        return videoById.getCommentList().stream().map(comment -> {
            return new CommentDto(comment.getId(), comment.getText(), comment.getAuthor());
        }).collect(Collectors.toList());
    }

    public List<VideoDto> getAllVideos() {
        return videoRepository.findAll().stream().map(video -> mapToDto(video)).collect(Collectors.toList());
    }

    public String deleteById(String id){
        Video videoById = getVideoById(id);
        User currentUser = userService.getCurrentUser();

        if(!currentUser.getId().equals(videoById.getUserId())){
            return "Cannot delete video";
        }

        videoRepository.deleteById(id);
        return "Video deleted successfully";
    }

    private Video getVideoById(String videoId){
        return videoRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find video by id: " + videoId));
    }

    private void increaseVideoCount(Video video) {
        video.incrementViewCount();
        videoRepository.save(video);
    }

    private VideoDto mapToDto(Video video) {
        VideoDto videoDto = new VideoDto();

        videoDto.setId(video.getId());
        videoDto.setUserId(video.getUserId());
        videoDto.setVideoUrl(video.getVideoUrl());
        videoDto.setThumbnailUrl(video.getThumbnailUrl());
        videoDto.setTitle(video.getTitle());
        videoDto.setDescription(video.getDescription());
        videoDto.setTags(video.getTags());
        videoDto.setVideoStatus(video.getVideoStatus());
        videoDto.setLikeCount(video.getLikes().get());
        videoDto.setDislikeCount(video.getDisLikes().get());
        videoDto.setViewCount(video.getViewCount().get());

        return videoDto;
    }
}
