package org.example.eatopia.domain.review.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReviewStatus {
    ACTIVE,
    REPORTED,
    HIDDEN
}
