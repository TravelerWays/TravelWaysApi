package travel.ways.travelwaysapi.trip.model.db.expense;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import travel.ways.travelwaysapi._core.model.db.BaseEntity;
import travel.ways.travelwaysapi.trip.model.db.trip.Trip;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import java.util.Date;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Expense extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String hash;

    @Column(length = 100)
    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    @Column()
    private Date spentAt;

    @DecimalMin(value = "0.01")
    @Column(nullable = false)
    private double cost;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "currency_id")
    private Currency currency;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "expense_category_id")
    private ExpenseCategory expenseCategory;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "trip_id")
    private Trip trip;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Expense expense = (Expense) o;
        return hash.equals(expense.hash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), hash);
    }
}
