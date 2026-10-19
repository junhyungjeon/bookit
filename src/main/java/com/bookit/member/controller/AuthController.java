package com.bookit.member.controller;

import com.bookit.auth.JwtTokenProvider;
import com.bookit.auth.dto.LoginResponse;
import com.bookit.member.dto.LoginRequest;
import com.bookit.member.dto.MemberResponse;
import com.bookit.member.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(MemberService memberService, JwtTokenProvider jwtTokenProvider) {
        this.memberService = memberService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        MemberResponse member = memberService.login(request);
        return new LoginResponse(jwtTokenProvider.createToken(member.email()), member);
    }
}
