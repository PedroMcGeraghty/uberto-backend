package ar.edu.unsam.phm.uberto.dto

import ar.edu.unsam.phm.uberto.model.Passenger

data class PassengerProfileDto(
    val id: Long,
    val firstname: String,
    val lastname: String,
    val age: Int,
    val money: Double,
    val cellphone: Int,
    val img: String
)

fun Passenger.toDTOProfile() = PassengerProfileDto(
    id = id!!,
    firstname = firstName,
    lastname = lastName,
    age = age(),
    money = balance,
    cellphone = cellphone,
    img = img
)

data class FriendDto(
    val id: Long,
    val firstname: String,
    val lastname: String,
    val img: String,
)

fun Passenger.toDTOFriend() = FriendDto(
    id = id!!,
    firstname = firstName,
    lastname = lastName,
    img = img
)

data class UpdatedPassengerDTO(
    val firstName: String?,
    val lastName: String?,
    val phone: Int?,
)


data class ImgDTO(
    val img: String
)

fun Passenger.toDTOImg() = ImgDTO(
    img = img
)