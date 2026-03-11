package com.ismaelebonaventura.home_service.repository;

import com.ismaelebonaventura.home_service.model.Home;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HomeRepository extends JpaRepository<Home, Long> {
    Optional<Home> findByHomeId(Integer homeId);

    boolean existsByHomeId(Integer homeId);

    List<Home> findAllByHeadUserId(UUID headUserId);

    @Query("select h.homeId from Home h where h.headUserId = :userId")
    List<Integer> findHomeIdsByHeadUserId(@Param("userId") UUID userId);

    List<Home> findByHomeIdIn(Collection<Integer> homeIds);
}