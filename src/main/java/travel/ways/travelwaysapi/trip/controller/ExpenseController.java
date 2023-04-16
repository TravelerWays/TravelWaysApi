package travel.ways.travelwaysapi.trip.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import travel.ways.travelwaysapi._core.model.dto.BaseResponse;
import travel.ways.travelwaysapi.trip.model.dto.request.AddExpensesRequest;
import travel.ways.travelwaysapi.trip.model.dto.request.EditExpenseRequest;
import travel.ways.travelwaysapi.trip.model.dto.response.ExpenseResponseDto;
import travel.ways.travelwaysapi.trip.service.internal.ExpenseService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/expense")
@RequiredArgsConstructor
public class ExpenseController {
    private final ExpenseService expenseService;

    @PostMapping
    public List<ExpenseResponseDto> addExpenses(@Valid @RequestBody AddExpensesRequest addExpensesRequest){
        return expenseService.addExpenses(addExpensesRequest);
    }

    @DeleteMapping("/{expenseHash}")
    public BaseResponse removeExpense(@PathVariable String expenseHash){
        expenseService.removeExpense(expenseHash);
        return new BaseResponse(true, "expense removed");
    }

    @GetMapping("/trip/{tripHash}")
    public List<ExpenseResponseDto> getExpensesByTrip(@PathVariable String tripHash){
        return expenseService.getExpensesByTrip(tripHash);
    }

    @PutMapping
    public ExpenseResponseDto editExpense(@Valid @RequestBody EditExpenseRequest editExpenseRequest){
        return expenseService.editExpense(editExpenseRequest);
    }
}
