package com.example.demo.mentoring;

import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.function.BiFunction;

@Getter
public enum SearchCategory {
    NULL((keyword, pageable, repo) -> repo.findAll(pageable)),
    TITLE((keyword, pageable, repo) -> repo.findAllByTitleKeyword("%" + keyword + "%", pageable)),
    WRITER((keyword, pageable, repo) -> repo.findAllByWriterKeyword("%" + keyword + "%", pageable)),
    INTEREST((keyword, pageable, repo) -> repo.findAllByInterestKeyword("%" + keyword + "%", pageable));

    private final TriFunction<String, Pageable, MentorPostJPARepostiory, Page<MentorPost>> function;

    SearchCategory(TriFunction<String, Pageable, MentorPostJPARepostiory, Page<MentorPost>> function) {
        this.function = function;
    }

    public Page<MentorPost> execute(String keyword, Pageable pageable, MentorPostJPARepostiory repository) {
        return function.apply(keyword, pageable, repository);
    }
}