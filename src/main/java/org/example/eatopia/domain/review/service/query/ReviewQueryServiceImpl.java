package org.example.eatopia.domain.review.service.query;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.review.repository.ReviewReportRepository;
import org.example.eatopia.domain.review.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewQueryServiceImpl {

    private final ReviewRepository reviewRepository;
    private final ReviewReportRepository reviewReportRepository;
}
