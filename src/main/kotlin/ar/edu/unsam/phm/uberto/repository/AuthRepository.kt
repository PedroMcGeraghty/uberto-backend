package ar.edu.unsam.phm.uberto.repository

import ar.edu.unsam.phm.uberto.model.Role
import ar.edu.unsam.phm.uberto.model.UserAuthCredentials
import org.springframework.data.jpa.repository.JpaRepository

interface AuthRepository: JpaRepository<UserAuthCredentials, Int> {

    fun findByUsername(username:String): UserAuthCredentials?

    fun findByRole(role: Role): List<UserAuthCredentials>
}