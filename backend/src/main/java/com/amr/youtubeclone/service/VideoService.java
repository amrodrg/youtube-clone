package com.amr.youtubeclone.service;

import com.amr.youtubeclone.dto.UploadVideoResponse;
import com.amr.youtubeclone.dto.VideoDto;
import com.amr.youtubeclone.model.Video;
import com.amr.youtubeclone.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final S3Service s3Service;
    private final VideoRepository videoRepository;
    private final UserService userService;


    Video getVideoById(String videoId) {
        return videoRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find video by id -" + videoId));
    }


    private VideoDto mapToVideoDto(Video video) {
        VideoDto videoDto = new VideoDto();
        videoDto.setId(video.getId());
        videoDto.setVideoUrl(video.getVideoUrl());
        videoDto.setVideoStatus(video.getVideoStatus());
        videoDto.setDescription(video.getDescription());
        videoDto.setTags(video.getTags());
        videoDto.setTitle(video.getTitle());
        videoDto.setThumbnailUrl(video.getThumbnailUrl());
        videoDto.setLikeCount(video.getLikes().get());
        videoDto.setDislikeCount(video.getDislikes().get());
        videoDto.setViewCount(video.getViewCount().get());
        return videoDto;
    }


    public UploadVideoResponse uploadVideo(MultipartFile file) {
        String videoUrl = s3Service.uploadFile(file);
        var video = new Video();
        video.setVideoUrl(videoUrl);

        Video savedVideo = videoRepository.save(video);
        return new UploadVideoResponse(savedVideo.getId(), savedVideo.getVideoUrl());
    }


    public VideoDto editVideo(VideoDto videoDto) {
        Video savedVideo = getVideoById(videoDto.getId());

        savedVideo.setTitle(videoDto.getTitle());
        savedVideo.setDescription(videoDto.getDescription());
        savedVideo.setTags(videoDto.getTags());
        savedVideo.setThumbnailUrl(videoDto.getThumbnailUrl());
        savedVideo.setVideoStatus(videoDto.getVideoStatus());

        videoRepository.save(savedVideo);
        return videoDto;
    }


    public String uploadThumbnail(MultipartFile file, String videoId) {
        Video savedVideo = getVideoById(videoId);
        String thumbnailUrl = s3Service.uploadFile(file);

        savedVideo.setThumbnailUrl(thumbnailUrl);

        videoRepository.save(savedVideo);
        return thumbnailUrl;
    }


    public VideoDto getVideoDetails(String videoId) {
        Video savedVideo = getVideoById(videoId);

        increaseVideoCount(savedVideo);
        userService.addVideoToHistory(videoId);

        return mapToVideoDto(savedVideo);
    }


    private void increaseVideoCount(Video video) {
        video.incrementViewCount();
        videoRepository.save(video);
    }


    public VideoDto likeVideo(String videoId) {
        Video savedVideo = getVideoById(videoId);

        if (userService.ifLikedVideo(videoId)) {
            savedVideo.decrementLikes();
            userService.removeFromLikedVideos(videoId);
        } else if (userService.ifDislikedVideo(videoId)) {
            savedVideo.decrementDislikes();
            userService.removeFromDislikedVideos(videoId);
            userService.addToLikedVideo(videoId);
        } else {
            savedVideo.incrementLikes();
            userService.addToLikedVideo(videoId);
        }

        videoRepository.save(savedVideo);

        return mapToVideoDto(savedVideo);
    }


    public VideoDto dislikeVideo(String videoId) {
        Video savedVideo = getVideoById(videoId);

        if (userService.ifDislikedVideo(videoId)) {
            savedVideo.decrementDislikes();
            userService.removeFromDislikedVideos(videoId);
        } else if (userService.ifLikedVideo(videoId)) {
            savedVideo.decrementLikes();
            userService.removeFromLikedVideos(videoId);
            userService.addToDislikedVideo(videoId);
        } else {
            savedVideo.incrementDislikes();
            userService.addToDislikedVideo(videoId);
        }

        videoRepository.save(savedVideo);

        return mapToVideoDto(savedVideo);
    }
}
