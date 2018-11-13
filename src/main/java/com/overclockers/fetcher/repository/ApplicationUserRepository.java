package com.overclockers.fetcher.repository;

import com.overclockers.fetcher.entity.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ApplicationUserRepository extends JpaRepository<ApplicationUser, Long> {
    @Query("FROM ApplicationUser u WHERE u.userId = :userId")
    ApplicationUser findUserById(@Param("userId") Long userId);

    @Query("FROM ApplicationUser u WHERE u.userName = :userName")
    ApplicationUser findUserByUserName(@Param("userName") String userName);

    @Query("FROM ApplicationUser u WHERE u.email = :email")
    ApplicationUser findUserByEmail(@Param("email") String email);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE ApplicationUser u SET u.email = :email WHERE u.userId = :userId")
    ApplicationUser updateUserEmail(@Param("userId") Long userId, @Param("email") String email);
}
