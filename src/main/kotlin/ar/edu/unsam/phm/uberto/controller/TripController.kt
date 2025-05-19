package ar.edu.unsam.phm.uberto.controller

import ar.edu.unsam.phm.uberto.dto.FormTripDTO
import ar.edu.unsam.phm.uberto.dto.PendingAndFinishedTripsDTO
import ar.edu.unsam.phm.uberto.dto.TripDTO
import ar.edu.unsam.phm.uberto.dto.toDTO
import ar.edu.unsam.phm.uberto.security.TokenJwtUtil
import ar.edu.unsam.phm.uberto.services.*
import exceptions.NotFoundException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@CrossOrigin(origins = ["http://localhost:8080", "http://localhost:5173"])
@RestController
@RequestMapping("/trip")

class TripsController(
    private val tripService: TripService,
    private val passengerService: PassengerService,
    private val driverService: DriverService,
    private val jwtUtil: TokenJwtUtil) {

    @PostMapping("/create")
    fun createTrip(@RequestBody trip: TripDTO, request: HttpServletRequest): ResponseEntity<String> {
        val idToken = jwtUtil.getIdFromTokenString(request)
        val client = passengerService.getByIdTrip(idToken)
        val driver = driverService.getByIdTrip(trip.driverId)
        return tripService.createTrip(trip, client, driver)
    }

    @GetMapping("/passenger")
    fun getAllByPassengerId(request: HttpServletRequest): List<TripDTO> {
        val idToken = jwtUtil.getIdFromTokenString(request)
        val passenger = passengerService.getByIdTrip(idToken)
        return tripService.getAllByPassenger(passenger).map { it.toDTO() }
    }

    @GetMapping("/driver")
    fun getAllByDriverId(request: HttpServletRequest): List<TripDTO> {
        val idToken = jwtUtil.getIdFromTokenString(request)
        val driver = driverService.getByIdTrip(idToken)
        return tripService.getAllByDriver(driver).map { it.toDTO() }
    }

    @GetMapping("/pending")
    fun getTripsPendingFromDriver(
        @RequestParam origin: String,
        @RequestParam destination: String,
        @RequestParam name: String,
        @RequestParam numberPassenger: Int,
        request: HttpServletRequest
    ): List<TripDTO> {
        val idToken = jwtUtil.getIdFromTokenString(request)
        return tripService.getTripsPendingFromDriver(
            origin,
            destination,
            numberPassenger,
            name,
            idToken,
        ).map { it.toDTO() }
    }

    @GetMapping("/profile/passenger")
    fun getProfilePassenger(request: HttpServletRequest): PendingAndFinishedTripsDTO {
        val idToken = jwtUtil.getIdFromTokenString(request)
        val passenger = passengerService.getByIdTrip(idToken)
        val finishedTrips = tripService.getFinishedTripPassenger(passenger).map { it.toDTO() }
        val pendingTrips = tripService.getPendingTripPassenger(passenger).map { it.toDTO() }
        return PendingAndFinishedTripsDTO(pendingTrips, finishedTrips)
    }

    @GetMapping("/profile/driver")
    fun getProfileDriver(request: HttpServletRequest): PendingAndFinishedTripsDTO {
        val idToken = jwtUtil.getIdFromTokenString(request)
        val driver = driverService.getByIdTrip(idToken)
        val finishedTrips = tripService.getFinishedTripDriver(driver).map { it.toDTO() }
        val pendingTrips = listOf<TripDTO>()
        return PendingAndFinishedTripsDTO(pendingTrips, finishedTrips)
    }
}