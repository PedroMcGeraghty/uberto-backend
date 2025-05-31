package ar.edu.unsam.phm.uberto.factory

import ar.edu.unsam.phm.uberto.model.*
import ar.edu.unsam.phm.uberto.security.TokenJwtUtil
import ar.edu.unsam.phm.uberto.services.AuthService
import ar.edu.unsam.phm.uberto.services.DriverService
import ar.edu.unsam.phm.uberto.services.PassengerService
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDate
import java.time.LocalDateTime

class TestFactory(
    @Autowired val authService: AuthService,
    @Autowired val passengerService: PassengerService,
    @Autowired val driverService: DriverService,
    @Autowired val jwtUtil: TokenJwtUtil
) {

    fun createPassenger(amount : Int): List<Passenger>{
        val listPassenger: MutableList<Passenger> = mutableListOf()
        for(i in 0..amount){
            val passenger = Passenger().apply {
                firstName = "Pasajero ${i}"
                lastName = "Test ${i}"
            }
            listPassenger.add(passenger)
        }
        return  listPassenger
    }

    fun createDriverPremium(amount : Int): List<Driver>{
        val listDriver: MutableList<Driver> = mutableListOf()
        for(i in 0..amount){
            val driver = PremiumDriver().apply {
                firstName = "Driver Premium ${i}"
                lastName = "Test Premium ${i}"
                img = ""
                serial = "TEST TEST"
                model = 1998
                brand = "TEST"
            }
            listDriver.add(driver)
        }
        return  listDriver
    }

    fun createTripFinished(amount: Int): List<Trip>{
        val listDriver = createDriverPremium(amount)
        val listPassenger = createPassenger(amount)
        val listTrip: MutableList<Trip> = mutableListOf()

        for(i in 0..amount){
            val trip = Trip().apply {
                client = listPassenger.get(i)
                driver = listDriver.get(i)
                date = LocalDateTime.now()
            }
            listTrip.add(trip)
        }
        return listTrip
    }

    fun createTripPending(amount: Int): List<Trip>{
        val listDriver = createDriverPremium(amount)
        val listPassenger = createPassenger(amount)
        val listTrip: MutableList<Trip> = mutableListOf()

        for(i in 0..amount){
            val trip = Trip().apply {
                client = listPassenger.get(i)
                driver = listDriver.get(i)
                date = LocalDateTime.now().plusDays(1)
            }
            listTrip.add(trip)
        }
        return listTrip
    }


    fun createTrip(passenger: Passenger, driver: Driver): Trip{
        return Trip().apply {
            client = passenger
            this.driver = driver
            date = LocalDateTime.now()
        }
    }

    fun createTripScore(trip: Trip){
        val msj = listOf(
            "¡Buen trabajo!",
            "Sigue así.",
            "Necesitas mejorar.",
            "Excelente desempeño.",
            "Puedes hacerlo mejor."
        )
        val score = TripScore().apply {
            scorePoints = (1..5).random()
            message = msj.random()
            date = LocalDate.now()
        }
        trip.apply {
            this.score = score
        }
    }

    fun generateTokenPassengerTest(username: String): String{
        val userAuth = authService.loadUserByUsername(username) as UserAuthCredentials
        val user = passengerService.getByCredentialsId(userAuth.id!!)
        return jwtUtil.generate(userAuth, user.id!!.toString())
    }

    fun generateTokenDriverTest(username: String): String{
        val userAuth = authService.loadUserByUsername(username) as UserAuthCredentials
        val user = driverService.getByCredentialsId(userAuth.id!!.toString())
        return jwtUtil.generate(userAuth, user.id!!)
    }

    fun generateInvalidToken(username:String): String{
        val userAuth = authService.loadUserByUsername(username) as UserAuthCredentials
        val user = driverService.getByCredentialsId(userAuth.id!!.toString())
        return jwtUtil.generate(userAuth, "28")
    }

}