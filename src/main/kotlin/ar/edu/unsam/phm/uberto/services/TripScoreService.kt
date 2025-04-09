package ar.edu.unsam.phm.uberto.services

import ar.edu.unsam.phm.uberto.BusinessException
import ar.edu.unsam.phm.uberto.dto.TripScoreDTO
import ar.edu.unsam.phm.uberto.model.Trip
import ar.edu.unsam.phm.uberto.model.TripScore
import ar.edu.unsam.phm.uberto.model.User
import ar.edu.unsam.phm.uberto.repository.DriverRepository
import ar.edu.unsam.phm.uberto.repository.PassengerRepository
import ar.edu.unsam.phm.uberto.repository.TripScoreRepository
import ar.edu.unsam.phm.uberto.repository.TripsRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class TripScoreService(
    private val tripScoreRepo: TripScoreRepository,
    private val tripRepo: TripsRepository,
    private val driverRepo: DriverRepository,
    private val passengerRepo: PassengerRepository
) {
    fun getFromUser(userId: Int): List<Trip?>{
//        Con JPA, se consulta directamente a los viajes que tengan una calificacion con ESE ID
        val users = driverRepo.instances.toList() + passengerRepo.instances.toList()
        val user: User? = users.find { it.userId == userId} ?: throw Exception("ERROR id")

        return user!!.trips.filter{ it.score != null}
    }

    fun getAllFromDriver(userId: Int): List<TripScore?>{
        val user = driverRepo.searchByUserID(userId)
        if(user == null ) throw Exception("ERROR id")
        return user.trips.filter{ it.score != null}.map { it.score }
    }

    fun delete(userId: Int, tripId: Int): ResponseEntity<String> {
        val trip = tripRepo.getByID(tripId)
        if (trip.client.id != userId) {
            throw BusinessException("Usuario no posee calificaciones para eliminar")
        }
        if(trip.score == null){
            throw BusinessException("El viaje no tiene puntuacion")
        }
        val tripScore = tripScoreRepo.getByID(trip.score!!.id)
        tripScoreRepo.delete(tripScore)
        trip.deleteScore()
        tripRepo.update(trip)
        return ResponseEntity
            .status(HttpStatus.OK)
            .body("Elimino calificacion")
    }

    fun create(tripScore: TripScoreDTO): ResponseEntity<String>{
        val trip = tripRepo.getByID(tripScore.tripId)
        val passenger = passengerRepo.searchByUserID(trip.client.id)
        if(passenger == null){
            throw BusinessException("No se encuentra pasajero")
        }
        passenger!!.scoreTrip(trip, tripScore.message, tripScore.scorePoints)

        if(trip.score == null){
            throw BusinessException("No se crea recomendacion")
        }
        tripScoreRepo.create(trip.score!!)

        return ResponseEntity
            .status(HttpStatus.OK)
            .body("Se crea recomendacion")
    }
}