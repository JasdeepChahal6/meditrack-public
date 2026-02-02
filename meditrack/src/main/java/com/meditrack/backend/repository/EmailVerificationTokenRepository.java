package com.meditrack.backend.repository;

import com.meditrack.backend.Model.EmailVerificationToken;
import com.meditrack.backend.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
    
    Optional<EmailVerificationToken> findByToken(String token);
    
    void deleteByUser(User user);
}
