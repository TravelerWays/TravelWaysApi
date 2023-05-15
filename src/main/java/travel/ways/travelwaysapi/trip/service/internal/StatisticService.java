package travel.ways.travelwaysapi.trip.service.internal;

import travel.ways.travelwaysapi.trip.model.dto.response.*;

import java.util.Date;
import java.util.List;

public interface StatisticService {
    BasicStatisticsDto getBasicStatistics(Date from, Date to);

    ExpenseStatisticsDto getExpensesStatistics(Date from, Date to);

    List<AttractionAndDateDto> getAttractionsPerMonth(Date from, Date to);

    List<ExpenseAndDateDto> getexpensesPerMonth(Date from, Date to);

    List<ExpenseSummaryDto> getExpensesForAttraction(String hash);
}
