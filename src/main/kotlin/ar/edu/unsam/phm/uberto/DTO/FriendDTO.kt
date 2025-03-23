package ar.edu.unsam.phm.uberto.dto

import ar.edu.unsam.phm.uberto.model.Passenger

data class FriendDTO(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val img: String
)

fun Passenger.toFriendDTO(): FriendDTO {
    return FriendDTO(
        id = userId,
        firstName = firstName,
        lastName = lastName,
        img = img
    )
}