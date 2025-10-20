package com.marisoft.booking.businesshour;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BusinessHourRepository extends JpaRepository<BusinessHour, Integer> {

    Optional<BusinessHour> findByDayOfWeek(String dayOfWeek);

    boolean existsByDayOfWeek(String dayOfWeek);
}