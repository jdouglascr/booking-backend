package com.marisoft.booking.dashboard;

import com.marisoft.booking.dashboard.DashboardDto.*;
import com.marisoft.booking.shared.enums.BookingStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DashboardRepository dashboardRepository;

    @Transactional(readOnly = true)
    public Stats getDashboardStats() {
        LocalDateTime now = LocalDateTime.now();
        
        // Calcular rangos de fechas
        YearMonth currentMonth = YearMonth.now();
        LocalDateTime startOfCurrentMonth = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfCurrentMonth = currentMonth.atEndOfMonth().atTime(LocalTime.MAX);
        
        YearMonth previousMonth = currentMonth.minusMonths(1);
        LocalDateTime startOfPreviousMonth = previousMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfPreviousMonth = previousMonth.atEndOfMonth().atTime(LocalTime.MAX);
        
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        LocalDateTime endOfToday = LocalDate.now().atTime(LocalTime.MAX);
        
        LocalDateTime startOfWeek = LocalDate.now().atStartOfDay();
        LocalDateTime endOfWeek = startOfWeek.plusDays(7);
        
        LocalDateTime last30Days = now.minusDays(30);

        // Construir KPIs mensuales
        MonthlyKpis kpis = buildMonthlyKpis(
                startOfCurrentMonth, endOfCurrentMonth,
                startOfPreviousMonth, endOfPreviousMonth,
                startOfToday, endOfToday,
                startOfWeek, endOfWeek
        );

        // Obtener datos para gráficos
        List<DailyBookingCount> dailyBookings = buildDailyBookings(last30Days);
        List<TopResource> topResources = buildTopResources(startOfCurrentMonth, endOfCurrentMonth);
        List<TopService> topServices = buildTopServices(startOfCurrentMonth, endOfCurrentMonth);
        BookingStatusDistribution statusDistribution = buildStatusDistribution(startOfCurrentMonth, endOfCurrentMonth);

        return new Stats(kpis, dailyBookings, topResources, topServices, statusDistribution);
    }

    private MonthlyKpis buildMonthlyKpis(
            LocalDateTime startOfCurrentMonth, LocalDateTime endOfCurrentMonth,
            LocalDateTime startOfPreviousMonth, LocalDateTime endOfPreviousMonth,
            LocalDateTime startOfToday, LocalDateTime endOfToday,
            LocalDateTime startOfWeek, LocalDateTime endOfWeek
    ) {
        // Reservas del mes actual y anterior
        Long currentBookings = dashboardRepository.countBookingsByDateRange(startOfCurrentMonth, endOfCurrentMonth);
        Long previousBookings = dashboardRepository.countBookingsByDateRange(startOfPreviousMonth, endOfPreviousMonth);
        
        // Ingresos del mes actual y anterior
        Integer currentRevenue = dashboardRepository.sumRevenueByDateRange(startOfCurrentMonth, endOfCurrentMonth);
        Integer previousRevenue = dashboardRepository.sumRevenueByDateRange(startOfPreviousMonth, endOfPreviousMonth);
        
        // Tasas
        Long confirmedCount = dashboardRepository.countConfirmedBookings(startOfCurrentMonth, endOfCurrentMonth);
        Long cancelledCount = dashboardRepository.countCancelledBookings(startOfCurrentMonth, endOfCurrentMonth);
        
        double confirmationRate = currentBookings > 0 ? 
                (confirmedCount.doubleValue() / currentBookings.doubleValue()) * 100 : 0.0;
        double cancellationRate = currentBookings > 0 ? 
                (cancelledCount.doubleValue() / currentBookings.doubleValue()) * 100 : 0.0;
        
        // Nuevos clientes
        Integer newCustomers = dashboardRepository.countNewCustomers(startOfCurrentMonth, endOfCurrentMonth);
        
        // Próximas reservas
        Integer upcomingToday = dashboardRepository.countUpcomingBookingsToday(startOfToday, endOfToday);
        Integer upcomingWeek = dashboardRepository.countUpcomingBookingsThisWeek(startOfWeek, endOfWeek);
        
        // Calcular cambios porcentuales
        Double bookingsChange = calculatePercentageChange(previousBookings, currentBookings);
        Double revenueChange = calculatePercentageChange(previousRevenue, currentRevenue);

        return new MonthlyKpis(
                currentBookings.intValue(),
                previousBookings.intValue(),
                bookingsChange,
                currentRevenue,
                previousRevenue,
                revenueChange,
                round(confirmationRate, 1),
                round(cancellationRate, 1),
                newCustomers,
                upcomingToday,
                upcomingWeek
        );
    }

    private List<DailyBookingCount> buildDailyBookings(LocalDateTime startDate) {
        List<Object[]> results = dashboardRepository.countDailyBookings(startDate);
        List<DailyBookingCount> dailyBookings = new ArrayList<>();
        
        for (Object[] row : results) {
            LocalDate date = ((java.sql.Date) row[0]).toLocalDate();
            Long count = ((Number) row[1]).longValue();
            dailyBookings.add(new DailyBookingCount(date, count));
        }
        
        return dailyBookings;
    }

    private List<TopResource> buildTopResources(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> results = dashboardRepository.findTopResources(startDate, endDate);
        List<TopResource> topResources = new ArrayList<>();
        
        for (Object[] row : results) {
            String name = (String) row[0];
            Long count = ((Number) row[1]).longValue();
            Integer revenue = ((Number) row[2]).intValue();
            topResources.add(new TopResource(name, count, revenue));
        }
        
        return topResources;
    }

    private List<TopService> buildTopServices(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> results = dashboardRepository.findTopServices(startDate, endDate);
        List<TopService> topServices = new ArrayList<>();
        
        for (Object[] row : results) {
            String name = (String) row[0];
            Long count = ((Number) row[1]).longValue();
            Integer revenue = ((Number) row[2]).intValue();
            topServices.add(new TopService(name, count, revenue));
        }
        
        return topServices;
    }

    private BookingStatusDistribution buildStatusDistribution(LocalDateTime startDate, LocalDateTime endDate) {
        Long pending = dashboardRepository.countByStatus(startDate, endDate, BookingStatus.PENDIENTE);
        Long confirmed = dashboardRepository.countByStatus(startDate, endDate, BookingStatus.CONFIRMADA);
        Long paid = dashboardRepository.countByStatus(startDate, endDate, BookingStatus.PAGADA);
        Long completed = dashboardRepository.countByStatus(startDate, endDate, BookingStatus.COMPLETADA);
        Long cancelled = dashboardRepository.countByStatus(startDate, endDate, BookingStatus.CANCELADA);
        
        return new BookingStatusDistribution(pending, confirmed, paid, completed, cancelled);
    }

    private Double calculatePercentageChange(Number previous, Number current) {
        if (previous == null || previous.doubleValue() == 0) {
            return current.doubleValue() > 0 ? 100.0 : 0.0;
        }
        
        double change = ((current.doubleValue() - previous.doubleValue()) / previous.doubleValue()) * 100;
        return round(change, 1);
    }

    private Double round(double value, int places) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return 0.0;
        }
        
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}