package ar.edu.unsam.phm.uberto.factory

import ar.edu.unsam.phm.uberto.model.Role
import ar.edu.unsam.phm.uberto.model.UserAuthCredentials

class AuthFactory() {
    fun createAccount(username:String, password:String, role:Role):UserAuthCredentials {
        val account: UserAuthCredentials = UserAuthCredentials()
        account.username = username
        account.setPassword(password)
        account.role = role
        return account
    }

}
