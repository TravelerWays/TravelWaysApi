package travel.ways.travelwaysapi.trip.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import travel.ways.travelwaysapi.trip.model.dto.response.*;
import travel.ways.travelwaysapi.trip.service.internal.StatisticService;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatisticController {

    private final StatisticService statisticService;

    @GetMapping("/basic-statistics")
    public BasicStatisticsDto getBasicStatistics(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date from,
                                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date to) {
        return statisticService.getBasicStatistics(from, to);
    }

    @GetMapping("/expenses-statistics")
    public ExpenseStatisticsDto getExpensesStatistics(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date from,
                                                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date to) {
        return statisticService.getExpensesStatistics(from, to);
    }

    @GetMapping("/attractions-per-month")
    public List<AttractionAndDateDto> getAttractionsPerMonth(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date from,
                                                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date to) {
        return statisticService.getAttractionsPerMonth(from, to);
    }

    @GetMapping("/expenses-per-month")
    public List<ExpenseAndDateDto> getExpensesPerMonth(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date from,
                                                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date to) {
        return statisticService.getexpensesPerMonth(from, to);
    }

    @GetMapping("/attraction/{hash}/expenses-statistics")
    public List<ExpenseSummaryDto> getExpensesForAttraction(@PathVariable String hash) {
        return statisticService.getExpensesForAttraction(hash);
    }
}
