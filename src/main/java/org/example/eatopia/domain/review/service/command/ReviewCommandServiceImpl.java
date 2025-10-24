package org.example.eatopia.domain.review.service.command;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.review.repository.ReviewReportRepository;
import org.example.eatopia.domain.review.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewCommandServiceImpl implements ReviewCommandService {

    private final ReviewRepository reviewRepository;
    private final ReviewReportRepository reviewReportRepository;
}
