package travel.ways.travelwaysapi.user.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import travel.ways.travelwaysapi.user.model.db.AppUser;
import travel.ways.travelwaysapi.user.model.db.Role;
import travel.ways.travelwaysapi.user.model.dto.request.CreateUserRequest;
import travel.ways.travelwaysapi.user.repository.RoleRepository;
import travel.ways.travelwaysapi.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;


class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(userRepository, roleRepository, passwordEncoder);
    }

    @Test
    void createUser_givenValidRequest_thenCreateNewUser() {
        // arrange
        var request = new CreateUserRequest(
                "Jhon",
                "Doe",
                "JD",
                "elo",
                "email@gmail.com"
        );
        // act
        var result = userService.createUser(request);
        // assert
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());
        assertSame(result.getName(), request.getName());
        assertSame(result.getUsername(), request.getUsername());
        assertSame(result.getEmail(), request.getEmail());
        assertSame(result.getSurname(), request.getSurname());
    }

    @Test
    void loadUserByUsername_whenUserDoesntExist_thenThrowError() {
        // arrange
        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(null);

        // act / assert
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("username"));
    }

    @Test
    void loadUserByUsername_whenUserExists_thenThrowError() {
        // arrange
        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(new AppUser(
                "Jhon",
                "Doe",
                "JD",
                "elo",
                "email@gmail.com",
                List.of(new Role("ROLE_USER"))
        ));

        // act
        var result = userService.loadUserByUsername("username");

        // assert
        assertSame(result.getUsername(), "JD");
    }
}