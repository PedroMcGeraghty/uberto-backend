package ar.edu.unsam.phm.uberto.controller

import ar.edu.unsam.phm.uberto.dto.TripScoreDTOMongo
import ar.edu.unsam.phm.uberto.dto.toTripScoreDTOMongo
import ar.edu.unsam.phm.uberto.dto.toTripScorePassengerDTOMongo
import ar.edu.unsam.phm.uberto.model.Trip
import ar.edu.unsam.phm.uberto.model.TripScore
import ar.edu.unsam.phm.uberto.security.TokenJwtUtil
import ar.edu.unsam.phm.uberto.services.DriverService
import ar.edu.unsam.phm.uberto.services.PassengerService
import ar.edu.unsam.phm.uberto.services.TripScoreService
import ar.edu.unsam.phm.uberto.services.TripService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@CrossOrigin(origins = ["http://localhost:8080", "http://localhost:5173"])
@RestController
@RequestMapping("/tripScore")
class TripScoreController(
    private val tripScoreService: TripScoreService,
    private val tripService: TripService,
    private val passengerService: PassengerService,
    private val driverService: DriverService,
    private val jwtUtil: TokenJwtUtil
){

    @GetMapping("/passenger")
    fun getScorePassenger(request: HttpServletRequest): List<TripScoreDTOMongo>{
        val idToken = jwtUtil.getIdFromTokenString(request)
        val trips = tripService.getAllByPassenger(idToken)

        return tripService.getTripScoresByTrips(trips)
    }

    @GetMapping("/driver")
    fun getScoreDriver(request: HttpServletRequest): List<TripScoreDTOMongo>{
        val idToken = jwtUtil.getIdDriverFromTokenString(request)
        val driver = driverService.getScoreByDriverID(idToken)
        return driver
    }

    @GetMapping("/confirmation")
    fun getScoreConfirmation( @RequestParam driverId: String): List<TripScoreDTOMongo>{
        val driver = driverService.getScoreByDriverID(driverId)
        return driver
    }

    @PostMapping()
    fun create(@RequestBody tripScore: TripScoreDTOMongo): ResponseEntity<String> {
        val trip = tripService.getById(tripScore.tripId)
        val score = TripScore()
        score.message = tripScore.message
        score.scorePoints = tripScore.scorePoints
        return tripScoreService.create(trip,score)
    }

    @DeleteMapping()
    fun delete(request: HttpServletRequest , @RequestParam tripId: Long): ResponseEntity<String>{
        val idToken = jwtUtil.getIdFromTokenString(request)
        val trip = tripService.getWithPassengerByIdAndPassengerId(tripId, idToken)
        return tripScoreService.delete(trip)
    }

}