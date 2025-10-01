package com.chuseok22.timemateserver.common.core.exception;

import lombok.Builder;

@Builder
public record ErrorResponse(ErrorCode errorCode, String errorMessage) {

}
