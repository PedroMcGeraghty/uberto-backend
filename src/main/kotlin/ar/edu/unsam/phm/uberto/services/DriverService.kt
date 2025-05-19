package ar.edu.unsam.phm.uberto.services

import ar.edu.unsam.phm.uberto.dto.DriverAvailableDto
import ar.edu.unsam.phm.uberto.dto.DriverDTO
import ar.edu.unsam.phm.uberto.model.Driver
//import ar.edu.unsam.phm.uberto.repository.DriverAvgDTO
import ar.edu.unsam.phm.uberto.repository.DriverRepository
import ar.edu.unsam.phm.uberto.repository.TripsRepository
import exceptions.BusinessException
import exceptions.NotFoundException
import jakarta.transaction.Transactional
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Service
class DriverService(
    val driverRepo: DriverRepository,
    private val tripsRepository: TripsRepository
) {

    fun getDriverData(userID: Long):Driver{
        val driver = driverRepo.findById(userID).orElseThrow { NotFoundException("Driver with id $userID not found") }
        return driver
    }

    fun getByIdTrip(id: Long): Driver =
        driverRepo.getByIdTrip(id)
            .orElseThrow { NotFoundException("Driver with id $id not found") }

    @Transactional
    fun updateProfile(dto : DriverDTO, id: Long) : ResponseEntity<String> {
        try {
            val driver = getDriverData(id)
            val update = driver.update(dto)
            driverRepo.save(update)
            return ResponseEntity
                .status(HttpStatus.OK)
                .body("Updated profile")

        } catch (e: NotFoundException) {
            throw BusinessException("Driver not found")
        }
    }

    fun getDriversAvailable(date: LocalDateTime, time: Int): List<DriverAvailableDto> {
        try {
            val endTime = date.plus(time.toLong(), ChronoUnit.MINUTES)
            return tripsRepository.getAvailable(date,endTime)
        } catch ( e : Exception) {
            throw BusinessException(e.message ?: "Error in the driver search")
        }
    }

    fun getByCredentialsId(id: Long): Driver =
        driverRepo.findByCredentials_Id(id).orElseThrow{throw NotFoundException("Driver no encontrado")}


}