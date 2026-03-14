package com.ismaelebonaventura.analytics_service.dto;

import java.time.LocalDateTime;
import java.util.List;

public record HomeDashboardResponse(
        Integer homeId,
        String applianceTypeFilter,
        LocalDateTime from,
        LocalDateTime to,
        List<TopHourDto> topHours,
        List<TopDayDto> topDays,
        List<TopDeviceDto> topDevices,
        List<HeatingPeakDto> heatingPeaks,
        List<AverageByDeviceDto> averageByDevice,
        List<AverageByPeriodDto> averageByHour,
        List<AverageByPeriodDto> averageByWeekday,
        List<AverageByPeriodDto> averageByMonth) {
}
