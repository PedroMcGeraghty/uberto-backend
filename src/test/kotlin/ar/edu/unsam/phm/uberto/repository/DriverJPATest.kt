package ar.edu.unsam.phm.uberto.repository

import ar.edu.unsam.phm.uberto.builder.DriverBuilder
import ar.edu.unsam.phm.uberto.builder.PassengerBuilder
import ar.edu.unsam.phm.uberto.builder.TripBuilder
import ar.edu.unsam.phm.uberto.factory.TestFactory
import ar.edu.unsam.phm.uberto.model.SimpleDriver
import ar.edu.unsam.phm.uberto.security.TokenJwtUtil
import ar.edu.unsam.phm.uberto.services.AuthService
import ar.edu.unsam.phm.uberto.services.DriverService
import ar.edu.unsam.phm.uberto.services.PassengerService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.bean.override.mockito.MockitoBean

@DataJpaTest
class DriverJPATest {

    @Autowired
    lateinit var driverRepository: DriverRepository

    @MockitoBean
    lateinit var authService: AuthService

    @MockitoBean
    lateinit var passengerService: PassengerService

    @MockitoBean
    lateinit var jwtUtil: TokenJwtUtil

    @MockitoBean
    lateinit var driverService: DriverService


    @Test
    fun `dado un id, obtengo el driver con sus trips`() {
        // Arrange
        val driver = DriverBuilder(newDriver = SimpleDriver()).build()
        val trip = TripBuilder().driver(driver).build()

        // Act
        driver.trips.add(trip)
        driver.addTrip(trip)
        driverRepository.save(driver)

        val driverQuery = driverRepository.getByIdTrip(driver.id!!)

        // Assert
        assert(driverQuery.get().trips != null)
    }
}