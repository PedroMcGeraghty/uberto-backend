
package ar.edu.unsam.phm.uberto.dto

data class PendingAndFinishedTripsDTO(
    val pendingTrips: List<TripGenericDTO>,
    val finishedTrips: List<TripGenericDTO>
)

