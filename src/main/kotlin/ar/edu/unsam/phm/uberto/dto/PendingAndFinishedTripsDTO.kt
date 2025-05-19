package ar.edu.unsam.phm.uberto.dto

data class PendingAndFinishedTripsDTO(
    val pendingTrips: List<TripDTO>,
    val finishedTrips: List<TripDTO>
)

