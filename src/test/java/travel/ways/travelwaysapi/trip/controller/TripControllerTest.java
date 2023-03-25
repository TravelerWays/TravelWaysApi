package travel.ways.travelwaysapi.trip.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;
import travel.ways.travelwaysapi._core.exception.ServerException;
import travel.ways.travelwaysapi.auth.service.impl.JwtServiceImpl;
import travel.ways.travelwaysapi.file.service.shared.ImageService;
import travel.ways.travelwaysapi.trip.model.db.Trip;
import travel.ways.travelwaysapi.trip.model.dto.request.AddImageRequest;
import travel.ways.travelwaysapi.trip.model.dto.request.CreateTripRequest;
import travel.ways.travelwaysapi.trip.model.dto.request.EditTripMainImageRequest;
import travel.ways.travelwaysapi.trip.model.dto.request.EditTripRequest;
import travel.ways.travelwaysapi.trip.model.dto.response.ImageDto;
import travel.ways.travelwaysapi.trip.model.dto.response.TripResponse;
import travel.ways.travelwaysapi.trip.service.shared.TripService;
import travel.ways.travelwaysapi.user.service.shared.UserService;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@WithMockUser("JD")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
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
    @Autowired
    private ImageService imageService;


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

    @Test
    @Transactional
    public void editMainImage_shouldEditMainImage_whenProperRequest() throws Exception {
        // arrange
        Trip trip = tripService.createTrip(getCreateTripRequest(0));

        byte[] data = new byte[255];
        new Random().nextBytes(data);
        MultipartFile multipartFile = new MockMultipartFile("sample.png", "sample.png",
                MediaType.IMAGE_PNG_VALUE, data);

        ImageDto imageDto = tripService.addImage(new AddImageRequest(multipartFile, false), trip.getHash());

        EditTripMainImageRequest editTripMainImageRequest = new EditTripMainImageRequest(
                trip.getHash(),
                imageDto.getHash()
        );

        String jwt = jwtService.generateJwt("JD");
        //act & assert
        mvc.perform(put("/api/trip/edit/main-image")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .content(objectMapper.writeValueAsString(editTripMainImageRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful());
        jwtService.authenticateUser(jwt);

        Optional<ImageDto> newImageDto = tripService.getImageSummaryList(trip).stream().filter(ImageDto::isMain).findFirst();
        assertNotNull(newImageDto);
        assertEquals(imageDto.getHash(), newImageDto.get().getHash());
        //clean
        tripService.deleteTrip(trip);
    }

    @Test
    @Transactional
    public void editMainImage_shouldReturn403_whenForbidden() throws Exception {
        // arrange
        Trip trip = tripService.createTrip(getCreateTripRequest(0));


        byte[] data = new byte[255];
        new Random().nextBytes(data);
        MultipartFile multipartFile = new MockMultipartFile("sample.png", "sample.png",
                MediaType.IMAGE_PNG_VALUE, data);

        ImageDto imageDto = tripService.addImage(new AddImageRequest(multipartFile, false), trip.getHash());

        EditTripMainImageRequest editTripMainImageRequest = new EditTripMainImageRequest(
                trip.getHash(),
                imageDto.getHash()
        );

        String jwt = jwtService.generateJwt("JD_2");
        //act & assert
        mvc.perform(put("/api/trip/edit/main-image")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .content(objectMapper.writeValueAsString(editTripMainImageRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isForbidden());

        //clean
        jwtService.authenticateUser(jwtService.generateJwt("JD"));
        tripService.deleteTrip(trip);
    }

    @Test
    @Transactional
    public void addImageToTrip_shouldAddNotMainImage_whenProperRequest() throws Exception {
        // arrange
        Trip trip = tripService.createTrip(getCreateTripRequest(0));

        byte[] data = new byte[255];
        new Random().nextBytes(data);
        MockMultipartFile multipartFile = new MockMultipartFile("imageData", "sample.png",
                MediaType.IMAGE_PNG_VALUE, data);

        String jwt = jwtService.generateJwt("JD");
        //act & assert
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
                        .multipart("/api/trip/" + trip.getHash() + "/image")
                        .file(multipartFile)
                        .part(new MockPart("isMain", "false".getBytes()))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        ImageDto imageDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ImageDto.class);
        jwtService.authenticateUser(jwt);

        assertEquals(data,imageService.getImage(tripService.getImageSummaryList(trip).get(0).getHash()).getData());

        //clean
        tripService.deleteTrip(trip);
    }

    @Test
    @Transactional
    public void addImageToTrip_shouldAddMainImage_whenProperRequest() throws Exception {
        // arrange
        Trip trip = tripService.createTrip(getCreateTripRequest(0));


        byte[] data = new byte[255];
        new Random().nextBytes(data);
        MockMultipartFile multipartFile = new MockMultipartFile("imageData", "sample.png",
                MediaType.IMAGE_PNG_VALUE, data);

        String jwt = jwtService.generateJwt("JD");
        //act & assert
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
                        .multipart("/api/trip/" + trip.getHash() + "/image")
                        .file(multipartFile)
                        .part(new MockPart("isMain", "true".getBytes()))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        ImageDto imageDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ImageDto.class);
        jwtService.authenticateUser(jwt);

        assertEquals(data, imageService.getImage(imageDto.getHash()).getData());
        ImageDto newImageDto = tripService.getImageSummaryList(trip).stream().filter(ImageDto::isMain).findFirst().get();
        assertTrue(newImageDto.isMain());
        assertEquals(imageDto.getHash(), newImageDto.getHash());

        //clean
        tripService.deleteTrip(trip);
    }

    @Test
    @Transactional
    public void addImageToTrip_shouldReturn403_whenForbidden() throws Exception {
        // arrange
        Trip trip = tripService.createTrip(getCreateTripRequest(0));

        byte[] data = new byte[255];
        new Random().nextBytes(data);
        MockMultipartFile multipartFile = new MockMultipartFile("imageData", "sample.png",
                MediaType.IMAGE_PNG_VALUE, data);

        String jwt = jwtService.generateJwt("JD_2");
        //act & assert
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
                        .multipart("/api/trip/" + trip.getHash() + "/image")
                        .file(multipartFile)
                        .part(new MockPart("isMain", "true".getBytes()))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isForbidden())
                .andReturn();

        //clean
        jwtService.authenticateUser(jwtService.generateJwt("JD"));
        tripService.deleteTrip(trip);
    }

    @Test
    public void deleteImage_shouldDeleteImage_whenProperRequest() throws Exception {
        Trip trip = tripService.createTrip(getCreateTripRequest(0));


        byte[] data = new byte[255];
        new Random().nextBytes(data);
        MultipartFile multipartFile = new MockMultipartFile("sample.png", "sample.png",
                MediaType.IMAGE_PNG_VALUE, data);

        ImageDto imageDto = tripService.addImage(new AddImageRequest(multipartFile, false), trip.getHash());


        String jwt = jwtService.generateJwt("JD");
        //act & assert
        mvc.perform(delete("/api/trip/image/" + imageDto.getHash())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful());
        jwtService.authenticateUser(jwt);

        assertThrows(ServerException.class, () -> imageService.getImage(imageDto.getHash()));
        //clean
        tripService.deleteTrip(trip);
    }

    @Test
    public void deleteImage_shouldReturn403_whenForbidden() throws Exception {
        Trip trip = tripService.createTrip(getCreateTripRequest(0));


        byte[] data = new byte[255];
        new Random().nextBytes(data);
        MultipartFile multipartFile = new MockMultipartFile("sample.png", "sample.png",
                MediaType.IMAGE_PNG_VALUE, data);

        ImageDto imageDto = tripService.addImage(new AddImageRequest(multipartFile, false), trip.getHash());


        String jwt = jwtService.generateJwt("JD_2");
        //act & assert
        mvc.perform(delete("/api/trip/image/" + imageDto.getHash())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isForbidden());

        //clean
        jwtService.authenticateUser(jwtService.generateJwt("JD"));
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