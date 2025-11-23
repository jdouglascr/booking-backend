package com.marisoft.booking.booking;

import com.marisoft.booking.shared.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findByCustomerId(Integer customerId);

    List<Booking> findByStatus(BookingStatus status);

    Optional<Booking> findByConfirmationToken(String confirmationToken);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.customer " +
            "JOIN FETCH b.resourceService rs " +
            "JOIN FETCH rs.resource r " +
            "JOIN FETCH rs.service s " +
            "WHERE r.id = :resourceId " +
            "AND b.startDatetime >= :startDate " +
            "AND b.startDatetime < :endDate " +
            "ORDER BY b.startDatetime ASC")
    List<Booking> findByResourceAndDateRange(
            @Param("resourceId") Integer resourceId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}