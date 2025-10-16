package org.example.eatopia.domain.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.dto.Response;
import org.example.eatopia.domain.user.dto.UserSignUpRequest;
import org.example.eatopia.domain.user.dto.UserSignUpResponse;
import org.example.eatopia.domain.user.service.UserCommandService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
public class UserController {
    private final UserCommandService userCommandService;

    //회원가입 API
    @PostMapping("/signup")
    public ResponseEntity<Response<UserSignUpResponse>> signUp(@Valid @RequestBody UserSignUpRequest request) {
        //중간변수를 활용하여 서비스 호출 및 결과받기
        UserSignUpResponse response = userCommandService.signUp(request);
        return ResponseEntity.ok(Response.success(response));
    }

}
