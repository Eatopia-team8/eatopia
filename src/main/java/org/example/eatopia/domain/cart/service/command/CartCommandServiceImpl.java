package org.example.eatopia.domain.cart.service.command;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.cart.repository.CartRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartCommandServiceImpl implements CartCommandService {

    private final CartRepository cartRepository;
}
