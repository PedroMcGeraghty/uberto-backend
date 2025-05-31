package ar.edu.unsam.phm.uberto.repository

import ar.edu.unsam.phm.uberto.dto.Driverwithscorage
import ar.edu.unsam.phm.uberto.dto.TripScoreDTOMongo
import ar.edu.unsam.phm.uberto.model.Driver
import org.springframework.data.mongodb.repository.Aggregation
import org.springframework.data.mongodb.repository.MongoRepository
import java.time.LocalDateTime
import org.springframework.data.mongodb.repository.Query
import java.time.Instant


interface MongoDriverRepository: MongoRepository<Driver,String> {


    fun findByCredentialsId(id: Long): Driver

    @Aggregation(
        pipeline = [
            "{ '\$match': { 'tripsDTO.passengerId': ?0 } }",
            "{ '\$addFields': { " +
                    "'tripsDTO': { '\$filter': { " +
                    "'input': '\$tripsDTO', 'as': 'trip', " +
                    "'cond': { '\$and': [ " +
                    "{ '\$eq': ['\$\$trip.passengerId', ?0] }, " +
                    "{ '\$gte': ['\$\$trip.finishedDateTime', ?1] } " +
                    "] } } } } }"
        ]
    )
    fun findByPassengerIdPendingTripsDTO(passengerId: Long, finisherdDate: LocalDateTime): List<Driver>
  
    @Aggregation(
        pipeline = [
            "{ '\$match': { 'tripsDTO.passengerId': ?0 } }",
            "{ '\$addFields': { " +
                    "'tripsDTO': { '\$filter': { " +
                    "'input': '\$tripsDTO', 'as': 'trip', " +
                    "'cond': { '\$and': [ " +
                    "{ '\$eq': ['\$\$trip.passengerId', ?0] }, " +
                    "{ '\$lt': ['\$\$trip.finishedDateTime', ?1] } " +
                    "] } } } } }"
        ]
    )
    fun findByPassengerIdFinishedTripsDTO(passengerId: Long, date: LocalDateTime): List<Driver>



    @Aggregation(pipeline = [
        "{ '\$match': { '\$or': [ " +
                "{ 'tripsDTO': { '\$exists': false } }, " +
                "{ 'tripsDTO': { '\$size': 0 } }, " +
                "{ 'tripsDTO': { '\$not': { '\$elemMatch': { " +
                "'date': { '\$lt': ?1 }, " +
                "'finishedDateTime': { '\$gt': ?0 } " +
                "} } } }" +
                "] } }",

        "{ '\$addFields': { " +
                "'averageScore': { " +
                "'\$avg': { " +
                "'\$map': { " +
                "'input': { '\$filter': { 'input': '\$tripsDTO', 'as': 't', 'cond': { '\$gt': ['\$\$t.rating', 0] } } }, " +
                "'as': 't', " +
                "'in': '\$\$t.rating' " +
                "} " +
                "} " +
                "} " +
                "} }",

        "{ '\$project': { " +
                "'_id': 1, " +
                "'firstName': 1, " +
                "'lastName': 1, " +
                "'balance': 1, " +
                "'credentialsId': 1, " +
                "'img': 1, " +
                "'model': 1, " +
                "'brand': 1, " +
                "'serial': 1, " +
                "'basePrice': 1, " +
                "'tripsDTO': 1, " +
                "'averageScore': { '\$ifNull': [ '\$averageScore', 0 ] }, " +
                "'_class': 1 " +
                "} }"
    ])
    fun getAvailable(start: Instant, end: Instant): List<Driverwithscorage>

    @Query(value = "{ '_id': ?0 }", fields = "{ 'tripsScoreDTO': 1, '_id': 0 }")
    fun getScoreByDriverID(driverId: String): DriverTripScoresProjection?


    @Aggregation(
        pipeline = [
            "{ '\$match': { 'tripsDTO.passengerId': ?0 } }",  // Filtrar conductores con tripsDTO donde passengerId coincide
            "{ '\$unwind': '\$tripsDTO' }",                  // Descomponer tripsDTO en documentos individuales
            "{ '\$match': { 'tripsDTO.passengerId': ?0 } }", // Filtrar únicamente los viajes con el passengerId específico
            "{ '\$project': { '_id': 0, 'tripId': '\$tripsDTO._id' } }" // Proyectar únicamente el tripId
        ]
    )
    fun findTripIdsByPassengerId(passengerId: Long): List<Long>

    @Aggregation(
        pipeline = [
            "{ '\$unwind': '\$tripsScoreDTO' }",
            "{ '\$match': { '\$and': [ " +
                    "{ 'tripsScoreDTO.tripId': { '\$in': ?0 } }, " + // Coincide con los tripIds de la lista
                    "{ 'tripsScoreDTO.scorePoints': { '\$gt': 0 } } " + // Solo valoraciones con puntuación
                    "] } }",
            "{ '\$project': { " +
                    "'tripId': '\$tripsScoreDTO.tripId', " +
                    "'name': { '\$concat': ['\$firstName', ' ', '\$lastName'] }, " + // Concatenar nombre y apellido
                    "'date': '\$tripsScoreDTO.date', " +
                    "'scorePoints': '\$tripsScoreDTO.scorePoints', " +
                    "'message': '\$tripsScoreDTO.message', " +
                    "'avatarUrlImg': '\$img', " + // Imagen del conductor
                    "'isDeleted': '\$tripsScoreDTO.isDeleted', " +
                    "'isEditMode': '\$tripsScoreDTO.isEditMode' " +
                    "} }"
        ]
    )
    fun findTripScoresByTripIds(tripIds: List<Long>): List<TripScoreDTOMongo>
}

data class DriverTripScoresProjection(
    val tripsScoreDTO: List<TripScoreDTOMongo>
)