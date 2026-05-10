package com.ungyul.api.sajuprofile;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SajuProfileRepository extends JpaRepository<SajuProfile, Long> {

  Optional<SajuProfile> findByUserId(Long userId);

  boolean existsByUserId(Long userId);
}
