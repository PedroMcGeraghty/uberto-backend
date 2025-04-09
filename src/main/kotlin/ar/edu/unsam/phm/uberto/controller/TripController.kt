package ar.edu.unsam.phm.uberto.controller

import ar.edu.unsam.phm.uberto.dto.FormTripDTO
import ar.edu.unsam.phm.uberto.dto.TripDTO
import ar.edu.unsam.phm.uberto.dto.toDTO
import ar.edu.unsam.phm.uberto.services.TravelTimeMockService
import ar.edu.unsam.phm.uberto.services.TripService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@CrossOrigin(origins = ["http://localhost:8080", "http://localhost:5173"])
@RestController
@RequestMapping("/trip")
class TripsController(private val travelTimeService: TravelTimeMockService, private val tripService: TripService) {
    @GetMapping("/estimate") //este creo que no se usa REVISAR
    fun getTime() : Map<String, Int>{
        return travelTimeService.getTime()
    }

    @PostMapping("/create")
    fun createTrip(@RequestBody trip: TripDTO): ResponseEntity<String> {
        return tripService.createTrip(trip)
    }

    @GetMapping()
    fun getTrips(@RequestParam id:Int,rol: String): List<TripDTO> {
        return tripService.getById(id, rol).map { it.toDTO() }
    }

    @PostMapping("/pending")
    fun getTripsPendingFromDriver(@RequestBody formTripDTO: FormTripDTO): List<TripDTO> {
        return tripService.getTripsPendingFromDriver(formTripDTO).map { it.toDTO() }
    }

    @GetMapping("/passenger")
    fun getFromPassenger(@RequestParam id:Int, rol: String): Map<String, List<TripDTO>> {
        val finishedTrips = tripService.getFinished(id, rol).map { it.toDTO() }
        val pendingTrips = tripService.getPending(id, rol).map { it.toDTO() }
        return mapOf("pendingTrips" to pendingTrips, "finishedTrips" to finishedTrips)
    }
}