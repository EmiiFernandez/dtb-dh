
package dropthebass.equipo4.configuration;

import dropthebass.equipo4.filters.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;

    @Autowired
    public WebSecurityConfig(JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf((csrf) -> csrf.disable())
                .cors(cors -> {
                    cors.configurationSource(corsConfigurationSource());
                })
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/auth/**", "/signup/**", "/shopping-cart/**", "/detail-booking/check-availability").permitAll()
                                .requestMatchers(HttpMethod.DELETE, "/users/**").hasRole("DEV")
                                .requestMatchers(HttpMethod.GET, "/categories/**", "/product/**", "/brand/**", "/reviews/**", "/features/**", "/users/**", "/s3/**", "/detail-booking/occupied-dates").permitAll()
                                .requestMatchers("/booking/**","/favs/**", "/detail-booking/**", "/reviews/**").hasAnyRole("USER", "ADMIN", "DEV")
                                .requestMatchers("/categories/**", "/product/**", "/brand/**", "/features/**", "/administracion/**").hasAnyRole("ADMIN", "DEV")
                                .requestMatchers("admin").hasAnyRole("ADMIN", "DEV")
                                .requestMatchers("user").hasRole("USER")
                                .anyRequest().authenticated()
                        // .requestMatchers("/**").permitAll()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout
                        .logoutUrl("auth/logout")
                        .logoutSuccessUrl("/") // Página a la que redirigir después del cierre de sesión exitoso
                        .invalidateHttpSession(true) // Invalidar la sesión HTTP
                        .deleteCookies("JSESSIONID") // Eliminar las cookies relacionadas con la sesión
                   .permitAll());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://g4-deploy-react-app.s3-website.us-east-2.amazonaws.com");
        configuration.addAllowedOrigin("http://localhost:3000");
        configuration.addAllowedOrigin("https://frontend-grupo4-integradora.vercel.app/");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new
                UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}


