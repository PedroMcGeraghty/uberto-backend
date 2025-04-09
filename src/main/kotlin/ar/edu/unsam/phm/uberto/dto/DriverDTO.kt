package ar.edu.unsam.phm.uberto.dto

import ar.edu.unsam.phm.uberto.model.Driver

data class DriverDTO(
    val id: Int,
    val serial: String,
    val firstName: String,
    val lastName: String,
    val brand: String,
    val model: Int,
    val price: Double,

)

fun Driver.toDTO() = DriverDTO(
    id = userId,
    serial = serial,
    firstName = firstName,
    lastName = lastName,
    brand = brand,
    model = model,
    price = balance,
)

data class DriverCardDTO(
    val id: Int,
    val serial: String,
    val name: String,
    val brand: String,
    val model: Int,
    val price: Double,
    val img: String,
    val rating: Double,
    val type: String
)


fun Driver.toCardDTO(time: Int, numberPassenger: Int) = DriverCardDTO(
    id = userId,
    serial = serial,
    brand = brand,
    name = firstName + " " +lastName,
    model = model,
    price = fee(time, numberPassenger),
    img = img,
    rating = scoreAVG(),
    type = toString()
)

data class DriverCardAndTimeDTO(
    val time: Int,
    val cardDrivers: List<DriverCardDTO>
)


