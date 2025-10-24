package com.marisoft.booking.resource;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceServiceRepository extends JpaRepository<ResourceService, Integer> {

    List<ResourceService> findByResourceId(Integer resourceId);

    List<ResourceService> findByServiceId(Integer serviceId);

    @Modifying
    @Query("DELETE FROM ResourceService rs WHERE rs.resource.id = :resourceId")
    void deleteByResourceId(@Param("resourceId") Integer resourceId);

    @Modifying
    @Query("DELETE FROM ResourceService rs WHERE rs.resource.id = :resourceId AND rs.service.id NOT IN :serviceIds")
    void deleteByResourceIdAndServiceIdNotIn(@Param("resourceId") Integer resourceId, @Param("serviceIds") List<Integer> serviceIds);
}