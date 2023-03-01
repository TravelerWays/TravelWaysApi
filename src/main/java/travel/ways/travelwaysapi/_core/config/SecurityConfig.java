package travel.ways.travelwaysapi._core.config;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import travel.ways.travelwaysapi._core.util.Time;
import travel.ways.travelwaysapi.auth.filter.AuthenticationFilter;
import travel.ways.travelwaysapi.auth.filter.AuthorizationFilter;
import travel.ways.travelwaysapi.auth.service.internal.JwtService;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtService jwtUtils;
    private final Time time;

    public static List<String> PublicURI = List.of("/api/auth/**", "/api/account/**", "/swagger-ui/**", "/swagger-resources/**", "/v2/api-docs/**");

    @Bean
    public AuthenticationManager authenticationManagerBean(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
        return authenticationManagerBuilder.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    @SneakyThrows
    public SecurityFilterChain filterChain(HttpSecurity http) {
        var authenticationFilter = new AuthenticationFilter(authenticationManager(http.getSharedObject(AuthenticationConfiguration.class))
                , jwtUtils, time);
        authenticationFilter.setFilterProcessesUrl("/api/auth/login");
        http.csrf().disable();

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        for (var URI : PublicURI) {
            http.authorizeRequests().antMatchers(URI).permitAll();
        }
        http.authorizeRequests().anyRequest().authenticated();

        http.addFilter(authenticationFilter);
        http.addFilterBefore(new AuthorizationFilter(jwtUtils, time), UsernamePasswordAuthenticationFilter.class);
        http.cors();

        return http.build();
    }

}
