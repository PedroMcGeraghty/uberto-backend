package ar.edu.unsam.phm.uberto.repository

import ar.edu.unsam.phm.uberto.builder.PassengerBuilder
import ar.edu.unsam.phm.uberto.factory.TestFactory
import ar.edu.unsam.phm.uberto.model.Passenger
import ar.edu.unsam.phm.uberto.security.TokenJwtUtil
import ar.edu.unsam.phm.uberto.services.AuthService
import ar.edu.unsam.phm.uberto.services.DriverService
import ar.edu.unsam.phm.uberto.services.PassengerService
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import kotlin.test.Test

@DataJpaTest
class PassengerRepositorySpec {

    @Autowired
    lateinit var passengerRepository: PassengerRepository

    val passenger = PassengerBuilder().build()
    val notFriend = PassengerBuilder().lastName("JUAN").build()


    @Test
    fun `La busqueda de posibles amigos no trae personas que ya lo son`() {
        val friend = PassengerBuilder().build()

        passengerRepository.saveAll(listOf(friend, notFriend))
        passenger.addFriend(friend)
        passengerRepository.save(passenger)

        passengerRepository.findPossibleFriends(passenger.id!!, "") shouldNotContain friend
    }

    @Test
    fun `Me trae una lista de no-amigos`() {
        var notFriends = mutableListOf<Passenger>()
        for (i in 1..5) {
            notFriends.add(PassengerBuilder().build())
        }
        passengerRepository.saveAll(notFriends)
        passengerRepository.save(passenger)

        passengerRepository.findPossibleFriends(passenger.id!!, "") shouldBe notFriends
    }

    @Test
    fun `La busqueda no es case-sensitive`() {
        passengerRepository.saveAll(listOf(notFriend, passenger))

        passengerRepository.findPossibleFriends(passenger.id!!, notFriend.lastName.lowercase()) shouldContain notFriend
    }
}
