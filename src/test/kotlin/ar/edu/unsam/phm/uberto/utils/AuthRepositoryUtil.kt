package ar.edu.unsam.phm.uberto.utils

import ar.edu.unsam.phm.uberto.model.Role
import ar.edu.unsam.phm.uberto.model.UserAuthCredentials


class AuthRepositoryUtil {
    fun singleAccount(): UserAuthCredentials {
        return UserAuthCredentials().apply {
            username = "adrian"
            password = "adrian"
            role = Role.PASSENGER
        }
    }

    fun multipleMockedPassengerAccounts():List<UserAuthCredentials> {
        return listOf(
            UserAuthCredentials().apply {
                username = "adrian"
                password = "adrian"
                role = Role.PASSENGER
            },
            UserAuthCredentials().apply {
                username = "adrian2"
                password = "adrian2"
                role = Role.PASSENGER
            }
        )
    }

    fun multipleMockedDriverAccounts():List<UserAuthCredentials> {
        return listOf(
            UserAuthCredentials().apply {
                username = "simple"
                password = "simple"
                role = Role.DRIVER
            },
            UserAuthCredentials().apply {
                username = "premium"
                password = "premium"
                role = Role.DRIVER
            }
        )
    }
}