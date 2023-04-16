package travel.ways.travelwaysapi.trip.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import travel.ways.travelwaysapi.trip.model.db.expense.CurrencyEnum;
import travel.ways.travelwaysapi.trip.model.db.expense.ExpenseCategoryEnum;

import javax.validation.constraints.Size;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EditExpenseRequest {
    private String expenseHash;
    @Size(max = 100)
    private String description;
    private double cost;
    private ExpenseCategoryEnum expenseCategory;
    private CurrencyEnum currency;
    private Date spentAt;
}
