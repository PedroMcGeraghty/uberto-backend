package ar.edu.unsam.phm.uberto.model

import ar.edu.unsam.phm.uberto.dto.*
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.mongodb.core.mapping.Document


@Document(collection = "mongodriver")
abstract class Driver():User {

    @Id
    var id: String? = null

    @Transient
    var credentials: UserAuthCredentials? = null

    var credentialsId: Long? = null

    override lateinit var firstName: String

    override lateinit var lastName: String

    override var balance: Double = 0.0

    @Transient
    override var trips: MutableList<Trip> = mutableListOf()

    var tripsDTO: MutableList<TripDriver> = mutableListOf()

    var tripsScoreDTO: MutableList<TripScoreDTOMongo> = mutableListOf()

    override lateinit var img: String

    var model:Int = 0

    lateinit var brand:String

    lateinit var serial:String

    var basePrice:Double = 0.0

    @PrePersist
    @PreUpdate
    fun toTripDTO() {
        tripsDTO = trips.map { it.toTripDriverDTO() }.toMutableList()
    }

    override fun getScores(): List<TripScore> {
        return this.getScoredTrips().map{ it.score!! }
    }

    abstract fun plusBasePrice(time: Int, numberPassengers: Int):Double

    private fun getScoredTrips():List<Trip>{
        return this.trips.filter { it.score != null }
    }

    fun scoreAVG():Double{
        val avg = getScoredTrips().map { it.score!!.scorePoints }.average()
        return if(avg.isNaN()) 0.0 else avg
    }

    fun fee(time: Int, numberPassenger: Int):Double{
        return this.basePrice + this.plusBasePrice(time, numberPassenger) * time
    }

    fun addTrip(trip:Trip) {
        this.trips.add(trip)
        acceditTrip(trip.price)
    }

    fun acceditTrip(price: Double){
        this.balance += price
    }

    fun responseTrip(newTrip: Trip, time: Int) {
        addTrip(newTrip)
    }

    fun update(dto: DriverDTO): Driver {
        this.firstName = dto.firstName
        this.lastName = dto.lastName
        this.serial = dto.serial
        this.brand = dto.brand
        this.model = dto.model
        this.basePrice = dto.price
        return this
    }

}

@Document
class BikeDriver(): Driver() {
    private val reference:Double = 500.0
    override fun plusBasePrice(time: Int, numberPassengers: Int): Double {
        return if(time < 30) reference else reference + 100.0
    }
    override fun toString() = "Motorbiker"
}

@Document
class SimpleDriver(): Driver() {
    override fun plusBasePrice(time: Int, numberPassengers: Int): Double {
        return 1000.0
    }

    override fun toString() = "Simple Driver"
}

@Document
class PremiumDriver(): Driver() {
    private val reference:Double = 1500.0

    override fun plusBasePrice(time: Int, numberPassengers: Int): Double {
        return if(numberPassengers > 1) reference else reference + 500.0
    }

    override fun toString() = "Premium Driver"

}