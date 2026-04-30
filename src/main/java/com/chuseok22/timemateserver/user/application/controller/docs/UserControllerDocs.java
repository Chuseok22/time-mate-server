package com.chuseok22.timemateserver.user.application.controller.docs;

import com.chuseok22.apichangelog.annotation.ApiChangeLog;
import com.chuseok22.apichangelog.annotation.ApiChangeLogs;
import com.chuseok22.timemateserver.user.application.dto.response.UserInfoResponse;
import com.chuseok22.timemateserver.user.application.dto.response.UserRoomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

@Tag(name = "User", description = "로그인 사용자 정보 및 방 관리 API")
public interface UserControllerDocs {

  @ApiChangeLogs({
      @ApiChangeLog(
          date = "2026-04-29",
          author = "Chuseok22",
          description = "내 정보 조회 API 추가",
          issueUrl = "https://github.com/Chuseok22/time-mate-server/issues/33"
      )
  })
  @Operation(
      summary = "내 정보 조회",
      description = """
          ### 요청
          - 인증 필요: `Authorization: Bearer <token>`

          ### 응답 데이터
          - `userId` (UUID): 서비스 사용자 ID
          - `nickname` (String): 소셜 계정 닉네임
          - `email` (String): 소셜 계정 이메일

          ### 유의 사항
          - JWT 미포함 또는 만료 시 401 반환
          """
  )
  // Spring Security가 자동 주입하는 파라미터이므로 Swagger 문서에는 숨김 처리
  ResponseEntity<UserInfoResponse> getMyInfo(
      @Parameter(hidden = true) Authentication authentication
  );

  @ApiChangeLogs({
      @ApiChangeLog(
          date = "2026-04-29",
          author = "Chuseok22",
          description = "내 방 목록 조회 API 추가",
          issueUrl = "https://github.com/Chuseok22/time-mate-server/issues/33"
      )
  })
  @Operation(
      summary = "내 방 목록 조회",
      description = """
          ### 요청
          - 인증 필요: `Authorization: Bearer <token>`

          ### 응답 데이터
          - `roomId` (UUID): 방 ID
          - `title` (String): 방 제목
          - `joinCode` (String): 방 참가 코드
          - `isOwner` (boolean): 내가 방장인지 여부

          ### 유의 사항
          - 내가 만든 방 + 투표에 참여한 방을 통합하여 반환 (중복 제거)
          - JWT 미포함 또는 만료 시 401 반환
          """
  )
  // Spring Security가 자동 주입하는 파라미터이므로 Swagger 문서에는 숨김 처리
  ResponseEntity<List<UserRoomResponse>> getMyRooms(
      @Parameter(hidden = true) Authentication authentication
  );
}
