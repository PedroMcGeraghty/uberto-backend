package ar.edu.unsam.phm.uberto.repository

import ar.edu.unsam.phm.uberto.model.Role
import ar.edu.unsam.phm.uberto.model.UserAuthCredentials
import ar.edu.unsam.phm.uberto.utils.AuthRepositoryUtil
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import kotlin.test.assertEquals

@DataJpaTest
class AuthRepositoryTest {

    @Autowired
    lateinit var authRepository: AuthRepository

    val authRepoUtil = AuthRepositoryUtil()

    @Test
    fun findByUsernameFailed(){
        val account: UserAuthCredentials? = authRepository.findByUsername(username="invalid_username")
        assertEquals(expected = null, actual=account)
    }

    @Test
    fun findByUsernameSuccess(){
        authRepository.save(authRepoUtil.singleAccount())
        val account: UserAuthCredentials? = authRepository.findByUsername(username="adrian")
        assertEquals(expected = "adrian", actual=account!!.username)
        assertEquals(expected = Role.PASSENGER, actual=account!!.role)
    }

    @Test
    fun findPassengers(){
        authRepository.saveAll(authRepoUtil.multipleMockedPassengerAccounts())
        val accounts: List<UserAuthCredentials> = authRepository.findByRole(Role.PASSENGER)
        assertEquals(expected = 2, actual=accounts.size)
    }

    @Test
    fun findDrivers(){
        authRepository.saveAll(authRepoUtil.multipleMockedDriverAccounts())
        val accounts: List<UserAuthCredentials> = authRepository.findByRole(Role.DRIVER)
        assertEquals(expected = 2, actual=accounts.size)
    }

}