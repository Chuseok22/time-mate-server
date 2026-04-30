package com.chuseok22.timemateserver.user.application.dto.response;

import java.util.UUID;

public record UserInfoResponse(
    UUID userId,
    String nickname,
    String email
) {}
