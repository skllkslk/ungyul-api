package com.ungyul.api.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findBySocialProviderAndSocialId(String provider, String socialId);
}
