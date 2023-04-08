package travel.ways.travelwaysapi.trip.model.db.expense;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import travel.ways.travelwaysapi._core.model.db.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseCategory extends BaseEntity {
    @Column(nullable = false, length = 100)
    @Enumerated(EnumType.STRING)
    private ExpenseCategoryEnum expenseCategory;

}
