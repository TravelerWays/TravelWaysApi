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
import travel.ways.travelwaysapi.file.repository.ImageRepository;
import travel.ways.travelwaysapi.file.service.shared.ImageService;
import travel.ways.travelwaysapi.trip.model.db.attraction.Attraction;
import travel.ways.travelwaysapi.trip.model.dto.request.AddImageRequest;
import travel.ways.travelwaysapi.trip.model.dto.request.CreateAttractionRequest;
import travel.ways.travelwaysapi.trip.model.dto.request.EditAttractionMainImageRequest;
import travel.ways.travelwaysapi.trip.model.dto.request.EditAttractionRequest;
import travel.ways.travelwaysapi.trip.model.dto.response.AttractionResponse;
import travel.ways.travelwaysapi.trip.model.dto.response.ImageDto;
import travel.ways.travelwaysapi.trip.service.internal.AttractionService;
import travel.ways.travelwaysapi.user.service.shared.UserService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@WithMockUser("JD")
public class AttractionControllerTest {

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
    private UserService userService;
    @Autowired
    private ImageService imageService;
    @Autowired
    private ImageRepository imageRepository;


    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void getAttraction_shouldReturnAttraction_whenProperHash() throws Exception {
        // arrange
        CreateAttractionRequest createAttractionRequest = getCreateAttractionRequest(0);

        Attraction attraction = attractionService.createAttraction(createAttractionRequest);
        String jwt = jwtService.generateJwt("JD");
        //act
        MvcResult result = mvc.perform(get("/api/attraction/" + attraction.getHash())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().is2xxSuccessful())
                .andReturn();


        //assert
        AttractionResponse resultAttraction =
                objectMapper.readValue(result.getResponse().getContentAsString(), AttractionResponse.class);
        assertEquals(attraction.getHash(), resultAttraction.getHash());

        //clean
        jwtService.authenticateUser(jwt);
        attractionService.deleteAttraction(attraction.getHash());
    }

    @Test
    public void getAttraction_shouldThrowException_whenNotFoundAttraction() throws Exception {

        String jwt = jwtService.generateJwt("JD");
        //act & assert
        mvc.perform(get("/api/attraction/bad_hash")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ServerException))
                .andReturn();
    }

    @Test
    public void add_shouldAddAttraction_whenProperRequest() throws Exception {
        // arrange
        CreateAttractionRequest createAttractionRequest = getCreateAttractionRequest(0);
        String jwt = jwtService.generateJwt("JD");
        //act
        MvcResult result = mvc
                .perform(post("/api/attraction")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .content(objectMapper.writeValueAsString(createAttractionRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andReturn();


        //assert
        AttractionResponse resultAttraction =
                objectMapper.readValue(result.getResponse().getContentAsString(), AttractionResponse.class);
        Attraction newAttraction = attractionService.getAttraction(resultAttraction.getHash());
        assertEquals(createAttractionRequest.getTitle(), newAttraction.getTitle());

        //clean
        jwtService.authenticateUser(jwt);
        attractionService.deleteAttraction(newAttraction.getHash());
    }


    @Test
    public void add_shouldReturn400_whenBadRequest() throws Exception {
        // arrange
        String jwt = jwtService.generateJwt("JD");

        //act
        mvc.perform(post("/api/attraction")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .content("bad_request_content")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser("JD_2")
    public void getAll_shouldReturnAllAttractions_whenProperHash() throws Exception {
        // arrange
        CreateAttractionRequest createAttractionRequest = new CreateAttractionRequest(
                "osm_id",
                "title",
                "description",
                true,
                true,
                Date.valueOf(LocalDate.now().toString()),
                null,
                null

        );
        CreateAttractionRequest createAttractionRequest1 = getCreateAttractionRequest(0);
        Attraction attraction = attractionService.createAttraction(createAttractionRequest);
        Attraction attraction1 = attractionService.createAttraction(createAttractionRequest1);

        String jwt = jwtService.generateJwt("JD");
        //act
        MvcResult result = mvc.perform(get("/api/attraction/all/" + userService.getByUsername("JD_2").getHash())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().is2xxSuccessful())
                .andReturn();


        //assert
        List<AttractionResponse> Attractions = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertEquals(2, Attractions.size());
        assertEquals(attraction.getHash(), Attractions.get(0).getHash());
        assertEquals(attraction1.getHash(), Attractions.get(1).getHash());
        //clean
        jwt = jwtService.generateJwt("JD_2");
        jwtService.authenticateUser(jwt);
        attractionService.deleteAttraction(attraction.getHash());
        attractionService.deleteAttraction(attraction1.getHash());
    }

    @Test
    public void getAllLoggedUser_shouldReturnAllAttractionsForLoggedUser() throws Exception {
        // arrange
        CreateAttractionRequest createAttractionRequest = getCreateAttractionRequest(0);
        CreateAttractionRequest createAttractionRequest1 = getCreateAttractionRequest(1);
        Attraction attraction = attractionService.createAttraction(createAttractionRequest);
        Attraction attraction1 = attractionService.createAttraction(createAttractionRequest1);

        String jwt = jwtService.generateJwt("JD");
        //act
        MvcResult result = mvc.perform(get("/api/attraction/all")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().is2xxSuccessful())
                .andReturn();


        //assert
        List<AttractionResponse> Attractions = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertEquals(2, Attractions.size());
        assertEquals(attraction.getHash(), Attractions.get(0).getHash());
        assertEquals(attraction1.getHash(), Attractions.get(1).getHash());

        //clean
        jwtService.authenticateUser(jwt);
        attractionService.deleteAttraction(attraction.getHash());
        attractionService.deleteAttraction(attraction1.getHash());
    }


    @Test
    public void deleteAttraction_shouldDeleteAttraction_whenProperRequest() throws Exception {
        // arrange
        CreateAttractionRequest createAttractionRequest = getCreateAttractionRequest(0);
        Attraction attraction = attractionService.createAttraction(createAttractionRequest);

        String jwt = jwtService.generateJwt("JD");
        //act
        mvc.perform(delete("/api/attraction/" + attraction.getHash())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().is2xxSuccessful())
                .andReturn();


        //assert
        assertThrows(ServerException.class, () -> attractionService.getAttraction(attraction.getHash()));
    }

    @Test
    public void deleteAttraction_shouldThrow_whenNotPermission() throws Exception {
        // arrange
        CreateAttractionRequest createAttractionRequest = getCreateAttractionRequest(0);
        Attraction attraction = attractionService.createAttraction(createAttractionRequest);

        String jwt = jwtService.generateJwt("JD_2");
        //act & assert
        mvc.perform(delete("/api/attraction/" + attraction.getHash())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().isForbidden())
                .andReturn();

        //clean
        jwtService.authenticateUser(jwtService.generateJwt("JD"));
        attractionService.deleteAttraction(attraction.getHash());

    }

    @Test
    public void editAttraction_shouldEditAttraction_whenProperRequest() throws Exception {
        // arrange
        Attraction attraction = attractionService.createAttraction(getCreateAttractionRequest(0));

        EditAttractionRequest editAttractionRequest = new EditAttractionRequest(
                attraction.getHash(),
                "new_title",
                "",
                true,
                true,
                null,
                null,
                null
        );

        String jwt = jwtService.generateJwt("JD");
        //act
        MvcResult result = mvc
                .perform(put("/api/attraction/edit")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .content(objectMapper.writeValueAsString(editAttractionRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        //assert
        AttractionResponse resultAttraction =
                objectMapper.readValue(result.getResponse().getContentAsString(), AttractionResponse.class);
        Attraction newAttraction = attractionService.getAttraction(resultAttraction.getHash());
        assertEquals(editAttractionRequest.getTitle(), newAttraction.getTitle());

        //clean
        jwtService.authenticateUser(jwt);
        attractionService.deleteAttraction(attraction.getHash());
    }

    @Test
    public void editAttraction_shouldReturn403_whenNotAuthorized() throws Exception {
        // arrange
        Attraction attraction = attractionService.createAttraction(getCreateAttractionRequest(0));

        EditAttractionRequest editAttractionRequest = new EditAttractionRequest(
                attraction.getHash(),
                "new_title",
                "",
                true,
                true,
                null,
                null,
                null
        );

        String jwt = jwtService.generateJwt("JD_2");
        //act & assert
        mvc.perform(put("/api/attraction/edit")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .content(objectMapper.writeValueAsString(editAttractionRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isForbidden());

        //clean
        jwtService.authenticateUser(jwtService.generateJwt("JD"));
        attractionService.deleteAttraction(attraction.getHash());
    }

    @Test
    @Transactional
    public void editMainImage_shouldEditMainImage_whenProperRequest() throws Exception {
        // arrange
        Attraction attraction = attractionService.createAttraction(getCreateAttractionRequest(0));

        byte[] data = new byte[255];
        new Random().nextBytes(data);
        MultipartFile multipartFile = new MockMultipartFile("sample.png", "sample.png",
                MediaType.IMAGE_PNG_VALUE, data);

        ImageDto imageDto = attractionService.addImage(new AddImageRequest(multipartFile, false), attraction.getHash());

        EditAttractionMainImageRequest editAttractionMainImageRequest = new EditAttractionMainImageRequest(
                attraction.getHash(),
                imageDto.getHash()
        );

        String jwt = jwtService.generateJwt("JD");
        //act & assert
        mvc.perform(put("/api/attraction/edit/main-image")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .content(objectMapper.writeValueAsString(editAttractionMainImageRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful());
        jwtService.authenticateUser(jwt);

        Optional<ImageDto> newImageDto = attractionService.getImageSummaryList(attraction).stream().filter(ImageDto::isMain).findFirst();
        assertNotNull(newImageDto);
        assertEquals(imageDto.getHash(), newImageDto.get().getHash());
        //clean
        attractionService.deleteAttraction(attraction.getHash());
    }

    @Test
    @Transactional
    public void editMainImage_shouldReturn403_whenForbidden() throws Exception {
        // arrange
        Attraction attraction = attractionService.createAttraction(getCreateAttractionRequest(0));

        byte[] data = new byte[255];
        new Random().nextBytes(data);
        MultipartFile multipartFile = new MockMultipartFile("sample.png", "sample.png",
                MediaType.IMAGE_PNG_VALUE, data);

        ImageDto imageDto = attractionService.addImage(new AddImageRequest(multipartFile, false), attraction.getHash());

        EditAttractionMainImageRequest editAttractionMainImageRequest = new EditAttractionMainImageRequest(
                attraction.getHash(),
                imageDto.getHash()
        );

        String jwt = jwtService.generateJwt("JD_2");
        //act & assert
        mvc.perform(put("/api/attraction/edit/main-image")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .content(objectMapper.writeValueAsString(editAttractionMainImageRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isForbidden());

        //clean
        jwtService.authenticateUser(jwtService.generateJwt("JD"));
        attractionService.deleteAttraction(attraction.getHash());
    }

    @Test
    @Transactional
    public void addImageToAttraction_shouldAddNotMainImage_whenProperRequest() throws Exception {
        // arrange
        Attraction attraction = attractionService.createAttraction(getCreateAttractionRequest(0));

        byte[] data = new byte[255];
        new Random().nextBytes(data);
        MockMultipartFile multipartFile = new MockMultipartFile("imageData", "sample.png",
                MediaType.IMAGE_PNG_VALUE, data);

        String jwt = jwtService.generateJwt("JD");
        //act & assert
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
                        .multipart("/api/attraction/" + attraction.getHash() + "/image")
                        .file(multipartFile)
                        .part(new MockPart("isMain", "false".getBytes()))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        ImageDto imageDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ImageDto.class);
        jwtService.authenticateUser(jwt);

        assertEquals(data, imageService.getImage(imageDto.getHash()).getData());

        //clean
        attractionService.deleteAttraction(attraction.getHash());
    }

    @Test
    @Transactional
    public void addImageToAttraction_shouldThrow_whenBadExtension() throws Exception {
        // arrange
        Attraction attraction = attractionService.createAttraction(getCreateAttractionRequest(0));

        byte[] data = new byte[255];
        new Random().nextBytes(data);
        MockMultipartFile multipartFile = new MockMultipartFile("imageData", "sample.png",
                "not_png_not_jpg", data);

        String jwt = jwtService.generateJwt("JD");
        //act & assert
        mvc.perform(MockMvcRequestBuilders
                        .multipart("/api/attraction/" + attraction.getHash() + "/image")
                        .file(multipartFile)
                        .part(new MockPart("isMain", "false".getBytes()))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isBadRequest())
                .andReturn();
        jwtService.authenticateUser(jwt);
        //clean
        attractionService.deleteAttraction(attraction.getHash());
    }

    @Test
    @Transactional
    public void addImageToAttraction_shouldAddMainImage_whenProperRequest() throws Exception {
        // arrange
        Attraction attraction = attractionService.createAttraction(getCreateAttractionRequest(0));

        byte[] data = new byte[255];
        new Random().nextBytes(data);
        MockMultipartFile multipartFile = new MockMultipartFile("imageData", "sample.png",
                MediaType.IMAGE_PNG_VALUE, data);

        String jwt = jwtService.generateJwt("JD");
        //act & assert
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
                        .multipart("/api/attraction/" + attraction.getHash() + "/image")
                        .file(multipartFile)
                        .part(new MockPart("isMain", "true".getBytes()))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        ImageDto imageDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ImageDto.class);
        jwtService.authenticateUser(jwt);

        assertEquals(data, imageService.getImage(imageDto.getHash()).getData());
        ImageDto newImageDto = attractionService.getImageSummaryList(attraction).stream().filter(ImageDto::isMain).findFirst().get();
        assertTrue(newImageDto.isMain());
        assertEquals(imageDto.getHash(), newImageDto.getHash());

        //clean
        attractionService.deleteAttraction(attraction.getHash());
    }

    @Test
    @Transactional
    public void addImageToAttraction_shouldReturn403_whenForbidden() throws Exception {
        // arrange
        Attraction attraction = attractionService.createAttraction(getCreateAttractionRequest(0));

        byte[] data = new byte[255];
        new Random().nextBytes(data);
        MockMultipartFile multipartFile = new MockMultipartFile("imageData", "sample.png",
                MediaType.IMAGE_PNG_VALUE, data);

        String jwt = jwtService.generateJwt("JD_2");
        //act & assert
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
                        .multipart("/api/attraction/" + attraction.getHash() + "/image")
                        .file(multipartFile)
                        .part(new MockPart("isMain", "true".getBytes()))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isForbidden())
                .andReturn();

        //clean
        jwtService.authenticateUser(jwtService.generateJwt("JD"));
        attractionService.deleteAttraction(attraction.getHash());
    }

    @Test
    public void deleteImage_shouldDeleteImage_whenProperRequest() throws Exception {
        Attraction attraction = attractionService.createAttraction(getCreateAttractionRequest(0));

        byte[] data = new byte[255];
        new Random().nextBytes(data);
        MultipartFile multipartFile = new MockMultipartFile("sample.png", "sample.png",
                MediaType.IMAGE_PNG_VALUE, data);

        ImageDto imageDto = attractionService.addImage(new AddImageRequest(multipartFile, false), attraction.getHash());


        String jwt = jwtService.generateJwt("JD");
        //act & assert
        mvc.perform(delete("/api/attraction/image/" + imageDto.getHash())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful());
        jwtService.authenticateUser(jwt);

        assertThrows(ServerException.class, () -> imageService.getImage(imageDto.getHash()));
        //clean
        attractionService.deleteAttraction(attraction.getHash());
    }

    @Test
    public void deleteImage_shouldReturn403_whenForbidden() throws Exception {
        Attraction attraction = attractionService.createAttraction(getCreateAttractionRequest(0));

        byte[] data = new byte[255];
        new Random().nextBytes(data);
        MultipartFile multipartFile = new MockMultipartFile("sample.png", "sample.png",
                MediaType.IMAGE_PNG_VALUE, data);

        ImageDto imageDto = attractionService.addImage(new AddImageRequest(multipartFile, false), attraction.getHash());


        String jwt = jwtService.generateJwt("JD_2");
        //act & assert
        mvc.perform(delete("/api/attraction/image/" + imageDto.getHash())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isForbidden());

        //clean
        jwtService.authenticateUser(jwtService.generateJwt("JD"));
        attractionService.deleteAttraction(attraction.getHash());
    }


    private CreateAttractionRequest getCreateAttractionRequest(int i) {
        return new CreateAttractionRequest(
                "osm_id",
                "title" + i,
                "description" + i,
                true,
                true,
                Date.valueOf(LocalDate.now().toString()),
                null,
                null

        );
    }

}