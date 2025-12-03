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

    @Query("""
            SELECT COUNT(b) > 0
            FROM Booking b
            JOIN b.resourceService rs
            WHERE rs.service.id = :serviceId
            AND b.startDatetime > :currentDateTime
            AND b.status != :canceledStatus
            """)
    boolean existsFutureBookingsByServiceId(
            @Param("serviceId") Integer serviceId,
            @Param("currentDateTime") LocalDateTime currentDateTime,
            @Param("canceledStatus") BookingStatus canceledStatus
    );

    @Query("""
            SELECT COUNT(b) > 0
            FROM Booking b
            JOIN b.resourceService rs
            WHERE rs.resource.id = :resourceId
            AND b.startDatetime > :currentDateTime
            AND b.status != :canceledStatus
            """)
    boolean existsFutureBookingsByResourceId(
            @Param("resourceId") Integer resourceId,
            @Param("currentDateTime") LocalDateTime currentDateTime,
            @Param("canceledStatus") BookingStatus canceledStatus
    );


    @Query("""
            SELECT COUNT(b) > 0
            FROM Booking b
            WHERE b.customer.id = :customerId
            AND b.startDatetime > :currentDateTime
            AND b.status != :canceledStatus
            """)
    boolean existsFutureBookingsByCustomerId(
            @Param("customerId") Integer customerId,
            @Param("currentDateTime") LocalDateTime currentDateTime,
            @Param("canceledStatus") BookingStatus canceledStatus
    );

    @Query("""
            SELECT COUNT(b) > 0
            FROM Booking b
            JOIN b.resourceService rs
            WHERE rs.resource.user.id = :userId
            AND b.startDatetime > :currentDateTime
            AND b.status != :canceledStatus
            """)
    boolean existsFutureBookingsByUserId(
            @Param("userId") Integer userId,
            @Param("currentDateTime") LocalDateTime currentDateTime,
            @Param("canceledStatus") BookingStatus canceledStatus
    );
}