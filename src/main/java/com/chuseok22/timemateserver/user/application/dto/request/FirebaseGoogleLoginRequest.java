package com.chuseok22.timemateserver.user.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record FirebaseGoogleLoginRequest(
    @NotBlank(message = "firebaseIdToken은 필수입니다.") String firebaseIdToken
) {

}
