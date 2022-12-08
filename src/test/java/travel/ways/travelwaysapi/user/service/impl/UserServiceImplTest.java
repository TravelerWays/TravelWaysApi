package travel.ways.travelwaysapi.user.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import travel.ways.travelwaysapi.auth.service.impl.UserDetailsServiceImpl;
import travel.ways.travelwaysapi.user.model.db.AppUser;
import travel.ways.travelwaysapi.user.model.db.Role;
import travel.ways.travelwaysapi.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;


class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    private UserServiceImpl userService;
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(userRepository);
        userDetailsService = new UserDetailsServiceImpl(userService);
    }

    @Test
    void loadUserByUsername_whenUserDoesntExist_thenThrowError() {
        // arrange
        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(null);

        // act / assert
        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("username"));
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
        var result = userDetailsService.loadUserByUsername("username");

        // assert
        assertSame(result.getUsername(), "JD");
    }
}