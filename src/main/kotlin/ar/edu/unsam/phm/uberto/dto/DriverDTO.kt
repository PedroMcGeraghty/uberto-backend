package ar.edu.unsam.phm.uberto.dto

import ar.edu.unsam.phm.uberto.model.BikeDriver
import ar.edu.unsam.phm.uberto.model.Driver
import ar.edu.unsam.phm.uberto.model.PremiumDriver
import ar.edu.unsam.phm.uberto.model.SimpleDriver

data class DriverDTO(
    val id: String,
    val serial: String,
    val firstName: String,
    val lastName: String,
    val brand: String,
    val model: Int,
    val price: Double,

    )

fun Driver.toDTO(): DriverDTO {
    return DriverDTO(
        id = this.id.toString(),
        serial = serial,
        firstName = firstName,
        lastName = lastName,
        brand = brand,
        model = model,
        price = basePrice
    )
}

data class DriverCardDTO(
    val id: String,
    val serial: String,
    val name: String,
    val brand: String,
    val model: Int,
    val price: Double,
    val img: String,
    val rating: Double,
    val type: String
)

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
data class Driverwithscorage(
    val _id: String?,
    val credentialsId: Long?,
    val firstName: String,
    val lastName: String,
    val balance: Double,
    val tripsDTO: List<TripDriver>,
    val img: String,
    val model: Int,
    val brand: String,
    val serial: String,
    val basePrice: Double,
    val averageScore: Double,
    val _class: String
)

fun Driverwithscorage.toDriverEntity(): Driver {
    val driver = when (_class) {
        "ar.edu.unsam.phm.uberto.model.PremiumDriver" -> PremiumDriver()
        "ar.edu.unsam.phm.uberto.model.SimpleDriver" -> SimpleDriver()
        "ar.edu.unsam.phm.uberto.model.BikeDriver" -> BikeDriver()
        else -> throw IllegalArgumentException("Tipo de driver no soportado: $_class")
    }
    driver.id = this._id
    driver.credentialsId = this.credentialsId
    driver.firstName = this.firstName
    driver.lastName = this.lastName
    driver.balance = this.balance
    driver.tripsDTO = this.tripsDTO.toMutableList()
    driver.img = this.img
    driver.model = this.model
    driver.brand = this.brand
    driver.serial = this.serial
    driver.basePrice = this.basePrice
    return driver
}

fun Driverwithscorage.toAvailableDto() = DriverAvailableDto(
    driver = this.toDriverEntity(),
    averageScore = this.averageScore
)

data class DriverCardAndTimeDTO(
    val time: Int,
    val cardDrivers: List<DriverCardDTO>
)

data class DriverImg(var img: String)
fun Driver.toImgDTO() = DriverImg(
    img = img
)

