package com.overclockers.fetcher.repository;

import com.overclockers.fetcher.entity.SearchRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchRequestRepository extends JpaRepository<SearchRequest, Long> {
    @Query("FROM SearchRequest s WHERE s.user.userId = :userId")
    List<SearchRequest> findSearchRequestByUserId(@Param("userId") Long userId);

    @Query("FROM SearchRequest s WHERE s.user.email = :email")
    List<SearchRequest> findSearchRequestByUserEmail(@Param("email") String email);

    @Query("FROM SearchRequest s WHERE s.requestId = :requestId")
    SearchRequest findSearchRequestById(@Param("requestId") Long requestId);
}
