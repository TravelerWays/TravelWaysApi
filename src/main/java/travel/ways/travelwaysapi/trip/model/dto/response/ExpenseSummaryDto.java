package travel.ways.travelwaysapi.trip.model.dto.response;

import travel.ways.travelwaysapi.trip.model.db.expense.ExpenseCategoryEnum;

public record ExpenseSummaryDto(ExpenseCategoryEnum category, double cost) {
}
