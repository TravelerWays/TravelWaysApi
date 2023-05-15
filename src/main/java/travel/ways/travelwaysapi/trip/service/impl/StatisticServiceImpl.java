package travel.ways.travelwaysapi.trip.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import travel.ways.travelwaysapi.trip.model.db.attraction.Attraction;
import travel.ways.travelwaysapi.trip.model.dto.response.*;
import travel.ways.travelwaysapi.trip.service.internal.AttractionService;
import travel.ways.travelwaysapi.trip.service.internal.ExpenseService;
import travel.ways.travelwaysapi.trip.service.internal.StatisticService;

import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {

    private final AttractionService attractionService;
    private final ExpenseService expenseService;

    @Override
    public BasicStatisticsDto getBasicStatistics(Date from, Date to) {
        List<Attraction> attractions = attractionService.getAttractionsForLoggedUserBetween(from, to);
        Set<String> countries = new HashSet<>();
        List<ExpenseResponseDto> expenses = new ArrayList<>();
        attractions.forEach(a -> {
                    countries.add(a.getLocation().getCountryCode());
                    expenses.addAll(expenseService.getExpensesByTrip(a.getTrip().getHash()));
                }
        );
        double totalSpent = 0;
        for (ExpenseResponseDto expense : expenses) {
            totalSpent += expense.getCost();
        }
        return new BasicStatisticsDto(attractions.size(), countries.size(), totalSpent);
    }

    @Override
    public ExpenseStatisticsDto getExpensesStatistics(Date from, Date to) {
        List<Attraction> attractions = attractionService.getAttractionsForLoggedUserBetween(from, to);
        List<ExpenseResponseDto> expenses = new ArrayList<>();
        List<AttractionMiniSummaryDto> listOfAttractions = new ArrayList<>();

        attractions.forEach(a -> {
                    expenses.addAll(expenseService.getExpensesByTrip(a.getTrip().getHash()));
                    listOfAttractions.add(new AttractionMiniSummaryDto(a.getHash(), a.getTitle()));
                }
        );
        List<ExpenseSummaryDto> listOfExpenses = new ArrayList<>();
        expenses.stream().collect(Collectors.groupingBy(e -> e.getExpenseCategory(), Collectors.summingDouble(e -> e.getCost())))
                .forEach((key, value) -> listOfExpenses.add(new ExpenseSummaryDto(key, value)));
        return new ExpenseStatisticsDto(listOfExpenses, listOfAttractions);
    }

    @Override
    public List<AttractionAndDateDto> getAttractionsPerMonth(Date from, Date to) {
        List<AttractionAndDateDto> attractionsPerMonthList = new ArrayList<>();
        List<Attraction> attractions = attractionService.getAttractionsForLoggedUserBetween(from, to);
        attractions.forEach(a -> System.out.println(a.getVisitedAt()));
        attractions.stream()
                .collect(Collectors.groupingBy(a -> YearMonth.of(
                                Instant.ofEpochMilli(a.getVisitedAt().getTime())
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate().getYear(),
                                Instant.ofEpochMilli(a.getVisitedAt().getTime())
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate().getMonth()
                        ),
                        Collectors.counting()))
                .forEach((k, v) -> attractionsPerMonthList.add(new AttractionAndDateDto(
                        Date.from(k.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant()), v.intValue())));
        return attractionsPerMonthList;
    }

    @Override
    public List<ExpenseAndDateDto> getexpensesPerMonth(Date from, Date to) {
        List<Attraction> attractions = attractionService.getAttractionsForLoggedUserBetween(from, to);
        List<ExpenseResponseDto> expenses = new ArrayList<>();
        List<ExpenseAndDateDto> expensePerMonthList = new ArrayList<>();

        attractions.forEach(a -> {
                    expenses.addAll(expenseService.getExpensesByTrip(a.getTrip().getHash()));
                }
        );
        expenses.stream().collect(Collectors.groupingBy(e -> YearMonth.of(
                                Instant.ofEpochMilli(e.getSpentAt().getTime())
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate().getYear(),
                                Instant.ofEpochMilli(e.getSpentAt().getTime())
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate().getMonth()
                        ),
                        Collectors.summingDouble(ExpenseResponseDto::getCost
                        )))
                .forEach((k, v) -> expensePerMonthList.add(new ExpenseAndDateDto(
                        Date.from(k.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant()), v.doubleValue())));
        return expensePerMonthList;
    }

    @Override
    public List<ExpenseSummaryDto> getExpensesForAttraction(String hash) {
        Attraction attraction = attractionService.getAttraction(hash);
        return attraction.getTrip().getExpenses().stream()
                .map(e -> new ExpenseSummaryDto(e.getExpenseCategory().getExpenseCategory(), e.getCost()))
                .toList();
    }
}
