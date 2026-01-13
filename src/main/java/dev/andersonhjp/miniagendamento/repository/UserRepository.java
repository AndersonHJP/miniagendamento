package dev.andersonhjp.miniagendamento.repository;

import dev.andersonhjp.miniagendamento.model.LoginUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository <LoginUser, UUID> {
    Optional<UserDetails> findUserByEmail(String username);
}
