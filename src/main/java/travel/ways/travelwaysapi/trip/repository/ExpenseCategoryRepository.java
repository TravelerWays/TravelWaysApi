package travel.ways.travelwaysapi.trip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import travel.ways.travelwaysapi.trip.model.db.expense.ExpenseCategory;
import travel.ways.travelwaysapi.trip.model.db.expense.ExpenseCategoryEnum;

public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategory, Long> {
    ExpenseCategory findByExpenseCategory(ExpenseCategoryEnum expenseCategory);
    boolean existsByExpenseCategory(ExpenseCategoryEnum expenseCategory);
}
