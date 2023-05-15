package travel.ways.travelwaysapi.trip.model.dto.response;

import java.util.List;

public record ExpenseStatisticsDto(List<ExpenseSummaryDto> listOfExpenses,
                                   List<AttractionMiniSummaryDto> listOfAttractions) {
}
