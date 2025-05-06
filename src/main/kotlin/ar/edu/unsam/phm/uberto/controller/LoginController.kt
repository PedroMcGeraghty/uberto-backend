package ar.edu.unsam.phm.uberto.controller

import ar.edu.unsam.phm.uberto.InvalidCredentialsException
import ar.edu.unsam.phm.uberto.dto.LoginDTO
import ar.edu.unsam.phm.uberto.dto.LoginRequest

import ar.edu.unsam.phm.uberto.repository.DriverRepository
import ar.edu.unsam.phm.uberto.services.AuthService
import ar.edu.unsam.phm.uberto.model.Role
import ar.edu.unsam.phm.uberto.model.UserAuthCredentials
import ar.edu.unsam.phm.uberto.repository.PassengerRepository
import ar.edu.unsam.phm.uberto.security.TokenJwtUtil
import ar.edu.unsam.phm.uberto.services.DriverService
import ar.edu.unsam.phm.uberto.services.PassengerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*


@CrossOrigin(origins = ["http://localhost:8080", "http://localhost:5173"])
@RestController
@RequestMapping("/login")
class LoginController(
    private val authService: AuthService,
    private val driverService: DriverService,
    private val passengerService: PassengerService,
    private val tokenUtil: TokenJwtUtil
) {
    @PostMapping()
    fun authLogin(@RequestBody loginRequestBody: LoginRequest): LoginDTO {
        val user = authService.loadUserByUsername(loginRequestBody.username) as UserAuthCredentials
        authService.validPassword(loginRequestBody.password, user)
        if (user.role == Role.DRIVER) {
            val driver = driverService.getByCredentialsId(user.id!!)
            return LoginDTO(id = driver.id!!, rol = user.role, token=tokenUtil.generate(user, driver.id!!))
        } else {
            val passenger = passengerService.getByCredentialsId(user.id!!)
            return LoginDTO(id = passenger.id!!, rol = user.role, token=tokenUtil.generate(user, passenger.id!!))
        }
    }

}