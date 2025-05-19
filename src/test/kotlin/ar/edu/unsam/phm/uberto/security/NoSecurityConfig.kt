//package ar.edu.unsam.phm.uberto.security
//
//import org.springframework.boot.test.context.TestConfiguration
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Profile
//import org.springframework.security.config.annotation.web.builders.HttpSecurity
//import org.springframework.security.web.SecurityFilterChain
//
//@TestConfiguration
//@Profile("test")
//class NoSecurityConfig {
//    @Bean
//    fun filterChain(http: HttpSecurity): SecurityFilterChain {
//        return http
//            .cors { it.disable() }
//            .authorizeHttpRequests { it.anyRequest().permitAll() }
//            .build()
//    }
//}