package com.marisoft.booking.dashboard;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.marisoft.booking.booking.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DashboardRepository extends JpaRepository<Booking, Integer> {

    // KPI: Total de reservas en un rango de fechas
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.createdAt >= :startDate AND b.createdAt < :endDate")
    Long countBookingsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // KPI: Ingresos totales en un rango de fechas
    @Query("SELECT COALESCE(SUM(b.price), 0) FROM Booking b WHERE b.createdAt >= :startDate AND b.createdAt < :endDate")
    Integer sumRevenueByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // KPI: Tasa de confirmación
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.createdAt >= :startDate AND b.createdAt < :endDate AND b.status IN ('CONFIRMADA', 'PAGADA', 'COMPLETADA')")
    Long countConfirmedBookings(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // KPI: Tasa de cancelación
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.createdAt >= :startDate AND b.createdAt < :endDate AND b.status = 'CANCELADA'")
    Long countCancelledBookings(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // KPI: Nuevos clientes en el mes
    @Query("SELECT COUNT(DISTINCT c) FROM Booking b JOIN b.customer c WHERE c.createdAt >= :startDate AND c.createdAt < :endDate")
    Integer countNewCustomers(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // KPI: Próximas reservas hoy
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.startDatetime >= :startOfDay AND b.startDatetime < :endOfDay AND b.status IN ('PENDIENTE', 'CONFIRMADA', 'PAGADA')")
    Integer countUpcomingBookingsToday(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    // KPI: Próximas reservas esta semana
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.startDatetime >= :startOfWeek AND b.startDatetime < :endOfWeek AND b.status IN ('PENDIENTE', 'CONFIRMADA', 'PAGADA')")
    Integer countUpcomingBookingsThisWeek(@Param("startOfWeek") LocalDateTime startOfWeek, @Param("endOfWeek") LocalDateTime endOfWeek);

    // Gráfico: Conteo diario de reservas (últimos 30 días)
    @Query("""
        SELECT DATE(b.createdAt) as bookingDate, COUNT(b) as bookingCount
        FROM Booking b
        WHERE b.createdAt >= :startDate
        GROUP BY DATE(b.createdAt)
        ORDER BY DATE(b.createdAt) ASC
        """)
    List<Object[]> countDailyBookings(@Param("startDate") LocalDateTime startDate);

    // Gráfico: Top recursos más reservados
    @Query("""
        SELECT r.name, COUNT(b), COALESCE(SUM(b.price), 0)
        FROM Booking b
        JOIN b.resourceService rs
        JOIN rs.resource r
        WHERE b.createdAt >= :startDate AND b.createdAt < :endDate
        GROUP BY r.id, r.name
        ORDER BY COUNT(b) DESC
        LIMIT 5
        """)
    List<Object[]> findTopResources(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Gráfico: Top servicios más solicitados
    @Query("""
        SELECT s.name, COUNT(b), COALESCE(SUM(b.price), 0)
        FROM Booking b
        JOIN b.resourceService rs
        JOIN rs.service s
        WHERE b.createdAt >= :startDate AND b.createdAt < :endDate
        GROUP BY s.id, s.name
        ORDER BY COUNT(b) DESC
        LIMIT 5
        """)
    List<Object[]> findTopServices(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Gráfico: Distribución por estado
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.createdAt >= :startDate AND b.createdAt < :endDate AND b.status = :status")
    Long countByStatus(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("status") com.marisoft.booking.shared.enums.BookingStatus status);
}