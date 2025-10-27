package org.example.eatopia.domain.review.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

public record ReviewRequest(

        @NotBlank
        @Length(min = 10)
        String content,

        @Range(min = 1, max = 5)
        int rating
) {
}
