package com.chuseok22.timemateserver.common.infrastructure.oauth2;

import com.chuseok22.timemateserver.common.core.exception.CustomException;
import com.chuseok22.timemateserver.common.core.exception.ErrorCode;
import com.chuseok22.timemateserver.user.core.service.UserService;
import com.chuseok22.timemateserver.user.infrastructure.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

  private final UserService userService;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oauth2User = super.loadUser(userRequest);
    String registrationId = userRequest.getClientRegistration().getRegistrationId();

    OAuth2UserInfo userInfo = switch (registrationId) {
      case "google" -> new GoogleOAuth2UserInfo(oauth2User.getAttributes());
      case "kakao" -> new KakaoOAuth2UserInfo(oauth2User.getAttributes());
      default -> {
        log.error("지원하지 않는 OAuth2 제공자: {}", registrationId);
        throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
      }
    };

    User user = userService.findOrCreateUser(
        userInfo.getProvider(),
        userInfo.getProviderId(),
        userInfo.getNickname(),
        userInfo.getEmail()
    );

    return new CustomOAuth2UserPrincipal(user.getId(), oauth2User.getAttributes());
  }
}
