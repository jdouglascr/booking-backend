package com.marisoft.booking.dashboard;

import java.time.LocalDate;
import java.util.List;

public sealed interface DashboardDto {

    record Stats(
            MonthlyKpis monthlyKpis,
            List<DailyBookingCount> dailyBookings,
            List<TopResource> topResources,
            List<TopService> topServices,
            BookingStatusDistribution statusDistribution
    ) implements DashboardDto {
    }

    record MonthlyKpis(
            Integer currentMonthBookings,
            Integer previousMonthBookings,
            Double bookingsChangePercent,
            Integer currentMonthRevenue,
            Integer previousMonthRevenue,
            Double revenueChangePercent,
            Double confirmationRate,
            Double cancellationRate,
            Integer newCustomers,
            Integer upcomingBookingsToday,
            Integer upcomingBookingsThisWeek
    ) implements DashboardDto {
    }

    record DailyBookingCount(
            LocalDate date,
            Long count
    ) implements DashboardDto {
    }

    record TopResource(
            String resourceName,
            Long bookingCount,
            Integer totalRevenue
    ) implements DashboardDto {
    }

    record TopService(
            String serviceName,
            Long bookingCount,
            Integer totalRevenue
    ) implements DashboardDto {
    }

    record BookingStatusDistribution(
            Long pending,
            Long confirmed,
            Long paid,
            Long completed,
            Long cancelled
    ) implements DashboardDto {
    }
}