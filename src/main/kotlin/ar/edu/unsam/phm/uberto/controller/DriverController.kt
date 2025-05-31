package ar.edu.unsam.phm.uberto.controller

import ar.edu.unsam.phm.uberto.dto.*
import ar.edu.unsam.phm.uberto.security.TokenJwtUtil
import ar.edu.unsam.phm.uberto.services.DriverService
import ar.edu.unsam.phm.uberto.services.TravelTimeMockService
import exceptions.BusinessException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@CrossOrigin(origins = ["http://localhost:8080", "http://localhost:5173"])
@RestController
@RequestMapping("/driver")
class DriverController(
    private val driverService: DriverService,
    private val timeTripsService: TravelTimeMockService,
    private val jwtUtil: TokenJwtUtil
) {

    @GetMapping()
    fun getByID(request: HttpServletRequest): DriverDTO {
        val idToken = jwtUtil.getIdDriverFromTokenString(request)
        return driverService.getDriverData(idToken).toDTO()
    }

    @GetMapping("/img")
    fun getImg(request: HttpServletRequest): DriverImg {
        val idToken = jwtUtil.getIdDriverFromTokenString(request)
        return driverService.getDriverData(idToken).toImgDTO()
    }
    @GetMapping("/available")
    fun getDriversAvailable(
        @RequestParam date: LocalDateTime,
        @RequestParam origin: String,
        @RequestParam destination: String,
        @RequestParam numberpassengers: Int
    ): DriverCardAndTimeDTO {
        validateParameters(date, origin, destination, numberpassengers)
    
        val time = timeTripsService.getTime()["time"] 
            ?: throw BusinessException("Failure in the time calculation system")
        val drivers = driverService.getAvailableDrivers(date, time)
        val availableDriverDTO = drivers.map {
                it.driver.toAvailableDTO(time, numberpassengers, it.averageScore)
            }
            return  DriverCardAndTimeDTO(time = time, cardDrivers = availableDriverDTO)
    }

    @PostMapping()
    fun changeProfile(@RequestBody driverDTO: DriverDTO, request: HttpServletRequest): ResponseEntity<String> {
        val idToken = jwtUtil.getIdDriverFromTokenString(request)
        return driverService.updateProfile(driverDTO, idToken)
    }
}

private fun validateParameters(date: LocalDateTime, origin: String, destination: String, numberpassengers: Int) {
    require(numberpassengers > 0) { "Number of passengers must be positive" }
    require(origin.isNotBlank()) { "Origin cannot be empty" }
    require(destination.isNotBlank()) { "Destination cannot be empty" }
    require(!date.isBefore(LocalDateTime.now())) { "Date cannot be in the past" }
}