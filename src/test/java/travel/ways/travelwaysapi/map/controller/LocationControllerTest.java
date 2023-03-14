package travel.ways.travelwaysapi.map.controller;

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
import travel.ways.travelwaysapi._core.model.dto.BaseResponse;
import travel.ways.travelwaysapi.auth.service.impl.JwtServiceImpl;
import travel.ways.travelwaysapi.map.model.dto.request.CreateLocationRequest;
import travel.ways.travelwaysapi.map.model.dto.response.LocationResponse;
import travel.ways.travelwaysapi.map.service.shared.LocationService;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@WithMockUser("JD")
class LocationControllerTest {

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private LocationService locationService;
    @Autowired
    private JwtServiceImpl jwtService;
    private MockMvc mvc;


    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void existsLocation_shouldReturnTrue_whenLocationExists() throws Exception {
        // arrange
        String jwt = jwtService.generateJwt("JD");
        //act
        MvcResult result = mvc
                .perform(get("/api/location/exists/osm_id")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        //assert
        jwtService.authenticateUser(jwt);
        BaseResponse baseResponse =
                objectMapper.readValue(result.getResponse().getContentAsString(), BaseResponse.class);
        assertTrue(baseResponse.isSuccess());
    }

    @Test
    public void existsLocation_shouldReturnFalse_whenLocationNotExists() throws Exception {
        // arrange
        String jwt = jwtService.generateJwt("JD");
        //act
        MvcResult result = mvc
                .perform(get("/api/location/exists/not_exists")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        //assert
        jwtService.authenticateUser(jwt);
        BaseResponse baseResponse =
                objectMapper.readValue(result.getResponse().getContentAsString(), BaseResponse.class);
        assertFalse(baseResponse.isSuccess());
    }

    @Test
    public void add_shouldAddLocation_whenProperRequest() throws Exception {
        // arrange
        CreateLocationRequest createLocationRequest = new CreateLocationRequest(
                "new_location_name",
                "34.343",
                "34.332",
                "new_location_display_name",
                "new_location"
        );

        String jwt = jwtService.generateJwt("JD");
        //act
        mvc.perform(post("/api/location")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .content(objectMapper.writeValueAsString(createLocationRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        //assert
        jwtService.authenticateUser(jwt);
        assertTrue(locationService.exitsByOsmId(createLocationRequest.getOsmId()));
    }

    @Test
    @Transactional
    public void add_shouldReturn400_whenBadRequest() throws Exception {
        // arrange
        String jwt = jwtService.generateJwt("JD");
        //act
        mvc.perform(post("/api/location")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .content("bad_request")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    @Transactional
    public void addIfNotExits_shouldAddLocationIfNotExists_whenProperRequest() throws Exception {
        // arrange
        CreateLocationRequest createLocationRequest = new CreateLocationRequest(
                "new_location_name",
                "34.343",
                "34.332",
                "new_location_display_name",
                "different_new_location"
        );

        String jwt = jwtService.generateJwt("JD");
        //act
        mvc.perform(post("/api/location/add-if-not-exits")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .content(objectMapper.writeValueAsString(createLocationRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        //assert
        jwtService.authenticateUser(jwt);
        assertTrue(locationService.exitsByOsmId(createLocationRequest.getOsmId()));
    }

    @Test
    @Transactional
    public void addIfNotExits_shouldReturnLocationIfExists_whenProperRequest() throws Exception {
        // arrange
        CreateLocationRequest createLocationRequest = new CreateLocationRequest(
                "name",
                "54.434",
                "43.343",
                "display_name",
                "osm_id"
        );

        String jwt = jwtService.generateJwt("JD");
        //act
        MvcResult result = mvc.perform(post("/api/location/add-if-not-exits")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .content(objectMapper.writeValueAsString(createLocationRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        jwtService.authenticateUser(jwt);
        LocationResponse locationResponse =
                objectMapper.readValue(result.getResponse().getContentAsString(), LocationResponse.class);
        //assert
        jwtService.authenticateUser(jwt);
        assertEquals("osm_id", locationResponse.getOsmId());
    }


}