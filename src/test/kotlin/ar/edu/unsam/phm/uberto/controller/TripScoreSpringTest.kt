package ar.edu.unsam.phm.uberto.controller

import ar.edu.unsam.phm.uberto.dto.LoginDTO
import ar.edu.unsam.phm.uberto.dto.TripScoreDTO
import ar.edu.unsam.phm.uberto.factory.TestFactory
import ar.edu.unsam.phm.uberto.repository.DriverRepository
import ar.edu.unsam.phm.uberto.repository.PassengerRepository
import ar.edu.unsam.phm.uberto.repository.TripsRepository
import ar.edu.unsam.phm.uberto.security.TokenJwtUtil
import ar.edu.unsam.phm.uberto.services.AuthService
import ar.edu.unsam.phm.uberto.services.DriverService
import ar.edu.unsam.phm.uberto.services.PassengerService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.jsonwebtoken.Claims
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Dado un controller de TripsScore")
class TripScoreSpringTest(
    @Autowired var passengerRepository: PassengerRepository,
    @Autowired var mockMvc: MockMvc,
    @Autowired var driverService: DriverService,
    @Autowired var driverRepository: DriverRepository,
    @Autowired var tripRepository: TripsRepository,
    @Autowired var authService: AuthService,
    @Autowired var passengerService: PassengerService,
    @Autowired var jwtUtil: TokenJwtUtil
) {

    val testFactory = TestFactory(authService, passengerService, driverService ,jwtUtil)
    val tokenDriver = testFactory.generateTokenDriverTest("simple")
    val tokenPassenger = testFactory.generateTokenPassengerTest("adrian")

    @Test
    fun `busco las calificaciones de un pasajero que no realizo ninguna calificacion`(){
        mockMvc.perform(MockMvcRequestBuilders.get("/tripScore/passenger")
            .header("Authorization", "Bearer $tokenPassenger"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
    }

    @Test
    fun `busco las calificaciones de un conductor que no tiene calificaciones`(){
        mockMvc.perform(MockMvcRequestBuilders.get("/tripScore/driver")
            .header("Authorization", "Bearer $tokenDriver"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
    }

    @Test
    fun `Si creo una calificacion tanto el pasajero como el conductor podran ver la misma`() {
        val trip      = testFactory.createTripFinished(1).get(0)
        val passenger = trip.client
        val driver0    = trip.driver

        passenger.apply {
            firstName = "Pica"
            lastName = ""
        }
        driver0.apply {
            firstName = "Colorado"
            lastName  = ""
        }

        trip.apply {
            client = passenger
            driver = driver0
        }
        passengerRepository.save(passenger)
        driverRepository.save(driver0)
        tripRepository.save(trip)

        val tripScore = TripScoreDTO(
             trip.id!!,
             "Viaje copado",
             3,
             "30/05/2025",
             passenger.firstName,
             driver0.firstName,
             "",
             "",
             true
        )

        val objectMapper = ObjectMapper()
        val createInfoJson = objectMapper.writeValueAsString(tripScore)

        mockMvc.perform(MockMvcRequestBuilders.post("/tripScore").header("Authorization", "Bearer $tokenPassenger")
            .contentType("application/json")
            .content(createInfoJson))
            .andExpect(MockMvcResultMatchers.status().isOk)

        mockMvc.perform(MockMvcRequestBuilders.get("/tripScore/passenger")
            .header("Authorization", "Bearer $tokenPassenger"))
            .andExpect {
                val lista:List<Object> = objectMapper.readValue(it.response.contentAsString)
                assertEquals(expected = lista.isEmpty(), actual = false)
            }

    }

}