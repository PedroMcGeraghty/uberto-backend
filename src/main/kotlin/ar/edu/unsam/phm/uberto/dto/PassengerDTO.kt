package ar.edu.unsam.phm.uberto.dto

import ar.edu.unsam.phm.uberto.model.Passenger

data class PassengerProfileDto(
    val id: Int,
    val firstname: String,
    val lastname: String,
    val age: Int,
    val money: Double,
    val cellphone: Int,
    val img: String,
    val friends: List<FriendDto>
)

fun Passenger.toDTOProfile() = PassengerProfileDto(
    id = userId,
    firstname = firstName,
    lastname = lastName,
    age = age,
    money = balance,
    cellphone = cellphone,
    img = img,
    friends = friends.map { it.toDTOFriend() }
)

data class FriendDto(
    val id: Int,
    val firstname: String,
    val lastname: String,
    val img: String,
)

fun Passenger.toDTOFriend() = FriendDto(
    id = userId,
    firstname = firstName,
    lastname = lastName,
    img = img
)

data class UpdatedPassengerDTO(
    val firstname: String?,
    val lastname: String?,
    val cellphone: Int?,
    val img: String?
)