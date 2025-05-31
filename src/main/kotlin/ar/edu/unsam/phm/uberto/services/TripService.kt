package ar.edu.unsam.phm.uberto.services

import ar.edu.unsam.phm.uberto.BusinessException
import ar.edu.unsam.phm.uberto.FailSaveException
import ar.edu.unsam.phm.uberto.dto.TripCreateDTO
import ar.edu.unsam.phm.uberto.dto.TripScoreDTOMongo
import ar.edu.unsam.phm.uberto.dto.toTripDriverDTO
import ar.edu.unsam.phm.uberto.model.Driver
import ar.edu.unsam.phm.uberto.model.Passenger
import ar.edu.unsam.phm.uberto.model.Trip
import ar.edu.unsam.phm.uberto.repository.MongoDriverRepository
import ar.edu.unsam.phm.uberto.repository.TripsRepository
import org.springframework.dao.DataAccessException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class TripService(
    val tripRepo: TripsRepository,
    val driverRepo: MongoDriverRepository,
    private val mongoDriverRepository: MongoDriverRepository
) {

    fun getById(id: Long): Trip {
        return tripRepo.findById(id).get()
    }

    @Transactional
    fun createTrip(trip: TripCreateDTO, client: Passenger, driver: Driver): ResponseEntity<String> {

        val newTrip = Trip().apply {
            duration = trip.duration
            numberPassengers = trip.numberPassengers
            date = trip.date
            origin = trip.origin
            destination = trip.destination
            this.client = client
            this.driver = driver
            driverId = driver.id!!
        }

        if(!checkAvailableDriver( driver, newTrip)){//Valida en postgres
            throw BusinessException("Driver no disponible en este momento")
        }

        //Pago y Cobro de viaje
        client.requestTrip(newTrip)
        driver.responseTrip(newTrip, trip.duration)

        try{
            tripRepo.save(newTrip)
        }catch (e: DataAccessException){
            throw FailSaveException("Error en la creación del viaje")
        }

        try{
            driver.tripsDTO.add(newTrip.toTripDriverDTO())
            driverRepo.save(driver)
        }catch (e: DataAccessException){
            throw FailSaveException("Error al asignar viaje al chofer ")
        }

        return ResponseEntity
            .status(HttpStatus.OK)
            .body("Se reserva de viaje exitosamente")
    }

    fun getAllByPassenger(id: Long): List<Long> {
        return mongoDriverRepository.findTripIdsByPassengerId(id)
    }

    fun getTripScoresByTrips(tripIds: List<Long>): List<TripScoreDTOMongo> {
        if (tripIds.isEmpty()) {
            return emptyList() // Si no hay IDs, devuelve una lista vacía
        }
        return mongoDriverRepository.findTripScoresByTripIds(tripIds)
    }

    fun getFinishedTripDriver(driverId: String): List<Trip> {
        return tripRepo.findByDriverIdFinishedTrips(driverId)
    }

    fun getDriverFinishedTripByPassenger(passengerId: Long): List<Driver> {
        return driverRepo.findByPassengerIdFinishedTripsDTO(passengerId, LocalDateTime.now())
    }

    fun getDriverPendingTripByPassenger(passengerId: Long): List<Driver> {
        return driverRepo.findByPassengerIdPendingTripsDTO(passengerId, LocalDateTime.now())
    }

    fun getTripsPendingFromDriver(
        origin: String,
        destination: String,
        numberPassenger: Int,
        name: String,
        driverId: String): List<Trip> {
        return tripRepo.searchByForm(origin, destination, numberPassenger, name, driverId)
    }

    fun checkAvailableDriver(driver: Driver, trip: Trip): Boolean{
        return tripRepo.checkAvailableDriver(driver.id!!, trip.date, trip.finalizationDate()).isEmpty()
    }

    fun getWithPassengerByIdAndPassengerId(tripId: Long, passengerId: Long): Trip {
        return tripRepo.findTripByIdAndClient_Id(tripId, passengerId).get()
    }
}