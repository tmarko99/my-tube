package com.youtube.clone.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    @Id
    private String id;
    private String text;
    private String author;
//    private Integer likeCount;
//    private Integer disLikeCount;
}
