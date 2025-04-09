package ar.edu.unsam.phm.uberto.services.auth


import ar.edu.unsam.phm.uberto.InvalidCredentialsException
import ar.edu.unsam.phm.uberto.dto.LoginRequest
import ar.edu.unsam.phm.uberto.repository.AuthCredentialsRepository
import org.springframework.stereotype.Service

@Service
class AuthService(val accountsRepo: AuthCredentialsRepository) {


    fun validateLogin(loginRequest: LoginRequest): UserAuthCredentials? {
        //Extract all accounts
        val accounts:List<UserAuthCredentials> = accountsRepo.instances.toList()

        //Find account by username.
        // Exist? Validate password.
            // Valid password? return. Not valid? Exception
        // Not Exist? Exception
        val user: UserAuthCredentials? = this.findUser(loginRequest.username, accounts) ?: throw InvalidCredentialsException()
        if(!this.validPassword(user!!, loginRequest.password)) throw InvalidCredentialsException()

        return user
    }

    private fun findUser(username: String, users:List<UserAuthCredentials>): UserAuthCredentials?{
        return users.find { account: UserAuthCredentials ->
            account.username == username
        }
    }

    private fun validPassword(user:UserAuthCredentials, password: String): Boolean{
        return user.password == password
    }
}