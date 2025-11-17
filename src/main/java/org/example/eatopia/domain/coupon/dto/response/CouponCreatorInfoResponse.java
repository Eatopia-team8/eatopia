package org.example.eatopia.domain.coupon.dto.response;

import org.example.eatopia.domain.user.config.UserRole;

public record CouponCreatorInfoResponse(Long id,
                                        String name,
                                        String company,
                                        UserRole userRole) {

    public static CouponCreatorInfoResponse of(Long id, String name, String company, UserRole role) {
        return new CouponCreatorInfoResponse(id, name, company, role);
    }
}
