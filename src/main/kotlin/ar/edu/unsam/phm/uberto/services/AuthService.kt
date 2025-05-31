package ar.edu.unsam.phm.uberto.services

import ar.edu.unsam.phm.uberto.InvalidCredentialsException
import ar.edu.unsam.phm.uberto.model.UserAuthCredentials
import ar.edu.unsam.phm.uberto.repository.AuthRepository
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    val authRepository: AuthRepository,
    val passwordEncoder: PasswordEncoder
): UserDetailsService {

    override fun loadUserByUsername(username: String?): UserDetails? {
        if(username == null) throw InvalidCredentialsException("Usuario o Contraseña Incorrecta 0")
        val user = this.findUserByUsername(username=username)
            ?: throw InvalidCredentialsException("Usuario o Contraseña Incorrecta 1")
    return  user
    }

    fun findUserByUsername(username: String): UserAuthCredentials? {
        return authRepository.findByUsername(username)
    }

    fun validPassword(password: String, user: UserDetails){
        if(!passwordEncoder.matches(password, user.password)){
            throw InvalidCredentialsException("Usuario o contraseña incorrecta 2")
        }
    }

    fun authenticate(user: UserAuthCredentials) : Authentication{
        return UsernamePasswordAuthenticationToken(user.username, user.password)
    }

    fun setContext(authorizedUser: Authentication) {
        SecurityContextHolder.getContext().authentication = authorizedUser
    }


}