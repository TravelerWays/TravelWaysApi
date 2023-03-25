package travel.ways.travelwaysapi.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import travel.ways.travelwaysapi._core.model.dto.BaseResponse;
import travel.ways.travelwaysapi.user.model.db.AppUser;
import travel.ways.travelwaysapi.user.model.db.PasswordRecovery;
import travel.ways.travelwaysapi.user.model.dto.request.ChangePasswordRequest;
import travel.ways.travelwaysapi.user.model.dto.request.CreateUserRequest;
import travel.ways.travelwaysapi.user.model.dto.request.InitPasswordRecoveryRequest;
import travel.ways.travelwaysapi.user.model.dto.response.ValidHashPasswordRecoveryResponse;
import travel.ways.travelwaysapi.user.repository.PasswordRecoveryRepository;
import travel.ways.travelwaysapi.user.service.internal.PasswordRecoveryService;
import travel.ways.travelwaysapi.user.service.shared.AccountService;
import travel.ways.travelwaysapi.user.service.shared.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AccountControllerTest {
    @Autowired
    private PasswordRecoveryService passwordRecoveryService;
    @Autowired
    private AccountService accountService;
    private MockMvc mvc;
    @Autowired
    private UserService userService;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PasswordRecoveryRepository passwordRecoveryRepository;


    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void registerUser_shouldRegisterUser() throws Exception {
        CreateUserRequest createUserRequest = new CreateUserRequest(
                "John_1",
                "Doe_1",
                "JD_1",
                "elo_1",
                "test_1@example.com"
        );
        //act
        MvcResult result = mvc
                .perform(post("/api/account/register")
                        .content(objectMapper.writeValueAsString(createUserRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        BaseResponse baseResponse = objectMapper.readValue(result.getResponse().getContentAsString(), BaseResponse.class);
        assertTrue(baseResponse.isSuccess());
        assertEquals("test_1@example.com", userService.getByUsername("JD_1").getEmail());

    }

    @Test
    public void activateAccount_shouldActivateAccount() throws Exception {
        CreateUserRequest createUserRequest = new CreateUserRequest(
                "John_3",
                "Doe_3",
                "JD_3",
                "elo_3",
                "test_3@example.com"
        );
        AppUser user = accountService.createUser(createUserRequest);
        assertFalse(user.isActive());
        //act
        MvcResult result = mvc
                .perform(post("/api/account/activate/" + user.getHash())
                        .content(objectMapper.writeValueAsString(createUserRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        BaseResponse baseResponse = objectMapper.readValue(result.getResponse().getContentAsString(), BaseResponse.class);
        assertTrue(baseResponse.isSuccess());
        assertTrue(userService.getByUsername("JD_2").isActive());
    }

    @Test
    public void activateAccount_shouldReturn404_whenNotFoundUser() throws Exception {
        //act
        mvc.perform(post("/api/account/activate/some_hash"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void passwordRecoverInit_shouldInitPasswordRecovery() throws Exception {
        AppUser user = userService.getByUsername("JD");
        //act
        MvcResult result = mvc
                .perform(post("/api/account/password-recovery/init")
                        .content(objectMapper.writeValueAsString(new InitPasswordRecoveryRequest(user.getEmail())))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        BaseResponse baseResponse = objectMapper.readValue(result.getResponse().getContentAsString(), BaseResponse.class);
        assertTrue(baseResponse.isSuccess());
        PasswordRecovery passwordRecovery = passwordRecoveryRepository.findByUser(user);
        assertNotNull(passwordRecovery);
        passwordRecoveryRepository.delete(passwordRecovery);
    }

    @Test
    public void validPasswordRecovery_shouldReturnTrue_WhenHashIsCorrect() throws Exception {
        AppUser user = userService.getByUsername("JD");
        //act
        passwordRecoveryService.initPasswordRecovery(new InitPasswordRecoveryRequest(user.getEmail()));
        PasswordRecovery passwordRecovery = passwordRecoveryRepository.findByUser(user);
        MvcResult result = mvc
                .perform(get("/api/account/password-recovery/valid/" + passwordRecovery.getHash()))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        ValidHashPasswordRecoveryResponse validHashPasswordRecoveryResponse
                = objectMapper.readValue(result.getResponse().getContentAsString(), ValidHashPasswordRecoveryResponse.class);
        assertTrue(validHashPasswordRecoveryResponse.isValid());
        passwordRecoveryRepository.delete(passwordRecovery);

    }

    @Test
    public void validPasswordRecovery_shouldReturn400_WhenHashIsIncorrect() throws Exception {
        mvc.perform(get("/api/account/password-recovery/valid/sample_hash"))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void changePassword_shouldChangePassword_WhenCorrectRequest() throws Exception {
        AppUser user = userService.getByUsername("JD");
        passwordRecoveryService.initPasswordRecovery(new InitPasswordRecoveryRequest(user.getEmail()));
        PasswordRecovery passwordRecovery = passwordRecoveryRepository.findByUser(user);
        //act
        MvcResult result = mvc
                .perform(post("/api/account/password-recovery/change-password/" + passwordRecovery.getHash())
                        .content(objectMapper.writeValueAsString(new ChangePasswordRequest("new_password")))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        BaseResponse baseResponse = objectMapper.readValue(result.getResponse().getContentAsString(), BaseResponse.class);
        assertTrue(baseResponse.isSuccess());
        passwordRecovery = passwordRecoveryRepository.findByUser(user);
        assertTrue(passwordRecovery.isUsed());
        assertNotEquals(user.getPassword(), userService.getByUsername("JD").getPassword());
        passwordRecoveryRepository.delete(passwordRecovery);
    }

    @Test
    public void changePassword_shouldReturn400_WhenBadRequest() throws Exception {

        mvc.perform(post("/api/account/password-recovery/change-password/bad_hash"))
                .andExpect(status().isBadRequest())
                .andReturn();
    }
}