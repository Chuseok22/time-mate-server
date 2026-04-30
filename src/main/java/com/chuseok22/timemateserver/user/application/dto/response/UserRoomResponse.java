package com.chuseok22.timemateserver.user.application.dto.response;

import java.util.UUID;

public record UserRoomResponse(
    UUID roomId,
    String title,
    String joinCode,
    boolean isOwner
) {}
