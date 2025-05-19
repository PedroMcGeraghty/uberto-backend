package ar.edu.unsam.phm.uberto.dto

import ar.edu.unsam.phm.uberto.model.Driver
//import ar.edu.unsam.phm.uberto.repository.DriverAvgDTO

data class DriverDTO(
    val id: Long,
    val serial: String,
    val firstName: String,
    val lastName: String,
    val brand: String,
    val model: Int,
    val price: Double,

    )

fun Driver.toDTO(): DriverDTO {
    val credId = requireNotNull(credentials?.id) { "UserAuthCredentials ID is null" }

    return DriverDTO(
        id = credId,
        serial = serial,
        firstName = firstName,
        lastName = lastName,
        brand = brand,
        model = model,
        price = basePrice
    )
}


data class DriverCardDTO(
    val id: Long,
    val serial: String,
    val name: String,
    val brand: String,
    val model: Int,
    val price: Double,
    val img: String,
    val rating: Double,
    val type: String
)


fun Driver.toCardDTO(time: Int, numberPassenger: Int): DriverCardDTO {
    val driverId = requireNotNull(id) { "Driver entity ID is null" }

    return DriverCardDTO(
        id = driverId,
        serial = serial,
        brand = brand,
        name = "$firstName $lastName",
        model = model,
        price = fee(time, numberPassenger),
        img = img,
        rating = scoreAVG(),
        type = toString()
    )
}

fun Driver.toAvailableDTO(time: Int, numberPassenger: Int, scores: Double): DriverCardDTO {
    val driverId = requireNotNull(id) { "Driver entity ID is null" }

    return DriverCardDTO(
        id = driverId,
        serial = serial,
        brand = brand,
        name = "$firstName $lastName",
        model = model,
        price = fee(time, numberPassenger),
        img = img,
        rating = scores,
        type = toString()
    )
}


data class DriverAvailableDto(
    val driver: Driver,
    val averageScore: Double
)


data class DriverCardAndTimeDTO(
    val time: Int,
    val cardDrivers: List<DriverCardDTO>
)

data class DriverImg(var img: String)
fun Driver.toImgDTO() = DriverImg(
    img = img
)

