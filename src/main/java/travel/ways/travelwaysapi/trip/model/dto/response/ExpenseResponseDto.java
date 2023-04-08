package travel.ways.travelwaysapi.trip.model.dto.response;

import lombok.*;
import travel.ways.travelwaysapi.trip.model.db.expense.CurrencyEnum;
import travel.ways.travelwaysapi.trip.model.db.expense.Expense;
import travel.ways.travelwaysapi.trip.model.db.expense.ExpenseCategoryEnum;

import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ExpenseResponseDto {
    private String expenseHash;
    private String description;
    private double cost;
    private ExpenseCategoryEnum expenseCategory;
    private CurrencyEnum currency;
    private Date spentAt;
    private String tripHash;

    public static ExpenseResponseDto of(Expense expense) {
        return new ExpenseResponseDto(
                expense.getHash(),
                expense.getDescription(),
                expense.getCost(),
                expense.getExpenseCategory().getExpenseCategory(),
                expense.getCurrency().getCurrency(),
                expense.getSpentAt(),
                expense.getTrip().getHash()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpenseResponseDto that = (ExpenseResponseDto) o;
        return Objects.equals(expenseHash, that.expenseHash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expenseHash);
    }
}
