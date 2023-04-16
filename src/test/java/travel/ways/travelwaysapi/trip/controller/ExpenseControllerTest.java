package travel.ways.travelwaysapi.trip.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import travel.ways.travelwaysapi._core.exception.ServerException;
import travel.ways.travelwaysapi.auth.service.impl.JwtServiceImpl;
import travel.ways.travelwaysapi.trip.model.db.expense.CurrencyEnum;
import travel.ways.travelwaysapi.trip.model.db.expense.ExpenseCategoryEnum;
import travel.ways.travelwaysapi.trip.model.db.trip.Trip;
import travel.ways.travelwaysapi.trip.model.dto.request.AddExpensesRequest;
import travel.ways.travelwaysapi.trip.model.dto.request.CreateTripRequest;
import travel.ways.travelwaysapi.trip.model.dto.request.EditExpenseRequest;
import travel.ways.travelwaysapi.trip.model.dto.request.ExpenseRequestDto;
import travel.ways.travelwaysapi.trip.model.dto.response.ExpenseResponseDto;
import travel.ways.travelwaysapi.trip.service.internal.ExpenseService;
import travel.ways.travelwaysapi.trip.service.shared.TripService;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@WithMockUser("JD")
class ExpenseControllerTest {

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JwtServiceImpl jwtService;
    private MockMvc mvc;
    @Autowired
    private TripService tripService;
    @Autowired
    private ExpenseService expenseService;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @Transactional
    public void addExpenses_shouldAddExpenses_whenProperRequest() throws Exception {
        // arrange
        Trip trip = tripService.createTrip(getCreateTripRequest());
        ExpenseRequestDto expenseRequestDto1 = getCreateExpenseRequest(1);
        ExpenseRequestDto expenseRequestDto2 = getCreateExpenseRequest(2);

        AddExpensesRequest addExpensesRequest = new AddExpensesRequest(
                trip.getHash(),
                List.of(expenseRequestDto1, expenseRequestDto2)
        );

        String jwt = jwtService.generateJwt("JD");
        //act
        MvcResult result = mvc.perform(post("/api/expense/")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .content(objectMapper.writeValueAsString(addExpensesRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        List<ExpenseResponseDto> expenses = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });

        //assert
        assertEquals(2, expenses.size());
        assertTrue(expenses.stream().anyMatch(e -> e.getDescription().equals(expenseRequestDto1.getDescription())));
        assertTrue(expenses.stream().anyMatch(e -> e.getDescription().equals(expenseRequestDto2.getDescription())));
        assertEquals(trip.getHash(), expenses.get(0).getTripHash());
        assertEquals(trip.getHash(), expenses.get(1).getTripHash());

        //clean
        jwtService.authenticateUser(jwt);
        tripService.deleteTrip(trip);
    }

    @Test
    public void addExpenses_return400_whenBadRequest() throws Exception {

        String jwt = jwtService.generateJwt("JD");
        //act
        mvc.perform(post("/api/expense/")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .content("bad_request")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    public void addExpenses_return403_whenForbidden() throws Exception {
        Trip trip = tripService.createTrip(getCreateTripRequest());
        ExpenseRequestDto expenseRequestDto1 = getCreateExpenseRequest(1);

        AddExpensesRequest addExpensesRequest = new AddExpensesRequest(
                trip.getHash(),
                List.of(expenseRequestDto1)
        );

        String jwt = jwtService.generateJwt("JD_2");
        //act
        mvc.perform(post("/api/expense/")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .content(objectMapper.writeValueAsString(addExpensesRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isForbidden());

        jwtService.authenticateUser(jwtService.generateJwt("JD"));
        tripService.deleteTrip(trip);
    }

    @Test
    @Transactional
    public void removeExpense_shouldRemoveExpense_whenProperRequest() throws Exception {
        Trip trip = tripService.createTrip(getCreateTripRequest());

        AddExpensesRequest addExpensesRequest = new AddExpensesRequest(
                trip.getHash(),
                List.of(getCreateExpenseRequest(1))
        );
        List<ExpenseResponseDto> expenseResponses = expenseService.addExpenses(addExpensesRequest);

        String jwt = jwtService.generateJwt("JD");
        //act
        mvc.perform(delete("/api/expense/" + expenseResponses.get(0).getExpenseHash())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().is2xxSuccessful());

        //assert
        assertThrows(ServerException.class, () -> expenseService.getExpense(expenseResponses.get(0).getExpenseHash()));

        jwtService.authenticateUser(jwt);
        tripService.deleteTrip(trip);
    }

    @Test
    @Transactional
    public void removeExpense_shouldReturn403_whenForbidden() throws Exception {
        Trip trip = tripService.createTrip(getCreateTripRequest());

        AddExpensesRequest addExpensesRequest = new AddExpensesRequest(
                trip.getHash(),
                List.of(getCreateExpenseRequest(1))
        );
        List<ExpenseResponseDto> expenseResponses = expenseService.addExpenses(addExpensesRequest);

        String jwt = jwtService.generateJwt("JD_2");
        //act
        mvc.perform(delete("/api/expense/" + expenseResponses.get(0).getExpenseHash())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().isForbidden());

        jwtService.authenticateUser(jwtService.generateJwt("JD"));
        tripService.deleteTrip(trip);
    }

    @Test
    @Transactional
    public void getExpensesByTrip_shouldReturnExpenses_whenProperRequest() throws Exception {
        Trip trip = tripService.createTrip(getCreateTripRequest());

        AddExpensesRequest addExpensesRequest = new AddExpensesRequest(
                trip.getHash(),
                List.of(getCreateExpenseRequest(1), getCreateExpenseRequest(2))
        );
        List<ExpenseResponseDto> expenseResponses = expenseService.addExpenses(addExpensesRequest);

        String jwt = jwtService.generateJwt("JD");
        //act
        MvcResult result = mvc.perform(get("/api/expense/trip/" + trip.getHash())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        List<ExpenseResponseDto> expenses = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertEquals(expenseResponses.size(), expenses.size());
        assertEquals(expenseResponses.get(0), expenses.get(0));
        assertEquals(expenseResponses.get(1), expenses.get(1));

        jwtService.authenticateUser(jwt);
        tripService.deleteTrip(trip);
    }

    @Test
    @Transactional
    public void getExpensesByTrip_shouldReturn403_whenForbidden() throws Exception {
        Trip trip = tripService.createTrip(getCreateTripRequest());

        String jwt = jwtService.generateJwt("JD_2");
        //act
        mvc.perform(get("/api/expense/trip/" + trip.getHash())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().isForbidden());

        jwtService.authenticateUser(jwtService.generateJwt("JD"));
        tripService.deleteTrip(trip);
    }

    @Test
    @Transactional
    public void editExpense_shouldEditExpense_whenProperRequest() throws Exception {
        Trip trip = tripService.createTrip(getCreateTripRequest());

        AddExpensesRequest addExpensesRequest = new AddExpensesRequest(
                trip.getHash(),
                List.of(getCreateExpenseRequest(1))
        );
        List<ExpenseResponseDto> expenseResponses = expenseService.addExpenses(addExpensesRequest);
        EditExpenseRequest editExpenseRequest = new EditExpenseRequest(
                expenseResponses.get(0).getExpenseHash(),
                "new_description",
                21.37,
                ExpenseCategoryEnum.Food,
                CurrencyEnum.EUR,
                new Date()
        );

        String jwt = jwtService.generateJwt("JD");
        //act
        MvcResult result = mvc.perform(put("/api/expense/")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .content(objectMapper.writeValueAsString(editExpenseRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        ExpenseResponseDto expense = objectMapper.readValue(result.getResponse().getContentAsString(), ExpenseResponseDto.class);

        assertNotEquals(expenseResponses.get(0).getDescription(), expense.getDescription());
        assertEquals(editExpenseRequest.getDescription(), expense.getDescription());

        jwtService.authenticateUser(jwt);
        tripService.deleteTrip(trip);
    }
    @Test
    @Transactional
    public void editExpense_shouldReturn403_whenForbidden() throws Exception {
        Trip trip = tripService.createTrip(getCreateTripRequest());

        AddExpensesRequest addExpensesRequest = new AddExpensesRequest(
                trip.getHash(),
                List.of(getCreateExpenseRequest(1))
        );
        List<ExpenseResponseDto> expenseResponses = expenseService.addExpenses(addExpensesRequest);
        EditExpenseRequest editExpenseRequest = new EditExpenseRequest(
                expenseResponses.get(0).getExpenseHash(),
                "new_description",
                21.37,
                ExpenseCategoryEnum.Food,
                CurrencyEnum.EUR,
                new Date()
        );

        String jwt = jwtService.generateJwt("JD_2");
        //act
        mvc.perform(put("/api/expense/")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .content(objectMapper.writeValueAsString(editExpenseRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isForbidden());

        jwtService.authenticateUser(jwtService.generateJwt("JD"));
        tripService.deleteTrip(trip);
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

    private CreateTripRequest getCreateTripRequest() {
        return new CreateTripRequest(
                "test_trip",
                true,
                "test_Description"
        );
    }
}
