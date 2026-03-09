package com.ismaelebonaventura.home_service.repository;

import com.ismaelebonaventura.home_service.model.AnalystHome;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface AnalystHomeRepository extends JpaRepository<AnalystHome, Long> {
    List<AnalystHome> findByAnalystUserId(UUID analystUserId);
    @Query("select ah.homeId from AnalystHome ah where ah.analystUserId = :userId")
    List<Integer> findHomeIdsByAnalystUserId(UUID userId);
    void deleteByAnalystUserIdAndHomeIdIn(UUID analystUserId, Collection<Integer> homeIds);
}