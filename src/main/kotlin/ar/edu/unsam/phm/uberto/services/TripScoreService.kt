package ar.edu.unsam.phm.uberto.services

import ar.edu.unsam.phm.uberto.FailSaveException
import ar.edu.unsam.phm.uberto.dto.toTripScoreDTOMongo
import ar.edu.unsam.phm.uberto.model.Trip
import ar.edu.unsam.phm.uberto.model.TripScore
import ar.edu.unsam.phm.uberto.repository.MongoDriverRepository
import ar.edu.unsam.phm.uberto.repository.PassengerRepository
import ar.edu.unsam.phm.uberto.repository.TripsRepository
import jakarta.transaction.Transactional
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class TripScoreService(
    private val tripRepo: TripsRepository,
    private val passengerRepo: PassengerRepository,
    private val driverRepo: MongoDriverRepository
) {

    @Transactional
    fun create(trip : Trip , score: TripScore) : ResponseEntity<String> {
        val passenger = passengerRepo.findById(trip.client.id!!).get()
        val driver = driverRepo.findById(trip.driverId).get()
        passenger.scoreTrip(trip,score.message,score.scorePoints)

        try {
            val savedTrip = tripRepo.save(trip)
            driver.tripsDTO.find { it.id == savedTrip.id }?.apply { this.rating = score.scorePoints }
            driver.tripsScoreDTO.add(savedTrip.toTripScoreDTOMongo())
            driverRepo.save(driver)
        } catch (e: Exception) {
            throw FailSaveException("Error en la calificacion de un viaje")
        }

        return ResponseEntity
            .status(HttpStatus.OK)
            .body("Creado con exito")
    }

    @Transactional
    fun delete( trip: Trip ) : ResponseEntity<String> {
        trip.deleteScore(trip.client)
        try {
            val driver = driverRepo.findById(trip.driverId).get()
            driver.tripsDTO.find {it.id == trip.id }?.rating = 0
            driver.tripsScoreDTO.removeIf {it.tripId == trip.id }
            driverRepo.save(driver)
            tripRepo.save(trip)
        } catch (e: Exception) {
            throw FailSaveException("Error en la eliminacion de una calificacion")
        }
        return ResponseEntity
            .status(HttpStatus.OK)
            .body("Eliminada con exito")
    }

}