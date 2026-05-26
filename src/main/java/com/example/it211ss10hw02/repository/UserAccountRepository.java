package com.example.it211ss10hw02.repository;

import com.example.it211ss10hw02.model.entity.User;
import com.example.it211ss10hw02.model.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByUser(User user);
}
