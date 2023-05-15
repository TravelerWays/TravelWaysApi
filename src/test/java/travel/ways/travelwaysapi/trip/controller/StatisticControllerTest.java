package travel.ways.travelwaysapi.trip.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import travel.ways.travelwaysapi.auth.service.impl.JwtServiceImpl;
import travel.ways.travelwaysapi.trip.model.db.attraction.Attraction;
import travel.ways.travelwaysapi.trip.model.db.expense.CurrencyEnum;
import travel.ways.travelwaysapi.trip.model.db.expense.ExpenseCategoryEnum;
import travel.ways.travelwaysapi.trip.model.dto.request.AddExpensesRequest;
import travel.ways.travelwaysapi.trip.model.dto.request.CreateAttractionRequest;
import travel.ways.travelwaysapi.trip.model.dto.request.ExpenseRequestDto;
import travel.ways.travelwaysapi.trip.model.dto.response.*;
import travel.ways.travelwaysapi.trip.service.internal.AttractionService;
import travel.ways.travelwaysapi.trip.service.internal.ExpenseService;
import travel.ways.travelwaysapi.trip.service.shared.TripService;
import travel.ways.travelwaysapi.user.service.shared.UserService;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@WithMockUser("JD")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)

public class StatisticControllerTest {
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TripService tripService;
    @Autowired
    private JwtServiceImpl jwtService;
    private MockMvc mvc;
    @Autowired
    private UserService userService;
    @Autowired
    private ExpenseService expenseService;
    @Autowired
    private AttractionService attractionService;


    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @Transactional
    public void getBasicStatistics_shouldReturnBasicStatistics_whenProperRequest() throws Exception {
        LocalDate start = LocalDate.now().minusDays(10);
        LocalDate end = LocalDate.now().minusDays(5);
        Attraction attraction1 = attractionService.createAttraction(getCreateAttractionRequestWithDate(1,
                java.sql.Date.valueOf(start.minusDays(1).toString())));
        Attraction attraction2 = attractionService.createAttraction(getCreateAttractionRequestWithDate(2,
                java.sql.Date.valueOf(start.plusDays(1).toString())));
        Attraction attraction3 = attractionService.createAttraction(getCreateAttractionRequestWithDate(3,
                java.sql.Date.valueOf(start.plusDays(2).toString())));
        Attraction attraction4 = attractionService.createAttraction(getCreateAttractionRequestWithDate(4,
                java.sql.Date.valueOf(end.plusDays(1).toString())));

        AddExpensesRequest addExpensesRequest1 = new AddExpensesRequest(
                attraction1.getTrip().getHash(),
                List.of(getCreateExpenseRequest(1))
        );
        AddExpensesRequest addExpensesRequest2 = new AddExpensesRequest(
                attraction2.getTrip().getHash(),
                List.of(getCreateExpenseRequest(2))
        );
        AddExpensesRequest addExpensesRequest3 = new AddExpensesRequest(
                attraction3.getTrip().getHash(),
                List.of(getCreateExpenseRequest(3))
        );
        AddExpensesRequest addExpensesRequest4 = new AddExpensesRequest(
                attraction4.getTrip().getHash(),
                List.of(getCreateExpenseRequest(4))
        );

        List<ExpenseResponseDto> expenseResponses1 = expenseService.addExpenses(addExpensesRequest1);
        List<ExpenseResponseDto> expenseResponses2 = expenseService.addExpenses(addExpensesRequest2);
        List<ExpenseResponseDto> expenseResponses3 = expenseService.addExpenses(addExpensesRequest3);
        List<ExpenseResponseDto> expenseResponses4 = expenseService.addExpenses(addExpensesRequest4);

        String jwt = jwtService.generateJwt("JD");
        //act

        MvcResult result = mvc.perform(get("/api/stats/basic-statistics")
                        .param("from", start.toString())
                        .param("to", end.toString())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        BasicStatisticsDto basicStatistics = objectMapper.readValue(result.getResponse().getContentAsString(), BasicStatisticsDto.class);

        assertEquals(expenseResponses2.get(0).getCost() + expenseResponses3.get(0).getCost(), basicStatistics.totalSpent());
        assertEquals(2, basicStatistics.totalTrips());
        assertEquals(1, basicStatistics.visitedCountries());
    }

    @Test
    @Transactional
    public void getBasicStatistics_shouldReturnAllBasicStatistics_whenDatesAreNull() throws Exception {
        LocalDate start = LocalDate.now().minusDays(10);
        LocalDate end = LocalDate.now().minusDays(5);
        Attraction attraction1 = attractionService.createAttraction(getCreateAttractionRequestWithDate(1,
                java.sql.Date.valueOf(start.minusDays(1).toString())));
        Attraction attraction2 = attractionService.createAttraction(getCreateAttractionRequestWithDate(2,
                java.sql.Date.valueOf(start.plusDays(1).toString())));
        Attraction attraction3 = attractionService.createAttraction(getCreateAttractionRequestWithDate(3,
                java.sql.Date.valueOf(start.plusDays(2).toString())));
        Attraction attraction4 = attractionService.createAttraction(getCreateAttractionRequestWithDate(4,
                java.sql.Date.valueOf(end.plusDays(1).toString())));

        AddExpensesRequest addExpensesRequest1 = new AddExpensesRequest(
                attraction1.getTrip().getHash(),
                List.of(getCreateExpenseRequest(1))
        );
        AddExpensesRequest addExpensesRequest2 = new AddExpensesRequest(
                attraction2.getTrip().getHash(),
                List.of(getCreateExpenseRequest(2))
        );
        AddExpensesRequest addExpensesRequest3 = new AddExpensesRequest(
                attraction3.getTrip().getHash(),
                List.of(getCreateExpenseRequest(3))
        );
        AddExpensesRequest addExpensesRequest4 = new AddExpensesRequest(
                attraction4.getTrip().getHash(),
                List.of(getCreateExpenseRequest(4))
        );

        List<ExpenseResponseDto> expenseResponses1 = expenseService.addExpenses(addExpensesRequest1);
        List<ExpenseResponseDto> expenseResponses2 = expenseService.addExpenses(addExpensesRequest2);
        List<ExpenseResponseDto> expenseResponses3 = expenseService.addExpenses(addExpensesRequest3);
        List<ExpenseResponseDto> expenseResponses4 = expenseService.addExpenses(addExpensesRequest4);

        String jwt = jwtService.generateJwt("JD");
        //act

        MvcResult result = mvc.perform(get("/api/stats/basic-statistics")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        BasicStatisticsDto basicStatistics = objectMapper.readValue(result.getResponse().getContentAsString(), BasicStatisticsDto.class);

        assertEquals(expenseResponses1.get(0).getCost() + expenseResponses2.get(0).getCost() +
                expenseResponses3.get(0).getCost() + expenseResponses4.get(0).getCost(), basicStatistics.totalSpent());
        assertEquals(4, basicStatistics.totalTrips());
        assertEquals(1, basicStatistics.visitedCountries());
    }

    @Test
    @Transactional
    public void getExpensesStatistics_shouldReturnExpensesStatistics_whenProperRequest() throws Exception {
        LocalDate start = LocalDate.now().minusDays(10);
        LocalDate end = LocalDate.now().minusDays(5);
        Attraction attraction1 = attractionService.createAttraction(getCreateAttractionRequestWithDate(1,
                java.sql.Date.valueOf(start.minusDays(1).toString())));
        Attraction attraction2 = attractionService.createAttraction(getCreateAttractionRequestWithDate(2,
                java.sql.Date.valueOf(start.plusDays(1).toString())));
        Attraction attraction3 = attractionService.createAttraction(getCreateAttractionRequestWithDate(3,
                java.sql.Date.valueOf(start.plusDays(2).toString())));
        Attraction attraction4 = attractionService.createAttraction(getCreateAttractionRequestWithDate(4,
                java.sql.Date.valueOf(end.plusDays(1).toString())));

        AddExpensesRequest addExpensesRequest1 = new AddExpensesRequest(
                attraction1.getTrip().getHash(),
                List.of(getCreateExpenseRequest(1))
        );
        AddExpensesRequest addExpensesRequest2 = new AddExpensesRequest(
                attraction2.getTrip().getHash(),
                List.of(getCreateExpenseRequest(2))
        );
        AddExpensesRequest addExpensesRequest3 = new AddExpensesRequest(
                attraction3.getTrip().getHash(),
                List.of(getCreateExpenseRequest(3),
                        getCreateExpenseRequestWithCategory(5, ExpenseCategoryEnum.Attractions),
                        getCreateExpenseRequestWithCategory(6, ExpenseCategoryEnum.Restaurants))
        );
        AddExpensesRequest addExpensesRequest4 = new AddExpensesRequest(
                attraction4.getTrip().getHash(),
                List.of(getCreateExpenseRequest(4))
        );

        List<ExpenseResponseDto> expenseResponses1 = expenseService.addExpenses(addExpensesRequest1);
        List<ExpenseResponseDto> expenseResponses2 = expenseService.addExpenses(addExpensesRequest2);
        List<ExpenseResponseDto> expenseResponses3 = expenseService.addExpenses(addExpensesRequest3);
        List<ExpenseResponseDto> expenseResponses4 = expenseService.addExpenses(addExpensesRequest4);

        String jwt = jwtService.generateJwt("JD");
        //act

        MvcResult result = mvc.perform(get("/api/stats/expenses-statistics")
                        .param("from", start.toString())
                        .param("to", end.toString())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        ExpenseStatisticsDto expenssStatistics = objectMapper.readValue(result.getResponse().getContentAsString(), ExpenseStatisticsDto.class);

        List<ExpenseSummaryDto> expectedListOfExpensesTemp = new ArrayList<>();
        expenseResponses2.stream()
                .collect(Collectors.groupingBy(e -> e.getExpenseCategory(), Collectors.summingDouble(e -> e.getCost())))
                .forEach((key, value) -> expectedListOfExpensesTemp.add(new ExpenseSummaryDto(key, value)));
        expenseResponses3.stream()
                .collect(Collectors.groupingBy(e -> e.getExpenseCategory(), Collectors.summingDouble(e -> e.getCost())))
                .forEach((key, value) -> expectedListOfExpensesTemp.add(new ExpenseSummaryDto(key, value)));
        List<ExpenseSummaryDto> expectedListOfExpenses = new ArrayList<>();
        expectedListOfExpensesTemp.stream()
                .collect(Collectors.groupingBy(e -> e.category(), Collectors.summingDouble(e -> e.cost())))
                .forEach((k, v) -> expectedListOfExpenses.add(new ExpenseSummaryDto(k, v)));

        List<AttractionMiniSummaryDto> attractionMiniSummaries =
                List.of(new AttractionMiniSummaryDto(attraction2.getHash(), attraction2.getTrip().getTitle()),
                        new AttractionMiniSummaryDto(attraction3.getHash(), attraction3.getTrip().getTitle()));

        assertThat(expenssStatistics.listOfExpenses()).isEqualTo(expectedListOfExpenses);
        assertThat(expenssStatistics.listOfAttractions()).isEqualTo(attractionMiniSummaries);
    }

    @Test
    @Transactional
    public void getExpensesStatistics_shouldReturnExpensesStatistics_whenDatesAreNull() throws Exception {
        LocalDate start = LocalDate.now().minusDays(10);
        LocalDate end = LocalDate.now().minusDays(5);
        Attraction attraction1 = attractionService.createAttraction(getCreateAttractionRequestWithDate(1,
                java.sql.Date.valueOf(start.minusDays(1).toString())));
        Attraction attraction2 = attractionService.createAttraction(getCreateAttractionRequestWithDate(2,
                java.sql.Date.valueOf(start.plusDays(1).toString())));
        Attraction attraction3 = attractionService.createAttraction(getCreateAttractionRequestWithDate(3,
                java.sql.Date.valueOf(start.plusDays(2).toString())));
        Attraction attraction4 = attractionService.createAttraction(getCreateAttractionRequestWithDate(4,
                java.sql.Date.valueOf(end.plusDays(1).toString())));

        AddExpensesRequest addExpensesRequest1 = new AddExpensesRequest(
                attraction1.getTrip().getHash(),
                List.of(getCreateExpenseRequest(1))
        );
        AddExpensesRequest addExpensesRequest2 = new AddExpensesRequest(
                attraction2.getTrip().getHash(),
                List.of(getCreateExpenseRequest(2))
        );
        AddExpensesRequest addExpensesRequest3 = new AddExpensesRequest(
                attraction3.getTrip().getHash(),
                List.of(getCreateExpenseRequest(3),
                        getCreateExpenseRequestWithCategory(5, ExpenseCategoryEnum.Attractions),
                        getCreateExpenseRequestWithCategory(6, ExpenseCategoryEnum.Restaurants))
        );
        AddExpensesRequest addExpensesRequest4 = new AddExpensesRequest(
                attraction4.getTrip().getHash(),
                List.of(getCreateExpenseRequest(4))
        );

        List<ExpenseResponseDto> expenseResponses1 = expenseService.addExpenses(addExpensesRequest1);
        List<ExpenseResponseDto> expenseResponses2 = expenseService.addExpenses(addExpensesRequest2);
        List<ExpenseResponseDto> expenseResponses3 = expenseService.addExpenses(addExpensesRequest3);
        List<ExpenseResponseDto> expenseResponses4 = expenseService.addExpenses(addExpensesRequest4);

        String jwt = jwtService.generateJwt("JD");
        //act

        MvcResult result = mvc.perform(get("/api/stats/expenses-statistics")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        ExpenseStatisticsDto expenssStatistics = objectMapper.readValue(result.getResponse().getContentAsString(), ExpenseStatisticsDto.class);

        List<ExpenseSummaryDto> expectedListOfExpensesTemp = new ArrayList<>();
        expenseResponses1.stream()
                .collect(Collectors.groupingBy(e -> e.getExpenseCategory(), Collectors.summingDouble(e -> e.getCost())))
                .forEach((key, value) -> expectedListOfExpensesTemp.add(new ExpenseSummaryDto(key, value)));
        expenseResponses2.stream()
                .collect(Collectors.groupingBy(e -> e.getExpenseCategory(), Collectors.summingDouble(e -> e.getCost())))
                .forEach((key, value) -> expectedListOfExpensesTemp.add(new ExpenseSummaryDto(key, value)));
        expenseResponses3.stream()
                .collect(Collectors.groupingBy(e -> e.getExpenseCategory(), Collectors.summingDouble(e -> e.getCost())))
                .forEach((key, value) -> expectedListOfExpensesTemp.add(new ExpenseSummaryDto(key, value)));
        expenseResponses4.stream()
                .collect(Collectors.groupingBy(e -> e.getExpenseCategory(), Collectors.summingDouble(e -> e.getCost())))
                .forEach((key, value) -> expectedListOfExpensesTemp.add(new ExpenseSummaryDto(key, value)));
        List<ExpenseSummaryDto> expectedListOfExpenses = new ArrayList<>();
        expectedListOfExpensesTemp.stream()
                .collect(Collectors.groupingBy(e -> e.category(), Collectors.summingDouble(e -> e.cost())))
                .forEach((k, v) -> expectedListOfExpenses.add(new ExpenseSummaryDto(k, v)));

        List<AttractionMiniSummaryDto> attractionMiniSummaries =
                List.of(new AttractionMiniSummaryDto(attraction1.getHash(), attraction1.getTrip().getTitle()),
                        new AttractionMiniSummaryDto(attraction2.getHash(), attraction2.getTrip().getTitle()),
                        new AttractionMiniSummaryDto(attraction3.getHash(), attraction3.getTrip().getTitle()),
                        new AttractionMiniSummaryDto(attraction4.getHash(), attraction4.getTrip().getTitle()));

        assertThat(expenssStatistics.listOfExpenses()).isEqualTo(expectedListOfExpenses);
        assertThat(expenssStatistics.listOfAttractions()).isEqualTo(attractionMiniSummaries);
    }

    @Test
    @Transactional
    public void getAttractionsPerMonth_shouldReturnExpensesStatistics_whenProperRequest() throws Exception {
        LocalDate start = LocalDate.of(2020, 1, 1);
        LocalDate end = LocalDate.of(2023, 1, 1);
        Attraction attraction1 = attractionService.createAttraction(getCreateAttractionRequestWithDate(1,
                java.sql.Date.valueOf(start.minusYears(1).toString())));
        Attraction attraction2 = attractionService.createAttraction(getCreateAttractionRequestWithDate(2,
                java.sql.Date.valueOf(LocalDate.of(2020, 2, 1).toString())));
        Attraction attraction3 = attractionService.createAttraction(getCreateAttractionRequestWithDate(3,
                java.sql.Date.valueOf(LocalDate.of(2021, 3, 1).toString())));
        Attraction attraction4 = attractionService.createAttraction(getCreateAttractionRequestWithDate(2,
                java.sql.Date.valueOf(LocalDate.of(2022, 1, 1).toString())));
        Attraction attraction5 = attractionService.createAttraction(getCreateAttractionRequestWithDate(3,
                java.sql.Date.valueOf(LocalDate.of(2022, 1, 1).toString())));
        Attraction attraction6 = attractionService.createAttraction(getCreateAttractionRequestWithDate(4,
                java.sql.Date.valueOf(end.plusYears(1).toString())));


        String jwt = jwtService.generateJwt("JD");
        //act

        MvcResult result = mvc.perform(get("/api/stats/attractions-per-month")
                        .param("from", start.toString())
                        .param("to", end.toString())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        List<AttractionAndDateDto> attractionsPerMonth = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertThat(attractionsPerMonth).asList().containsExactlyInAnyOrder(
                new AttractionAndDateDto(attraction2.getVisitedAt(), 1),
                new AttractionAndDateDto(attraction3.getVisitedAt(), 1),
                new AttractionAndDateDto(attraction4.getVisitedAt(), 2));
    }

    @Test
    @Transactional
    public void getExpensesPerMonth_shouldReturnExpensesPerMonth_whenProperRequest() throws Exception {
        LocalDate start = LocalDate.of(2020, 1, 1);
        LocalDate end = LocalDate.of(2023, 1, 1);
        Attraction attraction1 = attractionService.createAttraction(getCreateAttractionRequestWithDate(1,
                java.sql.Date.valueOf(start.minusYears(1).toString())));
        Attraction attraction2 = attractionService.createAttraction(getCreateAttractionRequestWithDate(2,
                java.sql.Date.valueOf(LocalDate.of(2020, 2, 1).toString())));
        Attraction attraction3 = attractionService.createAttraction(getCreateAttractionRequestWithDate(3,
                java.sql.Date.valueOf(LocalDate.of(2021, 3, 1).toString())));
        Attraction attraction4 = attractionService.createAttraction(getCreateAttractionRequestWithDate(2,
                java.sql.Date.valueOf(end.plusYears(1).toString())));


        AddExpensesRequest addExpensesRequest1 = new AddExpensesRequest(
                attraction1.getTrip().getHash(),
                List.of(getCreateExpenseRequest(1))
        );
        AddExpensesRequest addExpensesRequest2 = new AddExpensesRequest(
                attraction2.getTrip().getHash(),
                List.of(getCreateExpenseRequestWithDate(2, java.sql.Date.valueOf(LocalDate.of(2021, 2, 1).toString())))
        );
        AddExpensesRequest addExpensesRequest3 = new AddExpensesRequest(
                attraction3.getTrip().getHash(),
                List.of(getCreateExpenseRequestWithDate(3, java.sql.Date.valueOf(LocalDate.of(2020, 2, 1).toString())))
        );

        AddExpensesRequest addExpensesRequest4 = new AddExpensesRequest(
                attraction4.getTrip().getHash(),
                List.of(getCreateExpenseRequest(4))
        );

        List<ExpenseResponseDto> expenseResponses1 = expenseService.addExpenses(addExpensesRequest1);
        List<ExpenseResponseDto> expenseResponses2 = expenseService.addExpenses(addExpensesRequest2);
        List<ExpenseResponseDto> expenseResponses3 = expenseService.addExpenses(addExpensesRequest3);
        List<ExpenseResponseDto> expenseResponses4 = expenseService.addExpenses(addExpensesRequest4);

        String jwt = jwtService.generateJwt("JD");
        //act

        MvcResult result = mvc.perform(get("/api/stats/expenses-per-month")
                        .param("from", start.toString())
                        .param("to", end.toString())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        List<ExpenseAndDateDto> expensesStatistics = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertThat(expensesStatistics).asList().containsExactlyInAnyOrder(
                new ExpenseAndDateDto(expenseResponses2.get(0).getSpentAt(), expenseResponses2.get(0).getCost()),
                new ExpenseAndDateDto(expenseResponses3.get(0).getSpentAt(), expenseResponses3.get(0).getCost()));
    }

    @Test
    @Transactional
    public void getExpensesForAttraction_shouldReturnExpensesForAttraction_whenProperRequest() throws Exception {
        Attraction attraction1 = attractionService.createAttraction(getCreateAttractionRequest(1));


        AddExpensesRequest addExpensesRequest1 = new AddExpensesRequest(
                attraction1.getTrip().getHash(),
                List.of(getCreateExpenseRequest(1),
                        getCreateExpenseRequestWithCategory(2, ExpenseCategoryEnum.Food))
        );

        List<ExpenseResponseDto> expenseResponses1 = expenseService.addExpenses(addExpensesRequest1);


        String jwt = jwtService.generateJwt("JD");
        //act

        MvcResult result = mvc.perform(get("/api/stats/attraction/" + attraction1.getHash() + "/expenses-statistics")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        List<ExpenseSummaryDto> expenses = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });

        System.out.println(expenses);
    }

    private ExpenseRequestDto getCreateExpenseRequest(int i) {
        return new ExpenseRequestDto(
                "test_expense" + i,
                21.37,
                ExpenseCategoryEnum.Food,
                CurrencyEnum.EUR,
                new Date()
        );
    }

    private ExpenseRequestDto getCreateExpenseRequestWithDate(int i, Date date) {
        ExpenseRequestDto expenseRequestDto = getCreateExpenseRequest(i);
        expenseRequestDto.setSpentAt(date);
        return expenseRequestDto;
    }

    private ExpenseRequestDto getCreateExpenseRequestWithCategory(int i, ExpenseCategoryEnum category) {
        ExpenseRequestDto expenseRequestDto = getCreateExpenseRequest(i);
        expenseRequestDto.setExpenseCategory(category);
        return expenseRequestDto;
    }

    private CreateAttractionRequest getCreateAttractionRequest(int i) {
        return new CreateAttractionRequest(
                "osm_id",
                "title" + i,
                "description" + i,
                true,
                true,
                java.sql.Date.valueOf(LocalDate.now().toString()),
                null,
                null

        );
    }

    private CreateAttractionRequest getCreateAttractionRequestWithDate(int i, Date visitedAt) {
        CreateAttractionRequest createAttractionRequest = getCreateAttractionRequest(i);
        createAttractionRequest.setVisitedAt(visitedAt);
        return createAttractionRequest;
    }
}
