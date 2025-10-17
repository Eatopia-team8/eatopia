package org.example.eatopia.domain.coupon.service.query;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.coupon.repository.CouponRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponQueryServiceImpl implements CouponQueryService {

    private final CouponRepository couponRepository;
    
}
