package ar.edu.unsam.phm.uberto.model

import ar.edu.unsam.phm.uberto.DriverNotAvaliableException
import ar.edu.unsam.phm.uberto.repository.AvaliableInstance
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

abstract class Driver(
    override var firstName: String = "",
    override var lastName: String = "",
    override var balance: Double = 0.0,
    override var trips: MutableList<Trip> = mutableListOf(),
    override var img: String = "",
    override var userId: Int = 0,
    var model:Int = 0,
    var brand:String = "",
    var serial:String = "",
    var basePrice:Double = 0.0
):User, AvaliableInstance {

    fun avaliable(tripDate: LocalDateTime, tripDuration: Int):Boolean{
        val finishedDate =  tripDate.plus(tripDuration.toLong(), ChronoUnit.MINUTES)
        return !this.tripOverlapping(tripDate, finishedDate) || trips.isEmpty()
    }

    fun tripOverlapping(tripStart: LocalDateTime, tripEnd: LocalDateTime):Boolean{
        return this.pendingTrips().any{ pending:Trip ->
            tripStart < pending.finalizationDate() && tripEnd > pending.date
        }
    }

    fun scoreAVG():Double{
        val avg = getScoredTrips().map { it.score!!.scorePoints }.average()
        return if(avg.isNaN()) 0.0 else avg
    }

    fun fee(time: Int, numberPassenger: Int):Double{
        return this.basePrice + this.plusBasePrice(time, numberPassenger) * time
    }

    abstract fun plusBasePrice(time: Int, numberPassengers: Int):Double

    override fun getScores(): List<TripScore> {
        return this.getScoredTrips().map{ it.score!! }
    }

    private fun getScoredTrips():List<Trip>{
        return this.trips.filter { it.score != null }
    }

    fun addTrip(trip:Trip) {
        this.trips.add(trip)
        acceditTrip(trip.price())
    }

    fun acceditTrip(price: Double){
        this.balance += price
    }

    fun pendingTrips() = trips.filter { trip:Trip ->trip.pendingTrip()}
    fun finishedTrips() = trips.filter { trip:Trip ->trip.finished()}

    fun responseTrip(newTrip: Trip, time: Int) {
        if(!avaliable(newTrip.date, time)){
            throw DriverNotAvaliableException()
        }
        addTrip(newTrip)
    }

}

