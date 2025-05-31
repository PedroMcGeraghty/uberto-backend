package ar.edu.unsam.phm.uberto.controller

import ar.edu.unsam.phm.uberto.dto.*
import ar.edu.unsam.phm.uberto.model.Passenger
import ar.edu.unsam.phm.uberto.security.TokenJwtUtil
import ar.edu.unsam.phm.uberto.services.PassengerService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@CrossOrigin(origins = ["http://localhost:8080", "http://localhost:5173"])
@RestController
@RequestMapping("/passenger")
class PassengerController(
    private val passengerService: PassengerService,
    private val jwtUtil: TokenJwtUtil,
) {

    @GetMapping()
    fun getById(request: HttpServletRequest): PassengerProfileDto {
        val idToken = jwtUtil.getIdFromTokenString(request)
        val passenger = passengerService.getById(idToken)
        return passenger.toDTOProfile()
    }

    @PutMapping("/addBalance")
    fun addBalance(@RequestParam balance: Double, request: HttpServletRequest): ResponseEntity<String> {
        val idToken = jwtUtil.getIdFromTokenString(request)
        val currentPassenger = passengerService.getById(idToken)
        return passengerService.addBalance(currentPassenger, balance)
    }

    @PutMapping
    fun updatePassenger(
        request: HttpServletRequest,
        @RequestBody updatedInfo: UpdatedPassengerDTO
    ): ResponseEntity<String> {
        val idToken = jwtUtil.getIdFromTokenString(request)
        val currentPassenger = passengerService.getById(idToken)
        return passengerService.updateInfo(
            currentPassenger,
            updatedInfo.firstName, updatedInfo.lastName, updatedInfo.phone
        )
    }

    @GetMapping("/friends")
    fun getFriends(request: HttpServletRequest): List<FriendDto> {
        val idToken = jwtUtil.getIdFromTokenString(request)
        return passengerService.getFriends(idToken)
    }

    @PostMapping("/friends")
    fun addFriend(request: HttpServletRequest, @RequestParam friendId: Long): ResponseEntity<String> {
        val idToken = jwtUtil.getIdFromTokenString(request)
        return passengerService.addFriend(idToken, friendId)
    }

    @DeleteMapping("/friends")
    fun deleteFriend(request: HttpServletRequest, friendId: Long): ResponseEntity<String> {
        val idToken = jwtUtil.getIdFromTokenString(request)
        return passengerService.deleteFriend(idToken, friendId)
    }

    @GetMapping("/friends/search")
    fun filter(request: HttpServletRequest, @RequestParam filter: String): List<FriendDto> {
        val idToken = jwtUtil.getIdFromTokenString(request)
        val nonFriendsPassengers: List<Passenger> = passengerService.searchNonFriends(idToken, filter)
        return nonFriendsPassengers.map { friend: Passenger ->
            friend.toDTOFriend()
        }
    }

    @GetMapping("/img")
    fun getImg(request: HttpServletRequest): ImgDTO {
        val idToken = jwtUtil.getIdFromTokenString(request)
        val currentPassenger = passengerService.getById(idToken)
        return currentPassenger.toDTOImg()
    }
}