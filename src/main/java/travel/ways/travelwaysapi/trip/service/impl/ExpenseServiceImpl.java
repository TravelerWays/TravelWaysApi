package travel.ways.travelwaysapi.trip.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import travel.ways.travelwaysapi._core.exception.ServerException;
import travel.ways.travelwaysapi.trip.model.db.expense.Expense;
import travel.ways.travelwaysapi.trip.model.db.trip.Trip;
import travel.ways.travelwaysapi.trip.model.dto.request.AddExpensesRequest;
import travel.ways.travelwaysapi.trip.model.dto.request.EditExpenseRequest;
import travel.ways.travelwaysapi.trip.model.dto.request.ExpenseRequestDto;
import travel.ways.travelwaysapi.trip.model.dto.response.ExpenseResponseDto;
import travel.ways.travelwaysapi.trip.repository.CurrencyRepository;
import travel.ways.travelwaysapi.trip.repository.ExpenseCategoryRepository;
import travel.ways.travelwaysapi.trip.repository.ExpenseRepository;
import travel.ways.travelwaysapi.trip.service.internal.ExpenseService;
import travel.ways.travelwaysapi.trip.service.shared.TripService;
import travel.ways.travelwaysapi.user.model.db.AppUser;
import travel.ways.travelwaysapi.user.service.shared.UserFriendsService;
import travel.ways.travelwaysapi.user.service.shared.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final TripService tripService;
    private final UserService userService;
    private final CurrencyRepository currencyRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final UserFriendsService userFriendsService;

    @Override
    @SneakyThrows
    @Transactional
    public List<ExpenseResponseDto> addExpenses(AddExpensesRequest addExpensesRequest) {
        Trip trip = tripService.getTrip(addExpensesRequest.getTripHash());
        AppUser user = userService.getLoggedUser();
        if (!user.equals(tripService.findOwner(trip)) && !tripService.checkIfContributor(trip, user)) {
            throw new ServerException("You do not have permission to add expense", HttpStatus.FORBIDDEN);
        }
        List<Expense> finalExpenses = new ArrayList<>();
        for (ExpenseRequestDto expenseDto : addExpensesRequest.getExpenses()) {
            Expense expense = new Expense();
            expense.setExpenseCategory(expenseCategoryRepository.findByExpenseCategory(expenseDto.getExpenseCategory()));
            expense.setHash(UUID.randomUUID().toString());
            expense.setTrip(trip);
            expense.setDescription(expenseDto.getDescription());
            expense.setCurrency(currencyRepository.findByCurrency(expenseDto.getCurrency()));
            expense.setCost(expenseDto.getCost());
            expense.setSpentAt(expenseDto.getSpentAt());
            finalExpenses.add(expense);
        }

        expenseRepository.saveAll(finalExpenses);
        return finalExpenses.stream().map(ExpenseResponseDto::of).toList();
    }

    @SneakyThrows
    @Override
    public void removeExpense(String expenseHash) {
        Expense expense = this.getExpense(expenseHash);
        Trip trip = expense.getTrip();
        AppUser user = userService.getLoggedUser();
        if (!user.equals(tripService.findOwner(trip)) && !tripService.checkIfContributor(trip, user)) {
            throw new ServerException("You do not have permission to remove expense", HttpStatus.FORBIDDEN);
        }
        expenseRepository.delete(expense);
    }

    @SneakyThrows
    @Override
    public Expense getExpense(String expenseHash) {
        Expense expense = expenseRepository.findByHash(expenseHash);
        if (expense == null) {
            throw new ServerException("Expense not found", HttpStatus.NOT_FOUND);
        }
        return expense;
    }

    @SneakyThrows
    @Override
    public List<ExpenseResponseDto> getExpensesByTrip(String tripHash) {
        var trip = tripService.getTrip(tripHash);
        var user = tripService.findOwner(trip);
        var loggedUser = userService.getLoggedUser();
        if (!loggedUser.equals(user) && !tripService.checkIfContributor(trip, loggedUser) && !userFriendsService.isLoggedUserUserFriend(user)) {
            throw new ServerException("You do not have permission to get expenses", HttpStatus.FORBIDDEN);
        }
        return expenseRepository.findByTripHash(tripHash).stream().map(ExpenseResponseDto::of).toList();
    }


    @SneakyThrows
    @Override
    @Transactional
    public ExpenseResponseDto editExpense(EditExpenseRequest editExpenseRequest) {
        Expense expense = this.getExpense(editExpenseRequest.getExpenseHash());
        Trip trip = expense.getTrip();
        AppUser user = userService.getLoggedUser();
        if (!user.equals(tripService.findOwner(trip)) && !tripService.checkIfContributor(trip, user)) {
            throw new ServerException("You do not have permission to edit the expense", HttpStatus.FORBIDDEN);
        }
        expense.setDescription(editExpenseRequest.getDescription());
        expense.setCost(editExpenseRequest.getCost());
        expense.setSpentAt(editExpenseRequest.getSpentAt());
        expense.setCurrency(currencyRepository.findByCurrency(editExpenseRequest.getCurrency()));
        expense.setExpenseCategory(expenseCategoryRepository.findByExpenseCategory(editExpenseRequest.getExpenseCategory()));

        return ExpenseResponseDto.of(expense);
    }

//    @Override
//    public List<ExpenseResponseDto> getExpensesForAttractionsBetween(List<Attraction> attractions, Date start, Date end) {
//        if (start == null){
//            start = new Date(0);
//        }
//        if (end == null){
//            end = Date.from(Instant.now());
//        }
//        System.out.println(attractions);
//        System.out.println(expenseRepository.getExpensesForAttractionsBetween(new HashSet<>(attractions), start, end));
//
//        return expenseRepository.getExpensesForAttractionsBetween(new HashSet<>(attractions), start, end).stream()
//                .map(ExpenseResponseDto::of).toList();
//    }


}
