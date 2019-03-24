package com.overclockers.fetcher.repository;

import com.overclockers.fetcher.entity.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationUserRepository extends JpaRepository<ApplicationUser, Long> {
    @Query("FROM ApplicationUser u WHERE u.id = :userId")
    ApplicationUser findUserById(@Param("userId") Long userId);

    @Query("FROM ApplicationUser u WHERE u.email = :email")
    ApplicationUser findUserByEmail(@Param("email") String email);

    @Query("FROM ApplicationUser  u WHERE u.confirmationToken = :confirmationToken")
    ApplicationUser findUserByConfirmationToken(@Param("confirmationToken") String confirmationToken);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE ApplicationUser u SET u.email = :email WHERE u.id = :userId")
    ApplicationUser updateUserEmail(@Param("userId") Long userId, @Param("email") String email);

    @Query("FROM ApplicationUser u WHERE u.enabled = true")
    List<ApplicationUser> findAllEnabledUsers();
}
