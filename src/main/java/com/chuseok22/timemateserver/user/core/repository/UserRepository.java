package com.chuseok22.timemateserver.user.core.repository;

import com.chuseok22.timemateserver.user.infrastructure.entity.User;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

  User save(User user);

  User findById(UUID id);

  Optional<User> findByProviderAndProviderId(String provider, String providerId);

  void deleteById(UUID id);
}
