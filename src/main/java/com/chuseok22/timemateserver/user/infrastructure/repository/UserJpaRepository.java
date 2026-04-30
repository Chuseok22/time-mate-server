package com.chuseok22.timemateserver.user.infrastructure.repository;

import com.chuseok22.timemateserver.user.infrastructure.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, UUID> {

  Optional<User> findByProviderAndProviderId(String provider, String providerId);
}
