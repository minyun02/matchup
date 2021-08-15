package com.matchup.home.domain.posts;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class Posts extends BaseTimeEntity {

    @Id //테이블의 pk값을 나타낸다.
    @GeneratedValue(strategy = GenerationType.IDENTITY) //pk값 생성규칙
    private Long id;

    @Column(length = 500, nullable = false)
    private String title;

    @Column(columnDefinition  = "TEXT", nullable = false)
    private String content;

    private String author;

    @Builder
    public Posts(String title, String content, String author){
        this.title = title;
        this.content = content;
        this.author = author;
    }

    public void update(String title, String content){
        this.title = title;
        this.content = content;
    }

}
