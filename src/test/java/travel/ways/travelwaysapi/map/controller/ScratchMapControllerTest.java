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
import org.springframework.web.context.WebApplicationContext;
import travel.ways.travelwaysapi.auth.service.impl.JwtServiceImpl;
import travel.ways.travelwaysapi.map.model.dto.response.VisitedCountriesResponse;
import travel.ways.travelwaysapi.map.service.impl.ScratchMapService;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@WithMockUser("JD")
class ScratchMapControllerTest {

    @Autowired
    ScratchMapService scratchMapService;
    private MockMvc mvc;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JwtServiceImpl jwtService;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void getVisitedCountries_shouldReturnVisitedCountries() throws Exception {
        ArrayList<String> list = new ArrayList<>();
        list.add("PL");
        list.add("EN");
        scratchMapService.setVisitedCountries(list);
        String jwt = jwtService.generateJwt("JD");

        MvcResult result = mvc
                .perform(get("/api/scratch-map/visited-country")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        //assert
        jwtService.authenticateUser(jwt);
        VisitedCountriesResponse visitedCountriesResponse =
                objectMapper.readValue(result.getResponse().getContentAsString(), VisitedCountriesResponse.class);
        assertEquals(list.get(0), visitedCountriesResponse.getVisitedCountries().get(0));
        assertEquals(list.get(1), visitedCountriesResponse.getVisitedCountries().get(1));
        scratchMapService.setVisitedCountries(new ArrayList<>());
    }

    @Test
    public void setVisitedCountries_shouldSetVisitedCountries() throws Exception {
        ArrayList<String> list = new ArrayList<>();
        list.add("PL");
        list.add("EN");
        scratchMapService.setVisitedCountries(list);
        String jwt = jwtService.generateJwt("JD");

        mvc.perform(put("/api/scratch-map/visited-country")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(list)))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        //assert
        jwtService.authenticateUser(jwt);
        assertEquals(list.get(0), scratchMapService.getVisitedCountries().get(0));
        assertEquals(list.get(1), scratchMapService.getVisitedCountries().get(1));
        scratchMapService.setVisitedCountries(new ArrayList<>());
    }

}