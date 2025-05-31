package ar.edu.unsam.phm.uberto.security


import ar.edu.unsam.phm.uberto.repository.AuthRepository
import ar.edu.unsam.phm.uberto.security.filter.JwtTokenValidator
import ar.edu.unsam.phm.uberto.services.AuthService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource


@Configuration
@EnableWebSecurity
class ApplicationSecutityConfiguration(
    @Autowired val jwtUtil: TokenJwtUtil
) {
    @Bean
    fun securityFilterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
        return httpSecurity
            .cors { it.disable() }
            .csrf { it.disable() }
            .httpBasic(Customizer.withDefaults())
            .exceptionHandling(Customizer.withDefaults())
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authorizeHttpRequests { authorizeHttpRequests ->
                //Publicos
                authorizeHttpRequests.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                authorizeHttpRequests.requestMatchers(HttpMethod.POST, "/login").permitAll()
                authorizeHttpRequests.requestMatchers(HttpMethod.GET, "/error").permitAll()

                //Privados
                //trips controller
                authorizeHttpRequests.requestMatchers(HttpMethod.POST, "/trip/create").hasRole("PASSENGER")
                authorizeHttpRequests.requestMatchers(HttpMethod.GET, "/trip/passenger").hasRole("PASSENGER")
                authorizeHttpRequests.requestMatchers(HttpMethod.GET, "/trip/driver").hasRole("DRIVER")
                authorizeHttpRequests.requestMatchers(HttpMethod.GET, "/trip/pending").hasRole("DRIVER")
                authorizeHttpRequests.requestMatchers(HttpMethod.GET, "/trip/profile/passenger").hasRole("PASSENGER")
                authorizeHttpRequests.requestMatchers(HttpMethod.GET, "/trip/profile/driver").hasRole("DRIVER")

                //Passenger controller
                authorizeHttpRequests.requestMatchers(HttpMethod.PUT, "/passenger/addBalance").hasRole("PASSENGER")
                authorizeHttpRequests.requestMatchers(HttpMethod.PUT, "/passenger").hasRole("PASSENGER")
                authorizeHttpRequests.requestMatchers(HttpMethod.GET, "/passenger/friends").hasRole("PASSENGER")
                authorizeHttpRequests.requestMatchers(HttpMethod.POST, "/passenger/friends").hasRole("PASSENGER")
                authorizeHttpRequests.requestMatchers(HttpMethod.DELETE, "/passenger/friends").hasRole("PASSENGER")
                authorizeHttpRequests.requestMatchers(HttpMethod.GET, "/passenger/friends/search").hasRole("PASSENGER")
                authorizeHttpRequests.requestMatchers(HttpMethod.GET, "/passenger/img").hasRole("PASSENGER")
                authorizeHttpRequests.requestMatchers(HttpMethod.GET, "/passenger").hasAnyRole("PASSENGER", "DRIVER")

                //Driver controller
                authorizeHttpRequests.requestMatchers(HttpMethod.GET, "/driver/img").hasRole("DRIVER")
                authorizeHttpRequests.requestMatchers(HttpMethod.GET, "/driver/available").hasRole("PASSENGER")
                authorizeHttpRequests.requestMatchers(HttpMethod.POST, "/driver").hasRole("DRIVER")
                authorizeHttpRequests.requestMatchers(HttpMethod.GET, "/driver").hasAnyRole("PASSENGER", "DRIVER")

                //TripScore controller
                authorizeHttpRequests.requestMatchers(HttpMethod.GET, "/tripScore/passenger").hasRole("PASSENGER")
                authorizeHttpRequests.requestMatchers(HttpMethod.GET, "/tripScore/driver")
                    .hasAnyRole("DRIVER", "PASSENGER")
                authorizeHttpRequests.requestMatchers(HttpMethod.POST, "/tripScore").hasRole("PASSENGER")
                authorizeHttpRequests.requestMatchers(HttpMethod.DELETE, "/tripScore").hasRole("PASSENGER")
                authorizeHttpRequests.requestMatchers(HttpMethod.GET, "/tripScore/confirmation").hasRole("PASSENGER")

                //Analytics controller
                authorizeHttpRequests.requestMatchers(HttpMethod.POST, "/analytics").hasRole("PASSENGER")

                //SWAGER
                authorizeHttpRequests.requestMatchers(
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**",
                    "/v3/api-docs",
                    "/swagger-resources/**",
                    "/webjars/**"
                ).permitAll()

                //Default
                authorizeHttpRequests.anyRequest().denyAll()
            }
            .addFilterBefore(JwtTokenValidator(jwtUtil), BasicAuthenticationFilter::class.java)
            .build()
    }

    @Bean
    @Throws(Exception::class)
    fun authenticationManager(authConfig: AuthenticationConfiguration): AuthenticationManager {
        return authConfig.authenticationManager
    }

    @Bean
    fun authenticationProvider(authRepo: AuthRepository): AuthenticationProvider {
        val provider = DaoAuthenticationProvider()
        provider.setPasswordEncoder(this.passwordEncoder())
        provider.setUserDetailsService(this.userDetailService(authRepo, passwordEncoder()))
        return provider
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return Argon2PasswordEncoder(128, 255, 1, 1024, 1)
    }

    @Bean
    fun userDetailService(authRepo: AuthRepository, passwordEncoder: PasswordEncoder): UserDetailsService {
        return AuthService(authRepo, passwordEncoder)
    }

    @Bean
    fun corsConfigurationSource(): UrlBasedCorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("http://localhost:5173", "http://localhost:5174")
        configuration.allowedMethods = listOf("*")
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web: WebSecurity ->
            web.ignoring()
                .requestMatchers("/swagger-ui/", "/v3/api-docs*/")
        }
    }
}