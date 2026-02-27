package com.ismaelebonaventura.home_service.repository;

import com.ismaelebonaventura.home_service.model.HomeMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HomeMemberRepository extends JpaRepository<HomeMember, Long> {

    boolean existsByHomeIdAndMemberUserId(Integer homeId, UUID memberUserId);
    @Query("select hm.homeId from HomeMember hm where hm.memberUserId = :userId")
    List<Integer> findHomeIdsByMemberUserId(UUID userId);
}