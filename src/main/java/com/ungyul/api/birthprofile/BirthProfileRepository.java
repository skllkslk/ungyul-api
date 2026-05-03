package com.ungyul.api.birthprofile;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BirthProfileRepository extends JpaRepository<BirthProfile, Long > {

  Optional<BirthProfile> findByUserId(Long userId);

  boolean existsByUserId(Long userId);
}
