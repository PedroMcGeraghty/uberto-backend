package ar.edu.unsam.phm.uberto.controller

import ar.edu.unsam.phm.uberto.dto.DriverDTO
import ar.edu.unsam.phm.uberto.services.DriverService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import ar.edu.unsam.phm.uberto.factory.TestFactory
import ar.edu.unsam.phm.uberto.repository.MongoDriverRepository
import ar.edu.unsam.phm.uberto.repository.PassengerRepository
import ar.edu.unsam.phm.uberto.repository.TripScoreRepository
import ar.edu.unsam.phm.uberto.repository.TripsRepository
import ar.edu.unsam.phm.uberto.security.TokenJwtUtil
import ar.edu.unsam.phm.uberto.services.AuthService
import ar.edu.unsam.phm.uberto.services.PassengerService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Dado un controller de Driver")
class DriverControllerSpringTest(
    @Autowired private var tripScoreRepository: TripScoreRepository,
    @Autowired private var passengerRepository: PassengerRepository,
    @Autowired var mockMvc: MockMvc,
    @Autowired var driverService: DriverService,
    @Autowired var authService: AuthService,
    @Autowired var driverRepository: MongoDriverRepository,
    @Autowired var tripRepository: TripsRepository,
    @Autowired var passengerService: PassengerService,
    @Autowired var jwtUtil: TokenJwtUtil,
) {


    val testFactory = TestFactory(authService, passengerService, driverService ,jwtUtil)
    val tokenDriver = testFactory.generateTokenDriverTest("simple")
    val tokenPassenger = testFactory.generateTokenPassengerTest("adrian")
    val invalidToken = testFactory.generateInvalidToken("simple")

    @Test
    fun `busco un driver cuyo id no existe`() {
        mockMvc.perform(MockMvcRequestBuilders.get("/driver")
            .header("Authorization", "Bearer $invalidToken")
        ).andExpect(MockMvcResultMatchers.status().is4xxClientError)
    }

    @Test
    fun `busco un driver cuyo id si existe`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/driver")
                .header("Authorization", "Bearer $tokenDriver")
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `obtengo la img de un driver existente`() {
        mockMvc.perform(MockMvcRequestBuilders.get("/driver/img?driverid=1")
            .header("Authorization", "Bearer $tokenDriver"))
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `obtengo choferes disponibles`() {
//        tiro una fecha posterior, pueden todos los choferes
        mockMvc.perform(
            MockMvcRequestBuilders.get("/driver/available")
                .header("Authorization", "Bearer $tokenPassenger")
                .param("date", "2025-06-10T10:00:00")
                .param("origin", "Av Santa Fe 1000")
                .param("destination", "Av Calle Corrientes 1000")
                .param("numberpassengers", "2")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `creo un viaje y lo agrego, el chofer no estará disponible esa fecha`() {
        // Me creo el viaje
        val driver = driverRepository.findById("1").get()
        val passenger = passengerRepository.findById(1).get()
        val trip = testFactory.createTrip(passenger, driver)

        trip.apply {
            numberPassengers = 2
            destination = "destino"
            origin = "origen"
            date = LocalDateTime.now().plusDays(15) // El viaje es en 15 días
            duration = 10
        }

//        passenger.scoreTrip(trip, scoreMock.message, scoreMock.scorePoints)

        // Guardo el viaje
        tripRepository.save(trip)


        // Verifico que el chofer no esté disponible en esa fecha
        mockMvc.perform(
            MockMvcRequestBuilders.get("/driver/available")
                .header("Authorization", "Bearer $tokenPassenger")
                .param("date", trip.date.toString())
                .param("origin", trip.origin)
                .param("destination", trip.destination)
                .param("numberpassengers", trip.numberPassengers.toString())

        )
            .andExpect(MockMvcResultMatchers.status().isOk)


//            .andExpect(MockMvcResultMatchers.jsonPath("$[?(@.id == ${driver.id})]").doesNotExist()) // El chofer no debe estar disponible
    }

    @Test
    fun `recibo un driverDto y actualizo sus campos`() {
        val updateInfo = DriverDTO(
            "1",
            "XYZ123",
            "Pepito",
            "Pérez",
            "Toyota",
            2020,
            100.0
        )

        // Convertir el objeto a JSON
        val objectMapper = ObjectMapper()
        val updateInfoJson = objectMapper.writeValueAsString(updateInfo)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/driver").header("Authorization", "Bearer $tokenDriver")
                .contentType("application/json")
                .content(updateInfoJson)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)

        mockMvc.perform(MockMvcRequestBuilders.get("/driver").header("Authorization", "Bearer $tokenDriver"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.serial").value(updateInfo.serial))
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(updateInfo.firstName))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value(updateInfo.lastName))
            .andExpect(MockMvcResultMatchers.jsonPath("$.brand").value(updateInfo.brand))
            .andExpect(MockMvcResultMatchers.jsonPath("$.model").value(updateInfo.model))
            .andExpect(MockMvcResultMatchers.jsonPath("$.price").value(updateInfo.price))

    }


}