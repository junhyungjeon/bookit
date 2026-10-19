package com.bookit.auth.dto;

import com.bookit.member.dto.MemberResponse;

public record LoginResponse(String accessToken, MemberResponse member) {
}
