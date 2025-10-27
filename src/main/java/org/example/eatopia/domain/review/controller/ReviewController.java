package org.example.eatopia.domain.review.controller;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.review.service.command.ReviewCommandService;
import org.example.eatopia.domain.review.service.query.ReviewQueryService;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewCommandService reviewCommandService;
    private final ReviewQueryService reviewQueryService;
}
