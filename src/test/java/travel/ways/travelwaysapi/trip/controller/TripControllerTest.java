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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import travel.ways.travelwaysapi._core.exception.ServerException;
import travel.ways.travelwaysapi.auth.service.impl.JwtServiceImpl;
import travel.ways.travelwaysapi.trip.model.db.Trip;
import travel.ways.travelwaysapi.trip.model.dto.request.CreateTripRequest;
import travel.ways.travelwaysapi.trip.model.dto.request.EditTripRequest;
import travel.ways.travelwaysapi.trip.model.dto.response.TripResponse;
import travel.ways.travelwaysapi.trip.service.shared.TripService;
import travel.ways.travelwaysapi.user.service.shared.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@WithMockUser("JD")
class TripControllerTest {
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


    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @Transactional
    public void createTrip_shouldAddTrip_whenProperRequest() throws Exception {
        // arrange
        CreateTripRequest createTripRequest = getCreateTripRequest(0);
        String jwt = jwtService.generateJwt("JD");
        //act
        MvcResult result = mvc
                .perform(post("/api/trip")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .content(objectMapper.writeValueAsString(createTripRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        //assert
        jwtService.authenticateUser(jwt);
        TripResponse resultTrip =
                objectMapper.readValue(result.getResponse().getContentAsString(), TripResponse.class);
        Trip trip = tripService.getTrip(resultTrip.getHash());
        assertEquals(createTripRequest.getTitle(), trip.getTitle());

        //clean
        tripService.deleteTrip(trip);
    }

    @Test
    @Transactional
    public void createTrip_shouldReturn400_whenBadRequest() throws Exception {
        // arrange
        String jwt = jwtService.generateJwt("JD");
        //act & assert
        mvc.perform(post("/api/trip")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .content("bad_Request")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteTrip_shouldDeleteTrip_whenProperRequest() throws Exception {
        // arrange
        CreateTripRequest createTripRequest = getCreateTripRequest(0);
        Trip trip = tripService.createTrip(createTripRequest);
        String jwt = jwtService.generateJwt("JD");
        //act
        mvc.perform(delete("/api/trip/" + trip.getHash())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        //assert
        jwtService.authenticateUser(jwt);
        assertThrows(ServerException.class, () -> tripService.getTrip(trip.getHash()));
    }

    @Test
    public void deleteTrip_shouldThrow_whenForbidden() throws Exception {
        // arrange
        CreateTripRequest createTripRequest = getCreateTripRequest(0);
        Trip trip = tripService.createTrip(createTripRequest);
        String jwt = jwtService.generateJwt("JD_2");
        //act
        mvc.perform(delete("/api/trip/" + trip.getHash())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isForbidden())
                .andReturn();

        //clean
        jwtService.authenticateUser(jwtService.generateJwt("JD"));
        tripService.deleteTrip(trip);
    }

    @Test
    public void getUserTrips_shouldReturnTrips_whenProperRequest() throws Exception {

        // arrange
        Trip trip = tripService.createTrip(getCreateTripRequest(0));
        Trip trip1 = tripService.createTrip(getCreateTripRequest(1));

        String jwt = jwtService.generateJwt("JD_2");
        //act
        MvcResult result = mvc
                .perform(get("/api/trip/all/" + userService.getByUsername("JD").getHash())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        //assert
        jwtService.authenticateUser(jwtService.generateJwt("JD"));
        List<TripResponse> trips = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertEquals(2, trips.size());

        //clean
        tripService.deleteTrip(trip);
        tripService.deleteTrip(trip1);
    }

    @Test
    public void getLoggedUserTrips_shouldReturnTrips_whenProperRequest() throws Exception {
        // arrange
        Trip trip = tripService.createTrip(getCreateTripRequest(0));
        Trip trip1 = tripService.createTrip(getCreateTripRequest(1));
        String jwt = jwtService.generateJwt("JD");

        //act
        MvcResult result = mvc
                .perform(get("/api/trip/all/")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        //assert
        jwtService.authenticateUser(jwt);
        List<TripResponse> trips = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertEquals(2, trips.size());
        //clean
        tripService.deleteTrip(trip);
        tripService.deleteTrip(trip1);
    }

    @Test
    public void getTrip_shouldReturnTrip_whenProperRequest() throws Exception {
        // arrange
        Trip trip = tripService.createTrip(getCreateTripRequest(0));
        String jwt = jwtService.generateJwt("JD");
        //act
        MvcResult result = mvc
                .perform(get("/api/trip/" + trip.getHash())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        //assert
        jwtService.authenticateUser(jwt);
        TripResponse tripResponse = objectMapper.readValue(result.getResponse().getContentAsString(), TripResponse.class);
        assertEquals(trip.getHash(), tripResponse.getHash());

        //clean
        tripService.deleteTrip(trip);
    }

    @Test
    @Transactional
    public void getTrip_shouldReturn404_whenForbidden() throws Exception {
        // arrange
        Trip trip = tripService.createTrip(getCreateTripRequest(0));
        trip.setPublic(false);
        String jwt = jwtService.generateJwt("JD_2");
        //act
        mvc.perform(get("/api/trip/" + trip.getHash())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().isNotFound())
                .andReturn();

        //assert
        jwtService.authenticateUser(jwtService.generateJwt("JD"));
        //clean
        tripService.deleteTrip(trip);
    }

    @Test
    @Transactional
    public void getTrip_shouldReturn404_whenBadRequest() throws Exception {
        // arrange
        String jwt = jwtService.generateJwt("JD");
        //act
        mvc.perform(get("/api/trip/badHash")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    public void editTrip_shouldEditTrip_whenProperRequest() throws Exception {
        CreateTripRequest createTripRequest = getCreateTripRequest(0);
        Trip trip = tripService.createTrip(createTripRequest);
        // arrange
        EditTripRequest editTripRequest = new EditTripRequest(
                trip.getHash(),
                "new_title",
                true,
                "new_description"
        );
        String jwt = jwtService.generateJwt("JD");
        //act
        MvcResult result = mvc
                .perform(put("/api/trip/edit")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .content(objectMapper.writeValueAsString(editTripRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andReturn();


        //assert
        jwtService.authenticateUser(jwt);
        TripResponse resultTrip =
                objectMapper.readValue(result.getResponse().getContentAsString(), TripResponse.class);
        Trip newtrip = tripService.getTrip(resultTrip.getHash());
        assertEquals(editTripRequest.getTitle(), newtrip.getTitle());

        //clean
        tripService.deleteTrip(trip);
    }

    @Test
    public void editTrip_shouldReturn403_whenForbidden() throws Exception {
        // arrange
        CreateTripRequest createTripRequest = getCreateTripRequest(0);
        Trip trip = tripService.createTrip(createTripRequest);
        EditTripRequest editTripRequest = new EditTripRequest(
                trip.getHash(),
                "new_title",
                true,
                "new_description"
        );
        String jwt = jwtService.generateJwt("JD_2");
        //act
        mvc.perform(put("/api/trip/edit")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .content(objectMapper.writeValueAsString(editTripRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isForbidden())
                .andReturn();
        //assert
        jwtService.authenticateUser(jwtService.generateJwt("JD"));
        //clean
        tripService.deleteTrip(trip);
    }

    @Test
    public void closeTrip_shouldCloseTrip_whenProperRequest() throws Exception {
        // arrange
        CreateTripRequest createTripRequest = getCreateTripRequest(0);
        Trip trip = tripService.createTrip(createTripRequest);
        String jwt = jwtService.generateJwt("JD");
        //act
        mvc.perform(put("/api/trip/close/" + trip.getHash())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        //assert
        jwtService.authenticateUser(jwt);
        Trip newtrip = tripService.getTrip(trip.getHash());
        assertFalse(newtrip.isOpen());

        //clean
        tripService.deleteTrip(trip);
    }

    @Test
    public void closeTrip_shouldReturn403_whenForbidden() throws Exception {
        // arrange
        CreateTripRequest createTripRequest = getCreateTripRequest(0);
        Trip trip = tripService.createTrip(createTripRequest);
        String jwt = jwtService.generateJwt("JD_2");
        //act
        mvc.perform(put("/api/trip/close/" + trip.getHash())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isForbidden())
                .andReturn();

        //assert
        jwtService.authenticateUser(jwtService.generateJwt("JD"));
        //clean
        tripService.deleteTrip(trip);
    }

    @Test
    public void openTrip_shouldOpenTrip_whenProperRequest() throws Exception {
        // arrange
        CreateTripRequest createTripRequest = getCreateTripRequest(0);
        Trip trip = tripService.createTrip(createTripRequest);
        String jwt = jwtService.generateJwt("JD");
        //act
        mvc.perform(put("/api/trip/open/" + trip.getHash())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        //assert
        jwtService.authenticateUser(jwt);
        Trip newtrip = tripService.getTrip(trip.getHash());
        assertTrue(newtrip.isOpen());
        //clean
        tripService.deleteTrip(trip);
    }

    @Test
    public void openTrip_shouldReturn403_whenForbidden() throws Exception {
        // arrange
        CreateTripRequest createTripRequest = getCreateTripRequest(0);
        Trip trip = tripService.createTrip(createTripRequest);
        String jwt = jwtService.generateJwt("JD_2");
        //act
        mvc.perform(put("/api/trip/open/" + trip.getHash())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isForbidden())
                .andReturn();
        //assert
        jwtService.authenticateUser(jwtService.generateJwt("JD"));
        //clean
        tripService.deleteTrip(trip);
    }

    private CreateTripRequest getCreateTripRequest(int i) {
        return new CreateTripRequest(
                "title" + i,
                true,
                "description" + i
        );
    }
}