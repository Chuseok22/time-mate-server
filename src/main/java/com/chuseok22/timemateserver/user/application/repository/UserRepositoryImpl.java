package com.chuseok22.timemateserver.user.application.repository;

import com.chuseok22.timemateserver.common.core.exception.CustomException;
import com.chuseok22.timemateserver.common.core.exception.ErrorCode;
import com.chuseok22.timemateserver.user.core.repository.UserRepository;
import com.chuseok22.timemateserver.user.infrastructure.entity.User;
import com.chuseok22.timemateserver.user.infrastructure.repository.UserJpaRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

  private final UserJpaRepository jpaRepository;

  @Override
  public User save(User user) {
    return jpaRepository.save(user);
  }

  @Override
  public User findById(UUID id) {
    return jpaRepository.findById(id)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
  }

  @Override
  public Optional<User> findByProviderAndProviderId(String provider, String providerId) {
    return jpaRepository.findByProviderAndProviderId(provider, providerId);
  }
}
