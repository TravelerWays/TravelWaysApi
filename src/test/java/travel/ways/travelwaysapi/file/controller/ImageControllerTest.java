package travel.ways.travelwaysapi.file.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;
import travel.ways.travelwaysapi.auth.service.impl.JwtServiceImpl;
import travel.ways.travelwaysapi.trip.model.db.Attraction;
import travel.ways.travelwaysapi.trip.model.db.Trip;
import travel.ways.travelwaysapi.trip.model.dto.request.AddImageRequest;
import travel.ways.travelwaysapi.trip.model.dto.request.CreateAttractionRequest;
import travel.ways.travelwaysapi.trip.model.dto.request.CreateTripRequest;
import travel.ways.travelwaysapi.trip.model.dto.response.ImageDto;
import travel.ways.travelwaysapi.trip.service.internal.AttractionService;
import travel.ways.travelwaysapi.trip.service.shared.TripService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@WithMockUser("JD")
class ImageControllerTest {
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AttractionService attractionService;
    @Autowired
    private JwtServiceImpl jwtService;
    private MockMvc mvc;
    @Autowired
    private TripService tripService;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void getImage_shouldReturnAttractionImage_whenProperHash() throws Exception {
        // arrange
        Attraction attraction = attractionService.createAttraction(new CreateAttractionRequest(
                "osm_id",
                "title",
                "description",
                true,
                true,
                Date.valueOf(LocalDate.now().toString()),
                null,
                null

        ));
        byte[] data = new byte[255];
        new Random().nextBytes(data);
        MultipartFile multipartFile = new MockMultipartFile("sample.png", "sample.png",
                MediaType.IMAGE_PNG_VALUE, data);

        var multipartFileArray = new MultipartFile[]{multipartFile};

        var imageDto = attractionService.addImage(new AddImageRequest(multipartFileArray, false), attraction.getHash()).get(0);

        var jwt = jwtService.generateJwt("JD");
        //act
        MvcResult result = mvc.perform(get("/api/image/" + imageDto.getHash())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        //assert
        jwtService.authenticateUser(jwt);
        assertArrayEquals(data, result.getResponse().getContentAsByteArray());
        //clean
        attractionService.deleteAttraction(attraction.getHash());
    }

    @Test
    public void getImage_shouldReturnTripImage_whenProperHash() throws Exception {
        // arrange
        Trip trip = tripService.createTrip(new CreateTripRequest(
                "title",
                true,
                "description"
        ));

        byte[] data = new byte[255];
        new Random().nextBytes(data);
        MultipartFile multipartFile = new MockMultipartFile("sample.png", "sample.png",
                MediaType.IMAGE_PNG_VALUE, data);

        var multipartFileArray = new MultipartFile[]{multipartFile};
        ImageDto imageDto = tripService.addImage(new AddImageRequest(multipartFileArray, false), trip.getHash());

        String jwt = jwtService.generateJwt("JD");
        //act
        MvcResult result = mvc.perform(get("/api/image/" + imageDto.getHash())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        //assert
        jwtService.authenticateUser(jwt);
        assertArrayEquals(data, result.getResponse().getContentAsByteArray());
        //clean
        tripService.deleteTrip(trip);
    }

    @Test
    public void getImage_shouldReturn404_whenBadHash() throws Exception {
        // arrange
        String jwt = jwtService.generateJwt("JD");
        //act & assert
        mvc.perform(get("/api/image/bad_hash")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().isNotFound())
                .andReturn();
    }
}