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
import travel.ways.travelwaysapi.trip.model.dto.request.CreateTripInvitationRequest;
import travel.ways.travelwaysapi.trip.model.dto.request.CreateTripRequest;
import travel.ways.travelwaysapi.trip.model.dto.response.TripInvitationResponse;
import travel.ways.travelwaysapi.trip.service.internal.TripInvitationService;
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
class TripInvitationControllerTest {

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
    private TripInvitationService tripInvitationService;


    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @Transactional
    public void createTripInvitation_shouldAddTripInvitation_whenProperRequest() throws Exception {
        // arrange
        CreateTripRequest createTripRequest = getCreateTripRequest(0);
        Trip trip = tripService.createTrip(createTripRequest);
        CreateTripInvitationRequest createTripInvitationRequest = new CreateTripInvitationRequest(
                trip.getHash(),
                userService.getByUsername("JD_2").getHash()
        );
        String jwt = jwtService.generateJwt("JD");
        //act
        MvcResult result = mvc
                .perform(post("/api/trip/invitation")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .content(objectMapper.writeValueAsString(createTripInvitationRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        //assert
        jwtService.authenticateUser(jwt);
        TripInvitationResponse tripInvitationResponse =
                objectMapper.readValue(result.getResponse().getContentAsString(), TripInvitationResponse.class);
        assertEquals(trip, tripInvitationService.getByHash(tripInvitationResponse.getInvitationHash()).getTrip());
        //clean
        tripService.deleteTrip(trip);
    }

    @Test
    @Transactional
    public void deleteTripInvitation_shouldDeleteTripInvitation_whenProperRequest() throws Exception {
        // arrange
        CreateTripRequest createTripRequest = getCreateTripRequest(0);
        Trip trip = tripService.createTrip(createTripRequest);
        CreateTripInvitationRequest createTripInvitationRequest = new CreateTripInvitationRequest(
                trip.getHash(),
                userService.getByUsername("JD_2").getHash()
        );
        TripInvitationResponse tripInvitation = tripInvitationService.createTripInvitation(createTripInvitationRequest);
        String jwt = jwtService.generateJwt("JD");
        //act
        mvc.perform(delete("/api/trip/invitation/" + tripInvitation.getInvitationHash())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        //assert
        jwtService.authenticateUser(jwt);
        assertThrows(ServerException.class, () -> tripInvitationService.getByHash(tripInvitation.getInvitationHash()));

        //clean
        tripService.deleteTrip(trip);
    }

    @Test
    @Transactional
    public void deleteTripInvitation_shouldReturn403_whenForbidden() throws Exception {
        // arrange
        CreateTripRequest createTripRequest = getCreateTripRequest(0);
        Trip trip = tripService.createTrip(createTripRequest);
        CreateTripInvitationRequest createTripInvitationRequest = new CreateTripInvitationRequest(
                trip.getHash(),
                userService.getByUsername("JD_2").getHash()
        );
        TripInvitationResponse tripInvitation = tripInvitationService.createTripInvitation(createTripInvitationRequest);
        String jwt = jwtService.generateJwt("JD_2");
        //act
        MvcResult result = mvc
                .perform(delete("/api/trip/invitation/" + tripInvitation.getInvitationHash())
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
    @Transactional
    public void acceptInvitation_shouldAddUserToTrip_whenAccepted() throws Exception {
        // arrange
        CreateTripRequest createTripRequest = getCreateTripRequest(0);
        Trip trip = tripService.createTrip(createTripRequest);
        CreateTripInvitationRequest createTripInvitationRequest = new CreateTripInvitationRequest(
                trip.getHash(),
                userService.getByUsername("JD_2").getHash()
        );
        TripInvitationResponse tripInvitation = tripInvitationService.createTripInvitation(createTripInvitationRequest);
        String jwt = jwtService.generateJwt("JD_2");
        //act
        mvc.perform(put("/api/trip/invitation/" + tripInvitation.getInvitationHash() + "/accept")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        //assert
        jwtService.authenticateUser(jwtService.generateJwt("JD"));
        trip = tripService.getTrip(trip.getHash());
        assertTrue(trip.getUsers().stream().anyMatch(a -> a.getUser().equals(userService.getByUsername("JD_2"))));
        //clean
        tripService.deleteTrip(trip);
    }

    @Test
    @Transactional
    public void acceptInvitation_shouldThrow403_whenForbidden() throws Exception {
        // arrange
        CreateTripRequest createTripRequest = getCreateTripRequest(0);
        Trip trip = tripService.createTrip(createTripRequest);
        CreateTripInvitationRequest createTripInvitationRequest = new CreateTripInvitationRequest(
                trip.getHash(),
                userService.getByUsername("JD").getHash()
        );
        TripInvitationResponse tripInvitation = tripInvitationService.createTripInvitation(createTripInvitationRequest);
        String jwt = jwtService.generateJwt("JD_2");
        //act
        mvc.perform(put("/api/trip/invitation/" + tripInvitation.getInvitationHash() + "/accept")
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
    @Transactional
    public void declineInvitation_shouldDeclineInvitation_whenProperRequest() throws Exception {
        // arrange
        CreateTripRequest createTripRequest = getCreateTripRequest(0);
        Trip trip = tripService.createTrip(createTripRequest);
        CreateTripInvitationRequest createTripInvitationRequest = new CreateTripInvitationRequest(
                trip.getHash(),
                userService.getByUsername("JD_2").getHash()
        );
        TripInvitationResponse tripInvitation = tripInvitationService.createTripInvitation(createTripInvitationRequest);
        String jwt = jwtService.generateJwt("JD_2");
        //act
        mvc.perform(put("/api/trip/invitation/" + tripInvitation.getInvitationHash() + "/decline")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        //assert
        jwtService.authenticateUser(jwtService.generateJwt("JD"));
        trip = tripService.getTrip(trip.getHash());
        assertFalse(trip.getUsers().stream().anyMatch(a -> a.getUser().equals(userService.getByUsername("JD_2"))));
        //clean
        tripService.deleteTrip(trip);
    }

    @Test
    @Transactional
    public void declineInvitation_shouldThrow403_whenForbidden() throws Exception {
        // arrange
        CreateTripRequest createTripRequest = getCreateTripRequest(0);
        Trip trip = tripService.createTrip(createTripRequest);
        CreateTripInvitationRequest createTripInvitationRequest = new CreateTripInvitationRequest(
                trip.getHash(),
                userService.getByUsername("JD").getHash()
        );
        TripInvitationResponse tripInvitation = tripInvitationService.createTripInvitation(createTripInvitationRequest);
        String jwt = jwtService.generateJwt("JD_2");
        //act
        mvc.perform(put("/api/trip/invitation/" + tripInvitation.getInvitationHash() + "/decline")
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
    @Transactional
    public void getAllActiveInvitationForUser_shouldReturnActiveInvitations_whenProperRequest() throws Exception {
        // arrange
        CreateTripRequest createTripRequest = getCreateTripRequest(0);
        Trip trip = tripService.createTrip(createTripRequest);
        CreateTripInvitationRequest createTripInvitationRequest = new CreateTripInvitationRequest(
                trip.getHash(),
                userService.getByUsername("JD_2").getHash()
        );
        TripInvitationResponse tripInvitation = tripInvitationService.createTripInvitation(createTripInvitationRequest);
        String jwt = jwtService.generateJwt("JD_2");
        //act
        MvcResult result = mvc.perform(get("/api/trip/invitation/all")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        //assert
        jwtService.authenticateUser(jwtService.generateJwt("JD"));
        trip = tripService.getTrip(trip.getHash());
        List<TripInvitationResponse> tripInvitationResponseList
                = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertEquals(tripInvitationResponseList.get(0).getInvitationHash(), tripInvitation.getInvitationHash());
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