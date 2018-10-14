package com.overclockers.fetcher.repository;

import com.overclockers.fetcher.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("FROM User u WHERE u.userForumId = :userForumId")
    User findByForumId(@Param("userForumId") Long userForumId);
}
