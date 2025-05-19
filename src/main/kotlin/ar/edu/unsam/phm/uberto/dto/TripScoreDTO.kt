package ar.edu.unsam.phm.uberto.dto

data class TripScoreDTO(
    val tripId: Long,
    val message: String,
    val scorePoints: Int,
    val date: String,
    val passengerName: String,
    val driverName: String,
    val avatarUrlPassenger: String,
    val avatarUrlDriver: String,
    val delete: Boolean
)
