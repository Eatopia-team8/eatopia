package org.example.eatopia.domain.review.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record ReviewReportRequest(

        @NotBlank
        @Length(min = 10, max = 150)
        String reason
) {
}
