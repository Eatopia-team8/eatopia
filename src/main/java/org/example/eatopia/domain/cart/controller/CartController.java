package org.example.eatopia.domain.cart.controller;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.cart.service.command.CartCommandService;
import org.example.eatopia.domain.cart.service.query.CartQueryService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/eatopia")
public class CartController {

    private final CartCommandService cartCommandService;
    private final CartQueryService cartQueryService;
}
