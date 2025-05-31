package ar.edu.unsam.phm.uberto.dto

import ar.edu.unsam.phm.uberto.model.Driver
import ar.edu.unsam.phm.uberto.model.Trip
import java.time.LocalDateTime

data class FormTripDTO(
    val origin: String,
    val destination: String,
    val numberPassengers: Int,
    val name: String,
    val userId: Long
){}

data class TripGenericDTO(
    val id: Long,
    val entityId: String,
    val name: String,
    var duration: Int,
    val numberPassengers: Int,
    val date: LocalDateTime,
    val origin: String,
    val destination: String,
    val imgAvatar: String,
    val price: Double,
    val scored: Boolean
){
    fun toTrip(tripGenericDTO: TripGenericDTO): Trip = Trip().apply {
        duration = tripGenericDTO.duration
        numberPassengers = tripGenericDTO.numberPassengers
        date = tripGenericDTO.date
        origin = tripGenericDTO.origin
        destination = tripGenericDTO.destination
    }
}

fun Trip.toPassengerGenericDTO() : TripGenericDTO {
    val clientId = requireNotNull(client.id) { "Client ID is null" }
    val id = requireNotNull(id) { "ID is null" }

    return TripGenericDTO(
        id = id,
        entityId = clientId.toString(),
        name = client.firstName + " " + client.lastName,
        destination = destination,
        duration = duration,
        numberPassengers = numberPassengers,
        date = date,
        origin = origin,
        imgAvatar = client.img,
        scored = this.scored(),
        price = price
    )
}

fun Driver.toDriverGenericDTO(tripDriver: TripDriver) : TripGenericDTO {
    val id = requireNotNull(id) { "ID is null" }

    return TripGenericDTO(
        id = tripDriver.id,
        entityId = id,
        name = firstName + " " + lastName,
        destination = tripDriver.destination,
        duration = tripDriver.duration,
        numberPassengers = tripDriver.numberPassengers,
        date = tripDriver.date,
        origin = tripDriver.origin, 
        imgAvatar = img,
        scored = tripDriver.rating > 0,
        price = tripDriver.price
    )
}

fun listDriverToListDriverGenericDTO(listDriver: List<Driver>) :List<TripGenericDTO>{
    return listDriver.map { it.tripsDTO.map { trip -> it.toDriverGenericDTO(trip) } }.flatten()
}

data class TripDriver(
    var id: Long = 0,
    var duration: Int = 0,
    var numberPassengers: Int = 0,
    var date: LocalDateTime = LocalDateTime.now(),
    var origin: String = "",
    var destination: String = "",
    var price: Double = 0.0,
    var finishedDateTime: LocalDateTime = LocalDateTime.now(),
    var rating: Int = 0,
    var passengerId : Long = 0,
)
{}


fun Trip.toTripDriverDTO() = TripDriver(
    id = id!!,
    duration = duration,
    origin = origin,
    destination = destination,
    date = date,
    numberPassengers = numberPassengers,
    price = price,
    finishedDateTime = finishedDateTime,
    rating = this.score?.scorePoints ?: 0,
    passengerId = client.id!!,

)

data class TripCreateDTO(
    val driverId: String,
    val duration: Int,
    val numberPassengers: Int,
    val date: LocalDateTime,
    val origin: String,
    val destination: String
)