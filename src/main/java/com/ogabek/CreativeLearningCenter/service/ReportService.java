package com.ogabek.CreativeLearningCenter.service;

import com.ogabek.CreativeLearningCenter.dto.response.ReportResponse.*;

public interface ReportService {

    DailyReport getDailyReport(int year, int month, int day);

    MonthlyReport getMonthlyReport(int year, int month);

    YearlyReport getYearlyReport(int year);
}
