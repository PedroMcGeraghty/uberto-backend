package ar.edu.unsam.phm.uberto.repository

import ar.edu.unsam.phm.uberto.builder.DriverBuilder
import ar.edu.unsam.phm.uberto.builder.PassengerBuilder
import ar.edu.unsam.phm.uberto.builder.TripBuilder
import ar.edu.unsam.phm.uberto.factory.TestFactory
import ar.edu.unsam.phm.uberto.model.Passenger
import ar.edu.unsam.phm.uberto.model.PremiumDriver
import ar.edu.unsam.phm.uberto.model.Trip
import ar.edu.unsam.phm.uberto.security.TokenJwtUtil
import ar.edu.unsam.phm.uberto.services.AuthService
import ar.edu.unsam.phm.uberto.services.DriverService
import ar.edu.unsam.phm.uberto.services.PassengerService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFalse

@DataJpaTest
class TripRepositoryTest() {

    @Autowired lateinit var tripRepository: TripsRepository
    @Autowired lateinit var passengerRepository: PassengerRepository
    @Autowired lateinit var driverRepository: DriverRepository

    @Test
    fun `buscar un trip asociado a un passenger`(){
        //Arrange
        val passenger = PassengerBuilder().build()
        passengerRepository.save(passenger)

        val driver = DriverBuilder(PremiumDriver()).build()
        driverRepository.save(driver)

        val trip = TripBuilder()
            .driver(driver)
            .passenger(passenger)
            .setDate(LocalDateTime.now().toString())
            .build()
        tripRepository.save(trip)

        //Act
        val client = tripRepository.findByClient(passenger)

        //Assert
        assertFalse(client.isEmpty())
    }

    @Test
    fun `buscar un trip asociado a un formulario de driver`(){
        //Arrange
        val passenger =  passengerRepository.save(PassengerBuilder().firstName("Mandarina").build())

        val driver = driverRepository.save(DriverBuilder(PremiumDriver()).build())

        val trip = tripRepository.save(
            TripBuilder()
                .destination("destino")
                .passenger(passenger)
                .driver(driver)
                .origin("origen")
                .setDate(LocalDateTime.now().toString())
                .passengerAmmount(2)
                .build()
        )

        //Act
        val foundTrip = tripRepository.searchByForm(
            origin = "origen",
            destination = "destino",
            numberPassengers = 2,
            name = "Mandarina",
            driverId = driver.id!!
        )

        //Assert
        assertEquals("destino", foundTrip.get(0).destination, "Los destinos no son iguales")
    }

    @Test
    fun `buscar un trip asociado a un formulario de driver con nombres similares`(){
        //Arrange
        val driver = driverRepository.save(
            DriverBuilder(PremiumDriver()).build()
        )

        val passengers = passengerRepository.saveAll<Passenger>(listOf<Passenger>(
            PassengerBuilder()
                .firstName("Mandarina")
                .lastName("Manzana")
                .build(),
            PassengerBuilder()
                .firstName("Mandarina")
                .lastName("Solution")
                .build()
        )).toList()


        tripRepository.saveAll(listOf<Trip>(
            TripBuilder()
                .destination("destino")
                .driver(driver)
                .passenger(passengers[0])
                .origin("origen")
                .setDate(LocalDateTime.now().toString())
                .passengerAmmount(2)
                .build(),
            TripBuilder()
                .destination("destino")
                .driver(driver)
                .passenger(passengers[0])
                .origin("origen")
                .setDate(LocalDateTime.now().toString())
                .passengerAmmount(2)
                .build()

        ))

        //Act
        val foundTrip = tripRepository.searchByForm(
            origin = "origen",
            destination = "destino",
            numberPassengers = 2,
            name = "Mandarina",
            driverId = driver.id!!)

        //Assert
        assertEquals(2, foundTrip.size, "Deberia tener 2 viajes")
    }

}