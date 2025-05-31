package ar.edu.unsam.phm.uberto.services

import ar.edu.unsam.phm.uberto.dto.*
import ar.edu.unsam.phm.uberto.model.Driver
import ar.edu.unsam.phm.uberto.repository.MongoDriverRepository
import exceptions.NotFoundException
import jakarta.transaction.Transactional
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneOffset

@Service
class DriverService(
    private val mongoDriverRepo: MongoDriverRepository,
) {

    fun getDriverData(userID: String):Driver{
        val driver = mongoDriverRepo.findById(userID).orElseThrow { NotFoundException("Driver with id $userID not found") }
        return driver
    }

    fun getByIdTrip(id: String): Driver =
        mongoDriverRepo.findById(id)
            .orElseThrow { NotFoundException("Driver with id $id not found") }

    fun getScoreByDriverID(driverId: String): List<TripScoreDTOMongo> =
        mongoDriverRepo.getScoreByDriverID(driverId)?.tripsScoreDTO ?: emptyList()

    @Transactional
    fun updateProfile(dto : DriverDTO, id: String) : ResponseEntity<String> {
        try {
            val driver = getDriverData(id)
            val update = driver.update(dto)
            mongoDriverRepo.save(update)
            return ResponseEntity
                .status(HttpStatus.OK)
                .body("Updated profile")

        } catch (e: NotFoundException) {
            throw NotFoundException("Driver not found")
        }
    }

    fun getAvailableDrivers(start: LocalDateTime, end: Int): List<DriverAvailableDto> {

        val start = start.atZone(ZoneOffset.UTC).toInstant()
        val endTime = start.plusSeconds(end.toLong())
        var driversFromRepository = mongoDriverRepo.getAvailable(start, endTime)
        return driversFromRepository.map {it.toAvailableDto()

        }
    }

    fun getByCredentialsId(id: String): Driver =
        mongoDriverRepo.findByCredentialsId(id.toLong())
}