package com.marisoft.booking.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Integer> {

    List<Service> findByCategoryId(Integer categoryId);

    boolean existsByCategoryIdAndName(Integer categoryId, String name);

    boolean existsByCategoryIdAndNameAndIdNot(Integer categoryId, String name, Integer id);
}