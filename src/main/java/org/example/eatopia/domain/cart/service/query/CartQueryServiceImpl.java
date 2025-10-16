package org.example.eatopia.domain.cart.service.query;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.cart.repository.CartRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartQueryServiceImpl implements CartQueryService {

    private final CartRepository cartRepository;
}
