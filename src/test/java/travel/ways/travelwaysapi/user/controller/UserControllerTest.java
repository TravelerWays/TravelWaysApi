package travel.ways.travelwaysapi.user.controller;

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
import travel.ways.travelwaysapi.auth.service.impl.JwtServiceImpl;
import travel.ways.travelwaysapi.file.model.dto.ImageSummaryDto;
import travel.ways.travelwaysapi.file.service.shared.ImageService;
import travel.ways.travelwaysapi.trip.model.dto.request.AddImageRequest;
import travel.ways.travelwaysapi.user.model.db.AppUser;
import travel.ways.travelwaysapi.user.model.dto.response.UserResponse;
import travel.ways.travelwaysapi.user.service.shared.UserService;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@WithMockUser("JD")
class UserControllerTest {
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private ObjectMapper objectMapper;
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
    public void getAll_shouldReturnAllUsers() throws Exception {
        String jwt = jwtService.generateJwt("JD");
        //act
        MvcResult result = mvc
                .perform(get("/api/user/all/")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        List<UserResponse> users = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertEquals("JD", users.get(0).getUsername());
        assertEquals("JD_2", users.get(1).getUsername());
    }

    @Test
    public void getLogged_shouldReturnLoggedUser() throws Exception {
        String jwt = jwtService.generateJwt("JD");
        //act
        MvcResult result = mvc
                .perform(get("/api/user/logged")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        UserResponse user = objectMapper.readValue(result.getResponse().getContentAsString(), UserResponse.class);
        assertEquals("JD", user.getUsername());
    }

    @Test
    @Transactional
    public void addUserImage_shouldAddUserImage() throws Exception {
        String jwt = jwtService.generateJwt("JD");

        byte[] data = new byte[255];
        new Random().nextBytes(data);
        MockMultipartFile multipartFile = new MockMultipartFile("imageData", "sample.png",
                MediaType.IMAGE_PNG_VALUE, data);

        AppUser user = userService.getByUsername("JD");
        //act
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                        .multipart("/api/user/" + user.getHash() + "/image")
                        .file(multipartFile)
                        .part(new MockPart("isMain", "true".getBytes()))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andReturn();


        ImageSummaryDto imageSummaryDto = objectMapper.readValue(result.getResponse().getContentAsString(), ImageSummaryDto.class);
        jwtService.authenticateUser(jwt);

        assertArrayEquals(data, imageService.getImage(imageSummaryDto.getHash()).getData());
        assertArrayEquals(data, user.getImage().getData());

    }

    @Test
    @Transactional
    public void addUserImage_shouldReturn403_WhenForbidden() throws Exception {
        String jwt = jwtService.generateJwt("JD_2");

        byte[] data = new byte[255];
        new Random().nextBytes(data);
        MockMultipartFile multipartFile = new MockMultipartFile("imageData", "sample.png",
                MediaType.IMAGE_PNG_VALUE, data);

        AppUser user = userService.getByUsername("JD");
        //act
        mvc.perform(MockMvcRequestBuilders
                        .multipart("/api/user/" + user.getHash() + "/image")
                        .file(multipartFile)
                        .part(new MockPart("isMain", "true".getBytes()))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isForbidden())
                .andReturn();
    }


    @Test
    @Transactional
    public void deleteUserImage_shouldDeleteUserImage() throws Exception {
        String jwt = jwtService.generateJwt("JD");

        byte[] data = new byte[255];
        new Random().nextBytes(data);
        MockMultipartFile multipartFile = new MockMultipartFile("imageData", "sample.png",
                MediaType.IMAGE_PNG_VALUE, data);
        AppUser user = userService.getByUsername("JD");
        userService.addImage(new AddImageRequest(multipartFile, true), user.getHash());

        //act
        mvc.perform(delete("/api/user/" + user.getHash() + "/image")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().is2xxSuccessful());

        jwtService.authenticateUser(jwt);
        assertNull(user.getImage());
    }

    @Test
    public void deleteUserImage_shouldReturn403_whenForbidden() throws Exception {
        String jwt = jwtService.generateJwt("JD_2");

        byte[] data = new byte[255];
        new Random().nextBytes(data);
        MockMultipartFile multipartFile = new MockMultipartFile("imageData", "sample.png",
                MediaType.IMAGE_PNG_VALUE, data);
        AppUser user = userService.getByUsername("JD");
        userService.addImage(new AddImageRequest(multipartFile, true), user.getHash());

        //act && assert
        mvc.perform(delete("/api/user/" + user.getHash() + "/image")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deleteUserImage_shouldReturn404_whenNotFoundImage() throws Exception {
        String jwt = jwtService.generateJwt("JD");
        AppUser user = userService.getByUsername("JD");
        //act && assert
        mvc.perform(delete("/api/user/" + user.getHash() + "/image")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isNotFound());
    }
}