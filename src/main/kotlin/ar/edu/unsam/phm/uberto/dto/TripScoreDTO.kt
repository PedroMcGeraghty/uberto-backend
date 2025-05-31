package ar.edu.unsam.phm.uberto.dto

import ar.edu.unsam.phm.uberto.model.Trip
import ar.edu.unsam.phm.uberto.model.TripScore
import java.time.LocalDateTime

data class TripScoreDTOMongo(
    val tripId: Long ,
    val name: String,
    val date: LocalDateTime,
    val scorePoints: Int,
    val message: String,
    val avatarUrlImg: String,
    val isDeleted : Boolean,
    val isEditMode : Boolean
)

fun Trip.toTripScoreDTOMongo() = TripScoreDTOMongo(
    tripId = id!!,
    name = client.firstName + client.lastName,
    date = date,
    scorePoints = score!!.scorePoints,
    message = score!!.message,
    avatarUrlImg = client.img,
    isDeleted = this.canDeleteScore(client.id!!),
    isEditMode =  this.scored()
)

fun Trip.toTripScorePassengerDTOMongo() = TripScoreDTOMongo(
    tripId = id!!,
    name = driver.firstName + driver.lastName,
    date = date,
    scorePoints = score!!.scorePoints,
    message = score!!.message,
    avatarUrlImg = driver.img,
    isDeleted = this.canDeleteScore(client.id!!),
    isEditMode =  !this.canDeleteScore(client.id!!)
)