package com.overclockers.fetcher.repository;

import com.overclockers.fetcher.entity.ForumUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ForumUserRepository extends JpaRepository<ForumUser, Long> {
    @Query("FROM ForumUser u WHERE u.userForumId = :userForumId")
    ForumUser findUserByForumId(@Param("userForumId") Long userForumId);

    @Query("FROM ForumUser u WHERE u.userForumId IN :userForumIds")
    List<ForumUser> findUsersByForumIds(@Param("userForumIds") Collection<Long> userForumIds);
}
