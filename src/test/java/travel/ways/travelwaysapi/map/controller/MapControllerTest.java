package travel.ways.travelwaysapi.map.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
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
import travel.ways.travelwaysapi.map.model.dto.osm.AddressDto;
import travel.ways.travelwaysapi.map.model.dto.osm.LocationDto;
import travel.ways.travelwaysapi.map.model.dto.response.LocationResponse;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@WithMockUser("JD")
@WireMockTest(proxyMode = true, httpPort = 6969)
class MapControllerTest {

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private ObjectMapper objectMapper;
    private MockMvc mvc;
    @Autowired
    private JwtServiceImpl jwtService;

    @BeforeEach
    void init() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void search_shouldRun_makeRequestQuery_MethodWithProperUrl() throws Exception {
        LocationDto locationDto = getLocationDto();
        stubFor(WireMock.get(urlMatching("/.*"))
                .withHost(equalTo("localhost"))
                .willReturn(ok(objectMapper.writeValueAsString(locationDto))));

        String jwt = jwtService.generateJwt("JD");
        //act
        MvcResult result = mvc
                .perform(get("/api/map/search/query")
                        .param("query", "some_query")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        List<LocationResponse> locationResponses = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertEquals(locationDto.getOsmId(), locationResponses.get(0).getOsmId());

    }

    @Test
    public void search_shouldRun_makeRequestCoordinates_MethodWithProperUrl() throws Exception {
        LocationDto locationDto = getLocationDto();
        stubFor(WireMock.get(urlMatching("/.*"))
                .withHost(equalTo("localhost"))
                .willReturn(ok(objectMapper.writeValueAsString(locationDto))));

        String jwt = jwtService.generateJwt("JD");
        //act
        MvcResult result = mvc
                .perform(get("/api/map/search/coordinates")
                        .param("lon", "54.34")
                        .param("lat", "54.34")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        List<LocationResponse> locationResponses = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertEquals(locationDto.getOsmId(), locationResponses.get(0).getOsmId());
    }

    private LocationDto getLocationDto() {
        return new LocationDto(
                new AddressDto(
                        "city",
                        "cityDistrict",
                        "continent",
                        "country",
                        "countryCode",
                        "24",
                        "3450-3",
                        "false"
                ),
                "display_name",
                "34.43",
                "34,43",
                "type",
                "osm_id",
                "en"
        );
    }
}