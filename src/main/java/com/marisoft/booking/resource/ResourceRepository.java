package com.marisoft.booking.resource;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Integer> {

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Integer id);

    List<Resource> findByUserId(Integer userId);

    @Query("SELECT r FROM Resource r LEFT JOIN FETCH r.resourceServices rs LEFT JOIN FETCH rs.service WHERE r.id = :id")
    Optional<Resource> findByIdWithServices(@Param("id") Integer id);

    @Query("SELECT DISTINCT r FROM Resource r LEFT JOIN FETCH r.resourceServices rs LEFT JOIN FETCH rs.service")
    List<Resource> findAllWithServices();
}