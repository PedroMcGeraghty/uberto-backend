package ar.edu.unsam.phm.uberto.services

import ar.edu.unsam.phm.uberto.NoFriendsFoundException
import ar.edu.unsam.phm.uberto.PassengerNotFoundException
import ar.edu.unsam.phm.uberto.dto.*
import ar.edu.unsam.phm.uberto.model.Passenger
import ar.edu.unsam.phm.uberto.repository.PassengerRepository
import exceptions.BusinessException
import exceptions.NotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.HttpClientErrorException.NotFound

@Service
class PassengerService(val passengerRepository: PassengerRepository) {
    fun getPassenger(passengerId: Int): Passenger {
        return passengerRepository.searchByUserID(passengerId) ?: throw PassengerNotFoundException()
    }

    fun getFriends(passengerId: Int): List<Passenger> {
        val friends: List<Passenger> = passengerRepository.getByID(passengerId).friends
        return friends
    }


    fun addBalance(passenger: Passenger, balance: Double): ResponseEntity<String> {
        passenger.loadBalance(balance)
        return ResponseEntity
            .status(HttpStatus.OK).body("Balance succesfully updated")
    }

    fun updateInfo(passenger: Passenger, firstName: String?, lastName: String?, cellphone: Int?): ResponseEntity<String> {
        firstName?.let { passenger.firstName = it }
        lastName?.let { passenger.lastName = it }
        cellphone?.let { passenger.cellphone = it }
        passengerRepository.update(passenger)
        return ResponseEntity
            .status(HttpStatus.OK).body("Profile succesfully updated")
    }

    fun deleteFriend(passenger: Passenger, friend: Passenger): ResponseEntity<String> {
        passenger.removeFriend(friend)
        passengerRepository.update(passenger)
        return ResponseEntity
            .status(HttpStatus.OK).body("Friend succesfully removed")
    }

    fun addFriend(passenger: Passenger, friend: Passenger): ResponseEntity<String> {
        passenger.addFriend(friend)
        passengerRepository.update(passenger)
        return ResponseEntity
            .status(HttpStatus.OK).body("You have a new friend!")
    }

    fun searchNonFriends(passengerId: Int, filter: String): List<Passenger> {
        //Que garron de metodo
        //Llama a metodos que deberia tener directamente el repo. Futuro refactor
        val passengers:List<Passenger> = passengerRepository.instances.filter { it.id != passengerId }
        if(filter.isEmpty()) return passengers
        val passengerUser:Passenger = passengerRepository.getByID(passengerId)
        val nonFriendsPassenger:List<Passenger> = passengers.filter { passenger:Passenger->
            passengerMatch(passenger, filter) && !passengerUser.isFriendOf(passenger)
        }
        if(nonFriendsPassenger.isEmpty()) throw NoFriendsFoundException()
        return nonFriendsPassenger
    }

    private fun passengerMatch(passenger: Passenger, textFilter: String): Boolean {
        val ignoreCase:Boolean = true
        return passenger.firstName.contains(textFilter, ignoreCase) || passenger.lastName.contains(textFilter, ignoreCase)
    }

    fun getImg(passengerId: Int): String {
        val passenger = getPassenger(passengerId)
        return passenger.img
    }
}
