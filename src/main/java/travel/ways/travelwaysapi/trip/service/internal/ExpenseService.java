package travel.ways.travelwaysapi.trip.service.internal;

import travel.ways.travelwaysapi.trip.model.db.expense.Expense;
import travel.ways.travelwaysapi.trip.model.dto.request.AddExpensesRequest;
import travel.ways.travelwaysapi.trip.model.dto.request.EditExpenseRequest;
import travel.ways.travelwaysapi.trip.model.dto.response.ExpenseResponseDto;

import java.util.List;

public interface ExpenseService {
    List<ExpenseResponseDto> addExpenses(AddExpensesRequest addExpensesRequest);
    void removeExpense(String expenseHash);
    Expense getExpense(String expenseHash);
    List<ExpenseResponseDto> getExpensesByTrip(String tripHash);
    ExpenseResponseDto editExpense(EditExpenseRequest editExpenseRequest);
}
