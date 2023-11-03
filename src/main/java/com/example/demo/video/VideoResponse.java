package com.example.demo.video;

import com.example.demo.mentoring.MentorPostResponse;
import com.example.demo.user.userInterest.UserInterest;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

public class VideoResponse {
    @Getter
    @Setter
    public static class VideoResponseDTO {
        private int videoID;
        private String videoUrl;
        private String videoInfoKorean;
        private String videoInfoEng;
        private List<String> interests;
        private Subtitle subtitle;

        public VideoResponseDTO(Video video, List<VideoInterest> videoInterests, Subtitle subtitle)
        {
            this.videoID = video.getId();
            this.videoUrl = video.getVideoUrl();
            this.videoInfoKorean = video.getVideoInfoKorean();
            this.videoInfoEng = video.getVideoInfoEng();
            this.interests = videoInterests.stream()
                .map(videoInterest -> videoInterest.getInterest().getCategory())
                .collect(Collectors.toList());
            this.subtitle = subtitle;
        }
    }
}
