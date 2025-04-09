package ar.edu.unsam.phm.uberto.controller

import ar.edu.unsam.phm.uberto.dto.*
import ar.edu.unsam.phm.uberto.services.DriverService
import ar.edu.unsam.phm.uberto.services.TravelTimeMockService
import exceptions.BusinessException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@CrossOrigin(origins = ["http://localhost:8080", "http://localhost:5173"])
@RestController
@RequestMapping("/driver")
class DriverController(private val driverService: DriverService, val timeTripsService: TravelTimeMockService) {

    @GetMapping("/{id}")
    fun getByID(@PathVariable id:Int): DriverDTO {
        val driver = driverService.getDriverData(id)
        return driver.toDTO()
    }

    @GetMapping("/avaliable")
    fun getDriversAvailable(@RequestParam date: LocalDateTime,
                            @RequestParam origin: String,
                            @RequestParam destination: String,
                            @RequestParam numberpassengers: Int): DriverCardAndTimeDTO {
        val timeMap = timeTripsService.getTime()
        val time = timeMap["time"] ?: throw BusinessException("Failure in the time calculation system")
        val avaliableDrivers = driverService.getDriversAvailable(date, time)
        val driverCardDTO = avaliableDrivers.map{it.toCardDTO(timeMap["time"]!!, numberpassengers)}
        return DriverCardAndTimeDTO(timeMap["time"]!!, driverCardDTO)
    }

    @PostMapping()
    fun changeProfile(@RequestBody driverDTO: DriverDTO): ResponseEntity<String>{
        val driver = driverService.getDriverData(driverDTO.id)
        driverService.changeProfile(driverDTO, driver)
        return driverService.update(driver)
    }

    @GetMapping("/img")
    fun getImg(@RequestParam driverid: Int): Map<String, String> {
        val driver = driverService.getDriverData(driverid)
        return mapOf("img" to driver.img)
    }
}