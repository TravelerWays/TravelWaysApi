package travel.ways.travelwaysapi.trip.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import travel.ways.travelwaysapi.trip.model.db.expense.CurrencyEnum;
import travel.ways.travelwaysapi.trip.model.db.expense.ExpenseCategoryEnum;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseRequestDto {
    @Size(max = 100)
    private String description;
    @NotNull
    private double cost;
    @NotNull
    private ExpenseCategoryEnum expenseCategory;
    @NotNull
    private CurrencyEnum currency;
    private Date spentAt;
}
